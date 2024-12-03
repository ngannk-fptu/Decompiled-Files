/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.MutableDateTime;
import org.joda.time.base.AbstractInstant;

public final class core$today_at
extends AFunction
implements IFn.LLLLO,
IFn.LLLO,
IFn.LLO {
    public static final Var const__0 = RT.var("clj-time.core", "now");
    public static final Var const__1 = RT.var("clj-time.core", "today-at");

    public static Object invokeStatic(long hours2, long seconds2, long l, long l2) {
        MutableDateTime mdt;
        MutableDateTime mutableDateTime = mdt = ((AbstractInstant)((IFn)const__0.getRawRoot()).invoke()).toMutableDateTime();
        mdt = null;
        MutableDateTime G__19151 = mutableDateTime;
        G__19151.setHourOfDay(RT.intCast(hours2));
        G__19151.setMinuteOfHour(RT.intCast(seconds2));
        G__19151.setSecondOfMinute(RT.intCast(l));
        G__19151.setMillisOfSecond(RT.intCast(l2));
        MutableDateTime mutableDateTime2 = G__19151;
        G__19151 = null;
        return ((AbstractInstant)mutableDateTime2).toDateTime();
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        return core$today_at.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2), RT.longCast((Number)object3), RT.longCast((Number)object4));
    }

    @Override
    public final Object invokePrim(long l, long l2, long l3, long l4) {
        return core$today_at.invokeStatic(l, l2, l3, l4);
    }

    public static Object invokeStatic(long hours2, long seconds2, long l) {
        return ((IFn.LLLLO)const__1.getRawRoot()).invokePrim(hours2, seconds2, l, 0L);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        return core$today_at.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2), RT.longCast((Number)object3));
    }

    @Override
    public final Object invokePrim(long l, long l2, long l3) {
        return core$today_at.invokeStatic(l, l2, l3);
    }

    public static Object invokeStatic(long hours2, long l) {
        return ((IFn.LLLO)const__1.getRawRoot()).invokePrim(hours2, l, 0L);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        return core$today_at.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2));
    }

    @Override
    public final Object invokePrim(long l, long l2) {
        return core$today_at.invokeStatic(l, l2);
    }
}

