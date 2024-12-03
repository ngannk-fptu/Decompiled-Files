/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTime;
import org.joda.time.ReadablePeriod;

public final class core$fn__18879
extends AFunction {
    public static Object invokeStatic(Object this_, Object period) {
        Object object = this_;
        this_ = null;
        Object object2 = period;
        period = null;
        return ((DateTime)object).plus((ReadablePeriod)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$fn__18879.invokeStatic(object3, object4);
    }
}

