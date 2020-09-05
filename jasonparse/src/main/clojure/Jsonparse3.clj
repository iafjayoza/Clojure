(ns Jsonparse3
  (:require [clojure.data.json :as json]
            [clojure.contrib.seq]))


(defn details []
  (def enc (json/read-str (slurp "C:\\Users\\JO049566\\Desktop\\DevEssentials\\medications.json")))
  (prn (keys enc)))

(defn details1 []
  (def enc1 (json/read-str (slurp "C:\\Users\\JO049566\\Desktop\\DevEssentials\\medications.json") :key-fn keyword))
  (prn (keys enc1)))

(details1)

(declare get-data-vec)
(defn get-data-map ([key pats r]
                    (doseq [[k v] pats]
                      (cond
                        (instance? clojure.lang.PersistentVector v)
                          (conj r
                                (get-data-vec (str key "." k) v r)
                          )
                        (instance? clojure.lang.PersistentArrayMap v)
                          (conj r
                                (get-data-map (str key "." k) v r)
                          )
                        :else
                          (let [res (str key "." k "\t\t" v)] res)
                        )
                      (prn r)
                      )
                     (let [finalre r] finalre)
                     )
  )

(defn get-data-vec ([key pats r]
                    (doseq [[k v] (clojure.contrib.seq/indexed pats)]
                      (cond
                        (instance? clojure.lang.PersistentVector v) (conj r (get-data-vec (str key "." k) v r))
                        (instance? clojure.lang.PersistentArrayMap v) (conj r (get-data-map (str key "." k) v r))
                        :else (let [res (str key "." k "\t\t" v)] res)
                        )
                      )
                     )
  )

(prn (get-data-map "" enc1 []))

;;(mm1 "personId" "birthDate" "gender" "languages" "maritalStatus" "ethnicity" "religion")

;;(mm1 "fullName" "startDate" "endDate" "city" "postalCode" "country" "number")