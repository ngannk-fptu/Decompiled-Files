/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class primitive_math$reverse_double__inliner__17850
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("clojure.core", "->");
    public static final AFn const__4 = Symbol.intern("primitive-math", "double->long");
    public static final AFn const__5 = Symbol.intern("primitive-math", "reverse-long");
    public static final AFn const__6 = Symbol.intern("primitive-math", "long->double");

    public static Object invokeStatic(Object x) {
        Object object = x;
        x = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(object), ((IFn)const__2.getRawRoot()).invoke(const__4), ((IFn)const__2.getRawRoot()).invoke(const__5), ((IFn)const__2.getRawRoot()).invoke(const__6)));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return primitive_math$reverse_double__inliner__17850.invokeStatic(object2);
    }
}

