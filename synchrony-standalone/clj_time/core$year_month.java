/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.YearMonth;

public final class core$year_month
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "year-month");
    public static final Object const__1 = 1L;

    public static Object invokeStatic(Object year, Object month) {
        Object object = year;
        year = null;
        Object object2 = month;
        month = null;
        return new YearMonth(RT.intCast((Number)object), RT.intCast((Number)object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$year_month.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object year) {
        Object object = year;
        year = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$year_month.invokeStatic(object2);
    }
}

