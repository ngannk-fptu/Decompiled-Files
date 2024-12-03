/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_streams$eval18194
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "seq-of");
    public static final Object const__1 = RT.classForName("java.lang.String");

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(const__1);
    }

    @Override
    public Object invoke() {
        return byte_streams$eval18194.invokeStatic();
    }
}

