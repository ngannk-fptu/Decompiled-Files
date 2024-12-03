/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$deftype_handler$fn__14774$fn__14780$fn__14784;

public final class walk$deftype_handler$fn__14774$fn__14780
extends AFunction {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__4 = RT.var("riddley.compiler", "locals");

    public walk$deftype_handler$fn__14774$fn__14780(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object p__14779) {
        Object vec__14781;
        Object object = p__14779;
        p__14779 = null;
        Object object2 = vec__14781 = object;
        vec__14781 = null;
        Object seq__14782 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14783 = ((IFn)const__1.getRawRoot()).invoke(seq__14782);
        Object object3 = seq__14782;
        seq__14782 = null;
        Object seq__147822 = ((IFn)const__2.getRawRoot()).invoke(object3);
        Object object4 = first__14783;
        first__14783 = null;
        Object nm = object4;
        Object first__147832 = ((IFn)const__1.getRawRoot()).invoke(seq__147822);
        Object object5 = seq__147822;
        seq__147822 = null;
        Object seq__147823 = ((IFn)const__2.getRawRoot()).invoke(object5);
        Object object6 = first__147832;
        first__147832 = null;
        Object args = object6;
        Object object7 = seq__147823;
        seq__147823 = null;
        Object body = object7;
        Object object8 = args;
        args = null;
        Object object9 = nm;
        nm = null;
        Object object10 = body;
        body = null;
        walk$deftype_handler$fn__14774$fn__14780 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__4.getRawRoot()).invoke()), new walk$deftype_handler$fn__14774$fn__14780$fn__14784(object8, object9, object10, this_.f));
    }
}

