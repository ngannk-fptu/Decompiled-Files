/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.compiler$register_local$fn__14694$fn__14695;

public final class compiler$register_local$fn__14694
extends AFunction {
    Object v;
    Object x;
    public static final Var const__0 = RT.var("clojure.core", "assoc");
    public static final Var const__1 = RT.var("clojure.core", "dissoc");
    public static final Var const__2 = RT.var("riddley.compiler", "locals");

    public compiler$register_local$fn__14694(Object object, Object object2) {
        this.v = object;
        this.x = object2;
    }

    @Override
    public Object invoke() {
        return Compiler.LOCAL_ENV.set(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(), this.v), this.v, ((IFn)new compiler$register_local$fn__14694$fn__14695(this.v, this.x)).invoke()));
    }
}

