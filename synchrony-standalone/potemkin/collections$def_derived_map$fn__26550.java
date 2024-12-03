/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$def_derived_map$fn__26550
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "list");

    @Override
    public Object invoke(Object p1__26529_SHARP_) {
        Object object = p1__26529_SHARP_;
        p1__26529_SHARP_ = null;
        collections$def_derived_map$fn__26550 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, PersistentVector.EMPTY);
    }
}

