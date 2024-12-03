/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$from_now
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "plus");
    public static final Var const__1 = RT.var("clj-time.core", "now");

    public static Object invokeStatic(Object period) {
        Object object = period;
        period = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(), object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$from_now.invokeStatic(object2);
    }
}

