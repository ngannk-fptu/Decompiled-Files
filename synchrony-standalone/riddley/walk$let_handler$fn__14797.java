/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$let_handler$fn__14797
extends AFunction {
    Object x;
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "doall");
    public static final Var const__1 = RT.var("clojure.core", "list*");
    public static final Var const__2 = RT.var("clojure.core", "first");
    public static final Var const__3 = RT.var("riddley.walk", "let-bindings");
    public static final Var const__4 = RT.var("clojure.core", "second");
    public static final Var const__5 = RT.var("clojure.core", "map");
    public static final Var const__6 = RT.var("clojure.core", "drop");
    public static final Object const__7 = 2L;

    public walk$let_handler$fn__14797(Object object, Object object2) {
        this.x = object;
        this.f = object2;
    }

    @Override
    public Object invoke() {
        walk$let_handler$fn__14797 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(this_.x), ((IFn)const__3.getRawRoot()).invoke(this_.f, ((IFn)const__4.getRawRoot()).invoke(this_.x)), ((IFn)const__5.getRawRoot()).invoke(this_.f, ((IFn)const__6.getRawRoot()).invoke(const__7, this_.x))));
    }
}

