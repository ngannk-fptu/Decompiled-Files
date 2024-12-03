/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class primitive_math$eval17734
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "constantly");

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(Boolean.TRUE);
    }

    @Override
    public Object invoke() {
        return primitive_math$eval17734.invokeStatic();
    }
}

