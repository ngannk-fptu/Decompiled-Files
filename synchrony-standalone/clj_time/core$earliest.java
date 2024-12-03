/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core$earliest$fn__19115;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$earliest
extends AFunction {
    public static final Var const__2 = RT.var("clojure.core", "reduce");

    public static Object invokeStatic(Object dt1, Object dt2) {
        Object object;
        if ((long)Util.compare(dt1, dt2) > 0L) {
            object = dt2;
            dt2 = null;
        } else {
            object = dt1;
            Object object2 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$earliest.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object dts) {
        Object object = dts;
        dts = null;
        return ((IFn)const__2.getRawRoot()).invoke(new core$earliest$fn__19115(), object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$earliest.invokeStatic(object2);
    }
}

