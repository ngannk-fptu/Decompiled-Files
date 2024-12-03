/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$transform_deftype_STAR_$fn__26148;

public final class types$transform_deftype_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("riddley.walk", "walk-exprs");

    public static Object invokeStatic(Object f, Object x) {
        Object object = f;
        f = null;
        Object object2 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(new types$transform_deftype_STAR_$fn__26148(), object, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return types$transform_deftype_STAR_.invokeStatic(object3, object4);
    }
}

