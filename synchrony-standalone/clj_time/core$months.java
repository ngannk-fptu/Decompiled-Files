/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.Months;
import org.joda.time.PeriodType;

public final class core$months
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Months.months(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$months.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.months();
    }

    @Override
    public Object invoke() {
        return core$months.invokeStatic();
    }
}

