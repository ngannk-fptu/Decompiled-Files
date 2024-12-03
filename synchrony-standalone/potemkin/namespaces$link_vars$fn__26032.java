/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class namespaces$link_vars$fn__26032
extends AFunction {
    Object dst;
    public static final Var const__0 = RT.var("clojure.core", "alter-var-root");
    public static final Var const__1 = RT.var("clojure.core", "constantly");
    public static final Var const__2 = RT.var("clojure.core", "deref");
    public static final Var const__3 = RT.var("clojure.core", "alter-meta!");
    public static final Var const__4 = RT.var("clojure.core", "merge");
    public static final Var const__5 = RT.var("clojure.core", "dissoc");
    public static final Var const__6 = RT.var("clojure.core", "meta");
    public static final Keyword const__7 = RT.keyword(null, "name");

    public namespaces$link_vars$fn__26032(Object object) {
        this.dst = object;
    }

    @Override
    public Object invoke(Object _2, Object src, Object old, Object object) {
        ((IFn)const__0.getRawRoot()).invoke(this_.dst, ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(src)));
        Object object2 = src;
        src = null;
        namespaces$link_vars$fn__26032 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(this_.dst, const__4.getRawRoot(), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(object2), const__7));
    }
}

