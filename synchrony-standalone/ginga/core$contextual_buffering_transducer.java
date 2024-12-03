/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import ginga.core$contextual_buffering_transducer$fn__8366;

public final class core$contextual_buffering_transducer
extends AFunction {
    public static Object invokeStatic(Object init_f, Object finalize_f, Object remaining_f, Object step_f) {
        Object object = step_f;
        step_f = null;
        Object object2 = remaining_f;
        remaining_f = null;
        Object object3 = init_f;
        init_f = null;
        Object object4 = finalize_f;
        finalize_f = null;
        return new core$contextual_buffering_transducer$fn__8366(object, object2, object3, object4);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return core$contextual_buffering_transducer.invokeStatic(object5, object6, object7, object8);
    }
}

