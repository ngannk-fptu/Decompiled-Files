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

public final class core$first_day_of_the_month
extends AFunction
implements IFn.LLO {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    public static final Var const__0;
    public static final Var const__1;

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(long year, long l) {
        Object object;
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(Numbers.num(year), Numbers.num(l));
        if (Util.classOf(object2) != __cached_class__0) {
            if (object2 instanceof DateTimeProtocol) {
                object = ((DateTimeProtocol)object2).first_day_of_the_month_();
                return object;
            }
            object2 = object2;
            __cached_class__0 = Util.classOf(object2);
        }
        object = const__0.getRawRoot().invoke(object2);
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        return core$first_day_of_the_month.invokeStatic(RT.longCast((Number)object), RT.longCast((Number)object2));
    }

    @Override
    public final Object invokePrim(long l, long l2) {
        return core$first_day_of_the_month.invokeStatic(l, l2);
    }

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object dt2) {
        Object object;
        Object object2 = dt2;
        dt2 = null;
        Object object3 = object2;
        if (Util.classOf(object2) != __cached_class__1) {
            if (object3 instanceof DateTimeProtocol) {
                object = ((DateTimeProtocol)object3).first_day_of_the_month_();
                return object;
            }
            object3 = object3;
            __cached_class__1 = Util.classOf(object3);
        }
        object = const__0.getRawRoot().invoke(object3);
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$first_day_of_the_month.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("clj-time.core", "first-day-of-the-month-");
        const__1 = RT.var("clj-time.core", "date-time");
    }
}

