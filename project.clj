(defproject claws "0.1.1-SNAPSHOT"
  :description "A monster to rule all the others - j/k - mostly stuff to manage my AWS infrastructure"
  :url "http://sidcarter.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta3"]
                 [amazonica "0.3.24"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main claws.core
  :aot :all)
