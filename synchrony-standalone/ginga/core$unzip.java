/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$unzip
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "apply");
    public static final Var const__2 = RT.var("clojure.core", "map");
    public static final Var const__3 = RT.var("clojure.core", "vector");

    public static Object invokeStatic(Object s2) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(s2);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = s2;
            s2 = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), const__3.getRawRoot(), object3);
        } else {
            object = PersistentVector.EMPTY;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$unzip.invokeStatic(object2);
    }
}

