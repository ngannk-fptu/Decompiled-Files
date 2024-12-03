/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class utils$re_nil__26274
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("clojure.core", "let");
    public static final Var const__4 = RT.var("clojure.core", "apply");
    public static final Var const__5 = RT.var("clojure.core", "vector");
    public static final AFn const__6 = Symbol.intern(null, "x__26267__auto__");
    public static final AFn const__7 = Symbol.intern(null, "if");
    public static final AFn const__8 = Symbol.intern("clojure.core", "identical?");
    public static final Keyword const__9 = RT.keyword("potemkin.utils", "nil");

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__6), ((IFn)const__2.getRawRoot()).invoke(object))))), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__7), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__8), ((IFn)const__2.getRawRoot()).invoke(const__9), ((IFn)const__2.getRawRoot()).invoke(const__6)))), ((IFn)const__2.getRawRoot()).invoke(null), ((IFn)const__2.getRawRoot()).invoke(const__6))))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$re_nil__26274.invokeStatic(object2);
    }
}

