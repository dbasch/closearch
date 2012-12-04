(ns closearch.testfiles
 (:gen-class :main true)
 (:import java.io.File)
 (:use [clojure.string :only (split)]))

(def dict (split (slurp "/usr/share/dict/words") #"\n"))

;; Use this to generate a relatively large number of files for performance tests.
;; e.g. 10000
(defn -main[& args]
 (.mkdir (new File (second args)))
 (let [nwords (count dict)]
 (dotimes [n (#(Integer/parseInt %) (first args))]
   (let [positions (take 1000 (repeatedly #(rand-int nwords)))]
  (spit (str (second args) "/file." n) (apply str (interpose " " (map (partial nth dict) positions))))))))
