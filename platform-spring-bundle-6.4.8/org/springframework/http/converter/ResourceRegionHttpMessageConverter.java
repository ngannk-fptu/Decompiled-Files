/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

public class ResourceRegionHttpMessageConverter
extends AbstractGenericHttpMessageConverter<Object> {
    public ResourceRegionHttpMessageConverter() {
        super(MediaType.ALL);
    }

    @Override
    protected MediaType getDefaultContentType(Object object) {
        Resource resource = null;
        if (object instanceof ResourceRegion) {
            resource = ((ResourceRegion)object).getResource();
        } else {
            Collection regions = (Collection)object;
            if (!regions.isEmpty()) {
                resource = ((ResourceRegion)regions.iterator().next()).getResource();
            }
        }
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected ResourceRegion readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canWrite(clazz, null, mediaType);
    }

    @Override
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            return type instanceof Class && ResourceRegion.class.isAssignableFrom((Class)type);
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        if (!(parameterizedType.getRawType() instanceof Class)) {
            return false;
        }
        Class rawType = (Class)parameterizedType.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) {
            return false;
        }
        if (parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class typeArgumentClass = (Class)typeArgument;
        return ResourceRegion.class.isAssignableFrom(typeArgumentClass);
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof ResourceRegion) {
            this.writeResourceRegion((ResourceRegion)object, outputMessage);
        } else {
            Collection regions = (Collection)object;
            if (regions.size() == 1) {
                this.writeResourceRegion((ResourceRegion)regions.iterator().next(), outputMessage);
            } else {
                this.writeResourceRegionCollection((Collection)object, outputMessage);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeResourceRegion(ResourceRegion region, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull((Object)region, "ResourceRegion must not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        long start = region.getPosition();
        long end = start + region.getCount() - 1L;
        long resourceLength = region.getResource().contentLength();
        end = Math.min(end, resourceLength - 1L);
        long rangeLength = end - start + 1L;
        responseHeaders.add("Content-Range", "bytes " + start + '-' + end + '/' + resourceLength);
        responseHeaders.setContentLength(rangeLength);
        InputStream in = region.getResource().getInputStream();
        try {
            StreamUtils.copyRange(in, outputMessage.getBody(), start, end);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException iOException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeResourceRegionCollection(Collection<ResourceRegion> resourceRegions, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(resourceRegions, "Collection of ResourceRegion should not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        MediaType contentType = responseHeaders.getContentType();
        String boundaryString = MimeTypeUtils.generateMultipartBoundaryString();
        responseHeaders.set("Content-Type", "multipart/byteranges; boundary=" + boundaryString);
        OutputStream out = outputMessage.getBody();
        Resource resource = null;
        InputStream in = null;
        long inputStreamPosition = 0L;
        try {
            for (ResourceRegion region : resourceRegions) {
                long start = region.getPosition() - inputStreamPosition;
                if (start < 0L || resource != region.getResource()) {
                    if (in != null) {
                        in.close();
                    }
                    resource = region.getResource();
                    in = resource.getInputStream();
                    inputStreamPosition = 0L;
                    start = region.getPosition();
                }
                long end = start + region.getCount() - 1L;
                ResourceRegionHttpMessageConverter.println(out);
                ResourceRegionHttpMessageConverter.print(out, "--" + boundaryString);
                ResourceRegionHttpMessageConverter.println(out);
                if (contentType != null) {
                    ResourceRegionHttpMessageConverter.print(out, "Content-Type: " + contentType);
                    ResourceRegionHttpMessageConverter.println(out);
                }
                long resourceLength = region.getResource().contentLength();
                end = Math.min(end, resourceLength - inputStreamPosition - 1L);
                ResourceRegionHttpMessageConverter.print(out, "Content-Range: bytes " + region.getPosition() + '-' + (region.getPosition() + region.getCount() - 1L) + '/' + resourceLength);
                ResourceRegionHttpMessageConverter.println(out);
                ResourceRegionHttpMessageConverter.println(out);
                StreamUtils.copyRange(in, out, start, end);
                inputStreamPosition += end + 1L;
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException iOException) {}
        }
        ResourceRegionHttpMessageConverter.println(out);
        ResourceRegionHttpMessageConverter.print(out, "--" + boundaryString + "--");
    }

    private static void println(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    private static void print(OutputStream os, String buf) throws IOException {
        os.write(buf.getBytes(StandardCharsets.US_ASCII));
    }
}

