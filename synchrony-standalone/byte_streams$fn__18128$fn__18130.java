/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.Numbers;
import clojure.lang.RT;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class byte_streams$fn__18128$fn__18130
extends AFunction {
    Object chunk_size;
    int lim;
    Object buf;

    public byte_streams$fn__18128$fn__18130(Object object, int n, Object object2) {
        this.chunk_size = object;
        this.lim = n;
        this.buf = object2;
    }

    @Override
    public Object invoke(Object p1__18124_SHARP_) {
        Object object = p1__18124_SHARP_;
        p1__18124_SHARP_ = null;
        return ((ByteBuffer)((Buffer)((ByteBuffer)this.buf).duplicate()).position(RT.intCast((Number)p1__18124_SHARP_)).limit(RT.intCast((Number)Numbers.min((long)this.lim, (Object)Numbers.add(object, this.chunk_size))))).slice();
    }
}

