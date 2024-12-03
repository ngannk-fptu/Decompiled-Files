/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

public final class format$fn__19174
extends AFunction {
    public static final Var const__3 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic(Object p__19173) {
        Object object = p__19173;
        p__19173 = null;
        Object vec__19175 = object;
        Object k = RT.nth(vec__19175, RT.intCast(0L), null);
        Object object2 = vec__19175;
        vec__19175 = null;
        Object f = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = k;
        k = null;
        Object object4 = f;
        f = null;
        return Tuple.create(object3, ((DateTimeFormatter)object4).withZone((DateTimeZone)const__3.getRawRoot()));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$fn__19174.invokeStatic(object2);
    }
}

