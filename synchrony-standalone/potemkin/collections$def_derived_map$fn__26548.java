/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$def_derived_map$fn__26548
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "symbol");
    public static final Var const__1 = RT.var("clojure.core", "str");

    @Override
    public Object invoke(Object p1__26528_SHARP_) {
        Object object = p1__26528_SHARP_;
        p1__26528_SHARP_ = null;
        collections$def_derived_map$fn__26548 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke("get__", object));
    }
}

