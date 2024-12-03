/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$yesterday
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "ago");
    public static final Var const__1 = RT.var("clj-time.core", "days");
    public static final Object const__2 = 1L;

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2));
    }

    @Override
    public Object invoke() {
        return core$yesterday.invokeStatic();
    }
}

