(ns claronte.transport.fetcher.fetcher-protocol)

(defprotocol Fetcher
  (fetch-one-message [this])
  (confirm-message [this])
  (rollback-message [this])
  )