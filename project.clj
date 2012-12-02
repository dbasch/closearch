(defproject closearch "1.0.0-SNAPSHOT"
  :main closearch.core
  :jvm-opts ["-Xmx4g" "-server"]
  :description "A very basic proof-of-concept search engine"
  :repositories {"local" ~(str (.toURI (java.io.File. "maven_repository")))}
  :dependencies [[org.clojure/clojure "1.4.0"] [ring "1.1.6"]]) 

