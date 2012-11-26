;; CloSearch - a very basic text search engine

(ns closearch.core
  (:use ring.adapter.jetty [clojure.set :only (union intersection)] [clojure.string :only (split lower-case)]))

(defn tokenize[text]
  (split (lower-case text) #"[\s#&!:,;\.\\\+-]+"))

; Create token -> file mappings for all tokens in a text file
; Sorry, no tf-idf for now. Who needs relevance anyway?
(defn mappings[dirname filename]
  (map #(hash-map % #{filename}) (-> (str dirname "/" filename) slurp tokenize)))

; Stick all the mappings for all the tokens in all the files into a single in-memory map
; If you kill the server, you'll have to reindex everything.
(defn build-index[dirname]
  (apply merge-with union (mapcat (comp (partial mappings dirname) #(.getName %)) (.listFiles (java.io.File. dirname)))))

(def idx (build-index "text/"))

; An "AND" query. The only type we support.
(defn search[q]
  (reduce intersection (map (comp idx lower-case) (split q #"\+"))))

; A quick and dirty way to render an html response for a query. Don't judge me :)
(defn response[q]
 (str "<html>" (apply str (interpose "<br/>" (search q))) "</html>"))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body
   (if (req :query-string) (response (req :query-string)))})

; Run the search server. You can do this from the REPL if you so please.
(defn boot []
  (run-jetty #'app {:port 8080 :join? false}))

(defn -main[& args] (boot))
