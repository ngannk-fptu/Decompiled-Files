/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import clout.core$re_escape$fn__34644;

public final class core$re_escape
extends AFunction {
    public static final Var const__0 = RT.var("clojure.string", "escape");

    public static Object invokeStatic(Object s2) {
        Object object = s2;
        s2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, new core$re_escape$fn__34644());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$re_escape.invokeStatic(object2);
    }
}

