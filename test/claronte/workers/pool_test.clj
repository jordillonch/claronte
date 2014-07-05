(ns claronte.workers.pool-test
  (:require [clojure.test :refer :all]
            [claronte.workers.pool :refer :all]
            [clojure.core.async :as async :refer [go <!!]]
            ))

(defn test-unit-of-work [worker-num subworker-num control-stop-atom]
  ;(Thread/sleep (rand-int 10))
  ;(println "w" worker-num "- s" subworker-num)
  )

(deftest pool-of-workers-test
  (testing
      (let [number-of-workers 4
            number-of-subworkers 10
            control-stop (atom false)
            pool-of-workers-result (pool-of-workers number-of-workers number-of-subworkers test-unit-of-work control-stop)
            ]
        ;(Thread/sleep 500)
        (reset! control-stop true)
        ;(println "stop!")
        ;(Thread/sleep 100)
        (let [result (flatten
                       (doall
                         (for [pool-of-worker-result pool-of-workers-result]
                           (let [
                                  pool-of-subworkers-result (deref pool-of-worker-result)]
                             (doall
                               (for [channel pool-of-subworkers-result]
                                 (<!! channel)
                                 )
                               )
                             )
                           )
                         )
                       )
              expected-result (for [x (range number-of-workers)
                                    y (range number-of-subworkers)]
                                (str x "-" y))
              ]
          (is (= expected-result result))
          )
        )
    ))

