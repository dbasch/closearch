(ns closearch.core
  (:gen-class :main true)
  (:use ring.adapter.jetty [clojure.set :only (union intersection)] [clojure.string :only (split lower-case)] [clj-time.core :only (now)]))

(def p (re-pattern #"[^\p{L}&&[^\p{M}]]"))
(def index)

;; Read a file, invert it, and add it to the index.
(defn add[idx file]
  (let [f (.getName file)]
    (loop [idx idx
           tokens (.split p (lower-case (slurp file)))]
      (if-not (seq tokens) idx
              (recur (assoc! idx (first tokens) (union (idx (first tokens)) #{f})) (rest tokens))))))

;; Index all files.
(defn build-index[dirname]
  (loop [files (.listFiles (java.io.File. dirname))
         idx (transient {})]
    (if-not (seq files) (persistent! idx)
            (recur (rest files) (add idx (first files))))))

;; An "AND" query. The only type we support.
(defn search[q]
  (reduce intersection (map (comp index lower-case) (split q #"\+"))))

;; A quick and dirty way to render an html response for a query. Don't judge me :)
(defn response[q]
  (str "<html>" (apply str (interpose "<br/>" (search q))) "</html>"))

(defn app [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body (if (req :query-string) (response (req :query-string)))})

;; Run the search server.
(defn -main[& args]
  (println (str "Building index: " (now)))
  (def index (build-index (first args)))
  (println (str "Starting search server: " (now)))
  (run-jetty #'app {:port 8080 :join? false}))
