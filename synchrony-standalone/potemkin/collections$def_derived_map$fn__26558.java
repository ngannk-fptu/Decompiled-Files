/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class collections$def_derived_map$fn__26558
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final Var const__3 = RT.var("clojure.core", "symbol");
    public static final Var const__4 = RT.var("clojure.core", "str");
    public static final AFn const__5 = Symbol.intern(null, "this#__26534__auto__");

    @Override
    public Object invoke(Object m4) {
        Object object = m4;
        m4 = null;
        collections$def_derived_map$fn__26558 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(".", object))), ((IFn)const__2.getRawRoot()).invoke(const__5)));
    }
}

