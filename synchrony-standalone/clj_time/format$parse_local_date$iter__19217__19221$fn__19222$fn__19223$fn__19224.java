/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local_date$iter__19217__19221$fn__19222$fn__19223$fn__19224
extends AFunction {
    Object f;
    Object s;
    public static final Var const__0 = RT.var("clj-time.format", "parse-local-date");

    public format$parse_local_date$iter__19217__19221$fn__19222$fn__19223$fn__19224(Object object, Object object2) {
        this.f = object;
        this.s = object2;
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

