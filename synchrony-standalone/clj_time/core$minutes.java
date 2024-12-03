/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.Minutes;
import org.joda.time.PeriodType;

public final class core$minutes
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Minutes.minutes(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$minutes.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.minutes();
    }

    @Override
    public Object invoke() {
        return core$minutes.invokeStatic();
    }
}

