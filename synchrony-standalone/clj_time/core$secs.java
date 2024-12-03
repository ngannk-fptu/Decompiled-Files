/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$secs
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "deprecated");
    public static final Var const__1 = RT.var("clj-time.core", "seconds");

    public static Object invokeStatic(Object n) {
        ((IFn)const__0.getRawRoot()).invoke("secs has been deprecated in favor of seconds");
        Object object = n;
        n = null;
        return ((IFn)const__1.getRawRoot()).invoke(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$secs.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        ((IFn)const__0.getRawRoot()).invoke("secs has been deprecated in favor of seconds");
        return ((IFn)const__1.getRawRoot()).invoke();
    }

    @Override
    public Object invoke() {
        return core$secs.invokeStatic();
    }
}

