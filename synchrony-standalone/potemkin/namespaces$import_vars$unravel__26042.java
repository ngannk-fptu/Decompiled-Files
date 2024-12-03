/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import potemkin.namespaces$import_vars$unravel__26042$fn__26043;

public final class namespaces$import_vars$unravel__26042
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("clojure.core", "mapcat");
    public static final Var const__3 = RT.var("clojure.core", "rest");

    @Override
    public Object invoke(Object x) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(x);
        if (object2 != null && object2 != Boolean.FALSE) {
            namespaces$import_vars$unravel__26042$fn__26043 namespaces$import_vars$unravel__26042$fn__26043 = new namespaces$import_vars$unravel__26042$fn__26043(x);
            Object object3 = x;
            x = null;
            Object object4 = ((IFn)const__2.getRawRoot()).invoke(this_, ((IFn)const__3.getRawRoot()).invoke(object3));
            namespaces$import_vars$unravel__26042 this_ = null;
            object = ((IFn)const__1.getRawRoot()).invoke(namespaces$import_vars$unravel__26042$fn__26043, object4);
        } else {
            Object object5 = x;
            x = null;
            object = Tuple.create(object5);
        }
        return object;
    }
}

