/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.List;
import zipkin2.Span;
import zipkin2.internal.Proto3Fields;
import zipkin2.internal.Proto3ZipkinFields;
import zipkin2.internal.WriteBuffer;

final class Proto3SpanWriter
implements WriteBuffer.Writer<Span> {
    static final byte[] EMPTY_ARRAY = new byte[0];

    Proto3SpanWriter() {
    }

    @Override
    public int sizeInBytes(Span span) {
        return Proto3ZipkinFields.SPAN.sizeInBytes(span);
    }

    @Override
    public void write(Span value, WriteBuffer b) {
        Proto3ZipkinFields.SPAN.write(b, value);
    }

    public String toString() {
        return "Span";
    }

    public byte[] writeList(List<Span> spans) {
        int lengthOfSpans = spans.size();
        if (lengthOfSpans == 0) {
            return EMPTY_ARRAY;
        }
        if (lengthOfSpans == 1) {
            return this.write(spans.get(0));
        }
        int sizeInBytes = 0;
        int[] sizeOfValues = new int[lengthOfSpans];
        for (int i = 0; i < lengthOfSpans; ++i) {
            int sizeOfValue = sizeOfValues[i] = Proto3ZipkinFields.SPAN.sizeOfValue(spans.get(i));
            sizeInBytes += Proto3Fields.sizeOfLengthDelimitedField(sizeOfValue);
        }
        byte[] result = new byte[sizeInBytes];
        WriteBuffer writeBuffer = WriteBuffer.wrap(result);
        for (int i = 0; i < lengthOfSpans; ++i) {
            this.writeSpan(spans.get(i), sizeOfValues[i], writeBuffer);
        }
        return result;
    }

    byte[] write(Span onlySpan) {
        int sizeOfValue = Proto3ZipkinFields.SPAN.sizeOfValue(onlySpan);
        byte[] result = new byte[Proto3Fields.sizeOfLengthDelimitedField(sizeOfValue)];
        this.writeSpan(onlySpan, sizeOfValue, WriteBuffer.wrap(result));
        return result;
    }

    void writeSpan(Span span, int sizeOfSpan, WriteBuffer result) {
        result.writeByte(Proto3ZipkinFields.SPAN.key);
        result.writeVarint(sizeOfSpan);
        Proto3ZipkinFields.SPAN.writeValue(result, span);
    }

    int writeList(List<Span> spans, byte[] out, int pos) {
        int lengthOfSpans = spans.size();
        if (lengthOfSpans == 0) {
            return 0;
        }
        WriteBuffer result = WriteBuffer.wrap(out, pos);
        for (int i = 0; i < lengthOfSpans; ++i) {
            Proto3ZipkinFields.SPAN.write(result, spans.get(i));
        }
        return result.pos() - pos;
    }
}

