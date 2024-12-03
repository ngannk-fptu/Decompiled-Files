/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$throw_map
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "throwable");

    public static Object invokeStatic(Object data2) {
        Object object = data2;
        data2 = null;
        throw (Throwable)((IFn)const__0.getRawRoot()).invoke(object);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$throw_map.invokeStatic(object2);
    }
}

