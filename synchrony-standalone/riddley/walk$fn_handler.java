/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$fn_handler$body_handler__14721;
import riddley.walk$fn_handler$fn__14731;

public final class walk$fn_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "take-while");
    public static final Var const__1 = RT.var("clojure.core", "complement");
    public static final Var const__2 = RT.var("clojure.core", "sequential?");
    public static final Var const__3 = RT.var("clojure.core", "drop");
    public static final Var const__5 = RT.var("clojure.core", "vector?");
    public static final Var const__6 = RT.var("clojure.core", "first");
    public static final Var const__7 = RT.var("clojure.core", "list");
    public static final Var const__8 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__9 = RT.var("riddley.compiler", "locals");

    public static Object invokeStatic(Object f, Object x) {
        Object object;
        Object prelude = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot()), x);
        Object object2 = x;
        x = null;
        Object remainder = ((IFn)const__3.getRawRoot()).invoke(RT.count(prelude), object2);
        Object object3 = ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(remainder));
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = remainder;
            remainder = null;
            object = ((IFn)const__7.getRawRoot()).invoke(object4);
        } else {
            object = remainder;
            remainder = null;
        }
        Object remainder2 = object;
        Object object5 = f;
        f = null;
        walk$fn_handler$body_handler__14721 body_handler = new walk$fn_handler$body_handler__14721(object5);
        Object object6 = prelude;
        prelude = null;
        Object object7 = remainder2;
        remainder2 = null;
        walk$fn_handler$body_handler__14721 walk$fn_handler$body_handler__14721 = body_handler;
        body_handler = null;
        return ((IFn)const__8.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__9.getRawRoot()).invoke()), new walk$fn_handler$fn__14731(object6, object7, walk$fn_handler$body_handler__14721));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$fn_handler.invokeStatic(object3, object4);
    }
}

