/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class primitive_math$ns_wrapper$fn__17766$fn__17767
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Keyword const__2 = RT.keyword(null, "refer-clojure");
    public static final Var const__3 = RT.var("clojure.core", "first");

    @Override
    public Object invoke(Object p1__17764_SHARP_) {
        Object object;
        Object and__5579__auto__17769;
        Object object2 = and__5579__auto__17769 = ((IFn)const__0.getRawRoot()).invoke(p1__17764_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = p1__17764_SHARP_;
            p1__17764_SHARP_ = null;
            primitive_math$ns_wrapper$fn__17766$fn__17767 this_ = null;
            object = Util.equiv((Object)const__2, ((IFn)const__3.getRawRoot()).invoke(object3)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            object = and__5579__auto__17769;
            Object var2_2 = null;
        }
        return object;
    }
}

