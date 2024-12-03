/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import riddley.walk$reify_handler$fn__14757;

public final class walk$reify_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "list*");
    public static final AFn const__4 = Symbol.intern(null, "reify*");
    public static final Var const__5 = RT.var("clojure.core", "doall");
    public static final Var const__6 = RT.var("clojure.core", "map");

    public static Object invokeStatic(Object f, Object x) {
        Object vec__14753;
        Object object = x;
        x = null;
        Object object2 = vec__14753 = object;
        vec__14753 = null;
        Object seq__14754 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14755 = ((IFn)const__1.getRawRoot()).invoke(seq__14754);
        Object object3 = seq__14754;
        seq__14754 = null;
        Object seq__147542 = ((IFn)const__2.getRawRoot()).invoke(object3);
        first__14755 = null;
        Object first__147552 = ((IFn)const__1.getRawRoot()).invoke(seq__147542);
        Object object4 = seq__147542;
        seq__147542 = null;
        Object seq__147543 = ((IFn)const__2.getRawRoot()).invoke(object4);
        Object object5 = first__147552;
        first__147552 = null;
        Object classes = object5;
        Object object6 = seq__147543;
        seq__147543 = null;
        Object fns = object6;
        Object object7 = classes;
        classes = null;
        Object object8 = f;
        f = null;
        Object object9 = fns;
        fns = null;
        return ((IFn)const__3.getRawRoot()).invoke(const__4, object7, ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(new walk$reify_handler$fn__14757(object8), object9)));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$reify_handler.invokeStatic(object3, object4);
    }
}

