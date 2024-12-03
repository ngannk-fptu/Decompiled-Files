/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.types$register_impl_callback$fn__26097;

public final class types$register_impl_callback
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "add-watch");

    public static Object invokeStatic(Object proto_var, Object callback) {
        Object object = proto_var;
        proto_var = null;
        Object object2 = callback;
        Object object3 = callback;
        callback = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, new types$register_impl_callback$fn__26097(object3));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return types$register_impl_callback.invokeStatic(object3, object4);
    }
}

