/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Numbers;
import clojure.lang.Util;

public final class core$nth_iteration
extends AFunction {
    public static Object invokeStatic(Object n, Object f, Object x) {
        Object object = x;
        x = null;
        Object x2 = object;
        Object object2 = n;
        n = null;
        Object n2 = object2;
        while (true) {
            if (Util.equiv(0L, n2)) break;
            Object object3 = x2;
            x2 = null;
            Object object4 = n2;
            n2 = null;
            n2 = Numbers.dec(object4);
            x2 = ((IFn)f).invoke(object3);
        }
        Object object5 = x2;
        x2 = null;
        return object5;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$nth_iteration.invokeStatic(object4, object5, object6);
    }
}

