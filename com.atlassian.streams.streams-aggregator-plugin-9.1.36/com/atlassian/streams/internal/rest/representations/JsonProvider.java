/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyReader
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 *  org.codehaus.jackson.jaxrs.JacksonJsonProvider
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializationConfig$Feature
 */
package com.atlassian.streams.internal.rest.representations;

import com.atlassian.streams.internal.rest.representations.ErrorResponseStatusObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

@Provider
@Produces(value={"application/vnd.atl.streams+json", "application/json"})
@Consumes(value={"application/vnd.atl.streams+json", "application/json"})
public class JsonProvider
implements MessageBodyReader<Object>,
MessageBodyWriter<Object> {
    private final JacksonJsonProvider provider = new JacksonJsonProvider();

    public JsonProvider() {
        this.provider.setMapper((ObjectMapper)new ErrorResponseStatusObjectMapper());
        this.provider.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
        this.provider.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, false);
        this.provider.configure(SerializationConfig.Feature.WRITE_NULL_PROPERTIES, false);
        this.provider.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.getSize(value, type, genericType, annotations, mediaType);
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.isWriteable(type, genericType, annotations, mediaType);
    }

    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        this.provider.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.provider.isReadable(type, genericType, annotations, mediaType);
    }

    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        return this.provider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }
}

