/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.container.filter.UriConnegFilter;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.api.core.ParentRef;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.ResourceConfigurator;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import com.sun.jersey.api.model.AbstractResourceModelListener;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.ResourceModelIssue;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderFactory;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCProviderFactory;
import com.sun.jersey.core.spi.factory.ContextResolverFactory;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.BuildId;
import com.sun.jersey.server.impl.ThreadLocalHttpContext;
import com.sun.jersey.server.impl.application.CloseableServiceFactory;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import com.sun.jersey.server.impl.application.ExceptionMapperFactory;
import com.sun.jersey.server.impl.application.ResourceMethodDispatcherFactory;
import com.sun.jersey.server.impl.application.RootResourceUriRules;
import com.sun.jersey.server.impl.application.WebApplicationContext;
import com.sun.jersey.server.impl.component.IoCResourceFactory;
import com.sun.jersey.server.impl.component.ResourceFactory;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderFactory;
import com.sun.jersey.server.impl.model.ResourceUriRules;
import com.sun.jersey.server.impl.model.RulesMap;
import com.sun.jersey.server.impl.model.parameter.CookieParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.FormParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.HeaderParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.HttpContextInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.MatrixParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.PathParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.QueryParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorFactory;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.StringReaderFactory;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import com.sun.jersey.server.impl.modelapi.validation.BasicValidator;
import com.sun.jersey.server.impl.monitoring.MonitoringProviderFactory;
import com.sun.jersey.server.impl.resource.PerRequestFactory;
import com.sun.jersey.server.impl.template.TemplateFactory;
import com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule;
import com.sun.jersey.server.impl.wadl.WadlApplicationContextInjectionProxy;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.spi.StringReaderWorkers;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchFactory;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationListener;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Inject;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.spi.inject.ServerSide;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.spi.monitoring.ResponseListener;
import com.sun.jersey.spi.service.ServiceFinder;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import com.sun.research.ws.wadl.Application;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;

public final class WebApplicationImpl
implements WebApplication {
    private static final Logger LOGGER = Logger.getLogger(WebApplicationImpl.class.getName());
    private final Map<Class, AbstractResource> abstractResourceMap = new HashMap<Class, AbstractResource>();
    private final ConcurrentMap<Class, UriRules<UriRule>> rulesMap = new ConcurrentHashMap<Class, UriRules<UriRule>>();
    private final ConcurrentMap<Class, ResourceComponentProvider> providerMap = new ConcurrentHashMap<Class, ResourceComponentProvider>();
    private final ConcurrentMap<Class, ResourceComponentProvider> singletonMap = new ConcurrentHashMap<Class, ResourceComponentProvider>();
    private final ConcurrentMap<ClassAnnotationKey, ResourceComponentProvider> providerWithAnnotationKeyMap = new ConcurrentHashMap<ClassAnnotationKey, ResourceComponentProvider>();
    private final ThreadLocalHttpContext context;
    private final CloseableServiceFactory closeableFactory;
    private boolean initiated;
    private ResourceConfig resourceConfig;
    private RootResourceClassesRule rootsRule;
    private ServerInjectableProviderFactory injectableFactory;
    private ProviderFactory cpFactory;
    private ResourceFactory rcpFactory;
    private IoCComponentProviderFactory provider;
    private List<IoCComponentProviderFactory> providerFactories;
    private Providers providers;
    private MessageBodyFactory bodyFactory;
    private StringReaderFactory stringReaderFactory;
    private TemplateContext templateContext;
    private ExceptionMapperFactory exceptionFactory;
    private ResourceMethodDispatchProvider dispatcherFactory;
    private ResourceContext resourceContext;
    private Set<AbstractResource> abstractRootResources;
    private Map<String, AbstractResource> explicitAbstractRootResources;
    private final AbstractResourceModelContext armContext = new AbstractResourceModelContext(){

        @Override
        public Set<AbstractResource> getAbstractRootResources() {
            return WebApplicationImpl.this.abstractRootResources;
        }
    };
    private FilterFactory filterFactory;
    private WadlFactory wadlFactory;
    private boolean isTraceEnabled;
    private RequestListener requestListener;
    private DispatchingListenerProxy dispatchingListener;
    private ResponseListener responseListener;
    private static final IoCComponentProcessor NULL_COMPONENT_PROCESSOR = new IoCComponentProcessor(){

        @Override
        public void preConstruct() {
        }

        @Override
        public void postConstruct(Object o) {
        }
    };

    public WebApplicationImpl() {
        this.context = new ThreadLocalHttpContext();
        InvocationHandler requestHandler = new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke((Object)WebApplicationImpl.this.context.getRequest(), args);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        };
        InvocationHandler uriInfoHandler = new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke((Object)WebApplicationImpl.this.context.getUriInfo(), args);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        };
        this.injectableFactory = new ServerInjectableProviderFactory();
        this.injectableFactory.add(new ContextInjectableProvider<ServerInjectableProviderFactory>((Type)((Object)InjectableProviderContext.class), this.injectableFactory));
        this.injectableFactory.add(new ContextInjectableProvider<ServerInjectableProviderFactory>((Type)((Object)ServerInjectableProviderContext.class), this.injectableFactory));
        final HashMap<Class, Object> m = new HashMap<Class, Object>();
        m.put(HttpContext.class, this.context);
        m.put(HttpHeaders.class, this.createProxy(HttpHeaders.class, requestHandler));
        m.put(UriInfo.class, this.createProxy(UriInfo.class, uriInfoHandler));
        m.put(ExtendedUriInfo.class, this.createProxy(ExtendedUriInfo.class, uriInfoHandler));
        m.put(Request.class, this.createProxy(Request.class, requestHandler));
        m.put(SecurityContext.class, this.createProxy(SecurityContext.class, requestHandler));
        this.injectableFactory.add(new InjectableProvider<Context, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable getInjectable(ComponentContext ic, Context a, Type c) {
                final Object o = m.get(c);
                if (o != null) {
                    return new Injectable(){

                        public Object getValue() {
                            return o;
                        }
                    };
                }
                return null;
            }
        });
        this.injectableFactory.add(new InjectableProvider<Context, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Injectable> getInjectable(ComponentContext ic, Context a, Type c) {
                ParameterizedType pt;
                if (c instanceof ParameterizedType && (pt = (ParameterizedType)c).getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                    final Injectable i = WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
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
        this.injectableFactory.add(new InjectableProvider<Inject, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Injectable> getInjectable(ComponentContext ic, Inject a, Type c) {
                ParameterizedType pt;
                if (c instanceof ParameterizedType && (pt = (ParameterizedType)c).getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                    final Injectable i = WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
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
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Injectable> getInjectable(ComponentContext ic, InjectParam a, Type c) {
                ParameterizedType pt;
                if (c instanceof ParameterizedType && (pt = (ParameterizedType)c).getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                    final Injectable i = WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
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
        this.closeableFactory = new CloseableServiceFactory(this.context);
        this.injectableFactory.add(this.closeableFactory);
    }

    @Override
    public FeaturesAndProperties getFeaturesAndProperties() {
        return this.resourceConfig;
    }

    @Override
    public WebApplication clone() {
        WebApplicationImpl wa = new WebApplicationImpl();
        wa.initiate(this.resourceConfig, this.provider);
        return wa;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    UriRules<UriRule> getUriRules(final Class c) {
        assert (c != null);
        UriRules<UriRule> r = (UriRules<UriRule>)this.rulesMap.get(c);
        if (r != null) {
            return r;
        }
        Map<Class, AbstractResource> map = this.abstractResourceMap;
        synchronized (map) {
            r = (UriRules)this.rulesMap.get(c);
            if (r != null) {
                return r;
            }
            r = Errors.processWithErrors(new Errors.Closure<ResourceUriRules>(){

                @Override
                public ResourceUriRules f() {
                    return WebApplicationImpl.this.newResourceUriRules(WebApplicationImpl.this.getAbstractResource(c));
                }
            }).getRules();
            this.rulesMap.put(c, r);
        }
        return r;
    }

    ResourceComponentProvider getResourceComponentProvider(Class c) {
        return this.getOrCreateResourceComponentProvider(c, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ResourceComponentProvider getOrCreateResourceComponentProvider(final Class c, boolean create) {
        assert (c != null);
        ResourceComponentProvider rcp = (ResourceComponentProvider)this.providerMap.get(c);
        if (rcp != null) {
            return rcp;
        }
        if (!create && this.singletonMap.containsKey(c)) {
            return (ResourceComponentProvider)this.singletonMap.get(c);
        }
        Map<Class, AbstractResource> map = this.abstractResourceMap;
        synchronized (map) {
            rcp = (ResourceComponentProvider)this.providerMap.get(c);
            if (rcp != null) {
                return rcp;
            }
            final ResourceComponentProvider _rcp = rcp = this.rcpFactory.getComponentProvider(null, c);
            Errors.processWithErrors(new Errors.Closure<Void>(){

                @Override
                public Void f() {
                    _rcp.init(WebApplicationImpl.this.getAbstractResource(c));
                    return null;
                }
            });
            this.providerMap.put(c, rcp);
        }
        return rcp;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ResourceComponentProvider getResourceComponentProvider(ComponentContext cc, final Class c) {
        ClassAnnotationKey cak;
        ResourceComponentProvider rcp;
        assert (c != null);
        if (cc == null || cc.getAnnotations().length == 0) {
            return this.getOrCreateResourceComponentProvider(c, true);
        }
        if (cc.getAnnotations().length == 1) {
            String value;
            Annotation i;
            Annotation a = cc.getAnnotations()[0];
            if (a.annotationType() == Inject.class) {
                i = (Inject)Inject.class.cast(a);
                String string = value = i.value() != null ? i.value().trim() : "";
                if (value.isEmpty()) {
                    return this.getOrCreateResourceComponentProvider(c, true);
                }
            } else if (a.annotationType() == InjectParam.class) {
                i = (InjectParam)InjectParam.class.cast(a);
                String string = value = i.value() != null ? i.value().trim() : "";
                if (value.isEmpty()) {
                    return this.getOrCreateResourceComponentProvider(c, true);
                }
            }
        }
        if ((rcp = (ResourceComponentProvider)this.providerWithAnnotationKeyMap.get(cak = new ClassAnnotationKey(c, cc.getAnnotations()))) != null) {
            return rcp;
        }
        Map<Class, AbstractResource> map = this.abstractResourceMap;
        synchronized (map) {
            rcp = (ResourceComponentProvider)this.providerWithAnnotationKeyMap.get(cak);
            if (rcp != null) {
                return rcp;
            }
            final ResourceComponentProvider _rcp = rcp = this.rcpFactory.getComponentProvider(cc, c);
            Errors.processWithErrors(new Errors.Closure<Void>(){

                @Override
                public Void f() {
                    _rcp.init(WebApplicationImpl.this.getAbstractResource(c));
                    return null;
                }
            });
            this.providerWithAnnotationKeyMap.put(cak, rcp);
        }
        return rcp;
    }

    void initiateResource(AbstractResource ar) {
        this.initiateResource(ar.getResourceClass());
    }

    void initiateResource(Class c) {
        this.getUriRules(c);
        this.getOrCreateResourceComponentProvider(c, true);
    }

    void initiateResource(AbstractResource ar, final Object resource) {
        Class<?> c = ar.getResourceClass();
        this.getUriRules(c);
        if (!this.singletonMap.containsKey(c)) {
            this.singletonMap.put(c, new ResourceComponentProvider(){

                @Override
                public void init(AbstractResource abstractResource) {
                }

                @Override
                public ComponentScope getScope() {
                    return ComponentScope.Singleton;
                }

                @Override
                public Object getInstance(HttpContext hc) {
                    return this.getInstance();
                }

                @Override
                public void destroy() {
                }

                @Override
                public Object getInstance() {
                    return resource;
                }
            });
        }
    }

    Set<AbstractResource> getAbstractRootResources() {
        return this.abstractRootResources;
    }

    Map<String, AbstractResource> getExplicitAbstractRootResources() {
        return this.explicitAbstractRootResources;
    }

    private ResourceUriRules newResourceUriRules(AbstractResource ar) {
        assert (null != ar);
        BasicValidator validator = new BasicValidator();
        validator.validate(ar);
        for (ResourceModelIssue issue : validator.getIssueList()) {
            Errors.error(issue.getMessage(), issue.isFatal());
        }
        return new ResourceUriRules(this.resourceConfig, this.getDispatchProvider(), this.injectableFactory, this.filterFactory, this.wadlFactory, this.dispatchingListener, ar);
    }

    protected ResourceMethodDispatchProvider getDispatchProvider() {
        return this.dispatcherFactory;
    }

    @Override
    public RequestListener getRequestListener() {
        return this.requestListener;
    }

    @Override
    public DispatchingListener getDispatchingListener() {
        return this.dispatchingListener;
    }

    @Override
    public ResponseListener getResponseListener() {
        return this.responseListener;
    }

    AbstractResource getAbstractResource(Object o) {
        return this.getAbstractResource(o.getClass());
    }

    AbstractResource getAbstractResource(Class c) {
        AbstractResource ar = this.abstractResourceMap.get(c);
        if (ar == null) {
            ar = IntrospectionModeller.createResource(c);
            this.abstractResourceMap.put(c, ar);
        }
        return ar;
    }

    @Override
    public boolean isInitiated() {
        return this.initiated;
    }

    @Override
    public void initiate(ResourceConfig resourceConfig) {
        this.initiate(resourceConfig, null);
    }

    @Override
    public void initiate(final ResourceConfig rc, final IoCComponentProviderFactory _provider) {
        Errors.processWithErrors(new Errors.Closure<Void>(){

            @Override
            public Void f() {
                Errors.setReportMissingDependentFieldOrMethod(false);
                WebApplicationImpl.this._initiate(rc, _provider);
                return null;
            }
        });
    }

    private void _initiate(ResourceConfig rc, IoCComponentProviderFactory _provider) {
        boolean bl;
        if (rc == null) {
            throw new IllegalArgumentException("ResourceConfig instance MUST NOT be null");
        }
        if (this.initiated) {
            throw new ContainerException(ImplMessages.WEB_APP_ALREADY_INITIATED());
        }
        this.initiated = true;
        LOGGER.info("Initiating Jersey application, version '" + BuildId.getBuildId() + "'");
        Class<?>[] components = ServiceFinder.find("jersey-server-components").toClassArray();
        if (components.length > 0) {
            if (LOGGER.isLoggable(Level.INFO)) {
                Iterator<IoCComponentProviderFactory> b = new StringBuilder();
                ((StringBuilder)((Object)b)).append("Adding the following classes declared in META-INF/services/jersey-server-components to the resource configuration:");
                for (Class<?> c : components) {
                    ((StringBuilder)((Object)b)).append('\n').append("  ").append(c);
                }
                LOGGER.log(Level.INFO, ((StringBuilder)((Object)b)).toString());
            }
            this.resourceConfig = rc.clone();
            this.resourceConfig.getClasses().addAll(Arrays.asList(components));
        } else {
            this.resourceConfig = rc;
        }
        this.provider = _provider;
        this.providerFactories = new ArrayList<IoCComponentProviderFactory>(2);
        for (Object e : this.resourceConfig.getProviderSingletons()) {
            if (!(e instanceof IoCComponentProviderFactory)) continue;
            this.providerFactories.add((IoCComponentProviderFactory)e);
        }
        if (_provider != null) {
            this.providerFactories.add(_provider);
        }
        this.cpFactory = this.providerFactories.isEmpty() ? new ProviderFactory(this.injectableFactory) : new IoCProviderFactory((InjectableProviderContext)this.injectableFactory, this.providerFactories);
        this.rcpFactory = this.providerFactories.isEmpty() ? new ResourceFactory(this.resourceConfig, this.injectableFactory) : new IoCResourceFactory(this.resourceConfig, this.injectableFactory, this.providerFactories);
        for (IoCComponentProviderFactory ioCComponentProviderFactory : this.providerFactories) {
            if (!(ioCComponentProviderFactory instanceof IoCComponentProcessorFactoryInitializer)) continue;
            ComponentProcessorFactoryImpl cpf = new ComponentProcessorFactoryImpl();
            IoCComponentProcessorFactoryInitializer i = (IoCComponentProcessorFactoryInitializer)((Object)ioCComponentProviderFactory);
            i.init(cpf);
        }
        this.resourceContext = new ResourceContext(){

            @Override
            public ExtendedUriInfo matchUriInfo(URI u) throws ContainerException {
                try {
                    return WebApplicationImpl.this.handleMatchResourceRequest(u);
                }
                catch (ContainerException ex) {
                    throw ex;
                }
                catch (WebApplicationException ex) {
                    if (ex.getResponse().getStatus() == 404) {
                        return null;
                    }
                    throw new ContainerException(ex);
                }
                catch (RuntimeException ex) {
                    throw new ContainerException(ex);
                }
            }

            @Override
            public Object matchResource(URI u) throws ContainerException {
                ExtendedUriInfo ui = this.matchUriInfo(u);
                return ui != null ? ui.getMatchedResources().get(0) : null;
            }

            @Override
            public <T> T matchResource(URI u, Class<T> c) throws ContainerException, ClassCastException {
                return c.cast(this.matchResource(u));
            }

            @Override
            public <T> T getResource(Class<T> c) {
                return c.cast(WebApplicationImpl.this.getResourceComponentProvider(c).getInstance(WebApplicationImpl.this.context));
            }
        };
        ProviderServices providerServices = new ProviderServices(ServerSide.class, this.cpFactory, this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons());
        this.injectableFactory.add(new ContextInjectableProvider<ProviderServices>((Type)((Object)ProviderServices.class), providerServices));
        this.injectableFactory.add(new ContextInjectableProvider<ResourceMethodCustomInvokerDispatchFactory>((Type)((Object)ResourceMethodCustomInvokerDispatchFactory.class), new ResourceMethodCustomInvokerDispatchFactory(providerServices)));
        this.injectableFactory.add(new InjectableProvider<ParentRef, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, ParentRef a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final Class target = ReflectionHelper.getDeclaringClass(cc.getAccesibleObject());
                final Class inject = (Class)t;
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        ExtendedUriInfo ui = WebApplicationImpl.this.context.getUriInfo();
                        List<Object> l = ui.getMatchedResources();
                        Object parent = this.getParent(l, target);
                        if (parent == null) {
                            return null;
                        }
                        try {
                            return inject.cast(parent);
                        }
                        catch (ClassCastException ex) {
                            throw new ContainerException("The parent resource is expected to be of class " + inject.getName() + " but is of class " + parent.getClass().getName(), ex);
                        }
                    }

                    private Object getParent(List l, Class target2) {
                        if (l.isEmpty()) {
                            return null;
                        }
                        if (l.size() == 1) {
                            return l.get(0).getClass() == target2 ? null : l.get(0);
                        }
                        return l.get(0).getClass() == target2 ? l.get(1) : l.get(0);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, Inject a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Undefined;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, Inject a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() == ComponentScope.PerRequest) {
                    return null;
                }
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, Inject a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() != ComponentScope.Singleton) {
                    return null;
                }
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, InjectParam a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Undefined;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, InjectParam a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() == ComponentScope.PerRequest) {
                    return null;
                }
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<Object> getInjectable(ComponentContext cc, InjectParam a, Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() != ComponentScope.Singleton) {
                    return null;
                }
                return new Injectable<Object>(){

                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new ContextInjectableProvider<ResourceConfig>((Type)((Object)FeaturesAndProperties.class), this.resourceConfig));
        this.injectableFactory.add(new InjectableProvider<Context, Type>(){

            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }

            @Override
            public Injectable<ResourceConfig> getInjectable(ComponentContext cc, Context a, Type t) {
                if (t != ResourceConfig.class) {
                    return null;
                }
                return new Injectable<ResourceConfig>(){

                    @Override
                    public ResourceConfig getValue() {
                        return WebApplicationImpl.this.resourceConfig;
                    }
                };
            }
        });
        this.injectableFactory.add(new ContextInjectableProvider<ResourceContext>((Type)((Object)ResourceContext.class), this.resourceContext));
        this.injectableFactory.configure(providerServices);
        boolean bl2 = false;
        if (rc instanceof DeferredResourceConfig) {
            DeferredResourceConfig drc = (DeferredResourceConfig)rc;
            if (this.resourceConfig == drc) {
                this.resourceConfig = drc.clone();
            }
            DeferredResourceConfig.ApplicationHolder da = drc.getApplication(this.cpFactory);
            this.resourceConfig.add(da.getApplication());
            boolean bl3 = true;
            this.injectableFactory.add(new ContextInjectableProvider<javax.ws.rs.core.Application>((Type)((Object)javax.ws.rs.core.Application.class), da.getOriginalApplication()));
        } else {
            this.injectableFactory.add(new ContextInjectableProvider<ResourceConfig>((Type)((Object)javax.ws.rs.core.Application.class), this.resourceConfig));
        }
        for (ResourceConfigurator configurator : providerServices.getProviders(ResourceConfigurator.class)) {
            configurator.configure(this.resourceConfig);
            bl = true;
        }
        this.resourceConfig.validate();
        if (bl) {
            providerServices.update(this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons(), this.injectableFactory);
        }
        this.templateContext = new TemplateFactory(providerServices);
        this.injectableFactory.add(new ContextInjectableProvider<TemplateContext>((Type)((Object)TemplateContext.class), this.templateContext));
        final ContextResolverFactory crf = new ContextResolverFactory();
        this.exceptionFactory = new ExceptionMapperFactory();
        this.bodyFactory = new MessageBodyFactory(providerServices, this.getFeaturesAndProperties().getFeature("com.sun.jersey.config.feature.Pre14ProviderPrecedence"));
        this.injectableFactory.add(new ContextInjectableProvider<MessageBodyFactory>((Type)((Object)MessageBodyWorkers.class), this.bodyFactory));
        this.providers = new Providers(){

            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> c, Type t, Annotation[] as, MediaType m) {
                return WebApplicationImpl.this.bodyFactory.getMessageBodyReader(c, t, as, m);
            }

            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> c, Type t, Annotation[] as, MediaType m) {
                return WebApplicationImpl.this.bodyFactory.getMessageBodyWriter(c, t, as, m);
            }

            @Override
            public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> c) {
                if (Throwable.class.isAssignableFrom(c)) {
                    return WebApplicationImpl.this.exceptionFactory.find(c);
                }
                return null;
            }

            @Override
            public <T> ContextResolver<T> getContextResolver(Class<T> ct, MediaType m) {
                return crf.resolve(ct, m);
            }
        };
        this.injectableFactory.add(new ContextInjectableProvider<Providers>((Type)((Object)Providers.class), this.providers));
        this.stringReaderFactory = new StringReaderFactory();
        this.injectableFactory.add(new ContextInjectableProvider<StringReaderFactory>((Type)((Object)StringReaderWorkers.class), this.stringReaderFactory));
        MultivaluedParameterExtractorFactory mpep = new MultivaluedParameterExtractorFactory(this.stringReaderFactory);
        this.injectableFactory.add(new ContextInjectableProvider<MultivaluedParameterExtractorFactory>((Type)((Object)MultivaluedParameterExtractorProvider.class), mpep));
        this.injectableFactory.add(new CookieParamInjectableProvider(mpep));
        this.injectableFactory.add(new HeaderParamInjectableProvider(mpep));
        this.injectableFactory.add(new HttpContextInjectableProvider());
        this.injectableFactory.add(new MatrixParamInjectableProvider(mpep));
        this.injectableFactory.add(new PathParamInjectableProvider(mpep));
        this.injectableFactory.add(new QueryParamInjectableProvider(mpep));
        this.injectableFactory.add(new FormParamInjectableProvider(mpep));
        this.filterFactory = new FilterFactory(providerServices);
        this.dispatcherFactory = ResourceMethodDispatcherFactory.create(providerServices);
        this.dispatchingListener = new DispatchingListenerProxy();
        this.wadlFactory = new WadlFactory(this.resourceConfig, this.providers);
        WadlApplicationContextInjectionProxy wadlApplicationContextInjectionProxy = null;
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.DisableWADL")) {
            wadlApplicationContextInjectionProxy = new WadlApplicationContextInjectionProxy();
            this.injectableFactory.add(new SingletonTypeInjectableProvider<Context, WadlApplicationContext>(WadlApplicationContext.class, (WadlApplicationContext)wadlApplicationContextInjectionProxy){});
            final WadlApplicationContextInjectionProxy wac = wadlApplicationContextInjectionProxy;
            @Produces(value={"application/vnd.sun.wadl+xml", "application/vnd.sun.wadl+json", "application/xml"})
            class WadlContextResolver
            implements ContextResolver<JAXBContext> {
                WadlContextResolver() {
                }

                @Override
                public JAXBContext getContext(Class<?> type) {
                    if (Application.class.isAssignableFrom(type)) {
                        return wac.getJAXBContext();
                    }
                    return null;
                }
            }
            this.resourceConfig.getSingletons().add(new WadlContextResolver());
            providerServices.update(this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons(), this.injectableFactory);
        } else {
            this.injectableFactory.add(new SingletonTypeInjectableProvider<Context, WadlApplicationContext>(WadlApplicationContext.class, (WadlApplicationContext)wadlApplicationContextInjectionProxy){});
        }
        this.filterFactory.init(this.resourceConfig);
        if (!this.resourceConfig.getMediaTypeMappings().isEmpty() || !this.resourceConfig.getLanguageMappings().isEmpty()) {
            boolean present = false;
            for (ContainerRequestFilter f : this.filterFactory.getRequestFilters()) {
                present |= f instanceof UriConnegFilter;
            }
            if (!present) {
                this.filterFactory.getRequestFilters().add(new UriConnegFilter(this.resourceConfig.getMediaTypeMappings(), this.resourceConfig.getLanguageMappings()));
            } else {
                LOGGER.warning("The media type and language mappings declared in the ResourceConfig are ignored because there is an instance of " + UriConnegFilter.class.getName() + "present in the list of request filters.");
            }
        }
        crf.init(providerServices, this.injectableFactory);
        this.exceptionFactory.init(providerServices);
        this.bodyFactory.init();
        this.stringReaderFactory.init(providerServices);
        Errors.setReportMissingDependentFieldOrMethod(true);
        this.cpFactory.injectOnAllComponents();
        this.cpFactory.injectOnProviderInstances(this.resourceConfig.getProviderSingletons());
        for (IoCComponentProviderFactory providerFactory : this.providerFactories) {
            if (!(providerFactory instanceof WebApplicationListener)) continue;
            WebApplicationListener listener = (WebApplicationListener)((Object)providerFactory);
            listener.onWebApplicationReady();
        }
        this.createAbstractResourceModelStructures();
        RulesMap<UriRule> rootRules = new RootResourceUriRules(this, this.resourceConfig, this.wadlFactory, this.injectableFactory).getRules();
        this.rootsRule = new RootResourceClassesRule(rootRules);
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.DisableWADL")) {
            wadlApplicationContextInjectionProxy.init(this.wadlFactory);
        }
        this.requestListener = MonitoringProviderFactory.createRequestListener(providerServices);
        this.responseListener = MonitoringProviderFactory.createResponseListener(providerServices);
        this.dispatchingListener.init(providerServices);
        this.callAbstractResourceModelListenersOnLoaded(providerServices);
        this.isTraceEnabled = this.resourceConfig.getFeature("com.sun.jersey.config.feature.Trace") | this.resourceConfig.getFeature("com.sun.jersey.config.feature.TracePerRequest");
    }

    @Override
    public Providers getProviders() {
        return this.providers;
    }

    @Override
    public ResourceContext getResourceContext() {
        return this.resourceContext;
    }

    @Override
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.bodyFactory;
    }

    @Override
    public ExceptionMapperContext getExceptionMapperContext() {
        return this.exceptionFactory;
    }

    @Override
    public ServerInjectableProviderFactory getServerInjectableProviderFactory() {
        return this.injectableFactory;
    }

    @Override
    public void handleRequest(ContainerRequest request, ContainerResponseWriter responseWriter) throws IOException {
        ContainerResponse response = new ContainerResponse(this, request, responseWriter);
        this.handleRequest(request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleRequest(ContainerRequest request, ContainerResponse response) throws IOException {
        WebApplicationContext localContext = new WebApplicationContext(this, request, response);
        this.context.set(localContext);
        try {
            this._handleRequest(localContext, request, response);
        }
        finally {
            PerRequestFactory.destroy(localContext);
            this.closeableFactory.close(localContext);
            this.context.set(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private WebApplicationContext handleMatchResourceRequest(URI u) {
        WebApplicationContext oldContext = (WebApplicationContext)this.context.get();
        WebApplicationContext newContext = oldContext.createMatchResourceContext(u);
        this.context.set(newContext);
        try {
            this._handleRequest(newContext, newContext.getContainerRequest());
            WebApplicationContext webApplicationContext = newContext;
            return webApplicationContext;
        }
        finally {
            this.context.set(oldContext);
        }
    }

    @Override
    public void destroy() {
        for (ResourceComponentProvider rcp : this.providerMap.values()) {
            rcp.destroy();
        }
        for (ResourceComponentProvider rcp : this.singletonMap.values()) {
            rcp.destroy();
        }
        for (ResourceComponentProvider rcp : this.providerWithAnnotationKeyMap.values()) {
            rcp.destroy();
        }
        this.cpFactory.destroy();
    }

    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }

    @Override
    public void trace(String message) {
        this.context.get().trace(message);
    }

    private void _handleRequest(WebApplicationContext localContext, ContainerRequest request, ContainerResponse response) throws IOException {
        block14: {
            block13: {
                try {
                    this.requestListener.onRequest(Thread.currentThread().getId(), request);
                    this._handleRequest(localContext, request);
                }
                catch (WebApplicationException e) {
                    response.mapWebApplicationException(e);
                }
                catch (MappableContainerException e) {
                    response.mapMappableContainerException(e);
                }
                catch (RuntimeException e) {
                    if (response.mapException(e)) break block13;
                    LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", e);
                    throw e;
                }
            }
            try {
                for (ContainerResponseFilter f : localContext.getResponseFilters()) {
                    response = f.filter(request, response);
                    localContext.setContainerResponse(response);
                }
                for (ContainerResponseFilter f : this.filterFactory.getResponseFilters()) {
                    response = f.filter(request, response);
                    localContext.setContainerResponse(response);
                }
            }
            catch (WebApplicationException e) {
                response.mapWebApplicationException(e);
            }
            catch (MappableContainerException e) {
                response.mapMappableContainerException(e);
            }
            catch (RuntimeException e) {
                if (response.mapException(e)) break block14;
                LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", e);
                throw e;
            }
        }
        try {
            response.write();
            this.responseListener.onResponse(Thread.currentThread().getId(), response);
        }
        catch (WebApplicationException e) {
            if (response.isCommitted()) {
                LOGGER.log(Level.SEVERE, "The response of the WebApplicationException cannot be utilized as the response is already committed. Re-throwing to the HTTP container", e);
                throw e;
            }
            response.mapWebApplicationException(e);
            response.write();
        }
    }

    private void _handleRequest(WebApplicationContext localContext, ContainerRequest request) {
        for (ContainerRequestFilter f : this.filterFactory.getRequestFilters()) {
            request = f.filter(request);
            localContext.setContainerRequest(request);
        }
        StringBuilder path = new StringBuilder();
        path.append("/").append(request.getPath(false));
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.IgnoreMatrixParams")) {
            path = this.stripMatrixParams(path);
        }
        if (!this.rootsRule.accept(path, null, localContext)) {
            throw new NotFoundException(request.getRequestUri());
        }
    }

    @Override
    public HttpContext getThreadLocalHttpContext() {
        return this.context;
    }

    private StringBuilder stripMatrixParams(StringBuilder path) {
        int e = path.indexOf(";");
        if (e == -1) {
            return path;
        }
        int s = 0;
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(path, s, e);
        } while ((s = path.indexOf("/", e + 1)) != -1 && (e = path.indexOf(";", s)) != -1);
        if (s != -1) {
            sb.append(path, s, path.length());
        }
        return sb;
    }

    private void createAbstractResourceModelStructures() {
        HashSet<AbstractResource> rootARs = new HashSet<AbstractResource>();
        for (Object object : this.resourceConfig.getRootResourceSingletons()) {
            rootARs.add(this.getAbstractResource(object));
        }
        for (Class clazz : this.resourceConfig.getRootResourceClasses()) {
            rootARs.add(this.getAbstractResource(clazz));
        }
        HashMap<String, AbstractResource> explicitRootARs = new HashMap<String, AbstractResource>();
        for (Map.Entry<String, Object> e : this.resourceConfig.getExplicitRootResources().entrySet()) {
            Object o = e.getValue();
            Class<?> c = o instanceof Class ? (Class<?>)o : o.getClass();
            AbstractResource ar = new AbstractResource(e.getKey(), this.getAbstractResource(c));
            rootARs.add(ar);
            explicitRootARs.put(e.getKey(), ar);
        }
        this.abstractRootResources = Collections.unmodifiableSet(rootARs);
        this.explicitAbstractRootResources = Collections.unmodifiableMap(explicitRootARs);
    }

    private void callAbstractResourceModelListenersOnLoaded(ProviderServices providerServices) {
        for (AbstractResourceModelListener aml : providerServices.getProviders(AbstractResourceModelListener.class)) {
            aml.onLoaded(this.armContext);
        }
    }

    private <T> T createProxy(final Class<T> c, final InvocationHandler i) {
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                return c.cast(Proxy.newProxyInstance(WebApplicationImpl.this.getClass().getClassLoader(), new Class[]{c}, i));
            }
        });
    }

    private class DispatchingListenerProxy
    implements DispatchingListener {
        private DispatchingListener dispatchingListener;

        private DispatchingListenerProxy() {
        }

        @Override
        public void onSubResource(long id, Class subResource) {
            this.dispatchingListener.onSubResource(id, subResource);
        }

        @Override
        public void onSubResourceLocator(long id, AbstractSubResourceLocator locator) {
            this.dispatchingListener.onSubResourceLocator(id, locator);
        }

        @Override
        public void onResourceMethod(long id, AbstractResourceMethod method) {
            this.dispatchingListener.onResourceMethod(id, method);
        }

        public void init(ProviderServices providerServices) {
            this.dispatchingListener = MonitoringProviderFactory.createDispatchingListener(providerServices);
        }
    }

    private static class ContextInjectableProvider<T>
    extends SingletonTypeInjectableProvider<Context, T> {
        ContextInjectableProvider(Type type, T instance) {
            super(type, instance);
        }
    }

    private class ComponentProcessorFactoryImpl
    implements IoCComponentProcessorFactory {
        private final ConcurrentMap<Class, IoCComponentProcessor> componentProcessorMap = new ConcurrentHashMap<Class, IoCComponentProcessor>();

        private ComponentProcessorFactoryImpl() {
        }

        @Override
        public ComponentScope getScope(Class c) {
            return WebApplicationImpl.this.rcpFactory.getScope(c);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IoCComponentProcessor get(final Class c, final ComponentScope scope) {
            IoCComponentProcessor cp = (IoCComponentProcessor)this.componentProcessorMap.get(c);
            if (cp != null) {
                return cp == NULL_COMPONENT_PROCESSOR ? null : cp;
            }
            Map map = WebApplicationImpl.this.abstractResourceMap;
            synchronized (map) {
                cp = (IoCComponentProcessor)this.componentProcessorMap.get(c);
                if (cp != null) {
                    return cp == NULL_COMPONENT_PROCESSOR ? null : cp;
                }
                ResourceComponentInjector rci = Errors.processWithErrors(new Errors.Closure<ResourceComponentInjector>(){

                    @Override
                    public ResourceComponentInjector f() {
                        return new ResourceComponentInjector(WebApplicationImpl.this.injectableFactory, scope, WebApplicationImpl.this.getAbstractResource(c));
                    }
                });
                if (rci.hasInjectableArtifacts()) {
                    cp = new ComponentProcessorImpl(rci);
                    this.componentProcessorMap.put(c, cp);
                } else {
                    cp = null;
                    this.componentProcessorMap.put(c, NULL_COMPONENT_PROCESSOR);
                }
            }
            return cp;
        }
    }

    private class ComponentProcessorImpl
    implements IoCComponentProcessor {
        private final ResourceComponentInjector rci;

        ComponentProcessorImpl(ResourceComponentInjector rci) {
            this.rci = rci;
        }

        @Override
        public void preConstruct() {
        }

        @Override
        public void postConstruct(Object o) {
            this.rci.inject(WebApplicationImpl.this.context.get(), o);
        }
    }

    private static class ClassAnnotationKey {
        private final Class c;
        private final Set<Annotation> as;

        public ClassAnnotationKey(Class c, Annotation[] as) {
            this.c = c;
            this.as = new HashSet<Annotation>(Arrays.asList(as));
        }

        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.c != null ? this.c.hashCode() : 0);
            hash = 67 * hash + (this.as != null ? this.as.hashCode() : 0);
            return hash;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ClassAnnotationKey other = (ClassAnnotationKey)obj;
            if (!(this.c == other.c || this.c != null && this.c.equals(other.c))) {
                return false;
            }
            return this.as == other.as || this.as != null && this.as.equals(other.as);
        }
    }
}

