/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class debug$debug
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("clojure.core", "let");
    public static final Var const__4 = RT.var("clojure.core", "apply");
    public static final Var const__5 = RT.var("clojure.core", "vector");
    public static final AFn const__6 = Symbol.intern(null, "x__19305__auto__");
    public static final AFn const__7 = Symbol.intern("clojure.core", "binding");
    public static final AFn const__8 = Symbol.intern("clojure.core", "*out*");
    public static final AFn const__9 = Symbol.intern("clojure.core", "or");
    public static final AFn const__10 = Symbol.intern("ginga.debug", "*out*");
    public static final AFn const__11 = Symbol.intern("clojure.core", "*out*");
    public static final AFn const__12 = Symbol.intern(null, "if");
    public static final AFn const__13 = Symbol.intern("ginga.debug", "*prn*");
    public static final AFn const__14 = Symbol.intern("clojure.core", "prn");
    public static final AFn const__15 = Symbol.intern("clojure.pprint", "pprint");
    public static final Var const__16 = RT.var("clojure.core", "hash-map");
    public static final Keyword const__17 = RT.keyword(null, "expression");
    public static final AFn const__18 = Symbol.intern(null, "quote");
    public static final Keyword const__19 = RT.keyword(null, "result");
    public static final AFn const__20 = Symbol.intern("clojure.core", "flush");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object x) {
        Object object = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__6), ((IFn)const__2.getRawRoot()).invoke(x)))));
        Object object2 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), object, ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__7), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__8), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__9), ((IFn)const__2.getRawRoot()).invoke(const__10), ((IFn)const__2.getRawRoot()).invoke(const__11)))))))), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__12), ((IFn)const__2.getRawRoot()).invoke(const__13), ((IFn)const__2.getRawRoot()).invoke(const__14), ((IFn)const__2.getRawRoot()).invoke(const__15)))), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__16.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__17), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__18), ((IFn)const__2.getRawRoot()).invoke(object2)))), ((IFn)const__2.getRawRoot()).invoke(const__19), ((IFn)const__2.getRawRoot()).invoke(const__6))))))))))), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__20)))), ((IFn)const__2.getRawRoot()).invoke(const__6)));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return debug$debug.invokeStatic(object4, object5, object6);
    }
}

