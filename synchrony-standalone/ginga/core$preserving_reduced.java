/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import ginga.core$preserving_reduced$fn__8353;

public final class core$preserving_reduced
extends AFunction {
    public static Object invokeStatic(Object rf) {
        Object object = rf;
        rf = null;
        return new core$preserving_reduced$fn__8353(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$preserving_reduced.invokeStatic(object2);
    }
}

