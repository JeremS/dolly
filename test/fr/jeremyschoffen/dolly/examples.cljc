(ns fr.jeremyschoffen.dolly.examples
  (:require
    [net.cgrand.macrovich :as macro]
    [fr.jeremyschoffen.dolly.core :as dolly])
  #?(:cljs
     (:require-macros
       [fr.jeremyschoffen.dolly.examples])))


(dolly/add-keys-to-quote! :my-key)


(def a-value
  "An example value to be cloned and tested."
  :a)

(defn a-fn
  "An example fn to be cloned and tested."
  {:my-key 'a-symbol}
  [x] (* x 2))

(macro/deftime
  (defmacro my-when
    "An example macro to be cloned and tested."
    [test & body]
    (list 'if test (cons 'do body))))
