/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

public final class core$interval
extends AFunction {
    public static Object invokeStatic(Object dt_a, Object dt_b) {
        Object object = dt_a;
        dt_a = null;
        Object object2 = dt_b;
        dt_b = null;
        return new Interval((ReadableInstant)object, (ReadableInstant)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$interval.invokeStatic(object3, object4);
    }
}

