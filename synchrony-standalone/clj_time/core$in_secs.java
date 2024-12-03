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

public final class core$in_secs
extends AFunction {
    private static Class __cached_class__0;
    public static final Var const__0;
    public static final Var const__1;

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object in2) {
        Object object;
        ((IFn)const__0.getRawRoot()).invoke("in-secs has been deprecated in favor of in-seconds");
        Object object2 = in2;
        in2 = null;
        Object object3 = object2;
        if (Util.classOf(object2) != __cached_class__0) {
            if (object3 instanceof InTimeUnitProtocol) {
                object = ((InTimeUnitProtocol)object3).in_seconds();
                return object;
            }
            object3 = object3;
            __cached_class__0 = Util.classOf(object3);
        }
        object = const__1.getRawRoot().invoke(object3);
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$in_secs.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("clj-time.core", "deprecated");
        const__1 = RT.var("clj-time.core", "in-seconds");
    }
}

