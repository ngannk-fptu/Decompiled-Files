/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

public interface GenericHttpMessageConverter<T>
extends HttpMessageConverter<T> {
    public boolean canRead(Type var1, @Nullable Class<?> var2, @Nullable MediaType var3);

    public T read(Type var1, @Nullable Class<?> var2, HttpInputMessage var3) throws IOException, HttpMessageNotReadableException;

    public boolean canWrite(@Nullable Type var1, Class<?> var2, @Nullable MediaType var3);

    public void write(T var1, @Nullable Type var2, @Nullable MediaType var3, HttpOutputMessage var4) throws IOException, HttpMessageNotWritableException;
}

