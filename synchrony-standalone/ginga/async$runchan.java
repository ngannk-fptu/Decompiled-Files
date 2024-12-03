/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$runchan$fn__9781;

public final class async$runchan
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async", "reduce");

    public static Object invokeStatic(Object ch) {
        Object object = ch;
        ch = null;
        return ((IFn)const__0.getRawRoot()).invoke(new async$runchan$fn__9781(), null, object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return async$runchan.invokeStatic(object2);
    }
}

