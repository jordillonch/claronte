(ns claronte.transport-redis2rabbitmq
  (:require
    [claronte.workers.pool :refer :all]
    [claronte.config.claronte-config :refer :all]
    [claronte.transport.fetcher.redis.fetcher :refer :all]
    [claronte.transport.sender.rabbitmq.sender :refer :all]
    [claronte.transport.sender.rabbitmq.connection-factory :refer :all]
    [claronte.transport.transporter :refer :all]
    )
  )

(defn- transport-redis2rabbitmq-generic [id redis-source-key redis-backup-key rabbitmq-exchange-name rabbitmq-routing-key control-stop-atom]
  (let [
         fetcher (->RedisFetcher id fetcher-redis-server-connection-parameters redis-source-key redis-backup-key)

         connection (create-connection-rabbitmq sender-rabbitmq-server-connection-parameters)
         channel (open-channel-rabbitmq connection)
         sender (->RabbitMqSender id channel rabbitmq-exchange-name rabbitmq-routing-key)
         ]
    (while (not (deref control-stop-atom))
      (transport-message fetcher sender)
      )
    )
  )

(defn- transport-unit-of-work [worker-num subworker-num]
  (let [redis-source-key "claronte"
        redis-backup-key (str "claronte-backup-" worker-num "-" subworker-num)
        rabbitmq-exchange-name "claronte"
        rabbitmq-routing-key ""
        control-stop-transport (atom false) ; todo: refactor
        id (+ (* worker-num 1000) subworker-num)
        ]
    (transport-redis2rabbitmq-generic id redis-source-key redis-backup-key rabbitmq-exchange-name rabbitmq-routing-key control-stop-transport)
    )
  )

(defn transport-redis2rabbitmq [number-of-workers number-of-subworkers control-stop-atom]
  (pool-of-workers number-of-workers number-of-subworkers transport-unit-of-work control-stop-atom)
  )