(ns moccasin.core
  (:require [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn receive
  "Read a sequence of lines from the socket"
  [socket]
  (line-seq (io/reader socket)))

(defn say-hey
  "Say hello to the socket"
  [socket]
  (let [socket-out (io/writer socket)]
    (.write socket-out "HEYYY!!!!\r\n")
    (.flush socket-out)))

(defn handle
  [msg socket]
  (println msg)
  (say-hey socket))

(defn serve [port]
  "iterate through lines received on the established TCP connection"
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (doseq [msg-in (receive sock)]
      (handle msg-in sock))))
