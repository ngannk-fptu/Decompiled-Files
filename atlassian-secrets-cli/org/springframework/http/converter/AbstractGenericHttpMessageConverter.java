/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

public abstract class AbstractGenericHttpMessageConverter<T>
extends AbstractHttpMessageConverter<T>
implements GenericHttpMessageConverter<T> {
    protected AbstractGenericHttpMessageConverter() {
    }

    protected AbstractGenericHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    protected AbstractGenericHttpMessageConverter(MediaType ... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return type instanceof Class ? this.canRead((Class)type, mediaType) : this.canRead(mediaType);
    }

    @Override
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canWrite(clazz, mediaType);
    }

    @Override
    public final void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final HttpHeaders headers = outputMessage.getHeaders();
        this.addDefaultHeaders(headers, t, contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage)outputMessage;
            streamingOutputMessage.setBody(outputStream -> this.writeInternal(t, type, new HttpOutputMessage(){

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
            this.writeInternal(t, type, outputMessage);
            outputMessage.getBody().flush();
        }
    }

    @Override
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeInternal(t, null, outputMessage);
    }

    protected abstract void writeInternal(T var1, @Nullable Type var2, HttpOutputMessage var3) throws IOException, HttpMessageNotWritableException;
}

