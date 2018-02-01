(ns git-in-clojure.core
  (:require [pandect.algo.sha1 :refer [sha1]])
  (:require [zlib-tiny.core :as zlib])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn git-object
  [object-type content]
  (format "%s %d\0%s" object-type (count content) content))

(defn compress-obj
  [obj]
  (-> obj zlib/str->bytes zlib/deflate))

(defn decompress-file
  [path]
  (with-open [xin (io/input-stream path)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
