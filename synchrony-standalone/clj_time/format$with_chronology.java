/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import org.joda.time.Chronology;
import org.joda.time.format.DateTimeFormatter;

public final class format$with_chronology
extends AFunction {
    public static Object invokeStatic(Object f, Object c) {
        Object object = f;
        f = null;
        Object object2 = c;
        c = null;
        return ((DateTimeFormatter)object).withChronology((Chronology)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$with_chronology.invokeStatic(object3, object4);
    }
}

