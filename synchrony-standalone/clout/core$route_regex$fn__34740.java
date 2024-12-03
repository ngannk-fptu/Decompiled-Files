/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.Util;

public final class core$route_regex$fn__34740
extends AFunction {
    @Override
    public Object invoke(Object p1__34738_SHARP_) {
        Object object;
        if (Util.equiv(p1__34738_SHARP_, (Object)"//")) {
            object = "https?://";
        } else {
            object = p1__34738_SHARP_;
            Object var1_1 = null;
        }
        return object;
    }
}

