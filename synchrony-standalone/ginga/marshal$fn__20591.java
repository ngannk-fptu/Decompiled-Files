/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;

public final class marshal$fn__20591
extends AFunction {
    public static Object invokeStatic(Object value, Object unmarshaller) {
        Object object = unmarshaller;
        unmarshaller = null;
        Object object2 = value;
        value = null;
        return ((IFn)object).invoke(object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return marshal$fn__20591.invokeStatic(object3, object4);
    }
}

