/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.provider.entity;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class JacksonProviderProxy
implements MessageBodyReader<Object>,
MessageBodyWriter<Object> {
    JacksonJsonProvider pojoProvider = new JacksonJsonProvider();
    JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();
    boolean jacksonEntityProviderFeatureSet = false;

    @Context
    public void setFeaturesAndProperties(FeaturesAndProperties fp) {
        this.jacksonEntityProviderFeatureSet = fp.getFeature("com.sun.jersey.api.json.POJOMappingFeature");
    }

    @Context
    public void setProviders(Providers p) {
        new ComponentInjector<JacksonJsonProvider>(new ProvidersInjectableProviderContext(p), JacksonJsonProvider.class).inject(this.pojoProvider);
        new ComponentInjector<JacksonJaxbJsonProvider>(new ProvidersInjectableProviderContext(p), JacksonJaxbJsonProvider.class).inject(this.jaxbProvider);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.jacksonEntityProviderFeatureSet && (this.jaxbProvider.isReadable(type, genericType, annotations, mediaType) || this.pojoProvider.isReadable(type, genericType, annotations, mediaType));
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return this.jaxbProvider.isReadable(type, genericType, annotations, mediaType) ? this.jaxbProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream) : this.pojoProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.jacksonEntityProviderFeatureSet && (this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType) || this.pojoProvider.isWriteable(type, genericType, annotations, mediaType));
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType) ? this.jaxbProvider.getSize(t, type, genericType, annotations, mediaType) : this.pojoProvider.getSize(t, type, genericType, annotations, mediaType);
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        if (this.jaxbProvider.isWriteable(type, genericType, annotations, mediaType)) {
            this.jaxbProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } else {
            this.pojoProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        }
    }

    private static class ProvidersInjectableProviderContext
    implements InjectableProviderContext {
        final Providers p;
        final Injectable i;

        private ProvidersInjectableProviderContext(final Providers p) {
            this.p = p;
            this.i = new Injectable(){

                public Object getValue() {
                    return p;
                }
            };
        }

        @Override
        public boolean isAnnotationRegistered(Class<? extends Annotation> ac, Class<?> cc) {
            return ac == Context.class;
        }

        @Override
        public boolean isInjectableProviderRegistered(Class<? extends Annotation> ac, Class<?> cc, ComponentScope s) {
            return this.isAnnotationRegistered(ac, cc);
        }

        @Override
        public <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, ComponentScope s) {
            return c == Providers.class ? this.i : null;
        }

        @Override
        public <A extends Annotation, C> Injectable getInjectable(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, List<ComponentScope> ls) {
            return c == Providers.class ? this.i : null;
        }

        @Override
        public <A extends Annotation, C> InjectableProviderContext.InjectableScopePair getInjectableWithScope(Class<? extends Annotation> ac, ComponentContext ic, A a, C c, List<ComponentScope> ls) {
            return c == Providers.class ? new InjectableProviderContext.InjectableScopePair(this.i, ls.get(0)) : null;
        }
    }
}

