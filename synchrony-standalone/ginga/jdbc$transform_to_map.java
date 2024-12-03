/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.jdbc$transform_to_map$fn__20352;

public final class jdbc$transform_to_map
extends AFunction {
    public static final Var const__0 = RT.var("clojure.walk", "postwalk");

    public static Object invokeStatic(Object parsed) {
        Object object = parsed;
        parsed = null;
        return ((IFn)const__0.getRawRoot()).invoke(new jdbc$transform_to_map$fn__20352(), object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return jdbc$transform_to_map.invokeStatic(object2);
    }
}

