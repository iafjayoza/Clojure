(ns add
  (:use [clojure.set :only (union)]))



(defn get-top-pat ([pats]
                   (def top-pat "")
                   (def max-visit 0)
                   (doseq [pat pats]
                     (doseq [[k v] pat]
                       (prn k)
                       (prn (count v))
                       if max-value < (count v)
                       max-value = (count v)
                       top-pat = k
                       (prn top-pat)
                       (prn max-visit)
                       )
                     )
                    ))


