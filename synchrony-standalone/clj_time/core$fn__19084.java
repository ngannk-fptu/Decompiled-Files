/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.PeriodType;
import org.joda.time.base.AbstractInterval;

public final class core$fn__19084
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "years");

    public static Object invokeStatic(Object this_) {
        Object object = this_;
        this_ = null;
        return ((AbstractInterval)object).toPeriod((PeriodType)((IFn)const__0.getRawRoot()).invoke()).getYears();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19084.invokeStatic(object2);
    }
}

