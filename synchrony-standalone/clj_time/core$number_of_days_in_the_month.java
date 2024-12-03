/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.DateTimeProtocol;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$number_of_days_in_the_month
extends AFunction
implements IFn.OL,
IFn.LLL {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    private static Class __cached_class__2;
    private static Class __cached_class__3;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;

    /*
     * Unable to fully structure code
     */
    public static long invokeStatic(long year, long var2_1) {
        v0 = ((IFn)core$number_of_days_in_the_month.const__2.getRawRoot()).invoke(Numbers.num(year), Numbers.num(var2_1));
        if (Util.classOf(v0) == core$number_of_days_in_the_month.__cached_class__2) ** GOTO lbl6
        if (!(v0 instanceof DateTimeProtocol)) {
            v0 = v0;
            core$number_of_days_in_the_month.__cached_class__2 = Util.classOf(v0);
lbl6:
            // 2 sources

            v1 = core$number_of_days_in_the_month.const__1.getRawRoot().invoke(v0);
        } else {
            v1 = v2 = ((DateTimeProtocol)v0).last_day_of_the_month_();
        }
        if (Util.classOf(v1) == core$number_of_days_in_the_month.__cached_class__3) ** GOTO lbl13
        if (!(v2 instanceof DateTimeProtocol)) {
            v2 = v2;
            core$number_of_days_in_the_month.__cached_class__3 = Util.classOf(v2);
lbl13:
            // 2 sources

            v3 = core$number_of_days_in_the_month.const__0.getRawRoot().invoke(v2);
        } else {
            v3 = ((DateTimeProtocol)v2).day();
        }
        return ((Number)v3).longValue();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        return core$number_of_days_in_the_month.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2));
    }

    @Override
    public final long invokePrim(long l, long l2) {
        return core$number_of_days_in_the_month.invokeStatic(l, l2);
    }

    /*
     * Unable to fully structure code
     */
    public static long invokeStatic(Object dt) {
        v0 = dt;
        dt = null;
        v1 = v0;
        if (Util.classOf(v0) == core$number_of_days_in_the_month.__cached_class__0) ** GOTO lbl8
        if (!(v1 instanceof DateTimeProtocol)) {
            v1 = v1;
            core$number_of_days_in_the_month.__cached_class__0 = Util.classOf(v1);
lbl8:
            // 2 sources

            v2 = core$number_of_days_in_the_month.const__1.getRawRoot().invoke(v1);
        } else {
            v2 = v3 = ((DateTimeProtocol)v1).last_day_of_the_month_();
        }
        if (Util.classOf(v2) == core$number_of_days_in_the_month.__cached_class__1) ** GOTO lbl15
        if (!(v3 instanceof DateTimeProtocol)) {
            v3 = v3;
            core$number_of_days_in_the_month.__cached_class__1 = Util.classOf(v3);
lbl15:
            // 2 sources

            v4 = core$number_of_days_in_the_month.const__0.getRawRoot().invoke(v3);
        } else {
            v4 = ((DateTimeProtocol)v3).day();
        }
        return ((Number)v4).longValue();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$number_of_days_in_the_month.invokeStatic(object2);
    }

    @Override
    public final long invokePrim(Object object) {
        Object object2 = object;
        object = null;
        return core$number_of_days_in_the_month.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("clj-time.core", "day");
        const__1 = RT.var("clj-time.core", "last-day-of-the-month-");
        const__2 = RT.var("clj-time.core", "date-time");
    }
}

