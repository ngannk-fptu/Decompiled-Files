/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class collections$reify_map_type$fn__26525
extends AFunction {
    Object fns;
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Var const__1 = RT.var("clojure.core", "list*");
    public static final Var const__3 = RT.var("clojure.core", "first");
    public static final Var const__4 = RT.var("clojure.core", "rest");

    public collections$reify_map_type$fn__26525(Object object) {
        this.fns = object;
    }

    @Override
    public Object invoke(Object p1__26521_SHARP_) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(p1__26521_SHARP_);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = RT.get(this_.fns, ((IFn)const__3.getRawRoot()).invoke(p1__26521_SHARP_), ((IFn)const__3.getRawRoot()).invoke(p1__26521_SHARP_));
            Object object4 = p1__26521_SHARP_;
            p1__26521_SHARP_ = null;
            collections$reify_map_type$fn__26525 this_ = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object3, ((IFn)const__4.getRawRoot()).invoke(object4));
        } else {
            object = p1__26521_SHARP_;
            Object var1_1 = null;
        }
        return object;
    }
}

