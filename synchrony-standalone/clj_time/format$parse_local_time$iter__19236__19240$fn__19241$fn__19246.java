/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$parse_local_time$iter__19236__19240$fn__19241$fn__19246
extends AFunction {
    Object f;
    Object s;
    public static final Var const__0 = RT.var("clj-time.format", "parse-local-time");

    public format$parse_local_time$iter__19236__19240$fn__19241$fn__19246(Object object, Object object2) {
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

