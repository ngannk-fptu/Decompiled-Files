/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$route_regex$fn__34742
extends AFunction {
    public static final Var const__0 = RT.var("clout.core", "re-escape");
    public static final Var const__1 = RT.var("clojure.core", "subs");
    public static final Object const__2 = 1L;

    @Override
    public Object invoke(Object p1__34739_SHARP_) {
        Object object = p1__34739_SHARP_;
        p1__34739_SHARP_ = null;
        core$route_regex$fn__34742 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object, const__2));
    }
}

