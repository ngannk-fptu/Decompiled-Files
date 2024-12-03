/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;
import java.util.regex.Pattern;

public final class async$collapse_state_machine_stes$fn__8747
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "update");
    public static final Keyword const__1 = RT.keyword(null, "class");
    public static final Var const__2 = RT.var("clojure.string", "replace");
    public static final Object const__3 = Pattern.compile("\\$state_machine_\\w*(\\$.*|$)");

    @Override
    public Object invoke(Object p1__8742_SHARP_) {
        Object object = p1__8742_SHARP_;
        p1__8742_SHARP_ = null;
        async$collapse_state_machine_stes$fn__8747 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1, const__2.getRawRoot(), const__3, "");
    }
}

