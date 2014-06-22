(ns claronte.transport.sender.rabbitmq.sender
  (:require [claronte.transport.sender.sender-protocol :refer :all]
            [clojure.tools.logging :as log]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [langohr.exchange :as le]))

(deftype RabbitMqSender [rabbitmq-server-connection-parameters exchange-name routing-key]
  Sender

  (publish [this message]
    (try
      (let [conn (rmq/connect rabbitmq-server-connection-parameters)
            ch (lch/open conn)]
        (log/debug (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
        (lb/publish ch exchange-name routing-key message :content-type "text/plain")
        (log/debug "[main] Disconnecting...")
        (rmq/close ch)
        (rmq/close conn))
      (catch Exception e
        (println (str "caught exception: " (.getMessage e)))
        ))

    "Send messages to RabbitMQ. The exchange have to exist.")
  )