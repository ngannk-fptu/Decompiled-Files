/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.template;

import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.ServerSide;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.spi.template.TemplateContext;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;

@ConstrainedTo(value=ServerSide.class)
public final class ViewableMessageBodyWriter
implements MessageBodyWriter<Viewable> {
    @Context
    UriInfo ui;
    @Context
    TemplateContext tc;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Viewable.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(Viewable v, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        ResolvedViewable rv = this.resolve(v);
        if (rv == null) {
            throw new IOException("The template name, " + v.getTemplateName() + ", could not be resolved to a fully qualified template name");
        }
        rv.writeTo(entityStream);
    }

    private ResolvedViewable resolve(Viewable v) {
        if (v instanceof ResolvedViewable) {
            return (ResolvedViewable)v;
        }
        return this.tc.resolveViewable(v, this.ui);
    }

    @Override
    public long getSize(Viewable t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }
}

