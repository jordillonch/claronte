(ns claronte.workers.pool
  (:require [clojure.core.async :as async :refer [go]]
            ))

(defn- pool-of-subworkers [worker-num number-of-subworkers unit-of-work control-stop-atom]
  (doall
    (for [subworker-num (range number-of-subworkers)]
      (go (while (not (deref control-stop-atom))
            (unit-of-work worker-num subworker-num control-stop-atom)
            )
          (str worker-num "-" subworker-num))
      )
    )
  )

(defn pool-of-workers [number-of-workers number-of-subworkers unit-of-work control-stop-atom]
  (doall
    (for [worker-num (range number-of-workers)]
      (future (pool-of-subworkers worker-num number-of-subworkers unit-of-work control-stop-atom))
      )
    )
  )
