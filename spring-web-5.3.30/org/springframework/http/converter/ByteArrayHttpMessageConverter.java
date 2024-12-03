/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StreamUtils
 */
package org.springframework.http.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

public class ByteArrayHttpMessageConverter
extends AbstractHttpMessageConverter<byte[]> {
    public ByteArrayHttpMessageConverter() {
        super(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return byte[].class == clazz;
    }

    @Override
    public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
        long contentLength = inputMessage.getHeaders().getContentLength();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(contentLength >= 0L ? (int)contentLength : 4096);
        StreamUtils.copy((InputStream)inputMessage.getBody(), (OutputStream)bos);
        return bos.toByteArray();
    }

    @Override
    protected Long getContentLength(byte[] bytes, @Nullable MediaType contentType) {
        return bytes.length;
    }

    @Override
    protected void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
        StreamUtils.copy((byte[])bytes, (OutputStream)outputMessage.getBody());
    }
}

