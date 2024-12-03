/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$reify_map_type$fn__26523
extends AFunction {
    Object elide_QMARK_;
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Var const__1 = RT.var("clojure.core", "first");

    public collections$reify_map_type$fn__26523(Object object) {
        this.elide_QMARK_ = object;
    }

    @Override
    public Object invoke(Object p1__26522_SHARP_) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(p1__26522_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = p1__26522_SHARP_;
            p1__26522_SHARP_ = null;
            collections$reify_map_type$fn__26523 this_ = null;
            object = ((IFn)this_.elide_QMARK_).invoke(((IFn)const__1.getRawRoot()).invoke(object3));
        } else {
            object = Boolean.FALSE;
        }
        return object;
    }
}

