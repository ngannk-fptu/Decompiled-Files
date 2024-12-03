/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.core.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StringDecoder
extends AbstractDataBufferDecoder<String> {
    private static final DataBuffer END_FRAME = new DefaultDataBufferFactory().wrap(new byte[0]);
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final List<String> DEFAULT_DELIMITERS = Arrays.asList("\r\n", "\n");
    private final List<String> delimiters;
    private final boolean stripDelimiter;

    private StringDecoder(List<String> delimiters, boolean stripDelimiter, MimeType ... mimeTypes) {
        super(mimeTypes);
        Assert.notEmpty(delimiters, "'delimiters' must not be empty");
        this.delimiters = new ArrayList<String>(delimiters);
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return super.canDecode(elementType, mimeType) && String.class.equals(elementType.getRawClass());
    }

    @Override
    public Flux<String> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        List<byte[]> delimiterBytes = this.getDelimiterBytes(mimeType);
        Flux inputFlux = Flux.from(inputStream).flatMap(dataBuffer -> this.splitOnDelimiter((DataBuffer)dataBuffer, delimiterBytes)).bufferUntil(StringDecoder::isEndFrame).flatMap(StringDecoder::joinUntilEndFrame);
        return super.decode((Publisher<DataBuffer>)inputFlux, elementType, mimeType, hints);
    }

    private List<byte[]> getDelimiterBytes(@Nullable MimeType mimeType) {
        Charset charset = StringDecoder.getCharset(mimeType);
        return this.delimiters.stream().map(s -> s.getBytes(charset)).collect(Collectors.toList());
    }

    private Flux<DataBuffer> splitOnDelimiter(DataBuffer dataBuffer, List<byte[]> delimiterBytes) {
        ArrayList<DataBuffer> frames = new ArrayList<DataBuffer>();
        do {
            DataBuffer frame;
            int length = Integer.MAX_VALUE;
            byte[] matchingDelimiter = null;
            for (byte[] delimiter : delimiterBytes) {
                int idx = StringDecoder.indexOf(dataBuffer, delimiter);
                if (idx < 0 || idx >= length) continue;
                length = idx;
                matchingDelimiter = delimiter;
            }
            int readPosition = dataBuffer.readPosition();
            if (matchingDelimiter != null) {
                frame = this.stripDelimiter ? dataBuffer.slice(readPosition, length) : dataBuffer.slice(readPosition, length + matchingDelimiter.length);
                dataBuffer.readPosition(readPosition + length + matchingDelimiter.length);
                frames.add(DataBufferUtils.retain(frame));
                frames.add(END_FRAME);
                continue;
            }
            frame = dataBuffer.slice(readPosition, dataBuffer.readableByteCount());
            dataBuffer.readPosition(readPosition + dataBuffer.readableByteCount());
            frames.add(DataBufferUtils.retain(frame));
        } while (dataBuffer.readableByteCount() > 0);
        DataBufferUtils.release(dataBuffer);
        return Flux.fromIterable(frames);
    }

    private static int indexOf(DataBuffer dataBuffer, byte[] delimiter) {
        for (int i = dataBuffer.readPosition(); i < dataBuffer.writePosition(); ++i) {
            int delimiterPos;
            int dataBufferPos = i;
            for (delimiterPos = 0; delimiterPos < delimiter.length && dataBuffer.getByte(dataBufferPos) == delimiter[delimiterPos]; ++delimiterPos) {
                if (++dataBufferPos != dataBuffer.writePosition() || delimiterPos == delimiter.length - 1) continue;
                return -1;
            }
            if (delimiterPos != delimiter.length) continue;
            return i - dataBuffer.readPosition();
        }
        return -1;
    }

    private static boolean isEndFrame(DataBuffer dataBuffer) {
        return dataBuffer == END_FRAME;
    }

    private static Mono<DataBuffer> joinUntilEndFrame(List<DataBuffer> dataBuffers) {
        int lastIdx;
        if (!dataBuffers.isEmpty() && StringDecoder.isEndFrame(dataBuffers.get(lastIdx = dataBuffers.size() - 1))) {
            dataBuffers.remove(lastIdx);
        }
        Flux flux = Flux.fromIterable(dataBuffers);
        return DataBufferUtils.join((Publisher<DataBuffer>)flux);
    }

    @Override
    protected String decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = StringDecoder.getCharset(mimeType);
        CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
        DataBufferUtils.release(dataBuffer);
        return charBuffer.toString();
    }

    private static Charset getCharset(@Nullable MimeType mimeType) {
        if (mimeType != null && mimeType.getCharset() != null) {
            return mimeType.getCharset();
        }
        return DEFAULT_CHARSET;
    }

    @Deprecated
    public static StringDecoder textPlainOnly(boolean ignored) {
        return StringDecoder.textPlainOnly();
    }

    public static StringDecoder textPlainOnly() {
        return StringDecoder.textPlainOnly(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder textPlainOnly(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    @Deprecated
    public static StringDecoder allMimeTypes(boolean ignored) {
        return StringDecoder.allMimeTypes();
    }

    public static StringDecoder allMimeTypes() {
        return StringDecoder.allMimeTypes(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder allMimeTypes(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}

