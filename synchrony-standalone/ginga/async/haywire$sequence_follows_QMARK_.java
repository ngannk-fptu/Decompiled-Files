/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class haywire$sequence_follows_QMARK_
extends AFunction {
    public static final Var const__1 = RT.var("ginga.async.haywire", "inc-wraparound");

    public static Object invokeStatic(Object a, Object b) {
        Object object = a;
        a = null;
        Object object2 = b;
        b = null;
        return Util.equiv(((IFn)const__1.getRawRoot()).invoke(object), object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return haywire$sequence_follows_QMARK_.invokeStatic(object3, object4);
    }
}

