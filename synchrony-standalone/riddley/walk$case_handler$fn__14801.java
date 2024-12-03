/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;

public final class walk$case_handler$fn__14801
extends AFunction {
    Object f;

    public walk$case_handler$fn__14801(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object p__14800) {
        Object object = p__14800;
        p__14800 = null;
        Object vec__14802 = object;
        Object k = RT.nth(vec__14802, RT.intCast(0L), null);
        Object object2 = vec__14802;
        vec__14802 = null;
        Object vec__14805 = RT.nth(object2, RT.intCast(1L), null);
        Object idx = RT.nth(vec__14805, RT.intCast(0L), null);
        Object object3 = vec__14805;
        vec__14805 = null;
        Object form2 = RT.nth(object3, RT.intCast(1L), null);
        Object object4 = k;
        k = null;
        Object object5 = idx;
        idx = null;
        Object object6 = form2;
        form2 = null;
        return Tuple.create(object4, Tuple.create(object5, ((IFn)this.f).invoke(object6)));
    }
}

