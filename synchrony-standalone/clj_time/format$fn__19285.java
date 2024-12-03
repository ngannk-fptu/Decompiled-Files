/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.Period;

public final class format$fn__19285
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.format", "to-map");

    public static Object invokeStatic(Object period) {
        Integer n = ((Period)period).getYears();
        Integer n2 = ((Period)period).getMonths();
        Integer n3 = ((Period)period).getDays();
        Integer n4 = ((Period)period).getHours();
        Integer n5 = ((Period)period).getMinutes();
        Object object = period;
        period = null;
        return ((IFn)const__0.getRawRoot()).invoke(n, n2, n3, n4, n5, ((Period)object).getSeconds());
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$fn__19285.invokeStatic(object2);
    }
}

