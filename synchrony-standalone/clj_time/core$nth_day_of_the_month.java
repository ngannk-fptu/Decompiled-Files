/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$nth_day_of_the_month
extends AFunction
implements IFn.LLLO,
IFn.OLO {
    public static final Var const__0 = RT.var("clj-time.core", "nth-day-of-the-month");
    public static final Var const__1 = RT.var("clj-time.core", "date-time");
    public static final Var const__2 = RT.var("clj-time.core", "plus");
    public static final Var const__3 = RT.var("clj-time.core", "first-day-of-the-month");
    public static final Var const__4 = RT.var("clj-time.core", "days");

    public static Object invokeStatic(long year, long n, long l) {
        return ((IFn.OLO)const__0.getRawRoot()).invokePrim(((IFn)const__1.getRawRoot()).invoke(Numbers.num(year), Numbers.num(n)), l);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        return core$nth_day_of_the_month.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2), RT.longCast((Number)object3));
    }

    @Override
    public final Object invokePrim(long l, long l2, long l3) {
        return core$nth_day_of_the_month.invokeStatic(l, l2, l3);
    }

    public static Object invokeStatic(Object dt2, long n) {
        Object object = dt2;
        dt2 = null;
        return ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object), ((IFn)const__4.getRawRoot()).invoke(Numbers.num(Numbers.minus(n, 1L))));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        return core$nth_day_of_the_month.invokeStatic(object3, RT.longCast((Number)object2));
    }

    @Override
    public final Object invokePrim(Object object, long l) {
        Object object2 = object;
        object = null;
        return core$nth_day_of_the_month.invokeStatic(object2, l);
    }
}

