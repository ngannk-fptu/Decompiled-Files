/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public final class core$millis
extends AFunction {
    public static Object invokeStatic(Object n) {
        Object object = n;
        n = null;
        return Period.millis(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$millis.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return PeriodType.millis();
    }

    @Override
    public Object invoke() {
        return core$millis.invokeStatic();
    }
}

