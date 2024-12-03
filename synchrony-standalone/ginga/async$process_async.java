/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$process_async$fn__9013;

public final class async$process_async
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core.async", "take!");

    public static Object invokeStatic(Object f, Object in_ch) {
        Object object = in_ch;
        Object object2 = in_ch;
        in_ch = null;
        Object object3 = f;
        f = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, new async$process_async$fn__9013(object2, object3));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return async$process_async.invokeStatic(object3, object4);
    }
}

