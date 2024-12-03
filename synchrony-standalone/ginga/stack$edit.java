/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class stack$edit
extends AFunction {
    public static final Var const__0 = RT.var("ginga.stack", "replace");
    public static final Var const__1 = RT.var("ginga.stack", "peek");

    public static Object invokeStatic(Object f, Object stack2) {
        Object object = stack2;
        Object object2 = f;
        f = null;
        Object object3 = stack2;
        stack2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)object2).invoke(((IFn)const__1.getRawRoot()).invoke(object3)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return stack$edit.invokeStatic(object3, object4);
    }
}

