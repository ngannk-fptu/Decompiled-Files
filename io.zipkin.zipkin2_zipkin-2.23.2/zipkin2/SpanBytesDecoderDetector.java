/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.nio.ByteBuffer;
import zipkin2.Span;
import zipkin2.codec.BytesDecoder;
import zipkin2.codec.SpanBytesDecoder;

public final class SpanBytesDecoderDetector {
    static final byte[] ENDPOINT_FIELD_SUFFIX = new byte[]{69, 110, 100, 112, 111, 105, 110, 116, 34};
    static final byte[] TAGS_FIELD = new byte[]{34, 116, 97, 103, 115, 34};

    public static BytesDecoder<Span> decoderForMessage(byte[] span) {
        BytesDecoder<Span> decoder = SpanBytesDecoderDetector.detectDecoder(ByteBuffer.wrap(span));
        if (span[0] == 12 || span[0] == 91) {
            throw new IllegalArgumentException("Expected json or thrift object, not list encoding");
        }
        if (decoder == SpanBytesDecoder.JSON_V2 || decoder == SpanBytesDecoder.PROTO3) {
            throw new UnsupportedOperationException("v2 formats should only be used with list messages");
        }
        return decoder;
    }

    public static BytesDecoder<Span> decoderForListMessage(byte[] spans) {
        return SpanBytesDecoderDetector.decoderForListMessage(ByteBuffer.wrap(spans));
    }

    public static BytesDecoder<Span> decoderForListMessage(ByteBuffer spans) {
        BytesDecoder<Span> decoder = SpanBytesDecoderDetector.detectDecoder(spans);
        byte first = spans.get(spans.position());
        if (first != 12 && first != 11 && !SpanBytesDecoderDetector.protobuf3(spans) && first != 91) {
            throw new IllegalArgumentException("Expected json, proto3 or thrift list encoding");
        }
        return decoder;
    }

    static BytesDecoder<Span> detectDecoder(ByteBuffer bytes) {
        byte first = bytes.get(bytes.position());
        if (first <= 16) {
            if (SpanBytesDecoderDetector.protobuf3(bytes)) {
                return SpanBytesDecoder.PROTO3;
            }
            return SpanBytesDecoder.THRIFT;
        }
        if (first != 91 && first != 123) {
            throw new IllegalArgumentException("Could not detect the span format");
        }
        if (SpanBytesDecoderDetector.contains(bytes, ENDPOINT_FIELD_SUFFIX)) {
            return SpanBytesDecoder.JSON_V2;
        }
        if (SpanBytesDecoderDetector.contains(bytes, TAGS_FIELD)) {
            return SpanBytesDecoder.JSON_V2;
        }
        return SpanBytesDecoder.JSON_V1;
    }

    static boolean contains(ByteBuffer bytes, byte[] subsequence) {
        block0: for (int i = 0; i < bytes.remaining() - subsequence.length + 1; ++i) {
            for (int j = 0; j < subsequence.length; ++j) {
                if (bytes.get(bytes.position() + i + j) != subsequence[j]) continue block0;
            }
            return true;
        }
        return false;
    }

    static boolean protobuf3(ByteBuffer bytes) {
        return bytes.get(bytes.position()) == 10 && bytes.get(bytes.position() + 1) != 0;
    }

    SpanBytesDecoderDetector() {
    }
}

