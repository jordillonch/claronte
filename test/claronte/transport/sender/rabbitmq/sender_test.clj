(ns claronte.transport.sender.rabbitmq.sender-test
  (:require [clojure.test :refer :all]
            [claronte.config.claronte-config :refer :all]
            [claronte.transport.sender.rabbitmq.sender :refer :all]
            [claronte.transport.sender.rabbitmq.connection-factory :refer :all]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [langohr.queue :as lq]
            ))

(deftest send-one-message
  (testing
      (let [
             id 1
             message "foo"
             exchange-name "exchange_clojure_test"
             routing-key ""
             connection (create-connection-rabbitmq sender-rabbitmq-server-connection-parameters)
             channel (open-channel-rabbitmq connection)
             rabbitmq-sender (->RabbitMqSender id channel exchange-name routing-key)

             ; create the exchange
             _ (le/declare channel exchange-name "direct")
             ; create a queue in order to consume message that has to be sent
             queue (lq/declare-server-named channel :exclusive true)
             _ (lq/bind channel queue exchange-name :routing-key routing-key)

             ; send
             _ (.publish rabbitmq-sender message)

             ; consume
             [_ payload] (lb/get channel queue)
             consumed-message (String. payload)
             ]
        (is (= message consumed-message))
        )
    ))
