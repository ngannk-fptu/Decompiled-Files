/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.format.Mappable;
import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.base.AbstractInterval;

public final class format$fn__19287
extends AFunction {
    private static Class __cached_class__0;
    public static final Var const__0;

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object it) {
        Object object;
        Object object2 = it;
        it = null;
        Period period = ((AbstractInterval)object2).toPeriod(PeriodType.yearMonthDayTime());
        if (Util.classOf(period) != __cached_class__0) {
            if (period instanceof Mappable) {
                object = ((Mappable)((Object)period)).instant__GT_map();
                return object;
            }
            period = period;
            __cached_class__0 = Util.classOf(period);
        }
        object = const__0.getRawRoot().invoke(period);
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$fn__19287.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("clj-time.format", "instant->map");
    }
}

