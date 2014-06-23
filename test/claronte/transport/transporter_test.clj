(ns claronte.transport.transporter-test
  (:require [clojure.test :refer :all]
            [claronte.config.claronte-config :refer :all]
            [claronte.transport.fetcher.redis.fetcher :refer :all]
            [claronte.transport.sender.rabbitmq.sender :refer :all]
            [claronte.transport.transporter :refer :all]
            [taoensso.carmine :as car :refer (wcar)]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [langohr.queue :as lq])
  )

(deftest transport-one-message
  (testing
      (let [redis-server-connection-parameters fetcher-redis-server-connection-parameters
            ; fetcher
            source-key "claronte:test:source"
            backup-key "claronte:test:backup"
            message "foo"
            _ (car/wcar redis-server-connection-parameters (car/rpush source-key message))
            fetcher (->RedisFetcher redis-server-connection-parameters source-key backup-key)

            ; sender
            exchange-name "exchange_clojure_test"
            routing-key ""
            ; create channel
            conn (rmq/connect)
            ch (lch/open conn)
            ; create the exchange
            _ (le/declare ch exchange-name "direct")
            ; create a queue in order to consume message that has to be sent
            queue (lq/declare-server-named ch :exclusive true)
            _ (lq/bind ch queue exchange-name :routing-key routing-key)
            sender (->RabbitMqSender sender-rabbitmq-server-connection-parameters exchange-name routing-key)

            ; transporter
            _ (transport-message fetcher sender)

            ; consume
            [_ payload] (lb/get ch queue)
            consumed-message (String. payload)
            ]
        (is (= message consumed-message))
        )
    ))
