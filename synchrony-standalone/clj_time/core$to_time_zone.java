/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class core$to_time_zone
extends AFunction {
    public static Object invokeStatic(Object dt2, Object tz) {
        Object object = dt2;
        dt2 = null;
        Object object2 = tz;
        tz = null;
        return ((DateTime)object).withZone((DateTimeZone)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$to_time_zone.invokeStatic(object3, object4);
    }
}

