/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import java.util.Map;

public final class jwt$fn__20368
extends AFunction {
    public static final Var const__2 = RT.var("clojure.core", "into");

    public static Object invokeStatic(Object p1__20367_SHARP_) {
        Object object;
        Object G__20369 = p1__20367_SHARP_;
        Object object2 = p1__20367_SHARP_;
        p1__20367_SHARP_ = null;
        if (object2 instanceof Map) {
            Object object3 = G__20369;
            G__20369 = null;
            object = ((IFn)const__2.getRawRoot()).invoke(PersistentArrayMap.EMPTY, object3);
        } else {
            object = G__20369;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return jwt$fn__20368.invokeStatic(object2);
    }
}

