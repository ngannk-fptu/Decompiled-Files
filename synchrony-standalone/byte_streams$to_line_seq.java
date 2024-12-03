/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import java.io.BufferedReader;
import java.io.Reader;

public final class byte_streams$to_line_seq
extends AFunction {
    public static final Var const__0 = RT.var("byte-streams", "to-line-seq");
    public static final Var const__1 = RT.var("byte-streams", "convert");
    public static final Object const__2 = RT.classForName("java.io.Reader");

    public static Object invokeStatic(Object x, Object options2) {
        byte_streams$to_line_seq$line_BANG___18296 line_BANG_;
        BufferedReader reader2;
        Object reader3;
        Object object = x;
        x = null;
        Object object2 = options2;
        options2 = null;
        Object object3 = reader3 = ((IFn)const__1.getRawRoot()).invoke(object, const__2, object2);
        reader3 = null;
        BufferedReader bufferedReader = reader2 = new BufferedReader((Reader)object3);
        reader2 = null;
        byte_streams$to_line_seq$line_BANG___18296 byte_streams$to_line_seq$line_BANG___18296 = line_BANG_ = new byte_streams$to_line_seq$line_BANG___18296(bufferedReader);
        line_BANG_ = null;
        return ((IFn)byte_streams$to_line_seq$line_BANG___18296).invoke();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$to_line_seq.invokeStatic(object3, object4);
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
        return byte_streams$to_line_seq.invokeStatic(object2);
    }
}

