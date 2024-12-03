/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.Util;

public final class core$latest$fn__19118
extends AFunction {
    @Override
    public Object invoke(Object dt1, Object dt2) {
        Object object;
        if ((long)Util.compare(dt1, dt2) < 0L) {
            object = dt2;
            dt2 = null;
        } else {
            object = dt1;
            Object var1_1 = null;
        }
        return object;
    }
}

