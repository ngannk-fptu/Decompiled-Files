/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.compiler$register_arg$fn__14699;

public final class compiler$register_arg
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__1 = RT.var("riddley.compiler", "stub-method");

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(RT.map(Compiler.METHOD, ((IFn)const__1.getRawRoot()).invoke(), Compiler.CLEAR_SITES, null), new compiler$register_arg$fn__14699(object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return compiler$register_arg.invokeStatic(object2);
    }
}

