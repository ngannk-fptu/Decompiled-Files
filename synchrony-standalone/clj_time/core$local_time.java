/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.LocalTime;

public final class core$local_time
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "local-time");
    public static final Object const__1 = 0L;

    public static Object invokeStatic(Object hour, Object minute, Object second, Object millis2) {
        Object object = hour;
        hour = null;
        Object object2 = minute;
        minute = null;
        Object object3 = second;
        second = null;
        Object object4 = millis2;
        millis2 = null;
        return new LocalTime(RT.intCast((Number)object), RT.intCast((Number)object2), RT.intCast((Number)object3), RT.intCast((Number)object4));
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
        return core$local_time.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object hour, Object minute, Object second) {
        Object object = hour;
        hour = null;
        Object object2 = minute;
        minute = null;
        Object object3 = second;
        second = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, const__1);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$local_time.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object hour, Object minute) {
        Object object = hour;
        hour = null;
        Object object2 = minute;
        minute = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, const__1, const__1);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$local_time.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object hour) {
        Object object = hour;
        hour = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1, const__1, const__1);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$local_time.invokeStatic(object2);
    }
}

