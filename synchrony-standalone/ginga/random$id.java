/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class random$id
extends AFunction {
    public static final Var const__0 = RT.var("ginga.random", "make-id");
    public static final Var const__1 = RT.var("ginga.random", "secure-random");

    public static Object invokeStatic(Object size2) {
        Object object = size2;
        size2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return random$id.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot());
    }

    @Override
    public Object invoke() {
        return random$id.invokeStatic();
    }
}

