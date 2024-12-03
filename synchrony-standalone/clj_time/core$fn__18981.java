/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.ReadablePartial;
import org.joda.time.base.AbstractPartial;

public final class core$fn__18981
extends AFunction {
    public static Object invokeStatic(Object this_, Object that) {
        Object object = this_;
        this_ = null;
        Object object2 = that;
        that = null;
        return ((AbstractPartial)object).isEqual((ReadablePartial)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$fn__18981.invokeStatic(object3, object4);
    }
}

