/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;

public abstract class JSONLowLevelProvider<T>
extends AbstractMessageReaderWriterProvider<T> {
    private final Class<T> c;

    protected JSONLowLevelProvider(Class<T> c) {
        this.c = c;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }

    protected boolean isSupported(MediaType m) {
        return true;
    }
}

