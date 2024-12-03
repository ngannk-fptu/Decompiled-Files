/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.AsyncViewResource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ComponentsClientConfig;
import com.sun.jersey.api.client.ViewResource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.Filterable;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import com.sun.jersey.client.proxy.ViewProxy;
import com.sun.jersey.client.proxy.ViewProxyProvider;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderFactory;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCProviderFactory;
import com.sun.jersey.core.spi.factory.ContextResolverFactory;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.core.util.LazyVal;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.inject.ClientSide;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.spi.service.ServiceFinder;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

public class Client
extends Filterable
implements ClientHandler {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private ProviderFactory componentProviderFactory;
    private Providers providers;
    private boolean destroyed = false;
    private LazyVal<ExecutorService> executorService;
    private CopyOnWriteHashMap<String, Object> properties;
    private Set<ViewProxyProvider> vpps;
    private MessageBodyFactory workers;

    public Client() {
        this(Client.createDefaultClientHander(), new DefaultClientConfig(), null);
    }

    public Client(ClientHandler root) {
        this(root, new DefaultClientConfig(), null);
    }

    public Client(ClientHandler root, ClientConfig config) {
        this(root, config, null);
    }

    public Client(final ClientHandler root, final ClientConfig config, final IoCComponentProviderFactory provider) {
        super(root);
        Errors.processWithErrors(new Errors.Closure<Void>(){

            @Override
            public Void f() {
                Errors.setReportMissingDependentFieldOrMethod(false);
                Client.this.init(root, config, provider);
                return null;
            }
        });
    }

    private void init(ClientHandler root, ClientConfig config, IoCComponentProviderFactory provider) {
        MessageBodyFactory bodyContext;
        final Object threadpoolSize = config.getProperties().get("com.sun.jersey.client.property.threadpoolSize");
        this.executorService = new LazyVal<ExecutorService>(){

            @Override
            protected ExecutorService instance() {
                if (threadpoolSize != null && threadpoolSize instanceof Integer && (Integer)threadpoolSize > 0) {
                    return Executors.newFixedThreadPool((Integer)threadpoolSize);
                }
                return Executors.newCachedThreadPool();
            }
        };
        Class<?>[] components = ServiceFinder.find("jersey-client-components").toClassArray();
        if (components.length > 0) {
            if (LOGGER.isLoggable(Level.INFO)) {
                StringBuilder b = new StringBuilder();
                b.append("Adding the following classes declared in META-INF/services/jersey-client-components to the client configuration:");
                for (Class<?> c : components) {
                    b.append('\n').append("  ").append(c);
                }
                LOGGER.log(Level.INFO, b.toString());
            }
            config = new ComponentsClientConfig(config, components);
        }
        final InjectableProviderFactory injectableFactory = new InjectableProviderFactory();
        this.getProperties().putAll(config.getProperties());
        if (provider != null && provider instanceof IoCComponentProcessorFactoryInitializer) {
            IoCComponentProcessorFactoryInitializer i = (IoCComponentProcessorFactoryInitializer)((Object)provider);
            i.init(new ComponentProcessorFactoryImpl(injectableFactory));
        }
        this.componentProviderFactory = provider == null ? new ProviderFactory(injectableFactory) : new IoCProviderFactory((InjectableProviderContext)injectableFactory, provider);
        ProviderServices providerServices = new ProviderServices(ClientSide.class, this.componentProviderFactory, config.getClasses(), config.getSingletons());
        this.vpps = providerServices.getServices(ViewProxyProvider.class);
        injectableFactory.add(new ContextInjectableProvider<ClientConfig>((Type)((Object)FeaturesAndProperties.class), config));
        injectableFactory.add(new ContextInjectableProvider<ClientConfig>((Type)((Object)ClientConfig.class), config));
        injectableFactory.add(new ContextInjectableProvider<Client>((Type)((Object)Client.class), this));
        injectableFactory.configure(providerServices);
        final ContextResolverFactory crf = new ContextResolverFactory();
        this.workers = bodyContext = new MessageBodyFactory(providerServices, config.getFeature("com.sun.jersey.config.feature.Pre14ProviderPrecedence"));
        injectableFactory.add(new ContextInjectableProvider<MessageBodyFactory>((Type)((Object)MessageBodyWorkers.class), bodyContext));
        this.providers = new Providers(){

            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType m) {
                return bodyContext.getMessageBodyReader(c, t, as, m);
            }

            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType m) {
                return bodyContext.getMessageBodyWriter(c, t, as, m);
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
        injectableFactory.add(new ContextInjectableProvider<Providers>((Type)((Object)Providers.class), this.providers));
        injectableFactory.add(new InjectableProvider<Context, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Injectable> getInjectable(ComponentContext ic, Context a, Type c) {
                ParameterizedType pt;
                if (c instanceof ParameterizedType && (pt = (ParameterizedType)c).getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                    final Injectable i = injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
                    if (i == null) {
                        return null;
                    }
                    return new Injectable<Injectable>(){

                        @Override
                        public Injectable getValue() {
                            return i;
                        }
                    };
                }
                return null;
            }
        });
        crf.init(providerServices, injectableFactory);
        bodyContext.init();
        Errors.setReportMissingDependentFieldOrMethod(true);
        this.componentProviderFactory.injectOnAllComponents();
        this.componentProviderFactory.injectOnProviderInstances(config.getSingletons());
        this.componentProviderFactory.injectOnProviderInstance(root);
    }

    public void destroy() {
        if (!this.destroyed) {
            this.componentProviderFactory.destroy();
            this.destroyed = true;
        }
    }

    protected void finalize() throws Throwable {
        this.destroy();
        super.finalize();
    }

    public Providers getProviders() {
        return this.providers;
    }

    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.workers;
    }

    public WebResource resource(String u) {
        return this.resource(URI.create(u));
    }

    public WebResource resource(URI u) {
        return new WebResource(this, this.properties, u);
    }

    public AsyncWebResource asyncResource(String u) {
        return this.asyncResource(URI.create(u));
    }

    public AsyncWebResource asyncResource(URI u) {
        return new AsyncWebResource(this, this.properties, u);
    }

    public ViewResource viewResource(String u) {
        return this.viewResource(URI.create(u));
    }

    public ViewResource viewResource(URI u) {
        return new ViewResource(this, u);
    }

    public AsyncViewResource asyncViewResource(String u) {
        return this.asyncViewResource(URI.create(u));
    }

    public AsyncViewResource asyncViewResource(URI u) {
        return new AsyncViewResource(this, u);
    }

    public <T> T view(String u, Class<T> type) {
        ViewResource vr = this.viewResource(u);
        return (T)vr.get(type);
    }

    public <T> T view(URI uri, Class<T> type) {
        ViewResource vr = this.viewResource(uri);
        return (T)vr.get(type);
    }

    public <T> T view(String u, T t) {
        ViewResource vr = this.viewResource(u);
        return vr.get(t);
    }

    public <T> T view(URI uri, T t) {
        ViewResource vr = this.viewResource(uri);
        return vr.get(t);
    }

    public <T> Future<T> asyncView(String u, Class<T> type) {
        AsyncViewResource vr = this.asyncViewResource(u);
        return vr.get(type);
    }

    public <T> Future<T> asyncView(URI uri, Class<T> type) {
        AsyncViewResource vr = this.asyncViewResource(uri);
        return vr.get(type);
    }

    public <T> Future<T> asyncView(String u, T t) {
        AsyncViewResource vr = this.asyncViewResource(u);
        return vr.get(t);
    }

    public <T> Future<T> asyncView(URI uri, T t) {
        AsyncViewResource vr = this.asyncViewResource(uri);
        return vr.get(t);
    }

    public <T> T view(Class<T> c, ClientResponse response) {
        return (T)this.getViewProxy(c).view(c, response);
    }

    public <T> T view(T t, ClientResponse response) {
        return (T)this.getViewProxy(t.getClass()).view(t, response);
    }

    public <T> ViewProxy<T> getViewProxy(Class<T> c) {
        for (ViewProxyProvider vpp : this.vpps) {
            ViewProxy<T> vp = vpp.proxy(this, c);
            if (vp == null) continue;
            return vp;
        }
        throw new IllegalArgumentException("A view proxy is not available for the class '" + c.getName() + "'");
    }

    public void setExecutorService(ExecutorService es) {
        if (es == null) {
            throw new IllegalArgumentException("ExecutorService service MUST not be null");
        }
        this.executorService.set(es);
    }

    public ExecutorService getExecutorService() {
        return this.executorService.get();
    }

    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new CopyOnWriteHashMap();
        }
        return this.properties;
    }

    public void setFollowRedirects(Boolean redirect) {
        this.getProperties().put("com.sun.jersey.client.property.followRedirects", redirect);
    }

    public void setReadTimeout(Integer interval) {
        this.getProperties().put("com.sun.jersey.client.property.readTimeout", interval);
    }

    public void setConnectTimeout(Integer interval) {
        this.getProperties().put("com.sun.jersey.client.property.connectTimeout", interval);
    }

    public void setChunkedEncodingSize(Integer chunkSize) {
        this.getProperties().put("com.sun.jersey.client.property.chunkedEncodingSize", chunkSize);
    }

    @Override
    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
        HashMap<String, Object> effectiveProperties = new HashMap<String, Object>(this.properties);
        effectiveProperties.put(Client.class.getName(), this);
        effectiveProperties.putAll(request.getProperties());
        request.setProperties(effectiveProperties);
        ClientResponse response = this.getHeadHandler().handle(request);
        response.getProperties().put(Client.class.getName(), this);
        return response;
    }

    public void inject(Object o) {
        this.componentProviderFactory.injectOnProviderInstance(o);
    }

    public static Client create() {
        return new Client(Client.createDefaultClientHander());
    }

    public static Client create(ClientConfig cc) {
        return new Client(Client.createDefaultClientHander(), cc);
    }

    public static Client create(ClientConfig cc, IoCComponentProviderFactory provider) {
        return new Client(Client.createDefaultClientHander(), cc, provider);
    }

    private static ClientHandler createDefaultClientHander() {
        return new URLConnectionClientHandler();
    }

    private class ComponentProcessorFactoryImpl
    implements IoCComponentProcessorFactory {
        private final InjectableProviderFactory injectableFactory;

        ComponentProcessorFactoryImpl(InjectableProviderFactory injectableFactory) {
            this.injectableFactory = injectableFactory;
        }

        @Override
        public ComponentScope getScope(Class c) {
            return ComponentScope.Singleton;
        }

        @Override
        public IoCComponentProcessor get(Class c, ComponentScope scope) {
            final ComponentInjector ci = new ComponentInjector(this.injectableFactory, c);
            return new IoCComponentProcessor(){

                @Override
                public void preConstruct() {
                }

                @Override
                public void postConstruct(Object o) {
                    ci.inject(o);
                }
            };
        }
    }

    private static class ContextInjectableProvider<T>
    extends SingletonTypeInjectableProvider<Context, T> {
        ContextInjectableProvider(Type type, T instance) {
            super(type, instance);
        }
    }
}

