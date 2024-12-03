/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$put_wait_on_chan_BANG_$fn__9018;

public final class async$put_wait_on_chan_BANG_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "put-wait-on-chan!");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async", "put!");

    public static Object invokeStatic(Object ch, Object value, Object wait_ch) {
        Object object = ch;
        ch = null;
        Object object2 = value;
        value = null;
        ((IFn)const__3.getRawRoot()).invoke(object, object2, new async$put_wait_on_chan_BANG_$fn__9018(wait_ch), Boolean.TRUE);
        Object var2_2 = null;
        return wait_ch;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$put_wait_on_chan_BANG_.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object ch, Object value) {
        Object object = ch;
        ch = null;
        Object object2 = value;
        value = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)const__1.getRawRoot()).invoke(const__2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$put_wait_on_chan_BANG_.invokeStatic(object3, object4);
    }
}

