/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zipkin2.Span;
import zipkin2.codec.BytesDecoder;
import zipkin2.codec.Encoding;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.Nullable;
import zipkin2.internal.Proto3Codec;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.ThriftCodec;
import zipkin2.internal.V1JsonSpanReader;
import zipkin2.internal.V2SpanReader;
import zipkin2.v1.V1Span;
import zipkin2.v1.V1SpanConverter;

public enum SpanBytesDecoder implements BytesDecoder<Span>
{
    JSON_V1{

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public boolean decode(byte[] span, Collection<Span> out) {
            Span result = this.decodeOne(ReadBuffer.wrap(span));
            if (result == null) {
                return false;
            }
            out.add(result);
            return true;
        }

        @Override
        public boolean decodeList(byte[] spans, Collection<Span> out) {
            return new V1JsonSpanReader().readList(ReadBuffer.wrap(spans), out);
        }

        @Override
        public boolean decodeList(ByteBuffer spans, Collection<Span> out) {
            return new V1JsonSpanReader().readList(ReadBuffer.wrapUnsafe(spans), out);
        }

        @Override
        @Nullable
        public Span decodeOne(byte[] span) {
            return this.decodeOne(ReadBuffer.wrap(span));
        }

        @Override
        @Nullable
        public Span decodeOne(ByteBuffer span) {
            return this.decodeOne(ReadBuffer.wrapUnsafe(span));
        }

        Span decodeOne(ReadBuffer buffer) {
            V1Span v1 = JsonCodec.readOne(new V1JsonSpanReader(), buffer);
            ArrayList<Span> out = new ArrayList<Span>(1);
            V1SpanConverter.create().convert(v1, out);
            return (Span)out.get(0);
        }

        @Override
        public List<Span> decodeList(byte[] spans) {
            return 1.doDecodeList((SpanBytesDecoder)this, spans);
        }

        @Override
        public List<Span> decodeList(ByteBuffer spans) {
            return 1.doDecodeList((SpanBytesDecoder)this, spans);
        }
    }
    ,
    THRIFT{

        @Override
        public Encoding encoding() {
            return Encoding.THRIFT;
        }

        @Override
        public boolean decode(byte[] span, Collection<Span> out) {
            return ThriftCodec.read(ReadBuffer.wrap(span), out);
        }

        @Override
        public boolean decodeList(byte[] spans, Collection<Span> out) {
            return ThriftCodec.readList(ReadBuffer.wrap(spans), out);
        }

        @Override
        public boolean decodeList(ByteBuffer spans, Collection<Span> out) {
            return ThriftCodec.readList(ReadBuffer.wrapUnsafe(spans), out);
        }

        @Override
        @Nullable
        public Span decodeOne(byte[] span) {
            return ThriftCodec.readOne(ReadBuffer.wrap(span));
        }

        @Override
        @Nullable
        public Span decodeOne(ByteBuffer span) {
            return ThriftCodec.readOne(ReadBuffer.wrapUnsafe(span));
        }

        @Override
        public List<Span> decodeList(byte[] spans) {
            return 2.doDecodeList((SpanBytesDecoder)this, spans);
        }

        @Override
        public List<Span> decodeList(ByteBuffer spans) {
            return 2.doDecodeList((SpanBytesDecoder)this, spans);
        }
    }
    ,
    JSON_V2{

        @Override
        public Encoding encoding() {
            return Encoding.JSON;
        }

        @Override
        public boolean decode(byte[] span, Collection<Span> out) {
            return JsonCodec.read(new V2SpanReader(), ReadBuffer.wrap(span), out);
        }

        @Override
        public boolean decodeList(byte[] spans, Collection<Span> out) {
            return JsonCodec.readList(new V2SpanReader(), ReadBuffer.wrap(spans), out);
        }

        @Override
        public boolean decodeList(ByteBuffer spans, Collection<Span> out) {
            return JsonCodec.readList(new V2SpanReader(), ReadBuffer.wrapUnsafe(spans), out);
        }

        @Override
        @Nullable
        public Span decodeOne(byte[] span) {
            return JsonCodec.readOne(new V2SpanReader(), ReadBuffer.wrap(span));
        }

        @Override
        @Nullable
        public Span decodeOne(ByteBuffer span) {
            return JsonCodec.readOne(new V2SpanReader(), ReadBuffer.wrapUnsafe(span));
        }

        @Override
        public List<Span> decodeList(byte[] spans) {
            return 3.doDecodeList((SpanBytesDecoder)this, spans);
        }

        @Override
        public List<Span> decodeList(ByteBuffer spans) {
            return 3.doDecodeList((SpanBytesDecoder)this, spans);
        }
    }
    ,
    PROTO3{

        @Override
        public Encoding encoding() {
            return Encoding.PROTO3;
        }

        @Override
        public boolean decode(byte[] span, Collection<Span> out) {
            return Proto3Codec.read(ReadBuffer.wrap(span), out);
        }

        @Override
        public boolean decodeList(byte[] spans, Collection<Span> out) {
            return Proto3Codec.readList(ReadBuffer.wrap(spans), out);
        }

        @Override
        public boolean decodeList(ByteBuffer spans, Collection<Span> out) {
            return Proto3Codec.readList(ReadBuffer.wrapUnsafe(spans), out);
        }

        @Override
        @Nullable
        public Span decodeOne(byte[] span) {
            return Proto3Codec.readOne(ReadBuffer.wrap(span));
        }

        @Override
        @Nullable
        public Span decodeOne(ByteBuffer span) {
            return Proto3Codec.readOne(ReadBuffer.wrapUnsafe(span));
        }

        @Override
        public List<Span> decodeList(byte[] spans) {
            return 4.doDecodeList((SpanBytesDecoder)this, spans);
        }

        @Override
        public List<Span> decodeList(ByteBuffer spans) {
            return 4.doDecodeList((SpanBytesDecoder)this, spans);
        }
    };


    public abstract boolean decodeList(ByteBuffer var1, Collection<Span> var2);

    public abstract List<Span> decodeList(ByteBuffer var1);

    @Nullable
    public abstract Span decodeOne(ByteBuffer var1);

    static List<Span> doDecodeList(SpanBytesDecoder decoder, byte[] spans) {
        ArrayList<Span> out = new ArrayList<Span>();
        decoder.decodeList(spans, out);
        return out;
    }

    static List<Span> doDecodeList(SpanBytesDecoder decoder, ByteBuffer spans) {
        ArrayList<Span> out = new ArrayList<Span>();
        decoder.decodeList(spans, out);
        return out;
    }
}

