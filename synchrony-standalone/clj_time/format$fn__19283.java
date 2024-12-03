/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.base.AbstractDateTime;

public final class format$fn__19283
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.format", "to-map");

    public static Object invokeStatic(Object dt2) {
        Integer n = ((AbstractDateTime)dt2).getYear();
        Integer n2 = ((AbstractDateTime)dt2).getMonthOfYear();
        Integer n3 = ((AbstractDateTime)dt2).getDayOfMonth();
        Integer n4 = ((AbstractDateTime)dt2).getHourOfDay();
        Integer n5 = ((AbstractDateTime)dt2).getMinuteOfHour();
        Object object = dt2;
        dt2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(n, n2, n3, n4, n5, ((AbstractDateTime)object).getSecondOfMinute());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$fn__19283.invokeStatic(object2);
    }
}

