/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class namespaces$import_vars$unravel__26042$fn__26043
extends AFunction {
    Object x;
    public static final Var const__0 = RT.var("clojure.core", "symbol");
    public static final Var const__1 = RT.var("clojure.core", "str");
    public static final Var const__2 = RT.var("clojure.core", "first");
    public static final Var const__3 = RT.var("clojure.core", "namespace");
    public static final Var const__4 = RT.var("clojure.core", "name");

    public namespaces$import_vars$unravel__26042$fn__26043(Object object) {
        this.x = object;
    }

    @Override
    public Object invoke(Object p1__26041_SHARP_) {
        Object object;
        Object temp__5804__auto__26045;
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__1.getRawRoot();
        Object object2 = ((IFn)const__2.getRawRoot()).invoke(this_.x);
        Object object3 = temp__5804__auto__26045 = ((IFn)const__3.getRawRoot()).invoke(p1__26041_SHARP_);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object n;
            Object object4 = temp__5804__auto__26045;
            temp__5804__auto__26045 = null;
            Object object5 = n = object4;
            n = null;
            object = ((IFn)const__1.getRawRoot()).invoke(".", object5);
        } else {
            object = null;
        }
        Object object6 = p1__26041_SHARP_;
        p1__26041_SHARP_ = null;
        namespaces$import_vars$unravel__26042$fn__26043 this_ = null;
        return iFn.invoke(iFn2.invoke(object2, object), ((IFn)const__4.getRawRoot()).invoke(object6));
    }
}

