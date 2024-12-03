/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.format.DateTimeFormatter;

public final class format$formatter$fn__19164
extends AFunction {
    Object dtz;
    public static final Var const__0 = RT.var("clj-time.format", "formatter");

    public format$formatter$fn__19164(Object object) {
        this.dtz = object;
    }

    @Override
    public Object invoke(Object p1__19163_SHARP_) {
        Object object = p1__19163_SHARP_;
        p1__19163_SHARP_ = null;
        return ((DateTimeFormatter)((IFn)const__0.getRawRoot()).invoke(object, this.dtz)).getParser();
    }
}

