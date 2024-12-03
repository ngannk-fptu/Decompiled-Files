/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.compiler$register_local$fn__14694;

public final class compiler$register_local
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__1 = RT.var("riddley.compiler", "stub-method");

    public static Object invokeStatic(Object v, Object x) {
        Object object = v;
        v = null;
        Object object2 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(RT.map(Compiler.METHOD, ((IFn)const__1.getRawRoot()).invoke(), Compiler.CLEAR_SITES, null), new compiler$register_local$fn__14694(object, object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return compiler$register_local.invokeStatic(object3, object4);
    }
}

