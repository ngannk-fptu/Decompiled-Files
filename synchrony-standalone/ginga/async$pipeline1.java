/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$pipeline1
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "pipeline1");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async", "pipe");

    public static Object invokeStatic(Object to, Object xf, Object from, Object close_QMARK_, Object ex_handler2) {
        Object object;
        Object object2 = ex_handler2;
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = xf;
            xf = null;
            Object object4 = ex_handler2;
            ex_handler2 = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__2, object3, object4);
        } else {
            Object object5 = xf;
            xf = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__2, object5);
        }
        Object xf_ch = object;
        Object object6 = from;
        from = null;
        ((IFn)const__3.getRawRoot()).invoke(object6, xf_ch, close_QMARK_);
        Object object7 = xf_ch;
        xf_ch = null;
        Object object8 = to;
        to = null;
        Object object9 = close_QMARK_;
        close_QMARK_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object7, object8, object9);
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
        return async$pipeline1.invokeStatic(object6, object7, object8, object9, object10);
    }

    public static Object invokeStatic(Object to, Object xf, Object from) {
        Object object = to;
        to = null;
        Object object2 = xf;
        xf = null;
        Object object3 = from;
        from = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, Boolean.TRUE, null);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$pipeline1.invokeStatic(object4, object5, object6);
    }
}

