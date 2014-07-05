(ns claronte.workers.pool
  (:require [clojure.core.async :as async :refer [go]]
            ))

(defn- pool-of-subworkers [worker-num number-of-subworkers unit-of-work init-data]
  (doall
    (for [subworker-num (range number-of-subworkers)]
      (go (unit-of-work worker-num subworker-num init-data))
      )
    )
  )

(defn pool-of-workers [number-of-workers number-of-subworkers unit-of-work init-data]
  (doall
    (for [worker-num (range number-of-workers)]
      (future (pool-of-subworkers worker-num number-of-subworkers unit-of-work init-data))
      )
    )
  )
