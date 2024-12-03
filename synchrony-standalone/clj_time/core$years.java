/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.PeriodType;
import org.joda.time.Years;

public final class core$years
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Years.years(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$years.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.years();
    }

    @Override
    public Object invoke() {
        return core$years.invokeStatic();
    }
}

