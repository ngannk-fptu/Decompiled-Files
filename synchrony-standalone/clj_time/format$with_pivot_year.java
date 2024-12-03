/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import org.joda.time.format.DateTimeFormatter;

public final class format$with_pivot_year
extends AFunction {
    public static Object invokeStatic(Object f, Object pivot_year) {
        Object object = f;
        f = null;
        Object object2 = pivot_year;
        pivot_year = null;
        return ((DateTimeFormatter)object).withPivotYear(RT.intCast((Number)object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$with_pivot_year.invokeStatic(object3, object4);
    }
}

