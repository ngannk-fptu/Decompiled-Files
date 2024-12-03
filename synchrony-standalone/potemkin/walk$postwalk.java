/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$postwalk
extends AFunction {
    public static final Var const__0 = RT.var("potemkin.walk", "walk");
    public static final Var const__1 = RT.var("clojure.core", "partial");
    public static final Var const__2 = RT.var("potemkin.walk", "postwalk");

    public static Object invokeStatic(Object f, Object form2) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), f);
        Object object2 = f;
        f = null;
        Object object3 = form2;
        form2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$postwalk.invokeStatic(object3, object4);
    }
}

