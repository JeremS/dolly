(ns dolly.examples
  (:require [net.cgrand.macrovich :as macro])
  #?(:cljs
     (:require-macros
       [dolly.examples])))



(def a-value
  "An example value to be cloned and tested."
  :a)

(defn a-fn
  "An example fn to be cloned and tested."
  [x] (* x 2))

(macro/deftime
  (defmacro my-when
    "An example macro to be cloned and tested."
    [test & body]
    (list 'if test (cons 'do body))))
