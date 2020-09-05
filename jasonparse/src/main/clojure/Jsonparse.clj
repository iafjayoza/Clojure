(ns Jsonparse
  (:require [clojure.data.json :as json]))
(use 'clojure.pprint)

(defn details1 []
  (def enc1 (json/read-str (slurp "C:\\Users\\JO049566\\Desktop\\DevEssentials\\medications.json") :key-fn keyword))
  (prn (keys enc1)))

(def dbv (atom []))
(def db (atom {}))

(defn add
  "Adds key-value pairs."
  ([mm k v]
   (swap! mm assoc k v)))

(defn addv
  "Adds key-value pairs."
  ([vec v]
   (swap! vec conj v)))

(defn mm1 ([a b]
           (add db (keyword a) b)
           ))

(defn get-data1 ([pats]
                   (doseq [[k v] pats]
                     (if (= k :medications)
                       ;;(prn(first v))
                       (doseq [vv v]
                         (doseq [[k1 v1] vv]
                           #_(def fn1 "fullName")
                           (if (= k1 :prescribingProvider)
                             (do (doseq [[k2 v2] v1]
                               (if (= k2 :fullName)
                                 (mm1 k2 v2)
                                 )
                               ))
                             )
                           (if (or
                                 (or
                                   (or (= k1 :personId) (= k1 :displayName))
                                   (or (= k1 :medicationId) (= k1 :doseQuantity))
                                 )
                                 (or (= k1 :startDate) (= k1 :stopDate))
                               )
                             (mm1 k1 v1)
                             )
                           )
                           (addv dbv @db)
                           (def db (atom {})))
                     ))
                  ))

(details1)
(get-data1 enc1)

(prn @dbv)
(print-table @dbv)







