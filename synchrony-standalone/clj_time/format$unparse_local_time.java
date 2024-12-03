/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;

public final class format$unparse_local_time
extends AFunction {
    public static Object invokeStatic(Object fmt, Object lt) {
        Object object = fmt;
        fmt = null;
        Object object2 = lt;
        lt = null;
        return ((DateTimeFormatter)object).print((ReadablePartial)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$unparse_local_time.invokeStatic(object3, object4);
    }
}

