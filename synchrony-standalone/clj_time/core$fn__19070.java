/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.Numbers;
import org.joda.time.base.AbstractInterval;

public final class core$fn__19070
extends AFunction {
    public static Object invokeStatic(Object this_) {
        Object object = this_;
        this_ = null;
        return Numbers.num(((AbstractInterval)object).toDurationMillis());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19070.invokeStatic(object2);
    }
}

