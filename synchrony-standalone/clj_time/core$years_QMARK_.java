/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Years;

public final class core$years_QMARK_
extends AFunction {
    public static Object invokeStatic(Object val2) {
        Object object = val2;
        val2 = null;
        return object instanceof Years ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$years_QMARK_.invokeStatic(object2);
    }
}

