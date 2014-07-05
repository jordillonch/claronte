(ns claronte.transport.transporter
  (:require
    [claronte.transport.fetcher.fetcher-protocol :refer :all]
    [claronte.transport.sender.sender-protocol :refer :all]))

; improve transport message
;   - wait/sleep until there are messages from fetcher
(defn transport-message [fetcher sender]
  (let [message (.fetch-one-message fetcher)]
    (.publish sender message)
    (.confirm-message fetcher)
    )
  )