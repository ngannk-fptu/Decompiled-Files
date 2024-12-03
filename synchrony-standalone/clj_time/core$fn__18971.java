/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.LocalDateTime;

public final class core$fn__18971
extends AFunction {
    public static Object invokeStatic(Object this_) {
        Object object = this_;
        this_ = null;
        return ((LocalDateTime)object).dayOfMonth().withMinimumValue();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__18971.invokeStatic(object2);
    }
}

