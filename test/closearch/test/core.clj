(ns closearch.test.core
  (:use [closearch.core])
  (:use [clojure.test]))

(deftest test-build-index
 (def test-index (build-index "test-docs/"))
 (is (= (test-index "shoes") #{"file2.txt" "file3.txt"}))
 (is (= (test-index "youwontfindme") nil)))
