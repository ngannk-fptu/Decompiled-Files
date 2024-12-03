/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$assoc_keys_with_groups$fn__34665
extends AFunction {
    public static final Var const__3 = RT.var("clout.core", "assoc-conj");

    @Override
    public Object invoke(Object m4, Object p__34664) {
        Object object = p__34664;
        p__34664 = null;
        Object vec__34666 = object;
        Object k = RT.nth(vec__34666, RT.intCast(0L), null);
        Object object2 = vec__34666;
        vec__34666 = null;
        Object v = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = m4;
        m4 = null;
        Object object4 = k;
        k = null;
        Object object5 = v;
        v = null;
        core$assoc_keys_with_groups$fn__34665 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object3, object4, object5);
    }
}

