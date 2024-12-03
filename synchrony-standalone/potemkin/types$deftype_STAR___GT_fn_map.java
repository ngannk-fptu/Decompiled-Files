/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$deftype_STAR___GT_fn_map$fn__GT_key__26162;

public final class types$deftype_STAR___GT_fn_map
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "drop");
    public static final Object const__1 = 6L;
    public static final Var const__2 = RT.var("clojure.core", "zipmap");
    public static final Var const__3 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object x) {
        types$deftype_STAR___GT_fn_map$fn__GT_key__26162 fn__GT_key;
        Object object = x;
        x = null;
        Object fns = ((IFn)const__0.getRawRoot()).invoke(const__1, object);
        types$deftype_STAR___GT_fn_map$fn__GT_key__26162 types$deftype_STAR___GT_fn_map$fn__GT_key__26162 = fn__GT_key = new types$deftype_STAR___GT_fn_map$fn__GT_key__26162();
        fn__GT_key = null;
        Object object2 = ((IFn)const__3.getRawRoot()).invoke(types$deftype_STAR___GT_fn_map$fn__GT_key__26162, fns);
        Object object3 = fns;
        fns = null;
        return ((IFn)const__2.getRawRoot()).invoke(object2, object3);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$deftype_STAR___GT_fn_map.invokeStatic(object2);
    }
}

