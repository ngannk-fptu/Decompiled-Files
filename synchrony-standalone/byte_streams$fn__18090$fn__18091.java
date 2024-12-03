/*
 * Decompiled with CFR 0.152.
 */
import byte_streams.pushback_stream.PushbackStream;
import clojure.lang.AFunction;
import java.nio.ByteBuffer;

public final class byte_streams$fn__18090$fn__18091
extends AFunction {
    Object ps;

    public byte_streams$fn__18090$fn__18091(Object object) {
        this.ps = object;
    }

    @Override
    public Object invoke(Object buf) {
        Object object = buf;
        buf = null;
        return ((PushbackStream)this.ps).put((ByteBuffer)object);
    }
}

