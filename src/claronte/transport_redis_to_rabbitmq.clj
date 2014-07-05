(ns claronte.transport-redis-to-rabbitmq
  (:require
    [claronte.workers.pool :refer :all]
    [claronte.config.claronte-config :refer :all]
    [claronte.transport.fetcher.redis.fetcher :refer :all]
    [claronte.transport.sender.rabbitmq.sender :refer :all]
    [claronte.transport.sender.rabbitmq.connection-factory :refer :all]
    [claronte.transport.transporter :refer :all]
    )
  )

(defn- transport-redis-to-rabbitmq-generic [id redis-source-key redis-backup-key rabbitmq-exchange-name rabbitmq-routing-key init-data]
  (let [
         fetcher (->RedisFetcher id fetcher-redis-server-connection-parameters redis-source-key redis-backup-key)

         connection (create-connection-rabbitmq sender-rabbitmq-server-connection-parameters)
         channel (open-channel-rabbitmq connection)
         sender (->RabbitMqSender id channel rabbitmq-exchange-name rabbitmq-routing-key)
         ]
    (while (not (deref (init-data :stop-worker-atom)))
      (transport-message fetcher sender)
      )
    )
  )

(defn- transport-unit-of-work [worker-num subworker-num init-data]
  (let [redis-source-key "claronte"
        redis-backup-key (str "claronte-backup-" worker-num "-" subworker-num)
        rabbitmq-exchange-name "claronte"
        rabbitmq-routing-key ""
        id (+ (* worker-num 1000) subworker-num)
        ]
    (transport-redis-to-rabbitmq-generic id redis-source-key redis-backup-key rabbitmq-exchange-name rabbitmq-routing-key init-data)
    )
  )

(defn transport-redis-to-rabbitmq [number-of-workers number-of-subworkers init-data]
  (pool-of-workers number-of-workers number-of-subworkers transport-unit-of-work init-data)
  )