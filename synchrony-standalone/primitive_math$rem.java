/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class primitive_math$rem
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("primitive_math.Primitives", "rem");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object n, Object div2) {
        Object object = n;
        n = null;
        Object object2 = div2;
        div2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(object), ((IFn)const__2.getRawRoot()).invoke(object2)));
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
        return primitive_math$rem.invokeStatic(object5, object6, object7, object8);
    }
}

