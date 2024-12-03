/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class collections$throw_arity
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern(null, "throw");
    public static final AFn const__4 = Symbol.intern(null, "java.lang.RuntimeException.");
    public static final Var const__5 = RT.var("clojure.core", "str");

    public static Object invokeStatic(Object actual) {
        Object object = actual;
        actual = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__4), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke("Wrong number of args (", object, ")")))))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return collections$throw_arity.invokeStatic(object2);
    }
}

