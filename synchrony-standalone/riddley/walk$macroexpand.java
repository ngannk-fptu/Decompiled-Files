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
import riddley.walk$macroexpand$fn__14704;

public final class walk$macroexpand
extends AFunction {
    public static final Var const__0 = RT.var("riddley.walk", "macroexpand");
    public static final Var const__1 = RT.var("clojure.core", "push-thread-bindings");
    public static final Var const__2 = RT.var("clojure.core", "hash-map");
    public static final Var const__3 = RT.var("clojure.core", "*warn-on-reflection*");
    public static final Var const__4 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__5 = RT.var("riddley.compiler", "locals");
    public static final Var const__6 = RT.var("clojure.core", "pop-thread-bindings");

    public static Object invokeStatic(Object x, Object special_form_QMARK_) {
        Object object;
        ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3, Boolean.FALSE));
        try {
            Object object2 = ((IFn)const__5.getRawRoot()).invoke();
            Object object3 = special_form_QMARK_;
            special_form_QMARK_ = null;
            Object object4 = x;
            x = null;
            object = ((IFn)const__4.getRawRoot()).invoke(object2 != null && object2 != Boolean.FALSE ? PersistentArrayMap.EMPTY : RT.mapUniqueKeys(Compiler.LOCAL_ENV, PersistentArrayMap.EMPTY), new walk$macroexpand$fn__14704(object3, object4));
        }
        finally {
            ((IFn)const__6.getRawRoot()).invoke();
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$macroexpand.invokeStatic(object3, object4);
    }

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, null);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return walk$macroexpand.invokeStatic(object2);
    }
}

