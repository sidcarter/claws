(ns claws.core
  (:require [amazonica.aws.ec2 :as ec2]
            [amazonica.aws.elasticloadbalancing :as elb]
            [amazonica.aws.rds :as rds]
            [amazonica.aws.cloudwatch :as cw]
            [clojure.data.json :as json]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class claws.core))

(def cli-options
  [
    ;; list all the instances or security groups
    ["-l" "--list ENTITY" "list entity - either instances, vpcs, dbs, elbs or security_groups."]
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

(defn ec2-instances
  []
  (let [instances (map #(select-keys % [:instance-id :state :key-name :private-ip-address])
    (flatten
      (map
        :instances (:reservations (ec2/describe-instances)))))]
    (doseq [instance instances]
      (let [id (instance :instance-id)
            status ((instance :state) :name)
            keyname (instance :key-name)
            ip-address (instance :private-ip-address)]
        (println id ip-address keyname status)))))

(defn rds-dbs
  []
  (let [dbs (map #(select-keys % [:dbinstance-identifier :engine-version :dbinstance-status])
              ((rds/describe-dbinstances) :dbinstances))]
    (doseq [db dbs]
      (println (db :dbinstance-identifier)))))

(defn alarms
  []
  (let [metrics (map #(select-keys % [:threshold :namespace :alarm-name :comparison-operator :state-value :state-reason-data])
    ((cw/describe-alarms) :metric-alarms))]
    (doseq [metric metrics]
      (let [reason-data (json/read-str (metric :state-reason-data) :key-fn keyword)
            operator (metric :comparison-operator)
            threshold (metric :threshold)]
        (when (not= (metric :state-value) "OK")
          (println reason-data operator threshold))))))

(defn -main
  "I am the main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond (:list options)
      (case (:list options)
        "instances" (ec2-instances)
        "security_groups" (sgs :print)
        "alarms" (println (alarms))
        "dbs" (rds-dbs)
        "elbs" (println (count (elbs)))
        (print_usage summary))
      :else (ec2-instances))))