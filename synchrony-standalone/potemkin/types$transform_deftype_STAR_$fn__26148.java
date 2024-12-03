/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class types$transform_deftype_STAR_$fn__26148
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final AFn const__2 = Symbol.intern(null, "deftype*");
    public static final Var const__3 = RT.var("clojure.core", "first");

    @Override
    public Object invoke(Object p1__26147_SHARP_) {
        Object object;
        Object and__5579__auto__26150;
        Object object2 = and__5579__auto__26150 = ((IFn)const__0.getRawRoot()).invoke(p1__26147_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = p1__26147_SHARP_;
            p1__26147_SHARP_ = null;
            types$transform_deftype_STAR_$fn__26148 this_ = null;
            object = Util.equiv((Object)const__2, ((IFn)const__3.getRawRoot()).invoke(object3)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            object = and__5579__auto__26150;
            Object var2_2 = null;
        }
        return object;
    }
}

