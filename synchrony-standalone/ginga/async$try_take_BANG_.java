/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$try_take_BANG_$fn__9069;

public final class async$try_take_BANG_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "try-take!");
    public static final Var const__1 = RT.var("clojure.core.async", "chan");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object source2, Object default_val, Object timeout2, Object timeout_val) {
        Object c__5667__auto__9110 = ((IFn)const__1.getRawRoot()).invoke(const__2);
        Object captured_bindings__5668__auto__9111 = Var.getThreadBindingFrame();
        Object object = timeout_val;
        timeout_val = null;
        Object object2 = timeout2;
        timeout2 = null;
        Object object3 = source2;
        source2 = null;
        Object object4 = default_val;
        default_val = null;
        Object object5 = captured_bindings__5668__auto__9111;
        captured_bindings__5668__auto__9111 = null;
        ((IFn)const__3.getRawRoot()).invoke(new async$try_take_BANG_$fn__9069(object, object2, c__5667__auto__9110, object3, object4, object5));
        Object object6 = c__5667__auto__9110;
        c__5667__auto__9110 = null;
        return object6;
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
        return async$try_take_BANG_.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object source2, Object timeout2) {
        Object object = source2;
        source2 = null;
        Object object2 = timeout2;
        timeout2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, null, object2, null);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$try_take_BANG_.invokeStatic(object3, object4);
    }
}

