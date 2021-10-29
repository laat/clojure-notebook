(ns clojure.examples.hello
  (:require [clj-http.client :as client])
  (:gen-class))

(let [res (client/get "http://httpbin.org/get" {:accept :json :as :json})
      body (:body res)
      headers (:headers body)]
  headers)
