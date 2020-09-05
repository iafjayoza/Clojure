(ns mm_person
  (:use [clojure.set :only (union)]))

(def date1(.format (java.text.SimpleDateFormat. "09-23-2018") (new java.util.Date)))
(def date2(.format (java.text.SimpleDateFormat. "09-18-2018") (new java.util.Date)))
(def date3(.format (java.text.SimpleDateFormat. "09-06-2018") (new java.util.Date)))

(defn add
  "Adds key-value pairs."
  ([mm k v]
   (assoc mm k v))
  ([mm k v & kvs]
   (apply add (add mm k v) kvs)))

(defn add-mm
  "Adds key-value pairs the multimap."
  ([mm k v]
   (assoc mm k (conj (get mm k #{}) v)))
  ([mm k v & kvs]
   (apply add-mm (add-mm mm k v) kvs)))

(defn del
  "Removes key-value pairs from the multimap."
  ([mm k v]
   (let [mmv (disj (get mm k) v)]
     (if (seq mmv)
       (assoc mm k mmv)
       (dissoc mm k))))
  ([mm k v & kvs]
   (apply del (del mm k v) kvs)))

(defn mm-merge
  "Merges the multimaps, taking the union of values."
  [& mms]
  (apply (partial merge-with union) mms))

(comment
  (def mm (add {} :foo 1 :foo 2 :foo 3))
  ;; mm == {:foo #{1 2 3}}

  (mm-merge mm (add {} :foo 4 :bar 2))
  ;;=> {:bar #{2}, :foo #{1 2 3 4}}

  (del mm :foo 2)
  ;;=> {:foo #{1 3}}
  )

(defn add-person1
  ([pname date id dname]
   (add-mm {} (keyword pname) (add-mm {} (keyword date) {:id id :doctor dname})
           (keyword pname) (add-mm {} (keyword date1) {:id "4" :doctor dname} (keyword date1) {:id "5" :doctor dname})
           (keyword pname) (add-mm {} (keyword date2) {:id "6" :doctor dname})
           )))


(defn add-person2
  ([pname date id dname]
   (add-mm {} (keyword pname) (add-mm {} (keyword date) {:id id :doctor dname})
           (keyword pname) (add-mm {} (keyword date1) {:id "4" :doctor dname} (keyword date1) {:id "5" :doctor dname})
           )))

(defn add-person
  ([pname date id dname]
   (add-mm {} (keyword pname) (add-mm {} (keyword date) {:id id :doctor dname})
           (keyword pname) (add-mm {} (keyword date1) {:id "4" :doctor dname} (keyword date1) {:id "5" :doctor dname})
           (keyword pname) (add-mm {} (keyword date2) {:id "6" :doctor dname})
           (keyword pname) (add-mm {} (keyword date3) {:id "7" :doctor dname})
    )))

(def person (add-person "Jay" date3 "3" "Ms.Bakshi"))

(def person1 (add-person1"Namratha" date1 "3" "Ms.Malini"))

(def person2 (add-person2 "Pradeep" date2 "2" "Mr.Pandu"))

(def patient [person person1 person2])

(defn get-top-pat ([pats]

 (def my-atom (atom 0))
 (def person-atom (atom #{}))
 (doseq [pat pats]
   (doseq [[k v] pat]
     ;;(prn k)
     ;;(prn (count v))
     (if (< @my-atom (count v))
       (do (compare-and-set! my-atom 0 (count v))
           (swap! person-atom conj k)))
       )
     )
 (println @person-atom)
 (println @my-atom)))

(defn get-top-pat1 ([pats]
 (doseq [pat pats]
   (doseq [[k v] pat]
     (prn k)
     (prn (count v))
     (doseq [[k1 v1] k]
       (prn k1)
       (prn (count v1)))
       )
   )))

(defn last-encounter [x y]
  (let [c (compare (:Jay y) (:Namratha x))]
    (if (not= c 0)
      c)))

(defn add-merge
  ([pname date id dname]
    (mm-merge {} (keyword pname) (mm-merge {} (keyword date) {:id id :doctor dname}))
    (add-mm {} (keyword pname) (add-mm {} (keyword date) {:id id :doctor dname}))
    ))



