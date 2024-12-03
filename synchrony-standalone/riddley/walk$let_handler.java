/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$let_handler$fn__14797;

public final class walk$let_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__1 = RT.var("riddley.compiler", "locals");

    public static Object invokeStatic(Object f, Object x) {
        Object object = x;
        x = null;
        Object object2 = f;
        f = null;
        return ((IFn)const__0.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__1.getRawRoot()).invoke()), new walk$let_handler$fn__14797(object, object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$let_handler.invokeStatic(object3, object4);
    }
}

