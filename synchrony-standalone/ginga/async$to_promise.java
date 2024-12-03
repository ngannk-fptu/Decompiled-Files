/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$to_promise$fn__8736;

public final class async$to_promise
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "promise-chan");
    public static final Var const__1 = RT.var("clojure.core.async", "take!");

    public static Object invokeStatic(Object ch) {
        Object promise_ch = ((IFn)const__0.getRawRoot()).invoke();
        Object object = ch;
        ch = null;
        ((IFn)const__1.getRawRoot()).invoke(object, new async$to_promise$fn__8736(promise_ch));
        Object var1_1 = null;
        return promise_ch;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return async$to_promise.invokeStatic(object2);
    }
}

