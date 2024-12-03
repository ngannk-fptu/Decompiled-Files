/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePeriod;

public final class core$fn__19029
extends AFunction {
    public static Object invokeStatic(Object this_, Object period) {
        Object object = this_;
        this_ = null;
        Object object2 = period;
        period = null;
        return ((LocalTime)object).minus((ReadablePeriod)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$fn__19029.invokeStatic(object3, object4);
    }
}

