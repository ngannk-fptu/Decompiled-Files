/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractHttpMessageConverter<T>
implements HttpMessageConverter<T> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private List<MediaType> supportedMediaTypes = Collections.emptyList();
    @Nullable
    private Charset defaultCharset;

    protected AbstractHttpMessageConverter() {
    }

    protected AbstractHttpMessageConverter(MediaType supportedMediaType) {
        this.setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    protected AbstractHttpMessageConverter(MediaType ... supportedMediaTypes) {
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    protected AbstractHttpMessageConverter(Charset defaultCharset, MediaType ... supportedMediaTypes) {
        this.defaultCharset = defaultCharset;
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        Assert.notEmpty(supportedMediaTypes, "MediaType List must not be empty");
        this.supportedMediaTypes = new ArrayList<MediaType>(supportedMediaTypes);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public void setDefaultCharset(@Nullable Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    @Nullable
    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.supports(clazz) && this.canRead(mediaType);
    }

    protected boolean canRead(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (!supportedMediaType.includes(mediaType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.supports(clazz) && this.canWrite(mediaType);
    }

    protected boolean canWrite(@Nullable MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : this.getSupportedMediaTypes()) {
            if (!supportedMediaType.isCompatibleWith(mediaType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return this.readInternal(clazz, inputMessage);
    }

    @Override
    public final void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final HttpHeaders headers = outputMessage.getHeaders();
        this.addDefaultHeaders(headers, t, contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> this.writeInternal(t, new HttpOutputMessage(){

                @Override
                public OutputStream getBody() {
                    return outputStream;
                }

                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            }));
        } else {
            this.writeInternal(t, outputMessage);
            outputMessage.getBody().flush();
        }
    }

    protected void addDefaultHeaders(HttpHeaders headers, T t, @Nullable MediaType contentType) throws IOException {
        Long contentLength;
        if (headers.getContentType() == null) {
            MediaType contentTypeToUse = contentType;
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentTypeToUse = this.getDefaultContentType(t);
            } else if (MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                MediaType mediaType = this.getDefaultContentType(t);
                MediaType mediaType2 = contentTypeToUse = mediaType != null ? mediaType : contentTypeToUse;
            }
            if (contentTypeToUse != null) {
                Charset defaultCharset;
                if (contentTypeToUse.getCharset() == null && (defaultCharset = this.getDefaultCharset()) != null) {
                    contentTypeToUse = new MediaType(contentTypeToUse, defaultCharset);
                }
                headers.setContentType(contentTypeToUse);
            }
        }
        if (headers.getContentLength() < 0L && !headers.containsKey("Transfer-Encoding") && (contentLength = this.getContentLength(t, headers.getContentType())) != null) {
            headers.setContentLength(contentLength);
        }
    }

    @Nullable
    protected MediaType getDefaultContentType(T t) throws IOException {
        List<MediaType> mediaTypes = this.getSupportedMediaTypes();
        return !mediaTypes.isEmpty() ? mediaTypes.get(0) : null;
    }

    @Nullable
    protected Long getContentLength(T t, @Nullable MediaType contentType) throws IOException {
        return null;
    }

    protected abstract boolean supports(Class<?> var1);

    protected abstract T readInternal(Class<? extends T> var1, HttpInputMessage var2) throws IOException, HttpMessageNotReadableException;

    protected abstract void writeInternal(T var1, HttpOutputMessage var2) throws IOException, HttpMessageNotWritableException;
}

