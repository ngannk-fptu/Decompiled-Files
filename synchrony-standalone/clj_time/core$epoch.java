/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Var;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class core$epoch
extends AFunction {
    public static final Var const__2 = RT.var("clj-time.core", "utc");

    public static Object invokeStatic() {
        return new DateTime(0L, (DateTimeZone)const__2.getRawRoot());
    }

    @Override
    public Object invoke() {
        return core$epoch.invokeStatic();
    }
}

