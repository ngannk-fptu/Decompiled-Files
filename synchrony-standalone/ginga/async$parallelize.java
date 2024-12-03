/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$parallelize$fn__10399;

public final class async$parallelize
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "parallelize");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Var const__2 = RT.var("clojure.core.async", "pipeline-async");

    public static Object invokeStatic(Object n, Object f, Object to, Object from, Object close_QMARK_) {
        Object object = n;
        n = null;
        Object object2 = f;
        f = null;
        Object object3 = from;
        from = null;
        Object object4 = close_QMARK_;
        close_QMARK_ = null;
        ((IFn)const__2.getRawRoot()).invoke(object, to, new async$parallelize$fn__10399(object2), object3, object4);
        Object var2_2 = null;
        return to;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        Object object10 = object5;
        object5 = null;
        return async$parallelize.invokeStatic(object6, object7, object8, object9, object10);
    }

    public static Object invokeStatic(Object n, Object f, Object to, Object from) {
        Object object = n;
        n = null;
        Object object2 = f;
        f = null;
        Object object3 = from;
        from = null;
        Object object4 = to;
        to = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, object4, Boolean.TRUE);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return async$parallelize.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object n, Object f, Object from) {
        Object object = n;
        n = null;
        Object object2 = f;
        f = null;
        Object object3 = from;
        from = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, ((IFn)const__1.getRawRoot()).invoke());
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$parallelize.invokeStatic(object4, object5, object6);
    }
}

