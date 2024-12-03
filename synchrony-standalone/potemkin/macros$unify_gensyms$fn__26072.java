/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class macros$unify_gensyms$fn__26072
extends AFunction {
    Object gensym_STAR_;
    public static final Var const__0 = RT.var("potemkin.macros", "unified-gensym?");
    public static final Var const__1 = RT.var("clojure.core", "symbol");
    public static final Var const__2 = RT.var("clojure.core", "str");
    public static final Var const__3 = RT.var("potemkin.macros", "un-gensym");

    public macros$unify_gensyms$fn__26072(Object object) {
        this.gensym_STAR_ = object;
    }

    @Override
    public Object invoke(Object p1__26071_SHARP_) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(p1__26071_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = p1__26071_SHARP_;
            p1__26071_SHARP_ = null;
            macros$unify_gensyms$fn__26072 this_ = null;
            object = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)this_.gensym_STAR_).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object3), "__")), "__auto__"));
        } else {
            object = p1__26071_SHARP_;
            Object var1_1 = null;
        }
        return object;
    }
}

