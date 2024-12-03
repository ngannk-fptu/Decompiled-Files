/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;

public final class string$to_upper_case
extends AFunction {
    public static Object invokeStatic(Object s2) {
        Object object = s2;
        s2 = null;
        return ((String)object).toUpperCase();
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return string$to_upper_case.invokeStatic(object2);
    }
}

