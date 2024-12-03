/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$modify_alt_clauses$fn__8761;

public final class async$modify_alt_clauses
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "flatten1");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("clojure.core", "partition");
    public static final Object const__3 = 2L;

    public static Object invokeStatic(Object f, Object clauses) {
        Object object = f;
        f = null;
        Object object2 = clauses;
        clauses = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(new async$modify_alt_clauses$fn__8761(object), ((IFn)const__2.getRawRoot()).invoke(const__3, object2)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$modify_alt_clauses.invokeStatic(object3, object4);
    }
}

