/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.ResourceEncoder;
import org.springframework.core.codec.ResourceRegionEncoder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResourceHttpMessageWriter
implements HttpMessageWriter<Resource> {
    private static final ResolvableType REGION_TYPE = ResolvableType.forClass(ResourceRegion.class);
    private final ResourceEncoder encoder;
    private final ResourceRegionEncoder regionEncoder;
    private final List<MediaType> mediaTypes;

    public ResourceHttpMessageWriter() {
        this(4096);
    }

    public ResourceHttpMessageWriter(int bufferSize) {
        this.encoder = new ResourceEncoder(bufferSize);
        this.regionEncoder = new ResourceRegionEncoder(bufferSize);
        this.mediaTypes = MediaType.asMediaTypes(this.encoder.getEncodableMimeTypes());
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.encoder.canEncode(elementType, mediaType);
    }

    @Override
    public List<MediaType> getWritableMediaTypes() {
        return this.mediaTypes;
    }

    @Override
    public Mono<Void> write(Publisher<? extends Resource> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        return Mono.from(inputStream).flatMap(resource -> this.writeResource((Resource)resource, elementType, mediaType, message, hints));
    }

    private Mono<Void> writeResource(Resource resource, ResolvableType type, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        long length;
        HttpHeaders headers = message.getHeaders();
        MediaType resourceMediaType = ResourceHttpMessageWriter.getResourceMediaType(mediaType, resource);
        headers.setContentType(resourceMediaType);
        if (headers.getContentLength() < 0L && (length = ResourceHttpMessageWriter.lengthOf(resource)) != -1L) {
            headers.setContentLength(length);
        }
        return ResourceHttpMessageWriter.zeroCopy(resource, null, message).orElseGet(() -> {
            Mono input = Mono.just((Object)resource);
            DataBufferFactory factory = message.bufferFactory();
            Flux<DataBuffer> body = ((AbstractSingleValueEncoder)this.encoder).encode(input, factory, type, (MimeType)resourceMediaType, hints);
            return message.writeWith((Publisher<? extends DataBuffer>)body);
        });
    }

    private static MediaType getResourceMediaType(@Nullable MediaType mediaType, Resource resource) {
        if (mediaType != null && mediaType.isConcrete() && !mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
            return mediaType;
        }
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    private static long lengthOf(Resource resource) {
        if (InputStreamResource.class != resource.getClass()) {
            try {
                return resource.contentLength();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return -1L;
    }

    private static Optional<Mono<Void>> zeroCopy(Resource resource, @Nullable ResourceRegion region, ReactiveHttpOutputMessage message) {
        if (message instanceof ZeroCopyHttpOutputMessage && resource.isFile()) {
            try {
                File file = resource.getFile();
                long pos = region != null ? region.getPosition() : 0L;
                long count = region != null ? region.getCount() : file.length();
                return Optional.of(((ZeroCopyHttpOutputMessage)message).writeWith(file, pos, count));
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return Optional.empty();
    }

    @Override
    public Mono<Void> write(Publisher<? extends Resource> inputStream, @Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        List<HttpRange> ranges;
        HttpHeaders headers = response.getHeaders();
        headers.set("Accept-Ranges", "bytes");
        try {
            ranges = request.getHeaders().getRange();
        }
        catch (IllegalArgumentException ex) {
            response.setStatusCode(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            return response.setComplete();
        }
        return Mono.from(inputStream).flatMap(resource -> {
            if (ranges.isEmpty()) {
                return this.writeResource((Resource)resource, elementType, mediaType, response, hints);
            }
            response.setStatusCode(HttpStatus.PARTIAL_CONTENT);
            List<ResourceRegion> regions = HttpRange.toResourceRegions(ranges, resource);
            MediaType resourceMediaType = ResourceHttpMessageWriter.getResourceMediaType(mediaType, resource);
            if (regions.size() == 1) {
                ResourceRegion region = regions.get(0);
                headers.setContentType(resourceMediaType);
                long contentLength = ResourceHttpMessageWriter.lengthOf(resource);
                if (contentLength != -1L) {
                    long start = region.getPosition();
                    long end = start + region.getCount() - 1L;
                    end = Math.min(end, contentLength - 1L);
                    headers.add("Content-Range", "bytes " + start + '-' + end + '/' + contentLength);
                    headers.setContentLength(end - start + 1L);
                }
                return this.writeSingleRegion(region, response);
            }
            String boundary = MimeTypeUtils.generateMultipartBoundaryString();
            MediaType multipartType = MediaType.parseMediaType("multipart/byteranges;boundary=" + boundary);
            headers.setContentType(multipartType);
            HashMap<String, Object> theHints = new HashMap<String, Object>(hints);
            theHints.put(ResourceRegionEncoder.BOUNDARY_STRING_HINT, boundary);
            return this.encodeAndWriteRegions((Publisher<? extends ResourceRegion>)Flux.fromIterable(regions), resourceMediaType, response, theHints);
        });
    }

    private Mono<Void> writeSingleRegion(ResourceRegion region, ReactiveHttpOutputMessage message) {
        return ResourceHttpMessageWriter.zeroCopy(region.getResource(), region, message).orElseGet(() -> {
            Mono input = Mono.just((Object)region);
            MediaType mediaType = message.getHeaders().getContentType();
            return this.encodeAndWriteRegions((Publisher<? extends ResourceRegion>)input, mediaType, message, Collections.emptyMap());
        });
    }

    private Mono<Void> encodeAndWriteRegions(Publisher<? extends ResourceRegion> publisher, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        Flux<DataBuffer> body = this.regionEncoder.encode(publisher, message.bufferFactory(), REGION_TYPE, (MimeType)mediaType, hints);
        return message.writeWith((Publisher<? extends DataBuffer>)body);
    }
}

