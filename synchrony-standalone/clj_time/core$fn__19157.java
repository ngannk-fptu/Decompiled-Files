/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.Numbers;
import org.joda.time.ReadableInstant;

public final class core$fn__19157
extends AFunction {
    public static Object invokeStatic(Object inst) {
        Object object = inst;
        inst = null;
        return Numbers.num(((ReadableInstant)object).getMillis());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19157.invokeStatic(object2);
    }
}

