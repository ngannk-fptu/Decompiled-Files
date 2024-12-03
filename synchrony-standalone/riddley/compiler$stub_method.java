/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.compiler.proxy$clojure.lang.Compiler$ObjMethod$ff19274a;

public final class compiler$stub_method
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "init-proxy");

    public static Object invokeStatic() {
        Compiler$ObjMethod$ff19274a p__7254__auto__14683 = new Compiler$ObjMethod$ff19274a(new Compiler.ObjExpr(null), null);
        ((IFn)const__0.getRawRoot()).invoke(p__7254__auto__14683, PersistentArrayMap.EMPTY);
        Object var0 = null;
        return p__7254__auto__14683;
    }

    @Override
    public Object invoke() {
        return compiler$stub_method.invokeStatic();
    }
}

