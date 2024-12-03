/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import riddley.walk$fn_handler$fn__14731$fn__14732;

public final class walk$fn_handler$fn__14731
extends AFunction {
    Object prelude;
    Object remainder;
    Object body_handler;
    public static final Var const__0 = RT.var("clojure.core", "second");
    public static final Var const__1 = RT.var("riddley.compiler", "register-local");
    public static final Var const__2 = RT.var("clojure.core", "list*");
    public static final AFn const__3 = Symbol.intern(null, "fn*");
    public static final Var const__4 = RT.var("clojure.core", "map");
    public static final Var const__5 = RT.var("clojure.core", "concat");
    public static final Var const__6 = RT.var("clojure.core", "seq?");
    public static final Var const__7 = RT.var("clojure.core", "first");
    public static final Var const__8 = RT.var("clojure.core", "doall");

    public walk$fn_handler$fn__14731(Object object, Object object2, Object object3) {
        this.prelude = object;
        this.remainder = object2;
        this.body_handler = object3;
    }

    @Override
    public Object invoke() {
        Object temp__5804__auto__14735;
        Object object = temp__5804__auto__14735 = ((IFn)const__0.getRawRoot()).invoke(this_.prelude);
        if (object != null && object != Boolean.FALSE) {
            Object nm;
            Object object2 = temp__5804__auto__14735;
            temp__5804__auto__14735 = null;
            Object object3 = nm = object2;
            Object object4 = nm;
            nm = null;
            ((IFn)const__1.getRawRoot()).invoke(object3, ((IFn)const__2.getRawRoot()).invoke(const__3, object4, ((IFn)const__4.getRawRoot()).invoke(new walk$fn_handler$fn__14731$fn__14732(), this_.remainder)));
        }
        Object object5 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(this_.remainder));
        walk$fn_handler$fn__14731 this_ = null;
        return ((IFn)const__5.getRawRoot()).invoke(this_.prelude, object5 != null && object5 != Boolean.FALSE ? ((IFn)const__8.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(this_.body_handler, this_.remainder)) : Tuple.create(((IFn)this_.body_handler).invoke(this_.remainder)));
    }
}

