(ns claws.core
  (:require [amazonica.aws.ec2 :as ec2]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class claws.core))

(def cli-options
  ;; list all the instances or security groups
  [["-l" "--list ENTITY" "list entity - either instances or security_groups. default is to list instances"
    :default "security_groups"]
  ["-h" "--help"]])

(defn print_usage
  [summary]
  (let [usage (->> ["A program to manage and list crucial AWS data"
       ""
       "Usage: claws [options] arguments"
       ""
       "Options:"
       summary
       ""] (string/join \newline))]
    (println usage)))

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
  []
  (let [sgs ((ec2/describe-security-groups) :security-groups)]
    (println "Here's a list of all the Security Groups:")
    (doseq [sg sgs]
      (println (str (:group-id sg) " - " (:group-name sg))))
    (println (str "That's a total of " (count sgs) " Security Groups"))))

(defn instances
  []
  ((ec2/describe-instances) :reservations))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (= (:list options) "instances") (println (instances))
      (:help options) (print_usage summary)
      :else (print_sgs))))