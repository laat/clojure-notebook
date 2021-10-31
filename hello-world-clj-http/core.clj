(ns clojure.examples.hello
  (:require
   [clj-http.client :refer [get]]
   [clj-http.conn-mgr :refer [make-reusable-conn-manager shutdown-manager]]
   [clj-http.core :refer [build-http-client]])
  (:gen-class))

(defn get-some-json []
  (-> "http://httpbin.org/get?my=arg&has=values"
      (get {:as :json})
      :body :args))

(defn set-custom-useragent []
  (-> "http://httpbin.org/get?my=arg&has=values"
      (get {:headers {:User-Agent "Laat/clojure"}
            :as :json})
      :body :headers :User-Agent))

(comment
  (get-some-json)
  (set-custom-useragent))

;; connection manager for keep alive
(def cm (make-reusable-conn-manager {:timeout 2 :threads 3}))
(defn get-some-json-pooled []
  (-> "http://httpbin.org/get"
      (get {:as :json :connection-manager cm})
      :body :headers))

(comment
  (get-some-json-pooled)
  ;; Do I have to shutdown manually?
  (-> (Runtime/getRuntime)
      (.addShutdownHook (Thread. #(shutdown-manager cm))))
  ,)

;; TODO: client-reuse for caching
(def hclient (build-http-client {} false cm))
(defn get-some-json-pooled-with-client-reuse []
  (-> "http://httpbin.org/get"
      (get {:as :json :connection-manager cm :http-client hclient})))

(comment
  (= hclient (:http-client (get-some-json-pooled-with-client-reuse)))
  ,)
