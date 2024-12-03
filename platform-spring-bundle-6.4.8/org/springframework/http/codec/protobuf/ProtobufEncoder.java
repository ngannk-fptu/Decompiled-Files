/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.Message
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.protobuf;

import com.google.protobuf.Message;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.http.codec.protobuf.ProtobufCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProtobufEncoder
extends ProtobufCodecSupport
implements HttpMessageEncoder<Message> {
    private static final List<MediaType> streamingMediaTypes = MIME_TYPES.stream().map(mimeType -> new MediaType(mimeType.getType(), mimeType.getSubtype(), Collections.singletonMap("delimited", "true"))).collect(Collectors.toList());

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && this.supportsMimeType(mimeType);
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends Message> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(message -> this.encodeValue((Message)message, bufferFactory, !(inputStream instanceof Mono)));
    }

    @Override
    public DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return this.encodeValue(message, bufferFactory, false);
    }

    private DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, boolean delimited) {
        DataBuffer buffer = bufferFactory.allocateBuffer();
        boolean release = true;
        try {
            if (delimited) {
                message.writeDelimitedTo(buffer.asOutputStream());
            } else {
                message.writeTo(buffer.asOutputStream());
            }
            release = false;
            DataBuffer dataBuffer = buffer;
            return dataBuffer;
        }
        catch (IOException ex) {
            throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
        }
        finally {
            if (release) {
                DataBufferUtils.release(buffer);
            }
        }
    }

    @Override
    public List<MediaType> getStreamingMediaTypes() {
        return streamingMediaTypes;
    }

    @Override
    public List<MimeType> getEncodableMimeTypes() {
        return this.getMimeTypes();
    }
}

