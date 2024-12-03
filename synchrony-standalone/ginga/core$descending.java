/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.Util;

public final class core$descending
extends AFunction {
    public static Object invokeStatic(Object a, Object b) {
        Object object = b;
        b = null;
        Object object2 = a;
        a = null;
        return Util.compare(object, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$descending.invokeStatic(object3, object4);
    }
}

