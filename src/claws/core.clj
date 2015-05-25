(ns claws.core
  (:require [amazonica.aws.ec2 :as ec2]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class claws.core))

(def cli-options
  ;; list all the instances or security groups
  [["-l" "--list" "list either instances or security groups. default is to list instances"
    :default "instances"]
  ["-h" "--help" "Usage: claws -l to list all instances"]])

(defn usage
  [banner]
  (println banner))

(defn regions
  []
  ((ec2/describe-regions) :regions))

(defn vpcs
  []
  ((ec2/describe-vpcs) :vpcs))

(defn sgs
  []
  ((ec2/describe-security-groups) :security-groups))

(defn print_sgs
  [sgs]
  (doseq [sg sgs]
    (println (str (:group-name sg) " - " (:group-id sg)))))

(defn instances
  []
  ((ec2/describe-instances) :reservations))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (= (:list options) "instances") (println (instances))
      (:list options) (println options arguments)
      (:help options) (println "help"))))