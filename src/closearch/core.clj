;; CloSearch - a very basic text search engine

(ns closearch.core
  (:use ring.adapter.jetty [clojure.set :only (union intersection)] [clojure.string :only (split lower-case)]))

(defn tokenize [text]
  (split (lower-case text) #"[\s#&!:,;\.\\\+-]+"))

;; Create token -> file mappings for all tokens in a text file
(defn mappings [file]
  (map #(hash-map % #{file}) (-> file slurp tokenize distinct)))

;; Stick all the mappings for all the tokens in all the files into a single in-memory map
(def idx
  (let [files (.listFiles (java.io.File. "text/"))]
    (apply merge-with union (mapcat (comp mappings #(.getPath %)) files))))

;; An "AND" query. The only type we support.
(defn search [q]
  (apply intersection (map (comp idx lower-case) (split q #"\+"))))

;; A quick and dirty way to render an html response for a query.
(defn response [q]
  (str "<html>" (apply str (interpose "<br/>" (search q))) "</html>"))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body
   (if (req :query-string) (response (req :query-string)))})

;; Run the search server.
(defn boot []
  (run-jetty #'app {:port 8080 :join? false}))

(defn -main[& args] (boot))
