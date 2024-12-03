/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import potemkin.namespaces$link_vars$fn__26032;

public final class namespaces$link_vars
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "add-watch");

    public static Object invokeStatic(Object src, Object dst) {
        Object object = src;
        src = null;
        Object object2 = dst;
        Object object3 = dst;
        dst = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, new namespaces$link_vars$fn__26032(object3));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return namespaces$link_vars.invokeStatic(object3, object4);
    }
}

