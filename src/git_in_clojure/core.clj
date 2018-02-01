(ns git-in-clojure.core
  (:require [pandect.algo.sha1 :refer [sha1]])
  (:require [clojure.java.io :as io])
  (:gen-class))

(def GIT-DIR "git-test-dir")

(defn git-object
  [object-type content]
  (format "%s %d\0%s" object-type (count content) content))

(defn git-init
  ([] (git-init "."))
  ([root]
   (let [path (io/file root GIT-DIR)]
     (if (.exists path)
       (throw (RuntimeException. "Le dépot GIT existe déjà"))
       (io/make-parents root GIT-DIR "description")))))

(defn git-hash-object
  [content & {:keys [type]
              :or {type "blob"}}]
  (let [c-bytes (cond (string? content) (.getBytes content)
                      (-> content class .getComponentType (= Byte/TYPE)) content)
        h (sha1 (java.io.SequenceInputStream.
                 (-> (format "%s %d\0" type (count c-bytes))
                     .getBytes
                     java.io.ByteArrayInputStream.)
                 (-> c-bytes
                     java.io.ByteArrayInputStream.)))]
    h))

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
