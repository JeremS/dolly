(ns fr.jeremyschoffen.dolly.core-test
  (:require
    #?(:clj [clojure.test :refer [deftest is are testing run-tests]]
       :cljs [cljs.test :refer-macros [deftest is are testing run-tests]])
    [fr.jeremyschoffen.dolly.core :as dolly]
    [fr.jeremyschoffen.dolly.examples :as examples])
  #?(:cljs (:require-macros [fr.jeremyschoffen.dolly.core-test :refer [my-when]])))


(dolly/def-clone examples/a-value)

(dolly/def-clone examples/a-fn)

(dolly/def-clone examples/my-when)


(deftest clones
  (testing "Simple value"
    (are [x y] (= x y)
      a-value examples/a-value

      (-> a-value var meta ::dolly/clone-of)
      `examples/a-value))

  (testing "Function"
    (are [x y] (= x y)
      a-fn examples/a-fn
      (a-fn 2) (examples/a-fn 2)))

  (testing "macro"
    (is (= (my-when true 0 1) 1))
    (is (= (my-when false 0 1) nil))))


(comment
  (type 1)
  (run-tests)
  (meta (var a-value))
  (meta (var a-fn))
  (meta (var examples/a-fn))
  (clojure.repl/doc a-value)
  (cljs.repl/doc a-value)

  (clojure.repl/doc a-fn)
  (cljs.repl/doc a-fn)

  (clojure.repl/doc my-when)
  (cljs.repl/doc my-when))
