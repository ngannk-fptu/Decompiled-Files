/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

public final class format$with_zone
extends AFunction {
    public static Object invokeStatic(Object f, Object dtz) {
        Object object = f;
        f = null;
        Object object2 = dtz;
        dtz = null;
        return ((DateTimeFormatter)object).withZone((DateTimeZone)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$with_zone.invokeStatic(object3, object4);
    }
}

