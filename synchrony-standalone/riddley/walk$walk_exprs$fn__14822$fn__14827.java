/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$walk_exprs$fn__14822$fn__14827
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "doall");
    public static final Var const__1 = RT.var("clojure.core", "map");

    @Override
    public Object invoke(Object p1__14820_SHARP_, Object p2__14821_SHARP_) {
        Object object = p1__14820_SHARP_;
        p1__14820_SHARP_ = null;
        Object object2 = p2__14821_SHARP_;
        p2__14821_SHARP_ = null;
        walk$walk_exprs$fn__14822$fn__14827 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object, object2));
    }
}

