/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class utils$fast_bound_fn_STAR_$fn__26237
extends RestFn {
    Object f__26228__auto__;
    Object bound_frame__26227__auto__;
    public static final Var const__0 = RT.var("clojure.core", "apply");

    public utils$fast_bound_fn_STAR_$fn__26237(Object object, Object object2) {
        this.f__26228__auto__ = object;
        this.bound_frame__26227__auto__ = object2;
    }

    @Override
    public Object doInvoke(Object args__26229__auto__) {
        Object object;
        Object curr_frame__26230__auto__26239 = Var.getThreadBindingFrame();
        Var.resetThreadBindingFrame(this.bound_frame__26227__auto__);
        try {
            Object object2 = args__26229__auto__;
            args__26229__auto__ = null;
            object = ((IFn)const__0.getRawRoot()).invoke(this.f__26228__auto__, object2);
        }
        finally {
            Object object3 = curr_frame__26230__auto__26239;
            curr_frame__26230__auto__26239 = null;
            Var.resetThreadBindingFrame(object3);
        }
        return object;
    }

    @Override
    public int getRequiredArity() {
        return 0;
    }
}

