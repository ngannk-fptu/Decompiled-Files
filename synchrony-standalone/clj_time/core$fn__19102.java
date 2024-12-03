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

public final class core$fn__19102
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "instance?");
    public static final Object const__1 = RT.classForName("org.joda.time.Months");
    public static final Object const__5 = RT.classForName("org.joda.time.Years");

    public static Object invokeStatic(Object this_) {
        Integer n;
        Object pred__19103 = const__0.getRawRoot();
        Object expr__19104 = this_;
        Object object = ((IFn)pred__19103).invoke(const__1, expr__19104);
        if (object != null && object != Boolean.FALSE) {
            Object object2 = this_;
            this_ = null;
            n = RT.intCast(Numbers.divide((long)((Months)object2).getMonths(), 12L));
        } else {
            Object object3 = pred__19103;
            pred__19103 = null;
            Object object4 = expr__19104;
            expr__19104 = null;
            Object object5 = ((IFn)object3).invoke(const__5, object4);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = this_;
                this_ = null;
                n = ((Years)object6).getYears();
            } else {
                throw (Throwable)new UnsupportedOperationException("Cannot convert to Years because years vary in length.");
            }
        }
        return n;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$fn__19102.invokeStatic(object2);
    }
}

