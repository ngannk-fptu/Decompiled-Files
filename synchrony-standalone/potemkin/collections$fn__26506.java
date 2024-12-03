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
import potemkin.collections$fn__26506$fn__26507;

public final class collections$fn__26506
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "apply");
    public static final Var const__1 = RT.var("clojure.core", "vector");
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("clojure.core", "concat");
    public static final Var const__4 = RT.var("clojure.core", "list");
    public static final AFn const__5 = Symbol.intern(null, ".invoke");
    public static final Var const__6 = RT.var("clojure.core", "with-meta");
    public static final AFn const__7 = Symbol.intern(null, "this#__26499__auto__");
    public static final AFn const__9 = (AFn)((Object)RT.map(RT.keyword(null, "tag"), "clojure.lang.IFn"));
    public static final Var const__10 = RT.var("clojure.core", "map");
    public static final Var const__11 = RT.var("clojure.core", "range");

    public static Object invokeStatic(Object n) {
        Object object = ((IFn)const__4.getRawRoot()).invoke(n);
        Object object2 = n;
        n = null;
        return ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object, ((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7, const__9)), ((IFn)const__10.getRawRoot()).invoke(new collections$fn__26506$fn__26507(), ((IFn)const__11.getRawRoot()).invoke(object2))))))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return collections$fn__26506.invokeStatic(object2);
    }
}

