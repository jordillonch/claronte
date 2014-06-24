(defproject claronte "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.6.0"]
                  [com.taoensso/carmine "2.6.2"]
                  [com.novemberain/langohr "2.11.0"]
                  [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                     javax.jms/jms
                                                     com.sun.jdmk/jmxtools
                                                     com.sun.jmx/jmxri]]
                  [org.clojure/tools.logging "0.3.0"]
                  [org.clojure/core.async "0.1.303.0-886421-alpha"]
                ]
  :main ^:skip-aot claronte.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
