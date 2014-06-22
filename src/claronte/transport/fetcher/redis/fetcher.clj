(ns claronte.transport.fetcher.redis.fetcher
  (:require [taoensso.carmine :as car :refer (wcar)]
            [claronte.transport.fetcher.fetcher-protocol :refer :all]))

(deftype RedisFetcher [redis-server-connection-parameters source-key backup-key]
  Fetcher

  (fetch-one-message [this]
    "Fetch one message and backup it"
    (car/wcar redis-server-connection-parameters (car/rpoplpush source-key backup-key))
    )

  (confirm-message [this]
    "Delete from backup"
    (car/wcar redis-server-connection-parameters (car/del backup-key))
    )

  (rollback-message [this]
    (car/wcar redis-server-connection-parameters (car/rpoplpush backup-key source-key))
    )
  )

