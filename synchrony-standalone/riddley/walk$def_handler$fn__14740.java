/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class walk$def_handler$fn__14740
extends AFunction {
    Object n;
    Object r;
    Object f;
    public static final Var const__0 = RT.var("riddley.compiler", "register-local");
    public static final Var const__1 = RT.var("clojure.core", "list*");
    public static final AFn const__2 = Symbol.intern(null, "def");
    public static final Var const__3 = RT.var("clojure.core", "doall");
    public static final Var const__4 = RT.var("clojure.core", "map");

    public walk$def_handler$fn__14740(Object object, Object object2, Object object3) {
        this.n = object;
        this.r = object2;
        this.f = object3;
    }

    @Override
    public Object invoke() {
        ((IFn)const__0.getRawRoot()).invoke(this_.n, PersistentList.EMPTY);
        walk$def_handler$fn__14740 this_ = null;
        return ((IFn)const__1.getRawRoot()).invoke(const__2, ((IFn)this_.f).invoke(this_.n), ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(this_.f, this_.r)));
    }
}

