/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.LocalDate;

public final class core$today
extends AFunction {
    public static Object invokeStatic() {
        return new LocalDate();
    }

    @Override
    public Object invoke() {
        return core$today.invokeStatic();
    }
}

