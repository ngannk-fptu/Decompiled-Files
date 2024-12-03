/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.multipart.FileStorage;
import org.springframework.http.codec.multipart.MultipartParser;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.codec.multipart.PartGenerator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class DefaultPartHttpMessageReader
extends LoggingCodecSupport
implements HttpMessageReader<Part> {
    private int maxInMemorySize = 262144;
    private int maxHeadersSize = 10240;
    private long maxDiskUsagePerPart = -1L;
    private int maxParts = -1;
    private boolean streaming;
    private Scheduler blockingOperationScheduler = Schedulers.boundedElastic();
    private FileStorage fileStorage = FileStorage.tempDirectory(this::getBlockingOperationScheduler);
    private Charset headersCharset = StandardCharsets.UTF_8;

    public void setMaxHeadersSize(int byteCount) {
        this.maxHeadersSize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(int maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }

    public void setMaxDiskUsagePerPart(long maxDiskUsagePerPart) {
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
    }

    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    public void setFileStorageDirectory(Path fileStorageDirectory) throws IOException {
        Assert.notNull((Object)fileStorageDirectory, "FileStorageDirectory must not be null");
        this.fileStorage = FileStorage.fromPath(fileStorageDirectory);
    }

    public void setBlockingOperationScheduler(Scheduler blockingOperationScheduler) {
        Assert.notNull((Object)blockingOperationScheduler, "FileCreationScheduler must not be null");
        this.blockingOperationScheduler = blockingOperationScheduler;
    }

    private Scheduler getBlockingOperationScheduler() {
        return this.blockingOperationScheduler;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public void setHeadersCharset(Charset headersCharset) {
        Assert.notNull((Object)headersCharset, "HeadersCharset must not be null");
        this.headersCharset = headersCharset;
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return Part.class.equals(elementType.toClass()) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override
    public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error((Throwable)new UnsupportedOperationException("Cannot read multipart request body into single Part"));
    }

    @Override
    public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.defer(() -> {
            byte[] boundary = this.boundary(message);
            if (boundary == null) {
                return Flux.error((Throwable)new DecodingException("No multipart boundary found in Content-Type: \"" + message.getHeaders().getContentType() + "\""));
            }
            Flux<MultipartParser.Token> tokens = MultipartParser.parse(message.getBody(), boundary, this.maxHeadersSize, this.headersCharset);
            return PartGenerator.createParts(tokens, this.maxParts, this.maxInMemorySize, this.maxDiskUsagePerPart, this.streaming, this.fileStorage.directory(), this.blockingOperationScheduler);
        });
    }

    @Nullable
    private byte[] boundary(HttpMessage message) {
        String boundary;
        MediaType contentType = message.getHeaders().getContentType();
        if (contentType != null && (boundary = contentType.getParameter("boundary")) != null) {
            int len = boundary.length();
            if (len > 2 && boundary.charAt(0) == '\"' && boundary.charAt(len - 1) == '\"') {
                boundary = boundary.substring(1, len - 1);
            }
            return boundary.getBytes(this.headersCharset);
        }
        return null;
    }
}

