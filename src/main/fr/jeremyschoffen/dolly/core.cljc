(ns fr.jeremyschoffen.dolly.core
  (:require
    [clojure.set :as set]
    [net.cgrand.macrovich :as macro])
  #?(:cljs
     (:require-macros
       [fr.jeremyschoffen.dolly.core])))


(macro/deftime

  (def ^:private meta-keys-to-quote (atom #{::clone-of :arglists}))


  (defmacro add-keys-to-quote!
    "When using your own metadata on defs, you might use symbols as data.
    The data stored under these keys may need to be quoted by the
    [[fr.jeremyschoffen.dolly.core/def-clone]] macro. You can add those keys
    to be quoted with this function"
    [& keys]
    (swap! meta-keys-to-quote set/union (set keys))
    `(do))


  (defmacro remove-keys-to-quote!
    "Opposite of [[fr.jeremyschoffen.dolly.core/add-keys-to-quote!]]"
    [& keys]
    (swap! meta-keys-to-quote set/difference (set keys))
    `(do))


  (defn- resolve-cloned [cloned]
    (let [cloned-var (resolve cloned)]
      (if cloned-var
        cloned-var
        (throw (ex-info (str "Can't resolve `" cloned  "` while cloning var.") {})))))


  (defn cloned-info
    [cloned]
    (let [cloned-var (resolve-cloned cloned)
          cloned-meta (meta cloned-var)]
      {:cloned-var cloned-var
       :cloned-sym (symbol cloned-var)
       :cloned-meta cloned-meta
       :type (cond
               (:macro cloned-meta) :macro
               (:arglists cloned-meta) :function
               :else :value)}))


  (defn- make-added-meta [{:keys [cloned-sym cloned-meta]}]
    (-> cloned-meta
        (dissoc :line :column :file :name :ns)
        (assoc ::clone-of cloned-sym)))


  (defn- quote* [x]
    (list 'quote x))


  (defn- quote-relevant [m]
    (let [ks-to-quote @meta-keys-to-quote]
      (reduce-kv (fn [acc k v]
                   (if (contains? ks-to-quote k)
                     (assoc acc k (quote* v))
                     acc))
                 m
                 m)))


  (defmacro clone-value [new-name cloned]
    (let [{:keys [cloned-sym]
           :as info} (cloned-info cloned)
          added-meta (make-added-meta info)
          new-meta (merge (macro/case :clj (quote-relevant added-meta)
                                      :cljs added-meta)
                          (meta new-name))
          new-name (with-meta new-name new-meta)]
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
           (alter-meta! (var ~new-name) #(merge '~added-meta %))))))


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
