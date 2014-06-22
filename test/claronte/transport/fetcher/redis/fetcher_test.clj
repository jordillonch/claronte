(ns claronte.transport.fetcher.redis.fetcher-test
  (:require [clojure.test :refer :all]
            [taoensso.carmine :as car :refer (wcar)]
            [claronte.config.claronte-config :refer :all]
            [claronte.transport.fetcher.redis.fetcher :refer :all]))

(deftest fetch-and-confirm-one-message-test
  (testing
      (let [redis-server-connection-parameters fetcher-redis-server-connection-parameters

            source-key "claronte:test:source"
            backup-key "claronte:test:backup"
            message "foo"

            _ (car/wcar redis-server-connection-parameters (car/rpush source-key message))

            redis-fetcher (->RedisFetcher redis-server-connection-parameters source-key backup-key)

            ; fetch message
            message-fetched (.fetch-one-message redis-fetcher)

            message-backed-up (car/wcar redis-server-connection-parameters (car/lrange backup-key 0 0))

            ; confirm message
            _ (.confirm-message redis-fetcher)

            backup-message-list-len-after-confirm (car/wcar redis-server-connection-parameters (car/llen backup-key))

            ]

        (is (= message message-fetched))
        (is (= message (first message-backed-up)))
        (is (zero? backup-message-list-len-after-confirm))
        )
    ))

(deftest fetch-and-rollback-one-message-test
  (testing
      (let [redis-server-connection-parameters fetcher-redis-server-connection-parameters

            source-key "claronte:test:source"
            backup-key "claronte:test:backup"
            message "bar"

            _ (car/wcar redis-server-connection-parameters (car/rpush source-key message))

            redis-fetcher (->RedisFetcher redis-server-connection-parameters source-key backup-key)

            ; fetch message
            message-fetched (.fetch-one-message redis-fetcher)

            ; rollback message
            message-rolledback (.rollback-message redis-fetcher)

            message-in-the-source-key-after-rollback (car/wcar redis-server-connection-parameters (car/lrange source-key 0 0))

            backup-message-list-len-after-rollback (car/wcar redis-server-connection-parameters (car/llen backup-key))

            ; cleanup
            _ (car/wcar redis-server-connection-parameters (car/del source-key))
            ]

        (is (= message message-fetched))
        (is (= message message-rolledback))
        (is (= message (first message-in-the-source-key-after-rollback)))
        (is (zero? backup-message-list-len-after-rollback))
        )
      ))


