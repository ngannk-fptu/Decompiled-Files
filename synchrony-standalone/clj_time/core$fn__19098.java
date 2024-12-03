/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.Months;
import org.joda.time.Years;

public final class core$fn__19098
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "instance?");
    public static final Object const__1 = RT.classForName("org.joda.time.Months");
    public static final Object const__2 = RT.classForName("org.joda.time.Years");

    public static Object invokeStatic(Object this_) {
        Number number;
        Object pred__19099 = const__0.getRawRoot();
        Object expr__19100 = this_;
        Object object = ((IFn)pred__19099).invoke(const__1, expr__19100);
        if (object != null && object != Boolean.FALSE) {
            Object object2 = this_;
            this_ = null;
            number = ((Months)object2).getMonths();
        } else {
            Object object3 = pred__19099;
            pred__19099 = null;
            Object object4 = expr__19100;
            expr__19100 = null;
            Object object5 = ((IFn)object3).invoke(const__2, object4);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = this_;
                this_ = null;
                number = Numbers.num(Numbers.multiply(12L, (long)((Years)object6).getYears()));
            } else {
                throw (Throwable)new UnsupportedOperationException("Cannot convert to Months because months vary in length.");
            }
        }
        return number;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19098.invokeStatic(object2);
    }
}

