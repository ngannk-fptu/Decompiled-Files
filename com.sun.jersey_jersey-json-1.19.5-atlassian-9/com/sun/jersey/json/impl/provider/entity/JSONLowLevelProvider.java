/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider
 *  javax.ws.rs.core.MediaType
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

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == this.c && this.isSupported(mediaType);
    }

    protected boolean isSupported(MediaType m) {
        return true;
    }
}

