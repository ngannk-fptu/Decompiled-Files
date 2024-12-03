/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$dissoc_keys_with_nil_values$fn__8463;

public final class core$dissoc_keys_with_nil_values
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "into");
    public static final Var const__1 = RT.var("clojure.core", "remove");

    public static Object invokeStatic(Object a_map) {
        Object object = a_map;
        a_map = null;
        return ((IFn)const__0.getRawRoot()).invoke(PersistentArrayMap.EMPTY, ((IFn)const__1.getRawRoot()).invoke(new core$dissoc_keys_with_nil_values$fn__8463(), object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$dissoc_keys_with_nil_values.invokeStatic(object2);
    }
}

