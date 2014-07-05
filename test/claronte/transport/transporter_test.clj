(ns claronte.transport.transporter-test
  (:require [clojure.test :refer :all]
            [claronte.config.claronte-config :refer :all]
            [claronte.transport.fetcher.redis.fetcher :refer :all]
            [claronte.transport.sender.rabbitmq.sender :refer :all]
            [claronte.transport.sender.rabbitmq.connection-factory :refer :all]
            [claronte.transport.transporter :refer :all]
            [taoensso.carmine :as car :refer (wcar)]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [langohr.queue :as lq])
  )

(deftest transport-one-message
  (testing
      (let [redis-server-connection-parameters fetcher-redis-server-connection-parameters
            id 1
            ; fetcher
            source-key "claronte:test:source"
            backup-key "claronte:test:backup"
            message "foo"
            _ (car/wcar redis-server-connection-parameters (car/rpush source-key message))
            fetcher (->RedisFetcher id redis-server-connection-parameters source-key backup-key)

            ; sender
            exchange-name "exchange_clojure_test"
            routing-key ""
            ; create channel
            connection (create-connection-rabbitmq sender-rabbitmq-server-connection-parameters)
            channel (open-channel-rabbitmq connection)
            ; create the exchange
            _ (le/declare channel exchange-name "direct")
            ; create a queue in order to consume message that has to be sent
            queue (lq/declare-server-named channel :exclusive true)
            _ (lq/bind channel queue exchange-name :routing-key routing-key)
            sender (->RabbitMqSender id channel exchange-name routing-key)

            ; transporter
            _ (transport-message fetcher sender)

            ; consume
            [_ payload] (lb/get channel queue)
            consumed-message (String. payload)
            ]
        (is (= message consumed-message))
        )
    ))
