/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.LocalDateTime;

public final class core$fn__18975
extends AFunction {
    public static Object invokeStatic(Object this_) {
        Object object = this_;
        this_ = null;
        return ((LocalDateTime)object).getMinuteOfHour();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__18975.invokeStatic(object2);
    }
}

