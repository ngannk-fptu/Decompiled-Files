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
import riddley.walk$walk_exprs$fn__14822;

public final class walk$walk_exprs
extends AFunction {
    public static final Var const__0 = RT.var("riddley.walk", "walk-exprs");
    public static final Var const__1 = RT.var("clojure.core", "push-thread-bindings");
    public static final Var const__2 = RT.var("clojure.core", "hash-map");
    public static final Var const__3 = RT.var("clojure.core", "*warn-on-reflection*");
    public static final Var const__4 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__5 = RT.var("riddley.compiler", "locals");
    public static final Var const__6 = RT.var("clojure.core", "pop-thread-bindings");

    public static Object invokeStatic(Object predicate, Object handler2, Object special_form_QMARK_, Object x) {
        Object object;
        ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3, Boolean.FALSE));
        try {
            Object object2 = ((IFn)const__5.getRawRoot()).invoke();
            Object object3 = x;
            x = null;
            Object object4 = predicate;
            predicate = null;
            Object object5 = special_form_QMARK_;
            special_form_QMARK_ = null;
            Object object6 = handler2;
            handler2 = null;
            object = ((IFn)const__4.getRawRoot()).invoke(object2 != null && object2 != Boolean.FALSE ? PersistentArrayMap.EMPTY : RT.mapUniqueKeys(Compiler.LOCAL_ENV, PersistentArrayMap.EMPTY), new walk$walk_exprs$fn__14822(object3, object4, object5, object6));
        }
        finally {
            ((IFn)const__6.getRawRoot()).invoke();
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return walk$walk_exprs.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object predicate, Object handler2, Object x) {
        Object object = predicate;
        predicate = null;
        Object object2 = handler2;
        handler2 = null;
        Object object3 = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, null, object3);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return walk$walk_exprs.invokeStatic(object4, object5, object6);
    }
}

