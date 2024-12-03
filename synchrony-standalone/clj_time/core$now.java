/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class core$now
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic() {
        return new DateTime((DateTimeZone)const__0.getRawRoot());
    }

    @Override
    public Object invoke() {
        return core$now.invokeStatic();
    }
}

