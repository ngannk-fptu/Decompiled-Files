/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async.multiplex$make_bridge$fn__14588;

public final class multiplex$make_bridge
extends AFunction {
    public static final Var const__0 = RT.var("ginga.async.multiplex", "wrap-simplex-with-duplex-accept");

    public static Object invokeStatic(Object to_multi_from_init) {
        Object object = to_multi_from_init;
        to_multi_from_init = null;
        return ((IFn)const__0.getRawRoot()).invoke(new multiplex$make_bridge$fn__14588(object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return multiplex$make_bridge.invokeStatic(object2);
    }
}

