(ns re-replacefirst)

(defn Example []
  (def pat (re-pattern "H(e+)(\\S+)"))
  (def newstr (slurp "C:\\Users\\JO049566\\Desktop\\DevEssentials\\Topics.txt"))
  (def newstr1 (clojure.string/replace-first newstr pat "###"))
  (println newstr1))
(Example)
