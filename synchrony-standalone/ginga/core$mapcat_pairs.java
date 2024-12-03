/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$mapcat_pairs$fn__8392;

public final class core$mapcat_pairs
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "persistent!");
    public static final Var const__1 = RT.var("clojure.core", "reduce");
    public static final Var const__2 = RT.var("clojure.core", "transient");

    public static Object invokeStatic(Object f, Object s2) {
        Object object = f;
        f = null;
        Object object2 = s2;
        s2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(new core$mapcat_pairs$fn__8392(object), ((IFn)const__2.getRawRoot()).invoke(PersistentVector.EMPTY), object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$mapcat_pairs.invokeStatic(object3, object4);
    }
}

