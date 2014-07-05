(ns claronte.transport.sender.rabbitmq.connection-factory
  (:require
    [clojure.tools.logging :as log]
    [langohr.core :as rmq]
    [langohr.channel :as lch]
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
  (lch/open conn)
  ;ch (lch/open conn id)
  ;ch   (rmq/create-channel conn id)
  ;(log/debug (format "[main] Connected. Channel id: %d" (.getChannelNumber ch)))
  )

;(rmq/close ch)
;(rmq/close conn)
;(log/debug "[main] Disconnecting...")
