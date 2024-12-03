/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTimeZone;

public final class core$time_zone_for_id
extends AFunction {
    public static Object invokeStatic(Object id2) {
        Object object = id2;
        id2 = null;
        return DateTimeZone.forID((String)object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$time_zone_for_id.invokeStatic(object2);
    }
}

