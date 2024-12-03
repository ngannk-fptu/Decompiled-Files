/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$map_vals$fn__8505;

public final class core$map_vals
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "persistent!");
    public static final Var const__1 = RT.var("clojure.core", "reduce-kv");
    public static final Var const__2 = RT.var("clojure.core", "transient");

    public static Object invokeStatic(Object f, Object m4) {
        Object object = f;
        f = null;
        Object object2 = m4;
        m4 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(new core$map_vals$fn__8505(object), ((IFn)const__2.getRawRoot()).invoke(PersistentArrayMap.EMPTY), object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$map_vals.invokeStatic(object3, object4);
    }
}

