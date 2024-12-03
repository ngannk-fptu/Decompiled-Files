/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Interval;
import org.joda.time.ReadableInterval;

public final class core$abuts_QMARK_
extends AFunction {
    public static Object invokeStatic(Object i_a, Object i_b) {
        Object object = i_a;
        i_a = null;
        Object object2 = i_b;
        i_b = null;
        return ((Interval)object).abuts((ReadableInterval)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$abuts_QMARK_.invokeStatic(object3, object4);
    }
}

