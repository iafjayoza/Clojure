(ns parkinglot.test-parkinglot
  (:require [parkinglot.parkinglot-rules :refer :all]
            [clojure.test :refer :all]))

(deftest vehicle-detail-collection-test
  :description "Testing if collection of vehicle-detail is hash-map or not"
  (is (map? (swap! vehicle-detail assoc :slotno 1 :registrationno "KA05" :colour "blue"))true))

(deftest free-slots-collection-test
  :description "Testing if collection of free-slots is set or not"
  (is (set? (swap! free-slots conj 1))true))

(deftest parking-lot-condition-test
  :description "Testing the parking lot getting created with right number of cars as per request"
  (is (= "Created a parking lot with 6 slots\n" (with-out-str (parking-lot 6)))))

(deftest total-slots-condition-test
  :description "Testing the total slots are same as created by parking-lot function"
  (let [create-parking (parking-lot 3)
        car1 (park "KA-05-125" "blue")]
    create-parking
    car1)
  (is (= 3 @total-slots)))

(deftest park-condition-test
  :description "Testing that car is getting assigned to nearest available slot."
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")]
    create-parking
    car1
    car2)
  (is (= "Allocated slot number: 3\n" (with-out-str (park "KA-08-443" "blue")))))

(deftest leave-condition-test
  :description "Testing that right number of slot getting free when car leaves the parking lot"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")]
    create-parking
    car1
    car2
    car3)
  (is (= "Slot number 2 is free\n" (with-out-str (leave 2)))))

(deftest parking-condition-1
  :description "Testing the right number of cars getting assigned as per request
                * Created parking lot with 6
                * 5 cars assigned with nearest slot
                * Slot 6 is free"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-143" "blue")]
    create-parking
    car1
    car2
    car3
    car4
    car5)
  (is (= #{6} @free-slots)))

(deftest parking-condition-2
  :description "Testing that new car is getting assigned to the empty slot created by left car
                * Created parking lot with 6
                * Parking lot full with all 6 cars
                * Car from slot 4 leaves the parking lot hence new car get assigned with empty slot 4"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-153" "blue")
        car6 (park "KA-08-163" "blue")
        car-exit-1 (leave 4)
        car7 (park "KA-08-443" "blue")]
    create-parking
    car1
    car2
    car3
    car4
    car5
    car6
    car-exit-1
    car7)
  (is (= 4 (get-in (@vehicle-detail "KA-08-443") ["Slot No"]))))

(deftest parking-condition-3
  :description "testing that new car is not getting assigned to any of the occupied slots"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-153" "blue")
        car6 (park "KA-08-163" "blue")
        car-exit-1 (leave 4)
        car7 (park "KA-08-443" "blue")]
    create-parking
    car1
    car2
    car3
    car4
    car5
    car6
    car-exit-1
    car7)
  (is (= (boolean(some #(= (get-in (@vehicle-detail "KA-08-443") ["Slot No"]) %) #{1,2,3,5,6})) false)))

(deftest parking-condition-4
  :description "Testing that new car is not getting allowed to park when there are no slots available"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-153" "blue")
        car6 (park "KA-08-163" "blue")
        car-exit-1 (leave 4)
        car7 (park "KA-08-443" "blue")]
    create-parking
    car1
    car2
    car3
    car4
    car5
    car6
    car-exit-1
    car7)
  (is (= "Sorry,Parking lot is full\n" (with-out-str (park "KA-08-463" "red")))))

(deftest leave-condition-1
  :description "Testing the leave creates an empty slot and make it available to be assigned"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-153" "blue")
        car6 (park "KA-05-163" "blue")
        car-exit-1 (leave 4)]
    create-parking
    car1
    car2
    car3
    car4
    car5
    car6
    car-exit-1)
  (is (= (contains? @free-slots 4) true)))

(deftest leave-condition-2
  :description "Testing the leave creates an correct empty slot"
  (let [create-parking (parking-lot 6)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car5 (park "KA-05-153" "blue")
        car6 (park "KA-05-163" "blue")
        car-exit-1 (leave 4)]
    create-parking
    car1
    car2
    car3
    car4
    car5
    car6
    car-exit-1)
  (is (= #{4} @free-slots)))

(deftest car-from-color-condition-test
  :description "Testing that correct cars returned while searching by car colour"
  (let [create-parking (parking-lot 5)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car-exit-1 (leave 4)]
    create-parking
    car1
    car2
    car3
    car4
    car-exit-1)

  (is (= "KA-05-145\n" (with-out-str (carfromcolor "red")))))

(deftest slot-from-color-condition-test
  :description "Testing that correct slots returned while searching by car colour"
  (let [create-parking (parking-lot 8)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car-exit-1 (leave 4)]
    create-parking
    car1
    car2
    car3
    car4
    car-exit-1)

  (is (= "1, 3\n" (with-out-str (slotfromcolor "blue")))))

(deftest slot-from-car-condition-test
  :description "Testing that correct slots returned while searching by car registration number"
  (let [create-parking (parking-lot 5)
        car1 (park "KA-05-125" "blue")
        car2 (park "KA-05-145" "red")
        car3 (park "KA-05-453" "blue")
        car4 (park "KA-05-923" "red")
        car-exit-1 (leave 4)]
    create-parking
    car1
    car2
    car3
    car4
    car-exit-1)

  (is (= "3\n" (with-out-str (slotfromcar "KA-05-453")))))