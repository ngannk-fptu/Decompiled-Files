/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import potemkin.types$deftype_STAR___GT_fn_map$fn__GT_key__26162$fn__26163;

public final class types$deftype_STAR___GT_fn_map$fn__GT_key__26162
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "first");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("clojure.core", "second");

    @Override
    public Object invoke(Object f) {
        Object object = ((IFn)const__0.getRawRoot()).invoke(f);
        Object object2 = f;
        f = null;
        return Tuple.create(object, ((IFn)const__1.getRawRoot()).invoke(new types$deftype_STAR___GT_fn_map$fn__GT_key__26162$fn__26163(), ((IFn)const__2.getRawRoot()).invoke(object2)));
    }
}

