/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.Hours;
import org.joda.time.PeriodType;

public final class core$hours
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Hours.hours(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$hours.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.hours();
    }

    @Override
    public Object invoke() {
        return core$hours.invokeStatic();
    }
}

