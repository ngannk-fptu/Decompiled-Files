/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;

public final class core$weeks
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Weeks.weeks(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$weeks.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.weeks();
    }

    @Override
    public Object invoke() {
        return core$weeks.invokeStatic();
    }
}

