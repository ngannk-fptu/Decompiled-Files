/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$preserving_reduced$fn__8353
extends AFunction {
    Object rf;
    public static final Var const__1 = RT.var("clojure.core", "reduced");

    public core$preserving_reduced$fn__8353(Object object) {
        this.rf = object;
    }

    @Override
    public Object invoke(Object p1__8351_SHARP_, Object p2__8352_SHARP_) {
        Object object;
        Object object2 = p1__8351_SHARP_;
        p1__8351_SHARP_ = null;
        Object object3 = p2__8352_SHARP_;
        p2__8352_SHARP_ = null;
        Object ret = ((IFn)this_.rf).invoke(object2, object3);
        if (RT.isReduced(ret)) {
            Object object4 = ret;
            ret = null;
            core$preserving_reduced$fn__8353 this_ = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object4);
        } else {
            object = ret;
            Object var3_3 = null;
        }
        return object;
    }
}

