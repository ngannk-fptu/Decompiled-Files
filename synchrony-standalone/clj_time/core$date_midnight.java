/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;

public final class core$date_midnight
extends AFunction
implements IFn.LLO {
    public static final Var const__0 = RT.var("clj-time.core", "date-midnight");
    public static final Object const__1 = 1L;
    public static final Var const__2 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic(Object year, Object month, Object day) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        return new DateMidnight(RT.intCast((Number)object), RT.intCast((Number)object2), RT.intCast((Number)object3), (DateTimeZone)const__2.getRawRoot());
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$date_midnight.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(long year, long l) {
        return ((IFn)const__0.getRawRoot()).invoke(Numbers.num(year), Numbers.num(l), const__1);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        return core$date_midnight.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2));
    }

    @Override
    public final Object invokePrim(long l, long l2) {
        return core$date_midnight.invokeStatic(l, l2);
    }

    public static Object invokeStatic(Object year) {
        Object object = year;
        year = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1, const__1);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$date_midnight.invokeStatic(object2);
    }
}

