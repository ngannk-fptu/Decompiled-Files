/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import ginga.core$contextual_transducer$fn__8358;

public final class core$contextual_transducer
extends AFunction {
    public static Object invokeStatic(Object init_f, Object finalize_f, Object step_f) {
        Object object = finalize_f;
        finalize_f = null;
        Object object2 = step_f;
        step_f = null;
        Object object3 = init_f;
        init_f = null;
        return new core$contextual_transducer$fn__8358(object, object2, object3);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$contextual_transducer.invokeStatic(object4, object5, object6);
    }
}

