/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.LocalTime;

public final class core$time_now
extends AFunction {
    public static Object invokeStatic() {
        return new LocalTime();
    }

    @Override
    public Object invoke() {
        return core$time_now.invokeStatic();
    }
}

