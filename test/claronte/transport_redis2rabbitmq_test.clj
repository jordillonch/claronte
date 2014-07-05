(ns claronte.transport-redis2rabbitmq-test
  (:require
    [clojure.test :refer :all]
    [claronte.config.claronte-config :refer :all]
    [claronte.transport.sender.rabbitmq.connection-factory :refer :all]
    [claronte.transport-redis2rabbitmq :refer :all]
    [clojure.core.async :as async :refer [<!!]]
    [langohr.core :as rmq]
    [langohr.channel :as lch]
    [langohr.basic :as lb]
    [langohr.exchange :as le]
    [langohr.queue :as lq]
    )
  )

(deftest transport-redis2rabbitmq-test
  (testing
      (let [number-of-workers 4
            number-of-subworkers 10
            control-stop (atom false)

            ; create channel
            connection (create-connection-rabbitmq sender-rabbitmq-server-connection-parameters)
            channel (open-channel-rabbitmq connection)
            ; create the exchange
            _ (le/declare channel "claronte" "direct")

            pool-of-workers-result (transport-redis2rabbitmq number-of-workers number-of-subworkers control-stop)
            ]
        (Thread/sleep 1000) ; add more time in order to see how many messages per second are being published
        (reset! control-stop true)

        ;(prn pool-of-workers-result)

        (is (= 1 1))
        )
    ))

