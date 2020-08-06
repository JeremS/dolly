(ns dolly.core
  (:require
    [net.cgrand.macrovich :as macro])
  #?(:cljs
     (:require-macros
       [dolly.core])))


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


  (defn- make-added-meta [{:keys [cloned-sym cloned-meta]}]
    (-> cloned-meta
        (dissoc :line :column :file :name :ns)
        (assoc ::clone-of cloned-sym)))


  (defn- quote* [x]
    (list 'quote x))


  (defn- quote-relevant [m]
    (-> m
        (update ::clone-of quote*)
        (cond-> (:arglists m) (update :arglists quote*))))


  (defmacro clone-value [new-name cloned]
    (let [{:keys [cloned-sym]
           :as info} (cloned-info cloned)
          added-meta (make-added-meta info)
          new-name (with-meta new-name
                              (macro/case :clj (quote-relevant added-meta)
                                          :cljs added-meta))]
      (when (:macro added-meta)
        (throw (ex-info (str "Can't clone the macro `" cloned-sym "` as a value.") {})))

      `(def ~new-name ~cloned-sym)))


  (defmacro clone-macro [new-name cloned]
    (let [{:keys [cloned-sym]
           :as info} (cloned-info cloned)
          added-meta (make-added-meta info)]
      (when-not (:macro added-meta)
        (throw (ex-info (str "Can't clone the value `" cloned-sym "` as a macro.") {})))

      `(macro/deftime
         (do
           (defmacro ~new-name [& body#]
             (list* '~cloned-sym body#))
           (alter-meta! (var ~new-name) merge '~added-meta)))))


  (defmacro def-clone
    ([cloned]
     `(def-clone nil ~cloned))
    ([new-name cloned]
     (let [cloned-var (resolve-cloned cloned)
           new-name (or new-name
                        (-> cloned-var symbol name symbol))]
       (if (-> cloned-var meta :macro)
         `(clone-macro ~new-name ~cloned)
         `(clone-value ~new-name ~cloned))))))
