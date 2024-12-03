/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.macros$normalize_gensyms$fn__26079;
import potemkin.macros$normalize_gensyms$gensym_STAR___26077;

public final class macros$normalize_gensyms
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "atom");
    public static final Object const__1 = 0L;
    public static final Var const__2 = RT.var("potemkin.walk", "postwalk");

    public static Object invokeStatic(Object body) {
        macros$normalize_gensyms$gensym_STAR___26077 gensym_STAR_;
        Object cnt;
        Object object = cnt = ((IFn)const__0.getRawRoot()).invoke(const__1);
        cnt = null;
        macros$normalize_gensyms$gensym_STAR___26077 macros$normalize_gensyms$gensym_STAR___26077 = gensym_STAR_ = new macros$normalize_gensyms$gensym_STAR___26077(object);
        gensym_STAR_ = null;
        Object object2 = body;
        body = null;
        return ((IFn)const__2.getRawRoot()).invoke(new macros$normalize_gensyms$fn__26079(macros$normalize_gensyms$gensym_STAR___26077), object2);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return macros$normalize_gensyms.invokeStatic(object2);
    }
}

