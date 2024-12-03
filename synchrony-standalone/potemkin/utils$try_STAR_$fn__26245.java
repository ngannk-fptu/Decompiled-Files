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

public final class utils$try_STAR_$fn__26245
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "not");
    public static final Var const__1 = RT.var("clojure.core", "sequential?");
    public static final AFn const__3 = Symbol.intern(null, "catch");
    public static final Var const__4 = RT.var("clojure.core", "first");

    @Override
    public Object invoke(Object p1__26244_SHARP_) {
        Object object;
        Object or__5581__auto__26247;
        Object object2 = or__5581__auto__26247 = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(p1__26244_SHARP_));
        if (object2 != null && object2 != Boolean.FALSE) {
            object = or__5581__auto__26247;
            or__5581__auto__26247 = null;
        } else {
            Object object3 = p1__26244_SHARP_;
            p1__26244_SHARP_ = null;
            utils$try_STAR_$fn__26245 this_ = null;
            object = ((IFn)const__0.getRawRoot()).invoke(Util.equiv((Object)const__3, ((IFn)const__4.getRawRoot()).invoke(object3)) ? Boolean.TRUE : Boolean.FALSE);
        }
        return object;
    }
}

