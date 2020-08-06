(ns dolly.core-test
  (:require [clojure.test :refer [deftest is are testing]]
            [dolly.core :as dolly]
            [dolly.examples :as examples])
  #?(:cljs (:require-macros [dolly.core-test :refer [my-when]])))


(dolly/def-clone examples/a-value)

(dolly/def-clone examples/a-fn)

(dolly/def-clone examples/my-when)



(deftest clones
  (testing "Simple value"
    (is (= a-value examples/a-value)))

  (testing "Function"
    (are [x y] (= x y)
      a-fn examples/a-fn
      (a-fn 2) (examples/a-fn 2)))

  (testing "macro"
    (is (= (my-when true 0 1) 1))
    (is (= (my-when false 0 1) nil))))

;(clojure.test/run-tests)
