/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class macros$un_gensym
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "second");
    public static final Var const__1 = RT.var("clojure.core", "re-find");
    public static final Var const__2 = RT.var("potemkin.macros", "gensym-regex");
    public static final Var const__3 = RT.var("clojure.core", "str");

    public static Object invokeStatic(Object s2) {
        Object object = s2;
        s2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__3.getRawRoot()).invoke(object)));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return macros$un_gensym.invokeStatic(object2);
    }
}

