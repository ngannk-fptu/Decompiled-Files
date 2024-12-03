/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class byte_streams$to_byte_source
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "to-byte-source");
    public static final Var const__1 = RT.var("byte-streams", "convert");
    public static final Var const__2 = RT.var("byte-streams.protocols", "ByteSource");

    public static Object invokeStatic(Object x, Object options2) {
        Object object = x;
        x = null;
        Object object2 = options2;
        options2 = null;
        return ((IFn)const__1.getRawRoot()).invoke(object, const__2, object2);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$to_byte_source.invokeStatic(object3, object4);
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
        return byte_streams$to_byte_source.invokeStatic(object2);
    }
}

