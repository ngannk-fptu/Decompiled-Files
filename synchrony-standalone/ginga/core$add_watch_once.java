/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$add_watch_once$fn__8425;

public final class core$add_watch_once
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "atom");
    public static final Object const__1 = 0L;
    public static final Var const__2 = RT.var("clojure.core", "add-watch");

    public static Object invokeStatic(Object ref2, Object key2, Object f) {
        Object triggered = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object = ref2;
        ref2 = null;
        Object object2 = key2;
        key2 = null;
        Object object3 = triggered;
        triggered = null;
        Object object4 = f;
        f = null;
        return ((IFn)const__2.getRawRoot()).invoke(object, object2, new core$add_watch_once$fn__8425(object3, object4));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$add_watch_once.invokeStatic(object4, object5, object6);
    }
}

