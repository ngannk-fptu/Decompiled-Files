/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse$iter__19179__19183$fn__19184$fn__19189
extends AFunction {
    Object s;
    Object f;
    public static final Var const__0 = RT.var("clj-time.format", "parse");

    public format$parse$iter__19179__19183$fn__19184$fn__19189(Object object, Object object2) {
        this.s = object;
        this.f = object2;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            object = ((IFn)const__0.getRawRoot()).invoke(this.f, this.s);
        }
        catch (Exception _2) {
            object = null;
        }
        return object;
    }
}

