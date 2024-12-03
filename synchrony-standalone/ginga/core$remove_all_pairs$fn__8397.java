/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import clojure.lang.Tuple;

public final class core$remove_all_pairs$fn__8397
extends AFunction {
    Object pair_QMARK_;

    public core$remove_all_pairs$fn__8397(Object object) {
        this.pair_QMARK_ = object;
    }

    @Override
    public Object invoke(Object p1__8395_SHARP_, Object p2__8396_SHARP_) {
        IPersistentVector iPersistentVector;
        Object object = ((IFn)this.pair_QMARK_).invoke(p1__8395_SHARP_, p2__8396_SHARP_);
        if (object != null && object != Boolean.FALSE) {
            iPersistentVector = PersistentVector.EMPTY;
        } else {
            Object object2 = p1__8395_SHARP_;
            p1__8395_SHARP_ = null;
            Object object3 = p2__8396_SHARP_;
            p2__8396_SHARP_ = null;
            iPersistentVector = Tuple.create(object2, object3);
        }
        return iPersistentVector;
    }
}

