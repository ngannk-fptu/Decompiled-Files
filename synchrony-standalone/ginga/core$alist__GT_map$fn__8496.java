/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$alist__GT_map$fn__8496
extends AFunction {
    public static final Var const__3 = RT.var("ginga.core", "update!");
    public static final Var const__4 = RT.var("ginga.core", "conjv");

    @Override
    public Object invoke(Object r, Object p__8495) {
        Object object = p__8495;
        p__8495 = null;
        Object vec__8497 = object;
        Object k = RT.nth(vec__8497, RT.intCast(0L), null);
        Object object2 = vec__8497;
        vec__8497 = null;
        Object v = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = r;
        r = null;
        Object object4 = k;
        k = null;
        Object object5 = v;
        v = null;
        core$alist__GT_map$fn__8496 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object3, object4, const__4.getRawRoot(), object5);
    }
}

