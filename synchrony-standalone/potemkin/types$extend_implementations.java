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
import potemkin.types$extend_implementations$fn__26091;

public final class types$extend_implementations
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "deref");
    public static final Var const__1 = RT.var("clojure.core", "resolve");
    public static final Var const__2 = RT.var("clojure.core", "remove");
    public static final Var const__3 = RT.var("clojure.core", "eval");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "concat");
    public static final Var const__6 = RT.var("clojure.core", "list");
    public static final AFn const__7 = Symbol.intern("clojure.core", "extend-protocol");
    public static final Var const__8 = RT.var("clojure.core", "apply");
    public static final Var const__9 = RT.var("clojure.core", "interleave");
    public static final Var const__10 = RT.var("clojure.core", "map");
    public static final Var const__11 = RT.var("clojure.core", "repeat");

    public static Object invokeStatic(Object proto, Object impls, Object body) {
        Object proto_val;
        Object object = proto_val = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(proto));
        proto_val = null;
        Object object2 = impls;
        impls = null;
        Object impls2 = ((IFn)const__2.getRawRoot()).invoke(new types$extend_implementations$fn__26091(object), object2);
        Object object3 = proto;
        proto = null;
        Object object4 = impls2;
        impls2 = null;
        Object object5 = body;
        body = null;
        return ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7), ((IFn)const__6.getRawRoot()).invoke(object3), ((IFn)const__8.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__6.getRawRoot(), object4), ((IFn)const__11.getRawRoot()).invoke(object5))))));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return types$extend_implementations.invokeStatic(object4, object5, object6);
    }
}

