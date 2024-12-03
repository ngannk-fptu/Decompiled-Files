/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class tree$lazy_precat_STAR_$iter__20720__20724$fn__20725$fn__20730
extends AFunction {
    Object with_children;
    Object children;
    Object pre;
    public static final Var const__0 = RT.var("ginga.tree", "lazy-precat*");

    public tree$lazy_precat_STAR_$iter__20720__20724$fn__20725$fn__20730(Object object, Object object2, Object object3) {
        this.with_children = object;
        this.children = object2;
        this.pre = object3;
    }

    @Override
    public Object invoke(Object p1__20719_SHARP_) {
        Object object = p1__20719_SHARP_;
        p1__20719_SHARP_ = null;
        tree$lazy_precat_STAR_$iter__20720__20724$fn__20725$fn__20730 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(this_.children, this_.with_children, this_.pre, object);
    }
}

