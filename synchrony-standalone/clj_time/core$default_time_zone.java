/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTimeZone;

public final class core$default_time_zone
extends AFunction {
    public static Object invokeStatic() {
        return DateTimeZone.getDefault();
    }

    @Override
    public Object invoke() {
        return core$default_time_zone.invokeStatic();
    }
}

