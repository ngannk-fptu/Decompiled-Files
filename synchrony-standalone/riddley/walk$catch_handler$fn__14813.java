/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class walk$catch_handler$fn__14813
extends AFunction {
    Object f;
    Object type;
    Object body;
    Object var;
    public static final Var const__0 = RT.var("riddley.compiler", "register-arg");
    public static final Var const__1 = RT.var("clojure.core", "with-meta");
    public static final Var const__2 = RT.var("clojure.core", "merge");
    public static final Var const__3 = RT.var("clojure.core", "meta");
    public static final Keyword const__4 = RT.keyword(null, "tag");
    public static final Var const__5 = RT.var("clojure.core", "list*");
    public static final AFn const__6 = Symbol.intern(null, "catch");
    public static final Var const__7 = RT.var("clojure.core", "doall");
    public static final Var const__8 = RT.var("clojure.core", "map");

    public walk$catch_handler$fn__14813(Object object, Object object2, Object object3, Object object4) {
        this.f = object;
        this.type = object2;
        this.body = object3;
        this.var = object4;
    }

    @Override
    public Object invoke() {
        Object object = this_.var;
        if (object != null && object != Boolean.FALSE) {
            ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(this_.var, ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(this_.var), RT.mapUniqueKeys(const__4, this_.type))));
        }
        walk$catch_handler$fn__14813 this_ = null;
        return ((IFn)const__5.getRawRoot()).invoke(const__6, this_.type, this_.var, ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(this_.f, this_.body)));
    }
}

