/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.ReadablePeriod;

public final class core$fn__19096
extends AFunction {
    public static Object invokeStatic(Object this_) {
        Object object = this_;
        this_ = null;
        return ((ReadablePeriod)object).toPeriod().toStandardWeeks().getWeeks();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19096.invokeStatic(object2);
    }
}

