(ns dolly.core
  (:require
    [net.cgrand.macrovich :as macro])
  #?(:cljs
     (:require-macros
       [dolly.core])))

;; TODO add documentation notices
;; TODO clone specs
(macro/deftime
  (defn resolve-cloned [cloned]
    (let [cloned-var (resolve cloned)]
      (if cloned-var
        cloned-var
        (throw (ex-info (str "Can't resolve `" cloned  "` while cloning var.") {})))))


  (defn- cloned-info [cloned]
    (let [cloned-var (resolve-cloned cloned)]
      {:cloned-var cloned-var
       :cloned-sym (symbol cloned-var)
       :cloned-meta (meta cloned-var)}))


  (defn- quote* [x]
    (list 'quote x))


  (defn- make-added-meta [{:keys [cloned-sym cloned-meta]}]
    (let [arglists (when-let [arglists (:arglists cloned-meta)]
                     (println arglists)
                     (quote* arglists))]
      (println arglists)
      (-> cloned-meta
          (dissoc :line :column :file
                  :name :ns)
          (assoc ::clone-of (quote* cloned-sym))
          (cond-> arglists (assoc :arglists arglists)))))


  (defmacro clone-value [new-name cloned]
    (let [{:keys [cloned-sym]
           :as info} (cloned-info cloned)
          added-meta (make-added-meta info)
          new-name (with-meta new-name added-meta)]
      (when (:macro added-meta)
        (throw (ex-info (str "Can't clone the macro `" cloned-sym "` as a value.") {})))
      `(do
         (def ~new-name ~cloned-sym))))


  (defmacro clone-macro [new-name cloned]
    (let [{:keys [cloned-sym]
           :as info} (cloned-info cloned)
          added-meta (make-added-meta info)
          new-name (with-meta new-name added-meta)]

      (when-not (:macro added-meta)
        (throw (ex-info (str "Can't clone the value `" cloned-sym "` as a macro.") {})))

      `(macro/deftime
         (do
           (defmacro ~new-name [& body#]
             (list* '~cloned-sym body#))))))


  (defmacro def-clone
    ([cloned]
     `(def-clone nil ~cloned))
    ([new-name cloned]
     (let [cloned-var (resolve-cloned cloned)
           new-name (or new-name
                        (-> cloned-var symbol name symbol))]
       (if (-> cloned-var meta  :macro)
         `(clone-macro ~new-name ~cloned)
         `(clone-value ~new-name ~cloned))))))


(comment
  (defn add-alias-notice! [a-var aliased]
    (if-let [original (-> aliased resolve meta ::aliasing)]
      (alter-meta! a-var assoc ::aliasing original)
      (alter-meta! a-var #(-> %
                              (assoc ::aliasing aliased)
                              (update :doc str "\n\n  aliasing: " (format "[[%s]]" aliased)))))))