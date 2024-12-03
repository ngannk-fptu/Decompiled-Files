/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.DateTimeZone;

public final class core$time_zone_for_offset
extends AFunction {
    public static Object invokeStatic(Object hours2, Object minutes2) {
        Object object = hours2;
        hours2 = null;
        Object object2 = minutes2;
        minutes2 = null;
        return DateTimeZone.forOffsetHoursMinutes(RT.intCast((Number)object), RT.intCast((Number)object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$time_zone_for_offset.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object hours2) {
        Object object = hours2;
        hours2 = null;
        return DateTimeZone.forOffsetHours(RT.intCast((Number)object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$time_zone_for_offset.invokeStatic(object2);
    }
}

