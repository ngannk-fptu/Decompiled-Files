/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.InTimeUnitProtocol;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$mins_ago
extends AFunction {
    private static Class __cached_class__0;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object d) {
        Object object;
        Object object2 = d;
        d = null;
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(object2, ((IFn)const__2.getRawRoot()).invoke());
        if (Util.classOf(object3) != __cached_class__0) {
            if (object3 instanceof InTimeUnitProtocol) {
                object = ((InTimeUnitProtocol)object3).in_minutes();
                return object;
            }
            object3 = object3;
            __cached_class__0 = Util.classOf(object3);
        }
        object = const__0.getRawRoot().invoke(object3);
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$mins_ago.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("clj-time.core", "in-minutes");
        const__1 = RT.var("clj-time.core", "interval");
        const__2 = RT.var("clj-time.core", "now");
    }
}

