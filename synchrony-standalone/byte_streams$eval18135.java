/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_streams$eval18135
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "seq-of");
    public static final Object const__1 = RT.classForName("java.nio.ByteBuffer");

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(const__1);
    }

    @Override
    public Object invoke() {
        return byte_streams$eval18135.invokeStatic();
    }
}

