/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;

public final class core$today_at_midnight
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic(Object tz) {
        Object object = tz;
        tz = null;
        return new DateMidnight((DateTimeZone)object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$today_at_midnight.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return new DateMidnight((DateTimeZone)const__0.getRawRoot());
    }

    @Override
    public Object invoke() {
        return core$today_at_midnight.invokeStatic();
    }
}

