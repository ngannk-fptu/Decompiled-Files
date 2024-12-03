/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.DateTimeProtocol;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$minus
extends RestFn {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    public static final Var const__0;
    public static final Var const__1;

    /*
     * Unable to fully structure code
     */
    public static Object invokeStatic(Object dt, Object p, ISeq ps) {
        v0 = (IFn)core$minus.const__1.getRawRoot();
        v1 = core$minus.const__0.getRawRoot();
        v2 = dt;
        dt = null;
        v3 = v2;
        if (Util.classOf(v2) == core$minus.__cached_class__1) ** GOTO lbl10
        if (!(v3 instanceof DateTimeProtocol)) {
            v3 = v3;
            core$minus.__cached_class__1 = Util.classOf(v3);
lbl10:
            // 2 sources

            v4 = p;
            p = null;
            v5 = core$minus.const__0.getRawRoot().invoke(v3, v4);
        } else {
            v6 = p;
            p = null;
            v5 = ((DateTimeProtocol)v3).minus_(v6);
        }
        v7 = ps;
        ps = null;
        return v0.invoke(v1, v5, v7);
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return core$minus.invokeStatic(object4, object5, iSeq);
    }

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object dt2, Object p) {
        Object object;
        Object object2 = dt2;
        dt2 = null;
        Object object3 = object2;
        if (Util.classOf(object2) != __cached_class__0) {
            if (object3 instanceof DateTimeProtocol) {
                Object object4 = p;
                p = null;
                object = ((DateTimeProtocol)object3).minus_(object4);
                return object;
            }
            object3 = object3;
            __cached_class__0 = Util.classOf(object3);
        }
        Object object5 = p;
        p = null;
        object = const__0.getRawRoot().invoke(object3, object5);
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$minus.invokeStatic(object3, object4);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }

    static {
        const__0 = RT.var("clj-time.core", "minus-");
        const__1 = RT.var("clojure.core", "reduce");
    }
}

