/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class datetime$now
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "now");

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke();
    }

    @Override
    public Object invoke() {
        return datetime$now.invokeStatic();
    }
}

