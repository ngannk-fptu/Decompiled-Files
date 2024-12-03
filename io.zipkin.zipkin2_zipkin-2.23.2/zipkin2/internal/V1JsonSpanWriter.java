/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.Span;
import zipkin2.internal.V1SpanWriter;
import zipkin2.internal.WriteBuffer;
import zipkin2.v1.V1Span;
import zipkin2.v1.V2SpanConverter;

public final class V1JsonSpanWriter
implements WriteBuffer.Writer<Span> {
    final V2SpanConverter converter = V2SpanConverter.create();
    final V1SpanWriter v1SpanWriter = new V1SpanWriter();

    @Override
    public int sizeInBytes(Span value) {
        V1Span v1Span = this.converter.convert(value);
        return this.v1SpanWriter.sizeInBytes(v1Span);
    }

    @Override
    public void write(Span value, WriteBuffer b) {
        V1Span v1Span = this.converter.convert(value);
        this.v1SpanWriter.write(v1Span, b);
    }
}

