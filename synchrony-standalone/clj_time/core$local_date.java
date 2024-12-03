/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.LocalDate;

public final class core$local_date
extends AFunction {
    public static Object invokeStatic(Object year, Object month, Object day) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        return new LocalDate(RT.intCast((Number)object), RT.intCast((Number)object2), RT.intCast((Number)object3));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$local_date.invokeStatic(object4, object5, object6);
    }
}

