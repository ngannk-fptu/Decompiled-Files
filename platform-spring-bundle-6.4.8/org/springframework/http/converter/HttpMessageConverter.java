/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

public interface HttpMessageConverter<T> {
    public boolean canRead(Class<?> var1, @Nullable MediaType var2);

    public boolean canWrite(Class<?> var1, @Nullable MediaType var2);

    public List<MediaType> getSupportedMediaTypes();

    default public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        return this.canRead(clazz, null) || this.canWrite(clazz, null) ? this.getSupportedMediaTypes() : Collections.emptyList();
    }

    public T read(Class<? extends T> var1, HttpInputMessage var2) throws IOException, HttpMessageNotReadableException;

    public void write(T var1, @Nullable MediaType var2, HttpOutputMessage var3) throws IOException, HttpMessageNotWritableException;
}

