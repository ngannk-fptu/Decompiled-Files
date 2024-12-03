/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.Numbers;
import clojure.lang.RT;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class byte_streams$fn__18249
extends AFunction {
    public static Object invokeStatic(Object this_, Object n, Object _2) {
        ByteBuffer byteBuffer;
        if ((long)((Buffer)this_).remaining() > 0L) {
            Object object = n;
            n = null;
            Object n2 = Numbers.min((long)((Buffer)this_).remaining(), object);
            ByteBuffer buf = ((ByteBuffer)((Buffer)((ByteBuffer)this_).duplicate()).limit(RT.intCast(Numbers.add((long)((Buffer)this_).position(), n2)))).slice().order(((ByteBuffer)this_).order());
            Buffer buffer2 = (Buffer)this_;
            Object object2 = n2;
            n2 = null;
            Object object3 = this_;
            this_ = null;
            buffer2.position(RT.intCast(Numbers.add(object2, (long)((Buffer)object3).position())));
            byteBuffer = buf;
            buf = null;
        } else {
            byteBuffer = null;
        }
        return byteBuffer;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return byte_streams$fn__18249.invokeStatic(object4, object5, object6);
    }
}

