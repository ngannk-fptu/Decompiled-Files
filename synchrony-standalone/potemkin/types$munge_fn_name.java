/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$munge_fn_name$fn__26176;

public final class types$munge_fn_name
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "with-meta");
    public static final Var const__1 = RT.var("clojure.core", "symbol");
    public static final Var const__2 = RT.var("clojure.core", "reduce");
    public static final Var const__3 = RT.var("clojure.core", "name");
    public static final Var const__4 = RT.var("potemkin.types", "clojure-fn-subs");
    public static final Var const__5 = RT.var("clojure.core", "meta");

    public static Object invokeStatic(Object n) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(new types$munge_fn_name$fn__26176(), ((IFn)const__3.getRawRoot()).invoke(n), const__4.getRawRoot()));
        Object object2 = n;
        n = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__5.getRawRoot()).invoke(object2));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$munge_fn_name.invokeStatic(object2);
    }
}

