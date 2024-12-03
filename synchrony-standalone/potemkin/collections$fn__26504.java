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

public final class collections$fn__26504
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern(null, "invoke");
    public static final Var const__4 = RT.var("clojure.core", "apply");
    public static final Var const__5 = RT.var("clojure.core", "vector");
    public static final AFn const__6 = Symbol.intern(null, "this__26498__auto__");
    public static final Var const__7 = RT.var("clojure.core", "repeat");
    public static final AFn const__8 = Symbol.intern(null, "_");
    public static final Var const__9 = RT.var("potemkin.collections", "throw-arity");

    public static Object invokeStatic(Object n) {
        Object object = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__6), ((IFn)const__7.getRawRoot()).invoke(n, const__8)))));
        Object object2 = n;
        n = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), object, ((IFn)const__2.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(object2))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return collections$fn__26504.invokeStatic(object2);
    }
}

