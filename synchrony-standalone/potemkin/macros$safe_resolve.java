/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class macros$safe_resolve
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "resolve");

    public static Object invokeStatic(Object x) {
        Object object;
        try {
            Object object2 = x;
            x = null;
            object = ((IFn)const__0.getRawRoot()).invoke(object2);
        }
        catch (Exception _2) {
            object = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return macros$safe_resolve.invokeStatic(object2);
    }
}

