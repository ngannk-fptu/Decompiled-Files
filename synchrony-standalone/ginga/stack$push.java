/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class stack$push
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "cons");

    public static Object invokeStatic(Object s2, Object frame) {
        Object object = frame;
        frame = null;
        Object object2 = s2;
        s2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return stack$push.invokeStatic(object3, object4);
    }
}

