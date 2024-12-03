/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Reflector;
import clojure.lang.Var;

public final class string$ensure_begins_with
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "str");

    public static Object invokeStatic(Object path2, Object prefix) {
        Object object;
        Object object2 = Reflector.invokeInstanceMethod(path2, "startsWith", new Object[]{prefix});
        if (object2 != null && object2 != Boolean.FALSE) {
            object = path2;
            path2 = null;
        } else {
            Object object3 = prefix;
            prefix = null;
            Object object4 = path2;
            path2 = null;
            object = ((IFn)const__0.getRawRoot()).invoke(object3, object4);
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return string$ensure_begins_with.invokeStatic(object3, object4);
    }
}

