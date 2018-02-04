(ns git-in-clojure.core
  (:require [clojure.string :as str])
  (:require [pandect.algo.sha1 :refer [sha1]])
  (:require [clojure.java.io :as io])
  (:require [me.raynes.fs :as fs])
  (:import (java.security MessageDigest)
           ())
  (:gen-class))

(def GIT-DIR "git-test-dir")
(def RE-OBJ #"(.*)\s(\d+)\u0000(.*)")

;; Index ---------------------------------------------------------------------------------

(defn init-index []
  {:header {:version 0
            :n-of-entries 0}
   :entries []
   :extensions []
   :sha-1 0})

(defn load-index []
  (read-string (slurp (io/file GIT-DIR "index"))))

(defn save-index [index]
  (spit (io/file GIT-DIR "index")
        (str index)))

;; --------------------------------------------------------------------------------------

(defn git-object
  [object-type content]
  (format "%s %d\0%s" object-type (count content) content))

(defn git-init
  ([] (git-init "."))
  ([path]
   (let [git-root-dir (fs/file path GIT-DIR)]
     (if (fs/exists? git-root-dir)
       (throw (RuntimeException. "Le dépot GIT existe déjà"))
       (do
         (fs/mkdirs (fs/file git-root-dir "objects")))))))

(defn file->bytearray [io-file]
  (with-open [in-str  (io/input-stream io-file)
              out-str (java.io.ByteArrayOutputStream.)]
    (io/copy in-str out-str)
    (.toByteArray out-str)))

(defn git-hash-object
  [content & {:keys [type w]
              :or {type "blob"
                   w false}}]
  (let [c-bytes (cond (string? content) (.getBytes content)
                      (-> content class .getComponentType (= Byte/TYPE)) content
                      (= java.io.File (class content)) (file->bytearray content))
        hdr     (-> (format "%s %d\0" type (count c-bytes)) .getBytes)
        h       (->> (doto (MessageDigest/getInstance "SHA-1")
                       (.update hdr)
                       (.update c-bytes))
                     (.digest)
                     (map #(format "%02x" %))
                     (apply str))]
    (when w
      (let [d (fs/file GIT-DIR "objects" (subs h 0 2))]
        (fs/mkdir d)
        (with-open [out-str (->> (io/file d (subs h 2))
                                 io/output-stream
                                 java.util.zip.DeflaterOutputStream.)]
          (.write out-str hdr)
          (.write out-str c-bytes)
          )))
    h))

(defn git-cat-file
  [object & {:keys [t s]
             :or {t false
                  s false}}]
    (let [h object
          d (fs/file GIT-DIR "objects" (subs h 0 2))
          buf (with-open [in-str (->> (io/file d (subs h 2))
                                      io/input-stream
                                      java.util.zip.InflaterInputStream.)
                          out-str (java.io.ByteArrayOutputStream.)]
                (io/copy in-str out-str)
                (.toByteArray out-str))
          hdr-cnt (count (take-while #(not= 0 %) buf))
          hdr (String. buf 0 hdr-cnt)]
      (cond t (first (str/split hdr #"\s"))
            s (second (str/split hdr #"\s"))
            :else (String. buf (inc hdr-cnt) (- (count buf) hdr-cnt 1)))))


(defn decompress-file
  []
  (with-open [xin (-> () ".git/objects/16/b465f671ba7f4f18fc15d696f3493e42a1611f"
                      io/input-stream
                      java.util.zip.InflaterInputStream.)]
    (slurp xin)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
