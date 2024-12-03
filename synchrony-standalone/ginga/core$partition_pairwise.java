/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$partition_pairwise$fn__8386;
import ginga.core$partition_pairwise$fn__8389;

public final class core$partition_pairwise
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "buffering-transducer");

    public static Object invokeStatic(Object pair_QMARK_) {
        Object object = pair_QMARK_;
        pair_QMARK_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(new core$partition_pairwise$fn__8386(object), new core$partition_pairwise$fn__8389(), PersistentVector.EMPTY);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$partition_pairwise.invokeStatic(object2);
    }
}

