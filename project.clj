(defproject git-in-clojure "0.1.0-SNAPSHOT"
  :description "Un petit essai de réecriture des bases de GIT avec Clojure..."
  :url "https://github.com/rolandcla/git-in-clojure.git"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [pandect "0.6.1"]
                 [me.raynes/fs "1.4.6"]]
  :main ^:skip-aot git-in-clojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
