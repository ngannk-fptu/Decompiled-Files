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

public final class collections$def_derived_map$fn__26553
extends AFunction {
    public static final Var const__3 = RT.var("clojure.core", "list");
    public static final Var const__4 = RT.var("clojure.core", "apply");
    public static final Var const__5 = RT.var("clojure.core", "vector");
    public static final Var const__6 = RT.var("clojure.core", "seq");
    public static final Var const__7 = RT.var("clojure.core", "concat");
    public static final AFn const__8 = Symbol.intern(null, "___26533__auto__");

    @Override
    public Object invoke(Object p__26552) {
        Object object = p__26552;
        p__26552 = null;
        Object vec__26554 = object;
        Object name2 = RT.nth(vec__26554, RT.intCast(0L), null);
        Object object2 = vec__26554;
        vec__26554 = null;
        Object f = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = name2;
        name2 = null;
        Object object4 = f;
        f = null;
        collections$def_derived_map$fn__26553 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object3, ((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__8)))), object4);
    }
}

