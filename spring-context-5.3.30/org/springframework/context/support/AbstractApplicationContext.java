/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.CachedIntrospectionResults
 *  org.springframework.beans.PropertyEditorRegistrar
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.ObjectProvider
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.beans.factory.config.BeanExpressionResolver
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.support.ResourceEditorRegistrar
 *  org.springframework.core.NativeDetector
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.SpringProperties
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.PropertyResolver
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.metrics.ApplicationStartup
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.context.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.support.ApplicationContextAwareProcessor;
import org.springframework.context.support.ApplicationListenerDetector;
import org.springframework.context.support.ContextTypeMatchClassLoader;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.LiveBeansView;
import org.springframework.context.support.PostProcessorRegistrationDelegate;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.NativeDetector;
import org.springframework.core.ResolvableType;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractApplicationContext
extends DefaultResourceLoader
implements ConfigurableApplicationContext {
    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";
    public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";
    private static final boolean shouldIgnoreSpel = SpringProperties.getFlag((String)"spring.spel.ignore");
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String id = ObjectUtils.identityToString((Object)this);
    private String displayName = ObjectUtils.identityToString((Object)this);
    @Nullable
    private ApplicationContext parent;
    @Nullable
    private ConfigurableEnvironment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
    private long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Object startupShutdownMonitor = new Object();
    @Nullable
    private Thread shutdownHook;
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private LifecycleProcessor lifecycleProcessor;
    @Nullable
    private MessageSource messageSource;
    @Nullable
    private ApplicationEventMulticaster applicationEventMulticaster;
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;
    private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet();
    @Nullable
    private Set<ApplicationListener<?>> earlyApplicationListeners;
    @Nullable
    private Set<ApplicationEvent> earlyApplicationEvents;

    public AbstractApplicationContext() {
        this.resourcePatternResolver = this.getResourcePatternResolver();
    }

    public AbstractApplicationContext(@Nullable ApplicationContext parent) {
        this();
        this.setParent(parent);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getApplicationName() {
        return "";
    }

    public void setDisplayName(String displayName) {
        Assert.hasLength((String)displayName, (String)"Display name must not be empty");
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    @Nullable
    public ApplicationContext getParent() {
        return this.parent;
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment2) {
        this.environment = environment2;
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = this.createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return this.getBeanFactory();
    }

    @Override
    public long getStartupDate() {
        return this.startupDate;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.publishEvent(event, null);
    }

    @Override
    public void publishEvent(Object event) {
        this.publishEvent(event, null);
    }

    protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
        PayloadApplicationEvent<Object> applicationEvent;
        Assert.notNull((Object)event, (String)"Event must not be null");
        if (event instanceof ApplicationEvent) {
            applicationEvent = (PayloadApplicationEvent<Object>)event;
        } else {
            applicationEvent = new PayloadApplicationEvent<Object>((Object)this, event);
            if (eventType == null) {
                eventType = applicationEvent.getResolvableType();
            }
        }
        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else {
            this.getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
        }
        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }
    }

    ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        Assert.notNull((Object)applicationStartup, (String)"ApplicationStartup must not be null");
        this.applicationStartup = applicationStartup;
    }

    @Override
    public ApplicationStartup getApplicationStartup() {
        return this.applicationStartup;
    }

    LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
        if (this.lifecycleProcessor == null) {
            throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
        }
        return this.lifecycleProcessor;
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver((ResourceLoader)this);
    }

    @Override
    public void setParent(@Nullable ApplicationContext parent) {
        Environment parentEnvironment;
        this.parent = parent;
        if (parent != null && (parentEnvironment = parent.getEnvironment()) instanceof ConfigurableEnvironment) {
            this.getEnvironment().merge((ConfigurableEnvironment)parentEnvironment);
        }
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        Assert.notNull((Object)postProcessor, (String)"BeanFactoryPostProcessor must not be null");
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, (String)"ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        }
        this.applicationListeners.add(listener);
    }

    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void refresh() throws BeansException, IllegalStateException {
        Object object = this.startupShutdownMonitor;
        synchronized (object) {
            StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");
            this.prepareRefresh();
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            this.prepareBeanFactory(beanFactory);
            try {
                this.postProcessBeanFactory(beanFactory);
                StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
                this.invokeBeanFactoryPostProcessors(beanFactory);
                this.registerBeanPostProcessors(beanFactory);
                beanPostProcess.end();
                this.initMessageSource();
                this.initApplicationEventMulticaster();
                this.onRefresh();
                this.registerListeners();
                this.finishBeanFactoryInitialization(beanFactory);
                this.finishRefresh();
            }
            catch (BeansException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn((Object)("Exception encountered during context initialization - cancelling refresh attempt: " + (Object)((Object)ex)));
                }
                this.destroyBeans();
                this.cancelRefresh(ex);
                throw ex;
            }
            finally {
                this.resetCommonCaches();
                contextRefresh.end();
            }
        }
    }

    protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
        if (this.logger.isDebugEnabled()) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Refreshing " + this));
            } else {
                this.logger.debug((Object)("Refreshing " + this.getDisplayName()));
            }
        }
        this.initPropertySources();
        this.getEnvironment().validateRequiredProperties();
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet(this.applicationListeners);
        } else {
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }
        this.earlyApplicationEvents = new LinkedHashSet<ApplicationEvent>();
    }

    protected void initPropertySources() {
    }

    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        this.refreshBeanFactory();
        return this.getBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(this.getClassLoader());
        if (!shouldIgnoreSpel) {
            beanFactory.setBeanExpressionResolver((BeanExpressionResolver)new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        }
        beanFactory.addPropertyEditorRegistrar((PropertyEditorRegistrar)new ResourceEditorRegistrar((ResourceLoader)this, (PropertyResolver)this.getEnvironment()));
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationStartupAware.class);
        beanFactory.registerResolvableDependency(BeanFactory.class, (Object)beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, (Object)this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, (Object)this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, (Object)this);
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new ApplicationListenerDetector(this));
        if (!NativeDetector.inNativeImage() && beanFactory.containsBean("loadTimeWeaver")) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new LoadTimeWeaverAwareProcessor((BeanFactory)beanFactory));
            beanFactory.setTempClassLoader((ClassLoader)((Object)new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader())));
        }
        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", (Object)this.getEnvironment());
        }
        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", (Object)this.getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", (Object)this.getEnvironment().getSystemEnvironment());
        }
        if (!beanFactory.containsLocalBean("applicationStartup")) {
            beanFactory.registerSingleton("applicationStartup", (Object)this.getApplicationStartup());
        }
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, this.getBeanFactoryPostProcessors());
        if (!NativeDetector.inNativeImage() && beanFactory.getTempClassLoader() == null && beanFactory.containsBean("loadTimeWeaver")) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new LoadTimeWeaverAwareProcessor((BeanFactory)beanFactory));
            beanFactory.setTempClassLoader((ClassLoader)((Object)new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader())));
        }
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    protected void initMessageSource() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
            HierarchicalMessageSource hms;
            this.messageSource = (MessageSource)beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
            if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource && (hms = (HierarchicalMessageSource)this.messageSource).getParentMessageSource() == null) {
                hms.setParentMessageSource(this.getInternalParentMessageSource());
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Using MessageSource [" + this.messageSource + "]"));
            }
        } else {
            DelegatingMessageSource dms = new DelegatingMessageSource();
            dms.setParentMessageSource(this.getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, (Object)this.messageSource);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No 'messageSource' bean, using [" + this.messageSource + "]"));
            }
        }
    }

    protected void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
            this.applicationEventMulticaster = (ApplicationEventMulticaster)beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]"));
            }
        } else {
            this.applicationEventMulticaster = new SimpleApplicationEventMulticaster((BeanFactory)beanFactory);
            beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, (Object)this.applicationEventMulticaster);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No 'applicationEventMulticaster' bean, using [" + this.applicationEventMulticaster.getClass().getSimpleName() + "]"));
            }
        }
    }

    protected void initLifecycleProcessor() {
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
            this.lifecycleProcessor = (LifecycleProcessor)beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Using LifecycleProcessor [" + this.lifecycleProcessor + "]"));
            }
        } else {
            DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory((BeanFactory)beanFactory);
            this.lifecycleProcessor = defaultProcessor;
            beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, (Object)this.lifecycleProcessor);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No 'lifecycleProcessor' bean, using [" + this.lifecycleProcessor.getClass().getSimpleName() + "]"));
            }
        }
    }

    protected void onRefresh() throws BeansException {
    }

    protected void registerListeners() {
        String[] listenerBeanNames;
        for (ApplicationListener<?> applicationListener : this.getApplicationListeners()) {
            this.getApplicationEventMulticaster().addApplicationListener(applicationListener);
        }
        for (String listenerBeanName : listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false)) {
            this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }
        Set<ApplicationEvent> set = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (!CollectionUtils.isEmpty(set)) {
            for (ApplicationEvent earlyEvent : set) {
                this.getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        String[] weaverAwareNames;
        if (beanFactory.containsBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
            beanFactory.setConversionService((ConversionService)beanFactory.getBean("conversionService", ConversionService.class));
        }
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> this.getEnvironment().resolvePlaceholders(strVal));
        }
        for (String weaverAwareName : weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false)) {
            this.getBean(weaverAwareName);
        }
        beanFactory.setTempClassLoader(null);
        beanFactory.freezeConfiguration();
        beanFactory.preInstantiateSingletons();
    }

    protected void finishRefresh() {
        this.clearResourceCaches();
        this.initLifecycleProcessor();
        this.getLifecycleProcessor().onRefresh();
        this.publishEvent(new ContextRefreshedEvent(this));
        if (!NativeDetector.inNativeImage()) {
            LiveBeansView.registerApplicationContext(this);
        }
    }

    protected void cancelRefresh(BeansException ex) {
        this.active.set(false);
    }

    protected void resetCommonCaches() {
        ReflectionUtils.clearCache();
        AnnotationUtils.clearCache();
        ResolvableType.clearCache();
        CachedIntrospectionResults.clearClassLoader((ClassLoader)this.getClassLoader());
    }

    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread("SpringContextShutdownHook"){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    Object object = AbstractApplicationContext.this.startupShutdownMonitor;
                    synchronized (object) {
                        AbstractApplicationContext.this.doClose();
                    }
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    @Deprecated
    public void destroy() {
        this.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        Object object = this.startupShutdownMonitor;
        synchronized (object) {
            this.doClose();
            if (this.shutdownHook != null) {
                try {
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
            }
        }
    }

    protected void doClose() {
        if (this.active.get() && this.closed.compareAndSet(false, true)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Closing " + this));
            }
            if (!NativeDetector.inNativeImage()) {
                LiveBeansView.unregisterApplicationContext(this);
            }
            try {
                this.publishEvent(new ContextClosedEvent(this));
            }
            catch (Throwable ex) {
                this.logger.warn((Object)"Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
            }
            if (this.lifecycleProcessor != null) {
                try {
                    this.lifecycleProcessor.onClose();
                }
                catch (Throwable ex) {
                    this.logger.warn((Object)"Exception thrown from LifecycleProcessor on context close", ex);
                }
            }
            this.destroyBeans();
            this.closeBeanFactory();
            this.onClose();
            this.resetCommonCaches();
            if (this.earlyApplicationListeners != null) {
                this.applicationListeners.clear();
                this.applicationListeners.addAll(this.earlyApplicationListeners);
            }
            this.active.set(false);
        }
    }

    protected void destroyBeans() {
        this.getBeanFactory().destroySingletons();
    }

    protected void onClose() {
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    protected void assertBeanFactoryActive() {
        if (!this.active.get()) {
            if (this.closed.get()) {
                throw new IllegalStateException(this.getDisplayName() + " has been closed already");
            }
            throw new IllegalStateException(this.getDisplayName() + " has not been refreshed yet");
        }
    }

    public Object getBean(String name) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return (T)this.getBeanFactory().getBean(name, requiredType);
    }

    public Object getBean(String name, Object ... args) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBean(name, args);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        this.assertBeanFactoryActive();
        return (T)this.getBeanFactory().getBean(requiredType);
    }

    public <T> T getBean(Class<T> requiredType, Object ... args) throws BeansException {
        this.assertBeanFactoryActive();
        return (T)this.getBeanFactory().getBean(requiredType, args);
    }

    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanProvider(requiredType);
    }

    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanProvider(requiredType);
    }

    public boolean containsBean(String name) {
        return this.getBeanFactory().containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isSingleton(name);
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isPrototype(name);
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().isTypeMatch(name, typeToMatch);
    }

    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getType(name);
    }

    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getType(name, allowFactoryBeanInit);
    }

    public String[] getAliases(String name) {
        return this.getBeanFactory().getAliases(name);
    }

    public boolean containsBeanDefinition(String beanName) {
        return this.getBeanFactory().containsBeanDefinition(beanName);
    }

    public int getBeanDefinitionCount() {
        return this.getBeanFactory().getBeanDefinitionCount();
    }

    public String[] getBeanDefinitionNames() {
        return this.getBeanFactory().getBeanDefinitionNames();
    }

    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanProvider(requiredType, allowEagerInit);
    }

    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanProvider(requiredType, allowEagerInit);
    }

    public String[] getBeanNamesForType(ResolvableType type) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type);
    }

    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type);
    }

    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type);
    }

    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeanNamesForAnnotation(annotationType);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
        this.assertBeanFactoryActive();
        return this.getBeanFactory().getBeansWithAnnotation(annotationType);
    }

    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return (A)this.getBeanFactory().findAnnotationOnBean(beanName, annotationType);
    }

    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        this.assertBeanFactoryActive();
        return (A)this.getBeanFactory().findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
    }

    @Nullable
    public BeanFactory getParentBeanFactory() {
        return this.getParent();
    }

    public boolean containsLocalBean(String name) {
        return this.getBeanFactory().containsLocalBean(name);
    }

    @Nullable
    protected BeanFactory getInternalParentBeanFactory() {
        return this.getParent() instanceof ConfigurableApplicationContext ? ((ConfigurableApplicationContext)this.getParent()).getBeanFactory() : this.getParent();
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return this.getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.getMessageSource().getMessage(resolvable, locale);
    }

    private MessageSource getMessageSource() throws IllegalStateException {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized - call 'refresh' before accessing messages via the context: " + this);
        }
        return this.messageSource;
    }

    @Nullable
    protected MessageSource getInternalParentMessageSource() {
        return this.getParent() instanceof AbstractApplicationContext ? ((AbstractApplicationContext)this.getParent()).messageSource : this.getParent();
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return this.resourcePatternResolver.getResources(locationPattern);
    }

    @Override
    public void start() {
        this.getLifecycleProcessor().start();
        this.publishEvent(new ContextStartedEvent(this));
    }

    @Override
    public void stop() {
        this.getLifecycleProcessor().stop();
        this.publishEvent(new ContextStoppedEvent(this));
    }

    @Override
    public boolean isRunning() {
        return this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning();
    }

    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    protected abstract void closeBeanFactory();

    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getDisplayName());
        sb.append(", started on ").append(new Date(this.getStartupDate()));
        ApplicationContext parent = this.getParent();
        if (parent != null) {
            sb.append(", parent: ").append(parent.getDisplayName());
        }
        return sb.toString();
    }

    static {
        ContextClosedEvent.class.getName();
    }
}

