/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$compose$fn__10265;

public final class async$compose
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "chan");
    public static final Object const__1 = 1L;
    public static final Var const__2 = RT.var("clojure.core.async.impl.dispatch", "run");

    public static Object invokeStatic(Object compose2, Object from, Object to) {
        Object c__5667__auto__10320 = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object captured_bindings__5668__auto__10321 = Var.getThreadBindingFrame();
        Object object = compose2;
        compose2 = null;
        Object object2 = from;
        from = null;
        Object object3 = captured_bindings__5668__auto__10321;
        captured_bindings__5668__auto__10321 = null;
        Object object4 = to;
        to = null;
        ((IFn)const__2.getRawRoot()).invoke(new async$compose$fn__10265(object, object2, c__5667__auto__10320, object3, object4));
        Object var3_3 = null;
        return c__5667__auto__10320;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return async$compose.invokeStatic(object4, object5, object6);
    }
}

