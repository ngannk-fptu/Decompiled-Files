/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$deprecated
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "println");

    public static Object invokeStatic(Object message) {
        Object object = message;
        message = null;
        return ((IFn)const__0.getRawRoot()).invoke("DEPRECATION WARNING: ", object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$deprecated.invokeStatic(object2);
    }
}

