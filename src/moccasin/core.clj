(ns moccasin.core
  (:require [clojure.java.io :as io]
            [clojure.string :as strings])
  (:import [java.net ServerSocket]))

;; Write something back to the socket
;; You have to FLUSH to get it to send the message
;; without the FLUSH the method would be sticking around
;; until you closed it! Or you can just close the connections
;; and basically implement HTTP server close
;; Will need to check add config at some point
(defn send-response [response socket]
  (with-open [socket-out (io/writer socket)]
    (.write socket-out response)))

;; needs to be method SPACE request-target SPACE http-version
(defn parse-start-line [line]
  (let [[method target version] (strings/split line #" ")]
    {:method method
     :target target
     :version version}))

(defn valid-method?
  "Check to see if the method given is supported
  by out server"
  [method]
  (some #(= method %) '("GET" "HEAD")))

(defn valid-target?
  "Check to see if http target is correct"
  [target]
  true)

(defn valid-version?
  "Check to see if version is HTTP/1.0 or HTTP/1.1"
  [version]
  (some #(= version %) '("HTTP/1.0" "HTTP/1.1")))

;; Parse out the message into specific parts
;; Starting with the "startline"
;; Print the incoming message
;; Respond to the request
(defn handle [socket]
  (with-open [r (io/reader socket)]
    (let [start-line (parse-start-line (.readLine r))]
      (println start-line)
      (send-response "200 OK\r\n" socket))))

;; Start the server on [port]
;; Loop until killed
(defn serve [server]
  (with-open [server-sock (ServerSocket. (:port server))]
    (loop [socket (.accept server-sock) runs 1]
      (future
        (handle socket)
        (.close socket))
      (if (< runs 25)
        (recur (.accept server-sock) (inc runs))))))

(defrecord Moccasin [port])

(def server (Moccasin. 9001))

;; Startline (request line)
;; first line of the HTTP request
;; (are we supposed to keep the connection open)


;; method SP request-target SP http-version CRLF
;; MUST support GET HEAD, optional POST, PUT, DELETE, CONNECT, OPTIONS, TRACE
