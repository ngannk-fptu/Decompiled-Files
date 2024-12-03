/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$some_iteration$fn__9927;

public final class async$some_iteration
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "chan");
    public static final Object const__1 = 1L;
    public static final Var const__2 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object pred_QMARK_, Object f, Object init2) {
        Object c__5667__auto__9973 = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object captured_bindings__5668__auto__9974 = Var.getThreadBindingFrame();
        Object object = f;
        f = null;
        Object object2 = pred_QMARK_;
        pred_QMARK_ = null;
        Object object3 = init2;
        init2 = null;
        Object object4 = captured_bindings__5668__auto__9974;
        captured_bindings__5668__auto__9974 = null;
        ((IFn)const__2.getRawRoot()).invoke(new async$some_iteration$fn__9927(object, object2, object3, object4, c__5667__auto__9973));
        Object var3_3 = null;
        return c__5667__auto__9973;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$some_iteration.invokeStatic(object4, object5, object6);
    }
}

