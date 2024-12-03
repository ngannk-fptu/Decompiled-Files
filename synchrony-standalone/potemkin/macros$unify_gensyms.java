/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.macros$unify_gensyms$fn__26072;

public final class macros$unify_gensyms
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "memoize");
    public static final Var const__1 = RT.var("clojure.core", "gensym");
    public static final Var const__2 = RT.var("potemkin.walk", "postwalk");

    public static Object invokeStatic(Object body) {
        Object gensym_STAR_;
        Object object = gensym_STAR_ = ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot());
        gensym_STAR_ = null;
        Object object2 = body;
        body = null;
        return ((IFn)const__2.getRawRoot()).invoke(new macros$unify_gensyms$fn__26072(object), object2);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return macros$unify_gensyms.invokeStatic(object2);
    }
}

