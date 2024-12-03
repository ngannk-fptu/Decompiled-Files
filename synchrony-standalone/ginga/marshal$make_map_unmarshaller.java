/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import ginga.marshal$make_map_unmarshaller$fn__20531;

public final class marshal$make_map_unmarshaller
extends AFunction {
    public static Object invokeStatic(Object map__GT_v) {
        Object object = map__GT_v;
        map__GT_v = null;
        return new marshal$make_map_unmarshaller$fn__20531(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return marshal$make_map_unmarshaller.invokeStatic(object2);
    }
}

