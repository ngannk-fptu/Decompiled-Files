/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class haywire$new_sequence
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "swap!");
    public static final Var const__1 = RT.var("ginga.async.haywire", "inc-wraparound");

    public static Object invokeStatic(Object ref2) {
        Object object = ref2;
        ref2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1.getRawRoot());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return haywire$new_sequence.invokeStatic(object2);
    }
}

