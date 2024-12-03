/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractInstant;

public final class core$fn__18889
extends AFunction {
    public static Object invokeStatic(Object this_, Object that) {
        Object object = this_;
        this_ = null;
        Object object2 = that;
        that = null;
        return ((AbstractInstant)object).isBefore((ReadableInstant)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$fn__18889.invokeStatic(object3, object4);
    }
}

