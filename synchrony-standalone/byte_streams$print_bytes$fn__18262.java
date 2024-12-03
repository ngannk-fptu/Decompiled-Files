/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import java.nio.ByteBuffer;

public final class byte_streams$print_bytes$fn__18262
extends AFunction {
    Object buf;

    public byte_streams$print_bytes$fn__18262(Object object) {
        this.buf = object;
    }

    @Override
    public Object invoke() {
        return ((ByteBuffer)this.buf).get();
    }
}

