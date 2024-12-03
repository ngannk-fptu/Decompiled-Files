/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.MessageBodyWriter
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.core.rest.async.LegacyAsyncTaskRepresentation;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.representations.ErrorRepresentation;
import com.atlassian.upm.core.rest.representations.JsonProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(value={"text/html"})
public class HtmlWrappedJsonProvider
implements MessageBodyWriter<Object> {
    private final JsonProvider jsonProvider;

    public HtmlWrappedJsonProvider(BaseRepresentationFactory representationFactory) {
        this.jsonProvider = new JsonProvider(Objects.requireNonNull(representationFactory, "representationFactory"));
    }

    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return LegacyAsyncTaskRepresentation.class.isAssignableFrom(type) || ErrorRepresentation.class.isAssignableFrom(type);
    }

    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream);
        writer.write("<textarea>");
        ((Writer)writer).flush();
        this.jsonProvider.writeTo(value, type, genericType, annotations, MediaType.APPLICATION_JSON_TYPE, httpHeaders, entityStream);
        writer.write("</textarea>");
        ((Writer)writer).flush();
    }
}

