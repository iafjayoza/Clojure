(ns parkinglot.parkinglot-rules
  "Rules for Parkinglot problem statement.
   The documentation for the algorithm can be found at: parking_lot/ParkingLot-1.4.2.pdf"
  (:require [clojure.string :as str]
            [clojure.pprint :as pp])
  (:import (java.io BufferedReader FileReader)))


(def vehicle-detail (atom (hash-map)))

(def free-slots (atom (sorted-set)))

(def total-slots (atom 0))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parking-lot [x]
  "Check if given number x is valid positive Integer and if so creates a parking lot with size of x number."
  (if (pos-int? x)
    (do
      (reset! free-slots (->> (inc x)
                              (range 1)
                              (into (sorted-set))))
      (reset! total-slots x)
      (reset! vehicle-detail (hash-map))
      (println "Created a parking lot with"x "slots"))
    (println "Enter valid slot number")))

(defn park [carnumber color]
  "Function invokes while parking a car, Checks if there are free slots available to park the car and if so it will assign the nearest parking slot available for a car."
  (if (seq @free-slots)
    (do
      (swap! vehicle-detail assoc carnumber {"Slot No" (first @free-slots) "Registration No" carnumber "Colour" color})
      (println "Allocated slot number:" (first @free-slots))
      (swap! free-slots disj (first @free-slots)))
    (println "Sorry,Parking lot is full")))

(defn leave [slot]
  "Function invokes while car is leaving, Checks if given slot is valid and currently occupied if so it will leave the car and free up the parking spot."
  (if (and (pos-int? slot) (<= slot @total-slots))
    (do
      (swap! free-slots conj slot)
      (swap! vehicle-detail dissoc (->> (keys @vehicle-detail)
                                        (filter (comp #{slot} #(get-in % ["Slot No"]) @vehicle-detail))
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
                                                (if (= 2 (count space-splitted-line))
                                                  (leave (Integer. (nth space-splitted-line 1)))
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