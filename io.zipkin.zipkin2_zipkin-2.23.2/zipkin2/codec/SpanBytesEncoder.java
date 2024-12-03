/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.util.List;
import zipkin2.Span;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.Proto3Codec;
import zipkin2.internal.V1JsonSpanWriter;
import zipkin2.internal.V1ThriftSpanWriter;
import zipkin2.internal.V2SpanWriter;

public enum SpanBytesEncoder implements BytesEncoder<Span>
{
    JSON_V1{

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public int sizeInBytes(Span input) {
            return new V1JsonSpanWriter().sizeInBytes(input);
        }

        @Override
        public byte[] encode(Span span) {
            return JsonCodec.write(new V1JsonSpanWriter(), span);
        }

        @Override
        public byte[] encodeList(List<Span> spans) {
            return JsonCodec.writeList(new V1JsonSpanWriter(), spans);
        }

        @Override
        public int encodeList(List<Span> spans, byte[] out, int pos) {
            return JsonCodec.writeList(new V1JsonSpanWriter(), spans, out, pos);
        }
    }
    ,
    THRIFT{

        @Override
        public Encoding encoding() {
            return Encoding.THRIFT;
        }

        @Override
        public int sizeInBytes(Span input) {
            return new V1ThriftSpanWriter().sizeInBytes(input);
        }

        @Override
        public byte[] encode(Span span) {
            return new V1ThriftSpanWriter().write(span);
        }

        @Override
        public byte[] encodeList(List<Span> spans) {
            return new V1ThriftSpanWriter().writeList(spans);
        }

        @Override
        public int encodeList(List<Span> spans, byte[] out, int pos) {
            return new V1ThriftSpanWriter().writeList(spans, out, pos);
        }
    }
    ,
    JSON_V2{
        final V2SpanWriter writer = new V2SpanWriter();

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public int sizeInBytes(Span input) {
            return this.writer.sizeInBytes(input);
        }

        @Override
        public byte[] encode(Span span) {
            return JsonCodec.write(this.writer, span);
        }

        @Override
        public byte[] encodeList(List<Span> spans) {
            return JsonCodec.writeList(this.writer, spans);
        }

        @Override
        public int encodeList(List<Span> spans, byte[] out, int pos) {
            return JsonCodec.writeList(this.writer, spans, out, pos);
        }
    }
    ,
    PROTO3{
        final Proto3Codec codec = new Proto3Codec();

        @Override
        public Encoding encoding() {
            return Encoding.PROTO3;
        }

        @Override
        public int sizeInBytes(Span input) {
            return this.codec.sizeInBytes(input);
        }

        @Override
        public byte[] encode(Span span) {
            return this.codec.write(span);
        }

        @Override
        public byte[] encodeList(List<Span> spans) {
            return this.codec.writeList(spans);
        }

        @Override
        public int encodeList(List<Span> spans, byte[] out, int pos) {
            return this.codec.writeList(spans, out, pos);
        }
    };


    public abstract int encodeList(List<Span> var1, byte[] var2, int var3);
}

