/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class async$wrap
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "wrap");
    public static final Object const__1 = 1L;
    public static final Var const__2 = RT.var("clojure.core.async", "chan");
    public static final Var const__3 = RT.var("clojure.core.async", "pipe");

    public static Object invokeStatic(Object to, Object buf_or_n, Object xf, Object ex_handler2) {
        Object object;
        Object object2 = ex_handler2;
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = buf_or_n;
            buf_or_n = null;
            Object object4 = xf;
            xf = null;
            Object object5 = ex_handler2;
            ex_handler2 = null;
            object = ((IFn)const__2.getRawRoot()).invoke(object3, object4, object5);
        } else {
            Object object6 = buf_or_n;
            buf_or_n = null;
            Object object7 = xf;
            xf = null;
            object = ((IFn)const__2.getRawRoot()).invoke(object6, object7);
        }
        Object xf_ch = object;
        Object object8 = to;
        to = null;
        ((IFn)const__3.getRawRoot()).invoke(xf_ch, object8);
        Object object9 = xf_ch;
        xf_ch = null;
        return object9;
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
        return async$wrap.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object to, Object xf, Object ex_handler2) {
        Object object = to;
        to = null;
        Object object2 = xf;
        xf = null;
        Object object3 = ex_handler2;
        ex_handler2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1, object2, object3);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$wrap.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object to, Object xf) {
        Object object = to;
        to = null;
        Object object2 = xf;
        xf = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, null);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$wrap.invokeStatic(object3, object4);
    }
}

