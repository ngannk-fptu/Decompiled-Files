/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class compiler$locals
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "deref");

    public static Object invokeStatic() {
        return Compiler.LOCAL_ENV.isBound() ? ((IFn)const__0.getRawRoot()).invoke(Compiler.LOCAL_ENV) : null;
    }

    @Override
    public Object invoke() {
        return compiler$locals.invokeStatic();
    }
}

