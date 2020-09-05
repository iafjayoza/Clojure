(ns regex)

;;re-find to iterate through re.match

(defn details [phone-number]
  ;;  (def phone-number "02794-223-840")
  (def matcher (re-matcher #"\d+" phone-number))
  (let [x (re-find matcher) y (re-find matcher) z (re-find matcher)]
  [x y z]))