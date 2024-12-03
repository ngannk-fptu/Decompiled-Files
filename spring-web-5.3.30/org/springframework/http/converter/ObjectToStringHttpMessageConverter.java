/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ObjectToStringHttpMessageConverter
extends AbstractHttpMessageConverter<Object> {
    private final ConversionService conversionService;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public ObjectToStringHttpMessageConverter(ConversionService conversionService) {
        this(conversionService, StringHttpMessageConverter.DEFAULT_CHARSET);
    }

    public ObjectToStringHttpMessageConverter(ConversionService conversionService, Charset defaultCharset) {
        super(defaultCharset, MediaType.TEXT_PLAIN);
        Assert.notNull((Object)conversionService, (String)"ConversionService is required");
        this.conversionService = conversionService;
        this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
    }

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canRead(mediaType) && this.conversionService.canConvert(String.class, clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canWrite(mediaType) && this.conversionService.canConvert(clazz, String.class);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Object value = this.stringHttpMessageConverter.readInternal(String.class, inputMessage);
        Object result = this.conversionService.convert(value, clazz);
        if (result == null) {
            throw new HttpMessageNotReadableException("Unexpected null conversion result for '" + (String)value + "' to " + clazz, inputMessage);
        }
        return result;
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException {
        String value = (String)this.conversionService.convert(obj, String.class);
        if (value != null) {
            this.stringHttpMessageConverter.writeInternal(value, outputMessage);
        }
    }

    @Override
    protected Long getContentLength(Object obj, @Nullable MediaType contentType) {
        String value = (String)this.conversionService.convert(obj, String.class);
        if (value == null) {
            return 0L;
        }
        return this.stringHttpMessageConverter.getContentLength(value, contentType);
    }
}

