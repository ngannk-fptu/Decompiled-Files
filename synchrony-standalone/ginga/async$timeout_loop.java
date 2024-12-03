/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$timeout_loop$fn__9655;

public final class async$timeout_loop
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "timeout-loop");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object ms, Object ch) {
        Object c__5667__auto__9684 = ((IFn)const__1.getRawRoot()).invoke(const__2);
        Object captured_bindings__5668__auto__9685 = Var.getThreadBindingFrame();
        Object object = ms;
        ms = null;
        Object object2 = captured_bindings__5668__auto__9685;
        captured_bindings__5668__auto__9685 = null;
        ((IFn)const__3.getRawRoot()).invoke(new async$timeout_loop$fn__9655(c__5667__auto__9684, object, object2, ch));
        Object var1_1 = null;
        return ch;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$timeout_loop.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object ms) {
        Object object = ms;
        ms = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__1.getRawRoot()).invoke());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return async$timeout_loop.invokeStatic(object2);
    }
}

