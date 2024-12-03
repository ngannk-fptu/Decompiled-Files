/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class walk$let_bindings$fn__14744
extends AFunction {
    Object f;
    public static final Var const__3 = RT.var("riddley.compiler", "register-local");

    public walk$let_bindings$fn__14744(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object p__14743) {
        Object object = p__14743;
        p__14743 = null;
        Object vec__14745 = object;
        Object k = RT.nth(vec__14745, RT.intCast(0L), null);
        Object object2 = vec__14745;
        vec__14745 = null;
        Object v = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = k;
        k = null;
        Object object4 = v;
        v = null;
        IPersistentVector vec__14748 = Tuple.create(object3, ((IFn)this.f).invoke(object4));
        Object k2 = RT.nth(vec__14748, RT.intCast(0L), null);
        IPersistentVector iPersistentVector = vec__14748;
        vec__14748 = null;
        Object v2 = RT.nth(iPersistentVector, RT.intCast(1L), null);
        ((IFn)const__3.getRawRoot()).invoke(k2, v2);
        Object object5 = k2;
        k2 = null;
        Object object6 = v2;
        v2 = null;
        return Tuple.create(object5, object6);
    }
}

