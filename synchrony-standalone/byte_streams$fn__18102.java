/*
 * Decompiled with CFR 0.152.
 */
import byte_streams.ByteBufferInputStream;
import clojure.lang.AFunction;
import java.nio.ByteBuffer;

public final class byte_streams$fn__18102
extends AFunction {
    public static Object invokeStatic(Object buf, Object ___18005__auto__) {
        Object object = buf;
        buf = null;
        return new ByteBufferInputStream(((ByteBuffer)object).duplicate());
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$fn__18102.invokeStatic(object3, object4);
    }
}

