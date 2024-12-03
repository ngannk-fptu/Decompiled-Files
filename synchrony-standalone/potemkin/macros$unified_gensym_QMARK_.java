/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class macros$unified_gensym_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "symbol?");
    public static final Var const__1 = RT.var("clojure.core", "re-find");
    public static final Var const__2 = RT.var("potemkin.macros", "unified-gensym-regex");
    public static final Var const__3 = RT.var("clojure.core", "str");

    public static Object invokeStatic(Object s2) {
        Object object;
        Object and__5579__auto__26067;
        Object object2 = and__5579__auto__26067 = ((IFn)const__0.getRawRoot()).invoke(s2);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = s2;
            s2 = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(object3));
        } else {
            object = and__5579__auto__26067;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return macros$unified_gensym_QMARK_.invokeStatic(object2);
    }
}

