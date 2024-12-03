/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$let_bindings$fn__14744;

public final class walk$let_bindings
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "vec");
    public static final Var const__1 = RT.var("clojure.core", "mapcat");
    public static final Var const__2 = RT.var("clojure.core", "partition-all");
    public static final Object const__3 = 2L;

    public static Object invokeStatic(Object f, Object x) {
        Object object = f;
        f = null;
        Object object2 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(new walk$let_bindings$fn__14744(object), ((IFn)const__2.getRawRoot()).invoke(const__3, object2)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$let_bindings.invokeStatic(object3, object4);
    }
}

