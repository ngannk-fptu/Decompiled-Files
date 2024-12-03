/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$reify_handler$fn__14757$fn__14761;

public final class walk$reify_handler$fn__14757
extends AFunction {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__4 = RT.var("riddley.compiler", "locals");

    public walk$reify_handler$fn__14757(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object p__14756) {
        Object vec__14758;
        Object object = p__14756;
        p__14756 = null;
        Object object2 = vec__14758 = object;
        vec__14758 = null;
        Object seq__14759 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14760 = ((IFn)const__1.getRawRoot()).invoke(seq__14759);
        Object object3 = seq__14759;
        seq__14759 = null;
        Object seq__147592 = ((IFn)const__2.getRawRoot()).invoke(object3);
        Object object4 = first__14760;
        first__14760 = null;
        Object nm = object4;
        Object first__147602 = ((IFn)const__1.getRawRoot()).invoke(seq__147592);
        Object object5 = seq__147592;
        seq__147592 = null;
        Object seq__147593 = ((IFn)const__2.getRawRoot()).invoke(object5);
        Object object6 = first__147602;
        first__147602 = null;
        Object args = object6;
        Object object7 = seq__147593;
        seq__147593 = null;
        Object body = object7;
        Object object8 = nm;
        nm = null;
        Object object9 = args;
        args = null;
        Object object10 = body;
        body = null;
        walk$reify_handler$fn__14757 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__4.getRawRoot()).invoke()), new walk$reify_handler$fn__14757$fn__14761(object8, object9, object10, this_.f));
    }
}

