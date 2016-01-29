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

;; needs to be method SP request-tartget SP http-version
(defn parse-start-line [line]
  (let [[method target version] (strings/split line #" ")]
    {:method method
     :target target
     :version version}))

;; Parse out the message into specific parts
;; Starting with the "startline"
;; Print the incoming message
;; Respond to the request
(defn handle [socket]
  (with-open [request (io/reader socket)
              start-line (parse-start-line (.readLine request))]
    (println start-line)
    (send-response "Thanks for the info!" socket)))

;; Start the server!!
(defn serve [port]
  (with-open [server-sock (ServerSocket. port)
              socket (.accept server-sock)]
    (handle socket)))


;; Startline (request line)
;; first line of the HTTP request
;; (are we supposed to keep the connection open)


;; method SP request-target SP http-version CRLF

;; MUST support GET HEAD, optional POST, PUT, DELETE, CONNECT, OPTIONS, TRACE
