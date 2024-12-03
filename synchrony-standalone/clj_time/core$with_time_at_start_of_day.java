/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTime;

public final class core$with_time_at_start_of_day
extends AFunction {
    public static Object invokeStatic(Object dt2) {
        Object object = dt2;
        dt2 = null;
        return ((DateTime)object).withTimeAtStartOfDay();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$with_time_at_start_of_day.invokeStatic(object2);
    }
}

