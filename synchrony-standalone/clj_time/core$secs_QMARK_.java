/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$secs_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "deprecated");
    public static final Var const__1 = RT.var("clj-time.core", "seconds?");

    public static Object invokeStatic(Object val2) {
        ((IFn)const__0.getRawRoot()).invoke("secs? has been deprecated in favor of seconds?");
        Object object = val2;
        val2 = null;
        return ((IFn)const__1.getRawRoot()).invoke(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$secs_QMARK_.invokeStatic(object2);
    }
}

