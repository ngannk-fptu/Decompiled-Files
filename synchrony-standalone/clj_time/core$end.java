/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.base.AbstractInterval;

public final class core$end
extends AFunction {
    public static Object invokeStatic(Object in2) {
        Object object = in2;
        in2 = null;
        return ((AbstractInterval)object).getEnd();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$end.invokeStatic(object2);
    }
}

