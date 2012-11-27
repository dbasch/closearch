;; CloSearch - a very basic text search engine

(ns closearch.core
  (:gen-class :main true)
  (:use ring.adapter.jetty [clojure.set :only (union intersection)] [clojure.string :only (split lower-case)]))

(def p (re-pattern #"[\s#&!:,;\.\\\+-]+"))

(defn tokenize[text]
  (.split p (lower-case text)))

;; Create token -> file mappings for all tokens in a text file
(defn mappings[dirname filename]
  (map #(hash-map % #{filename}) (-> (str dirname "/" filename) slurp tokenize)))

;; Stick all the mappings for all the tokens in all the files into a single in-memory map
(defn build-index[dirname]
  (apply merge-with union (mapcat (comp (partial mappings dirname) #(.getName %)) (.listFiles (java.io.File. dirname)))))

(def idx (build-index "text/"))

;; An "AND" query. The only type we support.
(defn search[q]
  (reduce intersection (map (comp idx lower-case) (split q #"\+"))))

;; A quick and dirty way to render an html response for a query. Don't judge me :)
(defn response[q]
  (str "<html>" (apply str (interpose "<br/>" (search q))) "</html>"))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body
   (if (req :query-string) (response (req :query-string)))})

(defn -main[& args]
  (run-jetty #'app {:port 8080 :join? false}))
