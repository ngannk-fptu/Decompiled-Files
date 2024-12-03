/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.DateTimeZone;

public final class core$available_ids
extends AFunction {
    public static Object invokeStatic() {
        return DateTimeZone.getAvailableIDs();
    }

    @Override
    public Object invoke() {
        return core$available_ids.invokeStatic();
    }
}

