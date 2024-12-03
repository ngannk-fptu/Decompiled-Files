/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Months;

public final class core$months_QMARK_
extends AFunction {
    public static Object invokeStatic(Object val2) {
        Object object = val2;
        val2 = null;
        return object instanceof Months ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$months_QMARK_.invokeStatic(object2);
    }
}

