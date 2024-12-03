/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import java.util.concurrent.ConcurrentHashMap;
import potemkin.utils$fast_memoize$fn__26288;

public final class utils$fast_memoize
extends AFunction {
    public static Object invokeStatic(Object f) {
        ConcurrentHashMap m4 = new ConcurrentHashMap();
        Object object = f;
        f = null;
        ConcurrentHashMap concurrentHashMap = m4;
        m4 = null;
        return new utils$fast_memoize$fn__26288(object, concurrentHashMap);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$fast_memoize.invokeStatic(object2);
    }
}

