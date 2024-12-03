/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$catch_handler$fn__14813;

public final class walk$catch_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__4 = RT.var("riddley.compiler", "locals");

    public static Object invokeStatic(Object f, Object x) {
        Object vec__14810;
        Object object = x;
        x = null;
        Object object2 = vec__14810 = object;
        vec__14810 = null;
        Object seq__14811 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14812 = ((IFn)const__1.getRawRoot()).invoke(seq__14811);
        Object object3 = seq__14811;
        seq__14811 = null;
        Object seq__148112 = ((IFn)const__2.getRawRoot()).invoke(object3);
        first__14812 = null;
        Object first__148122 = ((IFn)const__1.getRawRoot()).invoke(seq__148112);
        Object object4 = seq__148112;
        seq__148112 = null;
        Object seq__148113 = ((IFn)const__2.getRawRoot()).invoke(object4);
        Object object5 = first__148122;
        first__148122 = null;
        Object type2 = object5;
        Object first__148123 = ((IFn)const__1.getRawRoot()).invoke(seq__148113);
        Object object6 = seq__148113;
        seq__148113 = null;
        Object seq__148114 = ((IFn)const__2.getRawRoot()).invoke(object6);
        Object object7 = first__148123;
        first__148123 = null;
        Object var = object7;
        Object object8 = seq__148114;
        seq__148114 = null;
        Object body = object8;
        Object object9 = f;
        f = null;
        Object object10 = type2;
        type2 = null;
        Object object11 = body;
        body = null;
        Object object12 = var;
        var = null;
        return ((IFn)const__3.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__4.getRawRoot()).invoke()), new walk$catch_handler$fn__14813(object9, object10, object11, object12));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$catch_handler.invokeStatic(object3, object4);
    }
}

