(ns parkinglot.parkinglot-rules
  "Rules for Parkinglot problem statement.
   The documentation for the algorithm can be found at: parking_lot/ParkingLot-1.4.2.pdf"
  (:require [clojure.string :as str]
            [clojure.pprint :as pp])
  (:import (java.io BufferedReader FileReader)))


(def vehicle-detail (atom (hash-map)))

(def free-slots (atom (hash-map)))

(def occupied-slots (atom (hash-map)))

(def total-slots (atom (hash-map)))

(def floor-number (atom 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn available-floor [x]
  (filter #(not= (get-in @total-slots [(first %)]) (count (second %))) x))

(defn parking-lot [x]
  "Check if given number x is valid positive Integer and if so creates a parking lot with size of x number."
  (if (pos-int? x)
    (do
      (swap! floor-number inc)
      (swap! free-slots assoc @floor-number (->> (inc x)
                                                 (range 1)
                                                 (into (sorted-set))))
      (swap! occupied-slots assoc @floor-number (sorted-set))
      (swap! total-slots assoc @floor-number x)
      (println "Created a parking lot with"x "slots"))
    (println "Enter valid slot number")))

(defn park [carnumber color]
  "Function invokes while parking a car, Checks if there are free slots available to park the car and if so it will assign the nearest parking slot available for a car."
  (if (some seq (vals @free-slots))
    (do
      (def assigned-floor (key (apply min-key #(count (second %)) (available-floor @occupied-slots))))
      (swap! vehicle-detail assoc carnumber {"Slot No" (first (get-in @free-slots [assigned-floor])) "Registration No" carnumber "Colour" color "Floor No" assigned-floor})
      (swap! occupied-slots update-in [assigned-floor] conj (first (get-in @free-slots [assigned-floor])))
      (println "Allocated slot number:" (first (get-in @free-slots [assigned-floor])))
      (swap! free-slots update-in [assigned-floor] disj (first (get-in @free-slots [assigned-floor]))))
    (println "Sorry,Parking lot is full")))

(defn leave [slot floor]
  "Function invokes while car is leaving, Checks if given slot is valid and currently occupied if so it will leave the car and free up the parking spot."
  (if (and
        (pos-int? slot)
        (<= slot (get-in @total-slots [floor]))
        #_(= {"Slot No" slot, "Floor No" floor} (select-keys (val (first @vehicle-detail)) ["Slot No" "Floor No"])))
    (do
      (swap! occupied-slots update-in [floor] disj slot)
      (swap! free-slots update-in [floor] conj slot)
      (swap! vehicle-detail dissoc (->> (keys @vehicle-detail)
                                        (filter (comp #{{"Slot No" slot, "Floor No" floor}} #(select-keys % ["Slot No" "Floor No"]) @vehicle-detail))
                                        (first)))
      (println "Slot number"slot "is free"))
    (println "Enter valid slot number")))

(defn carfromcolor [color]
  "Function invokes while fetching the registration number of car from colour"
  (->> (keys @vehicle-detail)
       (filter (comp #{color} #(get-in % ["Colour"]) @vehicle-detail))
       (clojure.string/join ", ")
       (println)))

(defn slotfromcolor [color]
  "Function invokes while fetching the slot number of car from colour"
  (->> (keys @vehicle-detail)
       (filter (comp #{color} #(get-in % ["Colour"]) @vehicle-detail))
       (map #(get-in (@vehicle-detail %) ["Slot No"]))
       (clojure.string/join ", ")
       (println)))

(defn slotfromcar [car]
  "Function invokes while fetching the slot number by car registration number"
  (if (seq (@vehicle-detail car))
    (println (get-in (@vehicle-detail car) ["Slot No"]))
    (println "Not Found")))

(defn status []
  "Function invokes while fetching the status of parking"
  (pp/print-table (vals @vehicle-detail)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Manage parking function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn manage-parking [line]
  "Based on input provided by user makes a related function call for parking lot application"
  (let [space-splitted-line (str/split line #" ")]
    (cond
      (= "create_parking_lot" (nth space-splitted-line 0)) (try
                                                             (if (= 2 (count space-splitted-line))
                                                               (parking-lot (Integer. (nth space-splitted-line 1)))
                                                               (println "Please Enter The Correct Input"))
                                                             (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= 0 @total-slots) (println "Please create a parking lot")

      (= "park" (nth space-splitted-line 0)) (try
                                               (if (= 3 (count space-splitted-line))
                                                 (park (nth space-splitted-line 1) (nth space-splitted-line 2))
                                                 (println "Please Enter The Correct Input"))
                                               (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= "leave" (nth space-splitted-line 0)) (try
                                                (if (= 3 (count space-splitted-line))
                                                  (leave (Integer. (nth space-splitted-line 1)) (Integer. (nth space-splitted-line 2)))
                                                  (println "Please Enter The Correct Input"))
                                                (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= "status" (nth space-splitted-line 0)) (try
                                                 (status)
                                                 (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= "registration_numbers_for_cars_with_colour" (nth space-splitted-line 0)) (try
                                                                                    (if (= 2 (count space-splitted-line))
                                                                                      (carfromcolor (nth space-splitted-line 1))
                                                                                      (println "Please Enter The Correct Input"))
                                                                                    (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= "slot_numbers_for_cars_with_colour" (nth space-splitted-line 0)) (try
                                                                            (if (= 2 (count space-splitted-line))
                                                                              (slotfromcolor (nth space-splitted-line 1))
                                                                              (println "Please Enter The Correct Input"))
                                                                            (catch Exception e (println "Error : Please Enter The Correct Input")))

      (= "slot_number_for_registration_number" (nth space-splitted-line 0)) (try
                                                                              (if (= 2 (count space-splitted-line))
                                                                                (slotfromcar (nth space-splitted-line 1))
                                                                                (println "Please Enter The Correct Input"))
                                                                              (catch Exception e (println "Error : Please Enter The Correct Input")))

      :else "Please Enter The Correct Input")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; I/O operations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-file [file-name]
  "Function takes an input file as an argument, Reads each line of the file and calls `manage-parking` to generate the output"
  (with-open [rdr (BufferedReader. (FileReader. file-name))]
    (doseq [line (line-seq rdr)]
      (manage-parking line))))

(defn interactive-commands []
  "Function provide us with an interactive command prompt based shell where commands can be typed in."
  (println "Enter The Commands:")
  (def line (read-line))
  (while (not= "exit" (str line))
    (do (manage-parking (str line))
        (def line (read-line)))))

(doseq [arg *command-line-args*]
  "Function provide us to choose between,
   * Interactive command prompt based shell where commands can be typed in or
   * Read filename as a parameter at the command prompt and read the commands from that file"
  (try
    (if (str/includes? arg "file_input.txt")
      (read-file arg))
    (if (str/includes? arg "interactive")
      (interactive-commands))
    (catch Exception e (println "Error : " (.getMessage e)))))