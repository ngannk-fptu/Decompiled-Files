/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.base.AbstractInterval;

public final class core$start
extends AFunction {
    public static Object invokeStatic(Object in2) {
        Object object = in2;
        in2 = null;
        return ((AbstractInterval)object).getStart();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$start.invokeStatic(object2);
    }
}

