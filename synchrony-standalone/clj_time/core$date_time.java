/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class core$date_time
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "date-time");
    public static final Object const__1 = 1L;
    public static final Object const__2 = 0L;
    public static final Var const__3 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic(Object year, Object month, Object day, Object hour, Object minute, Object second, Object millis2) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        Object object4 = hour;
        hour = null;
        Object object5 = minute;
        minute = null;
        Object object6 = second;
        second = null;
        Object object7 = millis2;
        millis2 = null;
        return new DateTime(RT.intCast((Number)object), RT.intCast((Number)object2), RT.intCast((Number)object3), RT.intCast((Number)object4), RT.intCast((Number)object5), RT.intCast((Number)object6), RT.intCast((Number)object7), (DateTimeZone)const__3.getRawRoot());
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        Object object8 = object;
        object = null;
        Object object9 = object2;
        object2 = null;
        Object object10 = object3;
        object3 = null;
        Object object11 = object4;
        object4 = null;
        Object object12 = object5;
        object5 = null;
        Object object13 = object6;
        object6 = null;
        Object object14 = object7;
        object7 = null;
        return core$date_time.invokeStatic(object8, object9, object10, object11, object12, object13, object14);
    }

    public static Object invokeStatic(Object year, Object month, Object day, Object hour, Object minute, Object second) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        Object object4 = hour;
        hour = null;
        Object object5 = minute;
        minute = null;
        Object object6 = second;
        second = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, object4, object5, object6, const__2);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        Object object7 = object;
        object = null;
        Object object8 = object2;
        object2 = null;
        Object object9 = object3;
        object3 = null;
        Object object10 = object4;
        object4 = null;
        Object object11 = object5;
        object5 = null;
        Object object12 = object6;
        object6 = null;
        return core$date_time.invokeStatic(object7, object8, object9, object10, object11, object12);
    }

    public static Object invokeStatic(Object year, Object month, Object day, Object hour, Object minute) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        Object object4 = hour;
        hour = null;
        Object object5 = minute;
        minute = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, object4, object5, const__2, const__2);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        Object object10 = object5;
        object5 = null;
        return core$date_time.invokeStatic(object6, object7, object8, object9, object10);
    }

    public static Object invokeStatic(Object year, Object month, Object day, Object hour) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        Object object4 = hour;
        hour = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, object4, const__2, const__2, const__2);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return core$date_time.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object year, Object month, Object day) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        Object object3 = day;
        day = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, const__2, const__2, const__2, const__2);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$date_time.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object year, Object month) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, const__1, const__2, const__2, const__2, const__2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$date_time.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object year) {
        Object object = year;
        year = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1, const__1, const__2, const__2, const__2, const__2);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$date_time.invokeStatic(object2);
    }
}

