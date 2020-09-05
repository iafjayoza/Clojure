(ns clojurespec
  (:require [clojure.spec.alpha :as s]))

(import java.util.Date)

(println "The spec library (API docs) specifies the structure of data, validates or conforms it, and can generate data based on the spec.\n
 can be used for runtime data validation.")

(s/conform even? 1000)

(s/valid? even? 10)

(s/valid? odd? 10)

(s/valid? nil? nil)

(s/valid? string? "abc")

(s/valid? string? 1)

(s/valid? #(> % 5) 10)

(s/valid? #(> % 5) 0)

(s/valid? inst? (Date.))

(s/valid? #{:club :diamond :heart :spead} :club)

(s/valid? #{:club :diamond :heart :spead} 40)

(s/valid? #{40} 40)

;; Registering a Spec

(s/def ::date inst?)

(s/def ::suit #{:club :spade :heart :diamond})

(s/conform ::date (Date.))
(s/valid? ::date (Date.))

(s/valid? ::suit :club)
(s/valid? ::suit 40)

;;Compose a SPEC

(s/def ::big-even (s/and int? even? #(> % 1000)))

(s/valid? ::big-even 10)
(s/valid? ::big-even 1000)
(s/valid? ::big-even 10000)


(s/def ::name-or-id (s/or :name string?
                          :id int?))

(s/valid? ::name-or-id "Jay")
(s/valid? ::name-or-id 1)
(s/valid? ::name-or-id :foo)

(s/conform ::name-or-id "Jay")
(s/conform ::name-or-id 1)

(s/valid? string? "abc")
(s/valid? (s/nilable string?) nil)
(s/valid? string? nil)

;; Explain in Spec

(s/explain ::suit 35)

(s/explain ::big-even 5)

(s/explain ::name-or-id :foo)

(s/explain-str ::name-or-id :foo)

(s/explain-data ::name-or-id :foo)

;;Entity Map

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::accid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))


(s/valid? ::person {::first-name "Jay"
                    ::last-name "Oza"
                    ::email "jay.oza@cerner.com"})

(s/explain ::person {::first-name "Jay"})

(s/explain ::person {::first-name "Jay"
                     ::last-name "Oza"
                     ::email "n/a"})

;;Unqualified Keys

(s/def :unq/person (s/keys :req-un [::first-name ::last-name ::email]
                           :opt-un [::phone]))

(s/conform :unq/person {:first-name "Jay"
                        :last-name "Oza"
                        :email "jay.oza@cerner.com"})

(s/conform :unq/person {:first-name "Jay"})
(s/explain :unq/person {:first-name "Jay"})
(s/explain :unq/person {:first-name "Jay"
                        :last-name "Oza"
                        :email "n/a"})

(defrecord Person [first-name last-name email phone])

(s/explain :unq/person (->Person "Jay" nil nil nil))

(s/conform :unq/person
           (->Person "Bugs" "Bunny" "bugs@example.com" nil))

;; Keys* which can be embedded inside a sequential regex structure.

(s/def ::port number?)
(s/def ::host string?)
(s/def ::id keyword?)

(s/def ::server (s/keys* :req [::id ::host]
                         :opt [::port]))

(s/conform ::server [::id :s1 ::port 555 ::host "host@example.com"])

;; merge for combining keys

(s/def :animal/kind string?)
(s/def :animal/say string?)
(s/def :animal/common (s/keys :req [:animal/kind :animal/say]))
(s/def :dog/tail? boolean?)
(s/def :dog/bread string?)
(s/def :animal/dog (s/merge :animal/common
                            (s/keys :req [:dog/tail? :dog/bread])))

(s/valid? :animal/dog {:animal/kind "dog"
                       :animal/say "bow"
                       :dog/tail? true
                       :dog/bread "german"})

;;multi-spec where we will be able to specify the required keys per entity type

(s/def :event/type keyword?)
(s/def :event/timestamp int?)
(s/def :search/url string?)
(s/def :error/message string?)
(s/def :error/code int?)

(defmulti event-type :event/type)
(defmethod event-type :event/search [_]
  (s/keys :req [:event/type :event/timestamp :search/url]))
(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/code :error/message]))

(s/def :event/event (s/multi-spec event-type :event/type))

(s/valid? :event/event
          {:event/type :event/search
           :event/timestamp 1463970123000
           :search/url "https://clojure.org"})

(s/valid? :event/event
          {:event/type :event/error
           :event/timestamp 1463970123000
           :error/message "Invalid host"
           :error/code 500})

(s/explain :event/event
           {:event/type :event/restart})

(s/explain :event/event
           {:event/type :event/search
            :search/url 200})

;;collection

;;1. collection of

(s/conform (s/coll-of keyword?) [:a :b :c])

(s/conform (s/coll-of int?) [1 2 3])

(s/def ::vnum3 (s/coll-of number? :kind vector? :count 3 :distinct true :into #{}))

(s/conform ::vnum3 [1 2 3])

(s/explain ::vnum3 #{1 2 3})

(s/explain ::vnum3 [1 1 1])

(s/explain ::vnum3 [1 2 :a])

;;2.tuple

(s/def ::point (s/tuple double? double? double?))

(s/conform ::point [1.1 2.3 2.3])

;;3.map-of

(s/def ::scores (s/map-of string? int?))

(s/conform ::scores {"Jay" 1, "Bhumi" 2})


;; Sequences

(s/def ::ingredient (s/cat :quantity number? :unit keyword?))

(s/conform ::ingredient [2 :spoon])

(s/explain ::ingredient [2 "spooon"])

(s/def ::seq-of-keywords (s/* keyword?))

(s/conform ::seq-of-keywords [:q :s :c])

(s/valid? ::seq-of-keywords [:a])

(s/valid? ::seq-of-keywords [])

(s/def ::odd-than-may-be-even (s/cat :odd (s/+ odd?)
                                     :even (s/? even?)))

(s/conform ::odd-than-may-be-even [1 3 5 100])

(s/conform ::odd-than-may-be-even [1 3 5 100 2])

(s/def ::opts (s/* (s/cat :opts keyword? :val boolean?)))

(s/conform ::opts [:silent false :verbal true])

(s/def ::config (s/*
                  (s/cat :prop string?
                         :val (s/alt :s string? :a boolean?))))

(s/conform ::config ["-server" "foo" "-verbose" true "-user" "joe"])

(s/describe ::odd-than-may-be-even)

;;Using spec for validation

(defn person-name
  [person]
  {:pre [(s/valid? ::person person)]
   :post [(s/valid? string? %)]}
  (str (::first-name person) " " (::last-name person)))

;(person-name 42)
(person-name {::first-name "Bugs" ::last-name "Bunny" ::email "bugs@example.com"})

(defn person-name
  [person]
  (let [p (s/assert ::person person)]
    (str (::first-name p) " " (::last-name p))))

(s/check-asserts true)
;(person-name 100)

;;Spec'ing Functions

(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))

(s/fdef ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

;; Higher order functions

(defn adder [x] #(+ x %))

(s/fdef adder
        :args (s/cat :x number?)
        :ret (s/fspec :args (s/cat :y number?)
                      :ret number?)
        :fn #(= (-> % :args :x) ((:ret %) 0)))
