(ns claronte.transport.sender.rabbitmq.connection-factory
  (:require
    [clojure.tools.logging :as log]
    [langohr.core :as rmq]
    [langohr.channel :as lch]
    [langohr.confirm :as lcf]
    )
  (:import
    com.novemberain.langohr.Connection
    com.novemberain.langohr.Channel
    )
  )

(defn ^Connection create-connection-rabbitmq [rabbitmq-server-connection-parameters]
  (rmq/connect rabbitmq-server-connection-parameters)
  )

(defn ^Channel open-channel-rabbitmq [^Connection conn]

  (let [channel (lch/open conn)]
    (lcf/select channel)
    channel
    )
  )
