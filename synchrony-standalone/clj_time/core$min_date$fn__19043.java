/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.DateTimeProtocol;
import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$min_date$fn__19043
extends AFunction {
    private static Class __cached_class__0;
    public static final Var const__0;

    /*
     * Unable to fully structure code
     */
    @Override
    public Object invoke(Object p1__19041_SHARP_, Object p2__19042_SHARP_) {
        v0 = p1__19041_SHARP_;
        if (Util.classOf(v0) == core$min_date$fn__19043.__cached_class__0) ** GOTO lbl6
        if (!(v0 instanceof DateTimeProtocol)) {
            v0 = v0;
            core$min_date$fn__19043.__cached_class__0 = Util.classOf(v0);
lbl6:
            // 2 sources

            v1 = core$min_date$fn__19043.const__0.getRawRoot().invoke(v0, p2__19042_SHARP_);
        } else {
            v1 = ((DateTimeProtocol)v0).before_QMARK_(p2__19042_SHARP_);
        }
        if (v1 != null && v1 != Boolean.FALSE) {
            v2 = p1__19041_SHARP_;
            p1__19041_SHARP_ = null;
        } else {
            v2 = p2__19042_SHARP_;
            var2_2 = null;
        }
        return v2;
    }

    static {
        const__0 = RT.var("clj-time.core", "before?");
    }
}

