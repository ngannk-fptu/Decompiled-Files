/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$expect_BANG_$fn__9250;

public final class async$expect_BANG_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "expect!");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object in2, Object close_promise) {
        Object c__5667__auto__9289 = ((IFn)const__1.getRawRoot()).invoke(const__2);
        Object captured_bindings__5668__auto__9290 = Var.getThreadBindingFrame();
        Object object = close_promise;
        close_promise = null;
        Object object2 = in2;
        in2 = null;
        Object object3 = captured_bindings__5668__auto__9290;
        captured_bindings__5668__auto__9290 = null;
        ((IFn)const__3.getRawRoot()).invoke(new async$expect_BANG_$fn__9250(object, c__5667__auto__9289, object2, object3));
        Object var2_2 = null;
        return c__5667__auto__9289;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$expect_BANG_.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object in2) {
        Object object = in2;
        in2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__1.getRawRoot()).invoke());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return async$expect_BANG_.invokeStatic(object2);
    }
}

