(ns clojurecrud
  "Clojure Implementation of simple CRUD based REST api using MONGODB"
  (:require [compojure.route :as route]
            [compojure.core :refer :all]
            [monger.core :as m]
            [monger.collection :as mc]
            [ring.adapter.jetty :refer :all]
            [ring.util.response :refer :all]
            [ring.middleware.json :refer :all]
            [ring.middleware.defaults :refer :all])
  (:import org.bson.types.ObjectId))

;;;;;;;;;;;;;;;;;;;;;;;
;;; Connecting to DB
;;;;;;;;;;;;;;;;;;;;;;;

(def conn (m/connect))
(def db (m/get-db conn "clojure-crud"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; CRUD related functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn greetings []
  (str
    "<div>
       <h1>Welcome to Clojure CRUD !</h1>
        <p>Submitted via a GET request.</p>
        <p><a href='..'>Return to main page</p>
     </div>"))

(defn insert-patient [req]
  (let [param-name (:name (:params req))
        req-type "POST"]
    (try
      (mc/insert db "person" (:params req))
      (str
        "<div>
           <h1>Inserted the details for " param-name "!</h1>
           <p>Submitted via a " req-type " request.</p>
           <p><a href='..'>Return to main page</p>
         </div>")
      (catch Exception e))))

(defn- transform-id-to-string [document]
  (if-let [id (:_id document)]
    (assoc document :_id (.toString id))))

(defn update-patient [req]
  (let [id (:_id (:params req))
        req-type "POST"]
    (try
      (mc/update-by-id db "person" (ObjectId. (str id)) (dissoc (:params req) :_id))
      (str
        "<div>
           <h1>Updated the details for ID " id "!</h1>
           <p>Submitted via a " req-type " request.</p>
           <p><a href='..'>Return to main page</p>
        </div>")
      (catch Exception e))))

(defn delete-patient [req]
  (let [id (:_id (:params req))
        req-type "POST"]
    (try
      (mc/remove-by-id db "person" (ObjectId. (str id)))
      (str
        "<div>
           <h1>Removed the details for ID " id "!</h1>
           <p>Submitted via a " req-type " request.</p>
           <p><a href='..'>Return to main page</p>
        </div>")
      (catch Exception e))))

(defn get-details-by-name [req]
  (let [patient-name (:name (:params req))]
    (try
      (->> {:name (str patient-name)}
           (mc/find-maps db "person")
           (map transform-id-to-string))
      (catch Exception e))))

(defn get-all []
  (try
    (map transform-id-to-string (mc/find-maps db "person"))
    (catch Exception e)))

(defn page-not-found []
  "<h1>404 Error!</h1>
   <b>Page not found!</b>
   <p><a href='..'>Return to main page</p>")

;;;;;;;;;;;;;;;;;;;;;
;;; Post Requests
;;;;;;;;;;;;;;;;;;;;;

(defroutes my_routes
  (GET "/" [] (greetings))
  (POST "/insert" req (insert-patient req))
  (POST "/update" req (update-patient req))
  (POST "/delete" req (delete-patient req))
  (GET "/getbyname" req (response (get-details-by-name req)))
  (GET "/get" [] (response (get-all)))
  (route/not-found (page-not-found)))

(run-jetty (->> (merge site-defaults {:security {:anti-forgery false}})
                (wrap-defaults (wrap-json-response my_routes))) {:port 3000})
