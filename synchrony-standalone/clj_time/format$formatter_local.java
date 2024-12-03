/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.format.DateTimeFormat;

public final class format$formatter_local
extends AFunction {
    public static Object invokeStatic(Object fmt) {
        Object object = fmt;
        fmt = null;
        return DateTimeFormat.forPattern((String)object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$formatter_local.invokeStatic(object2);
    }
}

