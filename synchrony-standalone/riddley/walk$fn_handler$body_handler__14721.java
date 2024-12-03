/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$fn_handler$body_handler__14721$fn__14722;

public final class walk$fn_handler$body_handler__14721
extends AFunction {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__1 = RT.var("riddley.compiler", "locals");

    public walk$fn_handler$body_handler__14721(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object x) {
        Object object = x;
        x = null;
        walk$fn_handler$body_handler__14721 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__1.getRawRoot()).invoke()), new walk$fn_handler$body_handler__14721$fn__14722(object, this_.f));
    }
}

