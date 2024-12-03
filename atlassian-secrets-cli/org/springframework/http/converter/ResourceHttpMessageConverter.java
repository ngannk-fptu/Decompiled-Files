/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

public class ResourceHttpMessageConverter
extends AbstractHttpMessageConverter<Resource> {
    private final boolean supportsReadStreaming;

    public ResourceHttpMessageConverter() {
        super(MediaType.ALL);
        this.supportsReadStreaming = true;
    }

    public ResourceHttpMessageConverter(boolean supportsReadStreaming) {
        super(MediaType.ALL);
        this.supportsReadStreaming = supportsReadStreaming;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz);
    }

    @Override
    protected Resource readInternal(Class<? extends Resource> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        if (this.supportsReadStreaming && InputStreamResource.class == clazz) {
            return new InputStreamResource(inputMessage.getBody()){

                @Override
                public String getFilename() {
                    return inputMessage.getHeaders().getContentDisposition().getFilename();
                }
            };
        }
        if (Resource.class == clazz || ByteArrayResource.class.isAssignableFrom(clazz)) {
            byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
            return new ByteArrayResource(body){

                @Override
                @Nullable
                public String getFilename() {
                    return inputMessage.getHeaders().getContentDisposition().getFilename();
                }
            };
        }
        throw new HttpMessageNotReadableException("Unsupported resource class: " + clazz);
    }

    @Override
    protected MediaType getDefaultContentType(Resource resource) {
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    protected Long getContentLength(Resource resource, @Nullable MediaType contentType) throws IOException {
        if (InputStreamResource.class == resource.getClass()) {
            return null;
        }
        long contentLength = resource.contentLength();
        return contentLength < 0L ? null : Long.valueOf(contentLength);
    }

    @Override
    protected void writeInternal(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeContent(resource, outputMessage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void writeContent(Resource resource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            InputStream in = resource.getInputStream();
            try {
                StreamUtils.copy(in, outputMessage.getBody());
            }
            catch (NullPointerException nullPointerException) {
            }
            finally {
                try {
                    in.close();
                }
                catch (Throwable throwable) {}
            }
        }
        catch (FileNotFoundException fileNotFoundException) {
            // empty catch block
        }
    }
}

