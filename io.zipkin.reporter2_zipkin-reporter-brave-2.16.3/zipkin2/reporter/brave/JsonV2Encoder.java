/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Tag
 *  brave.handler.MutableSpan
 *  brave.handler.MutableSpanBytesEncoder
 *  zipkin2.codec.BytesEncoder
 *  zipkin2.codec.Encoding
 */
package zipkin2.reporter.brave;

import brave.Tag;
import brave.handler.MutableSpan;
import brave.handler.MutableSpanBytesEncoder;
import java.util.List;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;

final class JsonV2Encoder
implements BytesEncoder<MutableSpan> {
    final MutableSpanBytesEncoder delegate;

    JsonV2Encoder(Tag<Throwable> errorTag) {
        this.delegate = MutableSpanBytesEncoder.zipkinJsonV2(errorTag);
    }

    public Encoding encoding() {
        return Encoding.JSON;
    }

    public int sizeInBytes(MutableSpan span) {
        return this.delegate.sizeInBytes(span);
    }

    public byte[] encode(MutableSpan span) {
        return this.delegate.encode(span);
    }

    public byte[] encodeList(List<MutableSpan> spans) {
        return this.delegate.encodeList(spans);
    }
}

