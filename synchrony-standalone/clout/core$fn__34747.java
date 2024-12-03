/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import clout.core.Route;

public final class core$fn__34747
extends AFunction {
    private static Class __cached_class__0;
    public static final Var const__0;
    public static final Var const__1;

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object route, Object request2) {
        Object object;
        Object object2 = route;
        route = null;
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(object2);
        if (Util.classOf(object3) != __cached_class__0) {
            if (object3 instanceof Route) {
                Object object4 = request2;
                request2 = null;
                object = ((Route)object3).route_matches(object4);
                return object;
            }
            object3 = object3;
            __cached_class__0 = Util.classOf(object3);
        }
        Object object5 = request2;
        request2 = null;
        object = const__0.getRawRoot().invoke(object3, object5);
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$fn__34747.invokeStatic(object3, object4);
    }

    static {
        const__0 = RT.var("clout.core", "route-matches");
        const__1 = RT.var("clout.core", "route-compile");
    }
}

