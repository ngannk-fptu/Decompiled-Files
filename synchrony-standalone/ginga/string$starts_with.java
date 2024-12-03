/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class string$starts_with
extends AFunction {
    public static Object invokeStatic(Object s2, Object prefix) {
        Object object = s2;
        s2 = null;
        Object object2 = prefix;
        prefix = null;
        return ((String)object).startsWith((String)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return string$starts_with.invokeStatic(object3, object4);
    }
}

