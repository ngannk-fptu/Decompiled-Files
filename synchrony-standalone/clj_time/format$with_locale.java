/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import java.util.Locale;
import org.joda.time.format.DateTimeFormatter;

public final class format$with_locale
extends AFunction {
    public static Object invokeStatic(Object f, Object l) {
        Object object = f;
        f = null;
        Object object2 = l;
        l = null;
        return ((DateTimeFormatter)object).withLocale((Locale)object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return format$with_locale.invokeStatic(object3, object4);
    }
}

