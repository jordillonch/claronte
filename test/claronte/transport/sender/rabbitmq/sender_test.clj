(ns claronte.transport.sender.rabbitmq.sender-test
  (:require [clojure.test :refer :all]
            [claronte.config.claronte-config :refer :all]
            [claronte.transport.sender.rabbitmq.sender :refer :all]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [langohr.queue :as lq]))

(deftest send-one-message
  (testing
      (let [
             message "foo"
             exchange-name "exchange_clojure_test"
             routing-key ""
             rabbitmq-sender (->RabbitMqSender sender-rabbitmq-server-connection-parameters exchange-name routing-key)

             ; create channel
             conn (rmq/connect)
             ch (lch/open conn)
             ; create the exchange
             _ (le/declare ch exchange-name "direct")
             ; create a queue in order to consume message that has to be sent
             queue (lq/declare-server-named ch :exclusive true)
             _ (lq/bind ch queue exchange-name :routing-key routing-key)

             ; send
             _ (.publish rabbitmq-sender message)

             ; consume
             [_ payload] (lb/get ch queue)
             consumed-message (String. payload)
             ]
        (is (= message consumed-message))
        )
    ))
