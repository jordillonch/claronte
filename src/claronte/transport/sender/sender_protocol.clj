(ns claronte.transport.sender.sender-protocol)

(defprotocol Sender
  (publish [this message])
  )