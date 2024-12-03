/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.codec.Hints
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.io.buffer.PooledDataBuffer
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartWriterSupport;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PartHttpMessageWriter
extends MultipartWriterSupport
implements HttpMessageWriter<Part> {
    public PartHttpMessageWriter() {
        super(MultipartHttpMessageReader.MIME_TYPES);
    }

    @Override
    public Mono<Void> write(Publisher<? extends Part> parts, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        byte[] boundary = this.generateMultipartBoundary();
        mediaType = this.getMultipartMediaType(mediaType, boundary);
        outputMessage.getHeaders().setContentType(mediaType);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)(Hints.getLogPrefix(hints) + "Encoding Publisher<Part>"));
        }
        Flux body = Flux.from(parts).concatMap(part -> this.encodePart(boundary, (Part)part, outputMessage.bufferFactory())).concatWith(this.generateLastLine(boundary, outputMessage.bufferFactory())).doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release);
        if (this.logger.isDebugEnabled()) {
            body = body.doOnNext(buffer -> Hints.touchDataBuffer((DataBuffer)buffer, (Map)hints, (Log)this.logger));
        }
        return outputMessage.writeWith((Publisher<? extends DataBuffer>)body);
    }

    private <T> Flux<DataBuffer> encodePart(byte[] boundary, Part part, DataBufferFactory bufferFactory) {
        HttpHeaders headers = new HttpHeaders(part.headers());
        String name = part.name();
        if (!headers.containsKey("Content-Disposition")) {
            headers.setContentDispositionFormData(name, part instanceof FilePart ? ((FilePart)part).filename() : null);
        }
        return Flux.concat((Publisher[])new Publisher[]{this.generateBoundaryLine(boundary, bufferFactory), this.generatePartHeaders(headers, bufferFactory), part.content(), this.generateNewLine(bufferFactory)});
    }
}

