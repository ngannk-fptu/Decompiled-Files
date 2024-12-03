/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class win$swap_BANG__transition_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "apply");
    public static final Var const__1 = RT.var("ginga.async.win", "transition?");
    public static final Var const__2 = RT.var("ginga.core", "swap-return-both!");

    public static Object invokeStatic(Object win_stats, Object f, Object cnt, Object size2) {
        Object object = win_stats;
        win_stats = null;
        Object object2 = f;
        f = null;
        Object object3 = cnt;
        cnt = null;
        Object object4 = size2;
        size2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), ((IFn)const__2.getRawRoot()).invoke(object, object2, object3, object4));
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
        return win$swap_BANG__transition_QMARK_.invokeStatic(object5, object6, object7, object8);
    }
}

