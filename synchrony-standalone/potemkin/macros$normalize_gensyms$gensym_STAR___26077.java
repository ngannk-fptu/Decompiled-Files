/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class macros$normalize_gensyms$gensym_STAR___26077
extends AFunction {
    Object cnt;
    public static final Var const__0 = RT.var("clojure.core", "str");
    public static final Var const__1 = RT.var("clojure.core", "swap!");
    public static final Var const__2 = RT.var("clojure.core", "inc");

    public macros$normalize_gensyms$gensym_STAR___26077(Object object) {
        this.cnt = object;
    }

    @Override
    public Object invoke(Object p1__26075_SHARP_) {
        Object object = p1__26075_SHARP_;
        p1__26075_SHARP_ = null;
        macros$normalize_gensyms$gensym_STAR___26077 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, "__norm__", ((IFn)const__1.getRawRoot()).invoke(this_.cnt, const__2.getRawRoot()));
    }
}

