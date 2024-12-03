/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$def_handler$fn__14740;

public final class walk$def_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__4 = RT.var("riddley.compiler", "locals");

    public static Object invokeStatic(Object f, Object x) {
        Object vec__14737;
        Object object = x;
        x = null;
        Object object2 = vec__14737 = object;
        vec__14737 = null;
        Object seq__14738 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14739 = ((IFn)const__1.getRawRoot()).invoke(seq__14738);
        Object object3 = seq__14738;
        seq__14738 = null;
        Object seq__147382 = ((IFn)const__2.getRawRoot()).invoke(object3);
        first__14739 = null;
        Object first__147392 = ((IFn)const__1.getRawRoot()).invoke(seq__147382);
        Object object4 = seq__147382;
        seq__147382 = null;
        Object seq__147383 = ((IFn)const__2.getRawRoot()).invoke(object4);
        Object object5 = first__147392;
        first__147392 = null;
        Object n = object5;
        Object object6 = seq__147383;
        seq__147383 = null;
        Object r = object6;
        Object object7 = n;
        n = null;
        Object object8 = r;
        r = null;
        Object object9 = f;
        f = null;
        return ((IFn)const__3.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__4.getRawRoot()).invoke()), new walk$def_handler$fn__14740(object7, object8, object9));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$def_handler.invokeStatic(object3, object4);
    }
}

