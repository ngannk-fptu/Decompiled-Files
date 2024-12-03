/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;

public final class string$index_of
extends AFunction {
    public static Object invokeStatic(Object s2, Object chr) {
        Object object = s2;
        s2 = null;
        Object object2 = chr;
        chr = null;
        return ((String)object).indexOf(RT.intCast(object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return string$index_of.invokeStatic(object3, object4);
    }
}

