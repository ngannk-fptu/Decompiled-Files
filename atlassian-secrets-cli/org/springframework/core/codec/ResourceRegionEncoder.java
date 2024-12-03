/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.core.codec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.OptionalLong;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResourceRegionEncoder
extends AbstractEncoder<ResourceRegion> {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final String BOUNDARY_STRING_HINT = ResourceRegionEncoder.class.getName() + ".boundaryString";
    private final int bufferSize;

    public ResourceRegionEncoder() {
        this(4096);
    }

    public ResourceRegionEncoder(int bufferSize) {
        super(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL);
        Assert.isTrue(bufferSize > 0, "'bufferSize' must be larger than 0");
        this.bufferSize = bufferSize;
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return super.canEncode(elementType, mimeType) && ResourceRegion.class.isAssignableFrom(elementType.resolve(Object.class));
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends ResourceRegion> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Assert.notNull(inputStream, "'inputStream' must not be null");
        Assert.notNull((Object)bufferFactory, "'bufferFactory' must not be null");
        Assert.notNull((Object)elementType, "'elementType' must not be null");
        if (inputStream instanceof Mono) {
            return ((Mono)inputStream).flatMapMany(region -> this.writeResourceRegion((ResourceRegion)region, bufferFactory));
        }
        Assert.notNull(hints, "'hints' must not be null");
        Assert.isTrue(hints.containsKey(BOUNDARY_STRING_HINT), "'hints' must contain boundaryString hint");
        String boundaryString = (String)hints.get(BOUNDARY_STRING_HINT);
        byte[] startBoundary = this.getAsciiBytes("\r\n--" + boundaryString + "\r\n");
        byte[] contentType = mimeType != null ? this.getAsciiBytes("Content-Type: " + mimeType + "\r\n") : new byte[]{};
        Flux regions = Flux.from(inputStream).concatMap(region -> Flux.concat((Publisher[])new Publisher[]{this.getRegionPrefix(bufferFactory, startBoundary, contentType, (ResourceRegion)region), this.writeResourceRegion((ResourceRegion)region, bufferFactory)}));
        return Flux.concat((Publisher[])new Publisher[]{regions, this.getRegionSuffix(bufferFactory, boundaryString)});
    }

    private Flux<DataBuffer> getRegionPrefix(DataBufferFactory bufferFactory, byte[] startBoundary, byte[] contentType, ResourceRegion region) {
        return Flux.just((Object[])new DataBuffer[]{bufferFactory.allocateBuffer(startBoundary.length).write(startBoundary), bufferFactory.allocateBuffer(contentType.length).write(contentType), bufferFactory.wrap(ByteBuffer.wrap(this.getContentRangeHeader(region)))});
    }

    private Flux<DataBuffer> writeResourceRegion(ResourceRegion region, DataBufferFactory bufferFactory) {
        Resource resource = region.getResource();
        long position = region.getPosition();
        Flux<DataBuffer> in = DataBufferUtils.read(resource, position, bufferFactory, this.bufferSize);
        return DataBufferUtils.takeUntilByteCount(in, region.getCount());
    }

    private Flux<DataBuffer> getRegionSuffix(DataBufferFactory bufferFactory, String boundaryString) {
        byte[] endBoundary = this.getAsciiBytes("\r\n--" + boundaryString + "--");
        return Flux.just((Object)bufferFactory.allocateBuffer(endBoundary.length).write(endBoundary));
    }

    private byte[] getAsciiBytes(String in) {
        return in.getBytes(StandardCharsets.US_ASCII);
    }

    private byte[] getContentRangeHeader(ResourceRegion region) {
        long start = region.getPosition();
        long end = start + region.getCount() - 1L;
        OptionalLong contentLength = this.contentLength(region.getResource());
        if (contentLength.isPresent()) {
            return this.getAsciiBytes("Content-Range: bytes " + start + '-' + end + '/' + contentLength.getAsLong() + "\r\n\r\n");
        }
        return this.getAsciiBytes("Content-Range: bytes " + start + '-' + end + "\r\n\r\n");
    }

    private OptionalLong contentLength(Resource resource) {
        if (InputStreamResource.class != resource.getClass()) {
            try {
                return OptionalLong.of(resource.contentLength());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return OptionalLong.empty();
    }
}

