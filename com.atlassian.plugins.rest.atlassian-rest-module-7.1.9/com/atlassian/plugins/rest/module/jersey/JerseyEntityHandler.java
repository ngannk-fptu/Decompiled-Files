/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  org.osgi.framework.Bundle
 */
package com.atlassian.plugins.rest.module.jersey;

import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugins.rest.module.OsgiComponentProviderFactory;
import com.atlassian.plugins.rest.module.ResourceConfigManager;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.core.spi.component.ioc.IoCProviderFactory;
import com.sun.jersey.core.spi.factory.ContextResolverFactory;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import org.osgi.framework.Bundle;

public class JerseyEntityHandler {
    private final MessageBodyFactory messageBodyFactory;
    private final ResourceConfigManager resourceConfigManager;

    public JerseyEntityHandler(ContainerManagedPlugin plugin, Bundle bundle2) {
        this.resourceConfigManager = new ResourceConfigManager(plugin, bundle2);
        this.messageBodyFactory = Errors.processWithErrors(() -> {
            DefaultResourceConfig config = this.resourceConfigManager.createResourceConfig(Collections.emptyMap(), new String[0], Collections.emptySet(), false);
            OsgiComponentProviderFactory provider = new OsgiComponentProviderFactory(config, plugin);
            final InjectableProviderFactory injectableFactory = new InjectableProviderFactory();
            IoCProviderFactory componentProviderFactory = new IoCProviderFactory((InjectableProviderContext)injectableFactory, provider);
            ProviderServices providerServices = new ProviderServices(componentProviderFactory, ((Application)config).getClasses(), ((Application)config).getSingletons());
            injectableFactory.add(new ContextInjectableProvider<DefaultResourceConfig>((Type)((Object)FeaturesAndProperties.class), config));
            injectableFactory.add(new InjectableProvider<Context, Type>(){

                @Override
                public ComponentScope getScope() {
                    return ComponentScope.Singleton;
                }

                @Override
                public Injectable<Injectable> getInjectable(ComponentContext ic, Context a, Type c) {
                    ParameterizedType pt;
                    if (c instanceof ParameterizedType && (pt = (ParameterizedType)c).getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                        Injectable i = injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
                        if (i == null) {
                            return null;
                        }
                        return () -> i;
                    }
                    return null;
                }
            });
            injectableFactory.configure(providerServices);
            final ContextResolverFactory crf = new ContextResolverFactory();
            crf.init(providerServices, injectableFactory);
            final MessageBodyFactory newMessageBodyFactory = new MessageBodyFactory(providerServices, false);
            injectableFactory.add(new ContextInjectableProvider<MessageBodyFactory>((Type)((Object)MessageBodyWorkers.class), newMessageBodyFactory));
            Providers providers = new Providers(){

                @Override
                public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType m) {
                    return newMessageBodyFactory.getMessageBodyReader(c, t, as, m);
                }

                @Override
                public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType m) {
                    return newMessageBodyFactory.getMessageBodyWriter(c, t, as, m);
                }

                @Override
                public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> c) {
                    throw new IllegalArgumentException("This method is not supported on the client side");
                }

                @Override
                public <T> ContextResolver<T> getContextResolver(Class<T> ct, MediaType m) {
                    return crf.resolve(ct, m);
                }
            };
            injectableFactory.add(new ContextInjectableProvider<2>((Type)((Object)Providers.class), providers));
            newMessageBodyFactory.init();
            Errors.setReportMissingDependentFieldOrMethod(true);
            componentProviderFactory.injectOnAllComponents();
            componentProviderFactory.injectOnProviderInstances(((Application)config).getSingletons());
            return newMessageBodyFactory;
        });
    }

    JerseyEntityHandler(MessageBodyFactory msgBodyFactory, ResourceConfigManager resourceConfigMgr) {
        this.messageBodyFactory = msgBodyFactory;
        this.resourceConfigManager = resourceConfigMgr;
    }

    public String marshall(Object entity, MediaType mediaType, Charset charset) throws IOException {
        Type entityType;
        if (entity instanceof GenericEntity) {
            GenericEntity ge = (GenericEntity)entity;
            entityType = ge.getType();
            entity = ge.getEntity();
        } else {
            entityType = entity.getClass();
        }
        Class<?> entityClass = entity.getClass();
        MessageBodyWriter<?> writer = this.messageBodyFactory.getMessageBodyWriter(entityClass, entityType, new Annotation[0], mediaType);
        if (writer == null) {
            throw new RuntimeException("Unable to find a message body writer for " + entityClass);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.writeTo(entity, entityClass, entityType, new Annotation[0], mediaType, new OutBoundHeaders(), outputStream);
        try {
            return outputStream.toString(charset.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Should never happen as we have already seen the Charset is supported as we have it as a parameter");
        }
    }

    public <T> T unmarshall(Class<T> entityClass, MediaType mediaType, InputStream entityStream, Map<String, List<String>> responseHeaders) throws IOException {
        MessageBodyReader<T> reader = this.messageBodyFactory.getMessageBodyReader(entityClass, entityClass, new Annotation[0], mediaType);
        InBoundHeaders headers = new InBoundHeaders();
        headers.putAll(responseHeaders);
        return reader.readFrom(entityClass, entityClass, new Annotation[0], mediaType, headers, entityStream);
    }

    public void destroy() {
        this.resourceConfigManager.destroy();
    }

    private static class ContextInjectableProvider<T>
    extends SingletonTypeInjectableProvider<Context, T> {
        ContextInjectableProvider(Type type, T instance) {
            super(type, instance);
        }
    }
}

