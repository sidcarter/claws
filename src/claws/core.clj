(ns claws.core
  (:gen-class claws.core)
  (:use [amazonica.aws.ec2]))

(defn regions
  []
  ((describe-regions) :regions))

(defn vpcs
  []
  ((describe-vpcs) :vpcs))

(defn -main
  "I am the main."
  [& args]
  (if (not= (count args) 0)
    (println
      (first args)))
  (println "Available VPCs: ")
  (doseq [vpc (vpcs)]
    (if (= (vpc :state) "available")
      (doseq [tag (vpc :tags)]
        (if (= (tag :key) "Name")
          (println (str (tag :value) "(" (vpc :vpc-id) ") - " (vpc :state)))))))
  (println "Available Regions: ")
  (doseq [region (regions)]
    (println (region :region-name)))
  )
