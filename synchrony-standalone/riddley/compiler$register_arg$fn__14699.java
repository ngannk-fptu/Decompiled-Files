/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import riddley.Util;

public final class compiler$register_arg$fn__14699
extends AFunction {
    Object x;
    public static final Var const__0 = RT.var("clojure.core", "assoc");
    public static final Var const__1 = RT.var("clojure.core", "dissoc");
    public static final Var const__2 = RT.var("riddley.compiler", "locals");
    public static final Var const__3 = RT.var("riddley.compiler", "local-id");
    public static final Var const__4 = RT.var("riddley.compiler", "tag-of");

    public compiler$register_arg$fn__14699(Object object) {
        this.x = object;
    }

    @Override
    public Object invoke() {
        return Compiler.LOCAL_ENV.set(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(), this.x), this.x, Util.localArgument(RT.intCast((Number)((IFn)const__3.getRawRoot()).invoke()), (Symbol)this.x, (Symbol)((IFn)const__4.getRawRoot()).invoke(this.x))));
    }
}

