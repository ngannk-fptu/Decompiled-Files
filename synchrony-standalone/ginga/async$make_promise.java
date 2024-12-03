/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class async$make_promise
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "promise-chan");
    public static final Var const__2 = RT.var("clojure.core.async", "close!");
    public static final Var const__3 = RT.var("clojure.core.async", "put!");

    public static Object invokeStatic(Object value) {
        Object promise_ch = ((IFn)const__0.getRawRoot()).invoke();
        if (Util.identical(value, null)) {
            ((IFn)const__2.getRawRoot()).invoke(promise_ch);
        } else {
            Object object = value;
            value = null;
            ((IFn)const__3.getRawRoot()).invoke(promise_ch, object);
        }
        Object var1_1 = null;
        return promise_ch;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return async$make_promise.invokeStatic(object2);
    }
}

