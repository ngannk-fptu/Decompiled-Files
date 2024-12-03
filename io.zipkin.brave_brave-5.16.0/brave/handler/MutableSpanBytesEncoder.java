/*
 * Decompiled with CFR 0.152.
 */
package brave.handler;

import brave.Tag;
import brave.handler.MutableSpan;
import brave.internal.codec.JsonWriter;
import brave.internal.codec.WriteBuffer;
import brave.internal.codec.ZipkinV2JsonWriter;
import java.util.List;

public abstract class MutableSpanBytesEncoder {
    public static MutableSpanBytesEncoder zipkinJsonV2(Tag<Throwable> errorTag) {
        if (errorTag == null) {
            throw new NullPointerException("errorTag == null");
        }
        return new ZipkinJsonV2(errorTag);
    }

    public abstract int sizeInBytes(MutableSpan var1);

    public abstract byte[] encode(MutableSpan var1);

    public abstract byte[] encodeList(List<MutableSpan> var1);

    public abstract int encodeList(List<MutableSpan> var1, byte[] var2, int var3);

    static final class ZipkinJsonV2
    extends MutableSpanBytesEncoder {
        final WriteBuffer.Writer<MutableSpan> writer;

        ZipkinJsonV2(Tag<Throwable> errorTag) {
            this.writer = new ZipkinV2JsonWriter(errorTag);
        }

        @Override
        public int sizeInBytes(MutableSpan input) {
            return this.writer.sizeInBytes(input);
        }

        @Override
        public byte[] encode(MutableSpan span) {
            return JsonWriter.write(this.writer, span);
        }

        @Override
        public byte[] encodeList(List<MutableSpan> spans) {
            return JsonWriter.writeList(this.writer, spans);
        }

        @Override
        public int encodeList(List<MutableSpan> spans, byte[] out, int pos) {
            return JsonWriter.writeList(this.writer, spans, out, pos);
        }
    }
}

