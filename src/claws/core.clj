(ns claws.core
  (:require [amazonica.aws.ec2 :as ec2]
            [amazonica.aws.elasticloadbalancing :as elb]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class claws.core))

(def cli-options
  [
    ;; list all the instances or security groups
    ["-l" "--list ENTITY" "list entity - either instances, vpcs, elbs or security_groups."]
    ["-h" "--help"]
    ])

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

(defn elbs
  []
  (map #(select-keys % [:load-balancer-name :vpc-id :instances])
    (flatten
      (:load-balancer-descriptions (elb/describe-load-balancers)))))

(defn vpcs
  []
  ((ec2/describe-vpcs) :vpcs))

(defn sgs
  ([]
    (map #(select-keys % [:group-name :group-id])
      (flatten
        ((ec2/describe-security-groups) :security-groups))))
  ([print]
    (doseq [sg (sgs)]
      (println (str (:group-id sg) " - " (:group-name sg))))))

(defn instances
  []
  (map #(select-keys % [:instance-id :state :tags :private-ip-address])
    (flatten
      (map
        :instances (:reservations (ec2/describe-instances))))))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond (:list options)
      (case (:list options)
        "instances" (println (instances))
        "security_groups" (sgs :print)
        "elbs" (println (count (elbs)))
        (print_usage summary))
      :else (print_usage summary))))