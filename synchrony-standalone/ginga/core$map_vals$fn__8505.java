/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$map_vals$fn__8505
extends AFunction {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "assoc!");

    public core$map_vals$fn__8505(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object r, Object k, Object v) {
        Object object = r;
        r = null;
        Object object2 = k;
        k = null;
        Object object3 = v;
        v = null;
        core$map_vals$fn__8505 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)this_.f).invoke(object3));
    }
}

