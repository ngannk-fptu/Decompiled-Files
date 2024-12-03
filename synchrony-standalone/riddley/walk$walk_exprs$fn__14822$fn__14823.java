/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$walk_exprs$fn__14822$fn__14823
extends AFunction {
    Object x;
    Object special_form_QMARK_;
    public static final Var const__0 = RT.var("riddley.walk", "macroexpand");

    public walk$walk_exprs$fn__14822$fn__14823(Object object, Object object2) {
        this.x = object;
        this.special_form_QMARK_ = object2;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            this.special_form_QMARK_ = null;
            object = ((IFn)const__0.getRawRoot()).invoke(this.x, this.special_form_QMARK_);
        }
        catch (ClassNotFoundException _2) {
            object = this.x = null;
        }
        return object;
    }
}

