/*
 * Decompiled with CFR 0.152.
 */
import byte_streams.Utils;
import clojure.lang.AFunction;
import clojure.lang.RT;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import primitive_math.Primitives;

public final class byte_streams$fn__18168
extends AFunction {
    public static Object invokeStatic(Object in2, Object options2) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(RT.intCast(Primitives.max(64L, ((InputStream)in2).available())));
        byte[] buf = Utils.byteArray(RT.intCast(16384L));
        while (true) {
            int len;
            if ((long)(len = ((InputStream)in2).read(buf, RT.intCast(0L), RT.intCast(16384L))) < 0L) break;
            out.write(buf, RT.intCast(0L), len);
        }
        ByteArrayOutputStream byteArrayOutputStream = out;
        out = null;
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$fn__18168.invokeStatic(object3, object4);
    }
}

