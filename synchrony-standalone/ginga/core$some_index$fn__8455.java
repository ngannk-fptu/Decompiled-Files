/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;

public final class core$some_index$fn__8455
extends AFunction {
    Object pred_QMARK_;

    public core$some_index$fn__8455(Object object) {
        this.pred_QMARK_ = object;
    }

    @Override
    public Object invoke(Object p1__8454_SHARP_, Object p2__8453_SHARP_) {
        Object object;
        Object object2 = p2__8453_SHARP_;
        p2__8453_SHARP_ = null;
        Object object3 = ((IFn)this.pred_QMARK_).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            object = p1__8454_SHARP_;
            p1__8454_SHARP_ = null;
        } else {
            object = null;
        }
        return object;
    }
}

