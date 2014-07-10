(ns claronte.transport.sender.rabbitmq.sender
  (:require [claronte.transport.sender.sender-protocol :refer :all]
            [clojure.tools.logging :as log]
            [langohr.basic :as lb]
            [langohr.confirm :as lcf]
            )
  (:import
    com.novemberain.langohr.Channel
    )
  )

(deftype RabbitMqSender [id ^Channel channel exchange-name routing-key]
  Sender

  (publish [this message]
    "Send messages to RabbitMQ. The exchange it should exist."
    (try
      (lb/publish channel exchange-name routing-key message :content-type "text/plain")
      (.waitForConfirms channel)
      (catch Exception e
             (println "caught exception: " (.getMessage e))
        ))
    )
  )