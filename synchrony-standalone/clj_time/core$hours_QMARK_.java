/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Hours;

public final class core$hours_QMARK_
extends AFunction {
    public static Object invokeStatic(Object val2) {
        Object object = val2;
        val2 = null;
        return object instanceof Hours ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$hours_QMARK_.invokeStatic(object2);
    }
}

