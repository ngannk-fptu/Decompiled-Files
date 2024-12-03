/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import clout.core$assoc_keys_with_groups$fn__34665;

public final class core$assoc_keys_with_groups
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "reduce");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("clojure.core", "vector");

    public static Object invokeStatic(Object groups, Object keys2) {
        Object object = keys2;
        keys2 = null;
        Object object2 = groups;
        groups = null;
        return ((IFn)const__0.getRawRoot()).invoke(new core$assoc_keys_with_groups$fn__34665(), PersistentArrayMap.EMPTY, ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), object, object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$assoc_keys_with_groups.invokeStatic(object3, object4);
    }
}

