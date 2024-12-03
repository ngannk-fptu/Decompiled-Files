/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public interface MessageBodyWorkers {
    public Map<MediaType, List<MessageBodyReader>> getReaders(MediaType var1);

    public Map<MediaType, List<MessageBodyWriter>> getWriters(MediaType var1);

    public String readersToString(Map<MediaType, List<MessageBodyReader>> var1);

    public String writersToString(Map<MediaType, List<MessageBodyWriter>> var1);

    public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

    public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> var1, Type var2, Annotation[] var3, MediaType var4);

    public <T> List<MediaType> getMessageBodyWriterMediaTypes(Class<T> var1, Type var2, Annotation[] var3);

    public <T> MediaType getMessageBodyWriterMediaType(Class<T> var1, Type var2, Annotation[] var3, List<MediaType> var4);
}

