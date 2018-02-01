(ns git-in-clojure.core
  (:require [pandect.algo.sha1 :refer [sha1]])
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn git-object
  [object-type content]
  (format "%s %d\0%s" object-type (count content) content))

(defn git-hash-object
  [content & {:keys [type]
              :or {type "blob"}}]
  [content type])

(defn decompress-file
  []
  (with-open [xin (-> ".git/objects/16/b465f671ba7f4f18fc15d696f3493e42a1611f"
                      io/input-stream
                      java.util.zip.InflaterInputStream.)]
    (slurp xin)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
