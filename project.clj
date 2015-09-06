(defproject claws "0.1.4"
  :description "A monster to rule all the others - j/k - mostly stuff to monitor AWS infrastructure"
  :url "http://sidcarter.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [amazonica "0.3.33"]
                 [org.clojure/tools.cli "0.3.2"]]
  :main ^:skip-aot claws.core
  :profiles {:uberjar {:aot :all}})
