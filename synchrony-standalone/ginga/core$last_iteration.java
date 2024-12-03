/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;

public final class core$last_iteration
extends AFunction {
    public static Object invokeStatic(Object f, Object x) {
        Object object = x;
        x = null;
        Object x2 = object;
        while (true) {
            Object x_SINGLEQUOTE_;
            Object temp__5802__auto__8228;
            Object object2 = temp__5802__auto__8228 = ((IFn)f).invoke(x2);
            if (object2 == null || object2 == Boolean.FALSE) break;
            Object object3 = temp__5802__auto__8228;
            temp__5802__auto__8228 = null;
            Object object4 = x_SINGLEQUOTE_ = object3;
            x_SINGLEQUOTE_ = null;
            x2 = object4;
        }
        Object var2_2 = null;
        return x2;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$last_iteration.invokeStatic(object3, object4);
    }
}

