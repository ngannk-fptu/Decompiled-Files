/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.base.AbstractDateTime;

public final class core$fn__18897
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "deprecated");

    public static Object invokeStatic(Object this_) {
        ((IFn)const__0.getRawRoot()).invoke("sec is being deprecated in favor of second");
        Object object = this_;
        this_ = null;
        return ((AbstractDateTime)object).getSecondOfMinute();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__18897.invokeStatic(object2);
    }
}

