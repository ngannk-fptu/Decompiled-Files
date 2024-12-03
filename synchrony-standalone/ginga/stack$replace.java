/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class stack$replace
extends AFunction {
    public static final Var const__0 = RT.var("ginga.stack", "push");
    public static final Var const__1 = RT.var("ginga.stack", "pop");

    public static Object invokeStatic(Object s2, Object frame) {
        Object object = s2;
        s2 = null;
        Object object2 = frame;
        frame = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object), object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return stack$replace.invokeStatic(object3, object4);
    }
}

