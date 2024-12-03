/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.Ordered
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.service.exporter.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ClassLoaderFactory;
import org.eclipse.gemini.blueprint.context.support.internal.scope.OsgiBundleScope;
import org.eclipse.gemini.blueprint.context.support.internal.security.SecurityUtils;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServicePropertiesResolver;
import org.eclipse.gemini.blueprint.service.exporter.support.AbstractOsgiServiceExporter;
import org.eclipse.gemini.blueprint.service.exporter.support.BeanNameServicePropertiesResolver;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeEvent;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesChangeListener;
import org.eclipse.gemini.blueprint.service.exporter.support.ServicePropertiesListenerManager;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterController;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterInternalActions;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.LazyTargetResolver;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ListenerNotifier;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.PublishingServiceFactory;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ServiceRegistrationDecorator;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.support.ServiceRegistrationWrapper;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class OsgiServiceFactoryBean
extends AbstractOsgiServiceExporter
implements BeanClassLoaderAware,
BeanFactoryAware,
BeanNameAware,
BundleContextAware,
FactoryBean<ServiceRegistration>,
InitializingBean,
Ordered {
    private static final Log log = LogFactory.getLog(OsgiServiceFactoryBean.class);
    private volatile BundleContext bundleContext;
    private volatile OsgiServicePropertiesResolver propertiesResolver;
    private volatile BeanFactory beanFactory;
    private volatile ServiceRegistrationDecorator serviceRegistration;
    private final ServiceRegistrationWrapper safeServiceRegistration = new ServiceRegistrationWrapper(null);
    private volatile Map serviceProperties;
    private volatile ServicePropertiesChangeListener propertiesListener;
    private volatile int ranking;
    private volatile String targetBeanName;
    private boolean hasNamedBean;
    private volatile Class<?>[] interfaces;
    private InterfaceDetector interfaceDetector = DefaultInterfaceDetector.DISABLED;
    private volatile ExportContextClassLoaderEnum contextClassLoader = ExportContextClassLoaderEnum.UNMANAGED;
    private volatile Object target;
    private volatile Class<?> targetClass;
    private int order = Integer.MAX_VALUE;
    private ClassLoader classLoader;
    private ClassLoader aopClassLoader;
    private String beanName;
    private boolean serviceRegistered = false;
    private boolean registerAtStartup = true;
    private boolean registerService = true;
    private final Object lock = new Object();
    private final ExporterController controller;
    private volatile LazyTargetResolver resolver;
    private ListenerNotifier notifier;
    private final AtomicBoolean activated = new AtomicBoolean(false);
    private boolean cacheTarget = false;

    public OsgiServiceFactoryBean() {
        this.controller = new ExporterController(new Executor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() throws Exception {
        boolean shouldRegisterAtStartup;
        Assert.notNull((Object)this.bundleContext, (String)"required property 'bundleContext' has not been set");
        this.hasNamedBean = StringUtils.hasText((String)this.targetBeanName);
        Assert.isTrue((this.hasNamedBean || this.target != null ? 1 : 0) != 0, (String)"Either 'targetBeanName' or 'target' properties have to be set.");
        if (this.hasNamedBean) {
            Assert.notNull((Object)this.beanFactory, (String)"Required property 'beanFactory' has not been set.");
        }
        if (this.hasNamedBean) {
            ConfigurableListableBeanFactory clbf;
            BeanDefinition definition;
            Assert.isTrue((boolean)this.beanFactory.containsBean(this.targetBeanName), (String)("Cannot locate bean named '" + this.targetBeanName + "' inside the running bean factory."));
            if (this.beanFactory.isSingleton(this.targetBeanName) && this.beanFactory instanceof ConfigurableListableBeanFactory && !(definition = (clbf = (ConfigurableListableBeanFactory)this.beanFactory).getBeanDefinition(this.targetBeanName)).isLazyInit()) {
                this.target = this.beanFactory.getBean(this.targetBeanName);
                this.targetClass = this.target.getClass();
            }
            if (this.targetClass == null) {
                this.targetClass = this.beanFactory.getType(this.targetBeanName);
            }
            this.addBeanFactoryDependency();
        } else {
            this.targetClass = this.target.getClass();
        }
        if (this.propertiesResolver == null) {
            this.propertiesResolver = new BeanNameServicePropertiesResolver();
            ((BeanNameServicePropertiesResolver)this.propertiesResolver).setBundleContext(this.bundleContext);
        }
        if (this.interfaces == null) {
            if (DefaultInterfaceDetector.DISABLED.equals(this.interfaceDetector)) {
                throw new IllegalArgumentException("No service interface(s) specified and auto-export discovery disabled; change at least one of these properties.");
            }
            this.interfaces = new Class[0];
        } else if (!ServiceFactory.class.isAssignableFrom(this.targetClass)) {
            for (int interfaceIndex = 0; interfaceIndex < this.interfaces.length; ++interfaceIndex) {
                Class<?> intf = this.interfaces[interfaceIndex];
                Assert.isAssignable(intf, this.targetClass, (String)"Exported service object does not implement the given interface: ");
            }
        }
        if (this.serviceProperties instanceof ServicePropertiesListenerManager) {
            this.propertiesListener = new PropertiesMonitor();
            ((ServicePropertiesListenerManager)((Object)this.serviceProperties)).addListener(this.propertiesListener);
        }
        Object object = this.lock;
        synchronized (object) {
            shouldRegisterAtStartup = this.registerAtStartup;
        }
        this.resolver = new LazyTargetResolver(this.target, this.beanFactory, this.targetBeanName, this.cacheTarget, this.getNotifier(), this.getLazyListeners());
        if (shouldRegisterAtStartup) {
            this.registerService();
        }
    }

    @Override
    public void destroy() {
        if (this.propertiesListener != null) {
            if (this.serviceProperties instanceof ServicePropertiesListenerManager) {
                ((ServicePropertiesListenerManager)((Object)this.serviceProperties)).removeListener(this.propertiesListener);
            }
            this.propertiesListener = null;
        }
        super.destroy();
    }

    private void addBeanFactoryDependency() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)this.beanFactory;
            if (StringUtils.hasText((String)this.beanName) && cbf.containsBean(this.beanName)) {
                cbf.registerDependentBean(this.targetBeanName, "&" + this.beanName);
                cbf.registerDependentBean(this.targetBeanName, this.beanName);
            }
        } else {
            log.warn((Object)"The running bean factory cannot support dependencies between beans - importer/exporter dependency cannot be enforced");
        }
    }

    private Dictionary mergeServiceProperties(Map serviceProperties, String beanName) {
        MapBasedDictionary<String, Integer> props = new MapBasedDictionary<String, Integer>();
        if (serviceProperties != null) {
            props.putAll(serviceProperties);
        }
        props.remove("org.eclipse.gemini.blueprint.bean.name");
        props.remove("org.springframework.osgi.bean.name");
        props.remove("osgi.service.blueprint.compname");
        props.remove("service.ranking");
        props.putAll(this.propertiesResolver.getServiceProperties(beanName));
        if (this.ranking != 0) {
            props.put("service.ranking", this.ranking);
        }
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void registerService() {
        Object object = this.lock;
        synchronized (object) {
            if (this.serviceRegistered || !this.registerService) {
                return;
            }
            this.serviceRegistered = true;
        }
        String beanName = !this.hasNamedBean ? null : this.targetBeanName;
        Dictionary serviceProperties = this.mergeServiceProperties(this.serviceProperties, beanName);
        Class<?>[] intfs = this.interfaces;
        ClassLoader beanClassLoader = ClassUtils.getClassLoader(this.targetClass);
        Object[] autoDetectedClasses = ClassUtils.getVisibleClasses(this.interfaceDetector.detect(this.targetClass), beanClassLoader);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Autoexport mode [" + this.interfaceDetector + "] discovered on class [" + this.targetClass + "] classes " + ObjectUtils.nullSafeToString((Object[])autoDetectedClasses)));
        }
        LinkedHashSet classes = new LinkedHashSet(intfs.length + autoDetectedClasses.length);
        CollectionUtils.mergeArrayIntoCollection(intfs, classes);
        CollectionUtils.mergeArrayIntoCollection(autoDetectedClasses, classes);
        Class[] mergedClasses = classes.toArray(new Class[classes.size()]);
        ServiceRegistration reg = this.registerService(mergedClasses, serviceProperties);
        this.serviceRegistration = new ServiceRegistrationDecorator(reg);
        this.safeServiceRegistration.swap(this.serviceRegistration);
        this.resolver.setDecorator(this.serviceRegistration);
        this.resolver.notifyIfPossible();
    }

    ServiceRegistration registerService(Class<?>[] classes, Dictionary serviceProperties) {
        Assert.notEmpty((Object[])classes, (String)"at least one class has to be specified for exporting (if autoExport is enabled then maybe the object doesn't implement any interface)");
        Object[] names = ClassUtils.toStringArray(classes);
        Arrays.sort(names);
        log.info((Object)("Publishing service under classes [" + ObjectUtils.nullSafeToString((Object[])names) + "]"));
        Object serviceFactory = new PublishingServiceFactory(this.resolver, classes, ExportContextClassLoaderEnum.SERVICE_PROVIDER.equals((Object)this.contextClassLoader), this.classLoader, this.aopClassLoader, this.bundleContext);
        if (this.isBeanBundleScoped()) {
            serviceFactory = new OsgiBundleScope.BundleScopeServiceFactory((ServiceFactory)serviceFactory);
        }
        if (System.getSecurityManager() != null) {
            AccessControlContext acc = SecurityUtils.getAccFrom(this.beanFactory);
            Object serviceFactoryFinal = serviceFactory;
            return AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>((String[])names, (ServiceFactory)serviceFactoryFinal, serviceProperties){
                final /* synthetic */ String[] val$names;
                final /* synthetic */ ServiceFactory val$serviceFactoryFinal;
                final /* synthetic */ Dictionary val$serviceProperties;
                {
                    this.val$names = stringArray;
                    this.val$serviceFactoryFinal = serviceFactory;
                    this.val$serviceProperties = dictionary;
                }

                @Override
                public ServiceRegistration run() {
                    return OsgiServiceFactoryBean.this.bundleContext.registerService(this.val$names, (Object)this.val$serviceFactoryFinal, this.val$serviceProperties);
                }
            }, acc);
        }
        return this.bundleContext.registerService((String[])names, serviceFactory, serviceProperties);
    }

    private boolean isBeanBundleScoped() {
        boolean bundleScoped = false;
        if (this.targetBeanName != null) {
            if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
                String beanScope = ((ConfigurableListableBeanFactory)this.beanFactory).getMergedBeanDefinition(this.targetBeanName).getScope();
                bundleScoped = "bundle".equals(beanScope);
            } else {
                bundleScoped = true;
            }
        }
        return bundleScoped;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.aopClassLoader = ClassLoaderFactory.getAopClassLoaderFor(classLoader);
    }

    public ServiceRegistration getObject() throws Exception {
        this.resolver.activate();
        return this.safeServiceRegistration;
    }

    public Class<? extends ServiceRegistration> getObjectType() {
        return ServiceRegistration.class;
    }

    public boolean isSingleton() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void unregisterService() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.serviceRegistered) {
                return;
            }
            this.serviceRegistered = false;
        }
        this.unregisterService(this.serviceRegistration);
        this.serviceRegistration = null;
    }

    void unregisterService(ServiceRegistration registration) {
        if (OsgiServiceUtils.unregisterService(registration)) {
            log.info((Object)("Unregistered service [" + registration + "]"));
            if (this.resolver != null) {
                this.resolver.setDecorator(null);
            }
        }
    }

    public void setExportContextClassLoader(ExportContextClassLoaderEnum ccl) {
        Assert.notNull((Object)((Object)ccl));
        this.contextClassLoader = ccl;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getTargetBeanName() {
        return this.targetBeanName;
    }

    public void setTargetBeanName(String name) {
        this.targetBeanName = name;
    }

    public void setInterfaceDetector(InterfaceDetector detector) {
        Assert.notNull((Object)detector);
        this.interfaceDetector = detector;
    }

    public Map getServiceProperties() {
        return this.serviceProperties;
    }

    public void setServiceProperties(Map serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public int getRanking() {
        return this.ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setRegisterService(boolean register) {
        this.registerService = register;
        if (this.registerService && this.targetClass != null) {
            this.registerService();
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
    }

    public OsgiServicePropertiesResolver getResolver() {
        return this.propertiesResolver;
    }

    public void setResolver(OsgiServicePropertiesResolver resolver) {
        this.propertiesResolver = resolver;
    }

    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setCacheTarget(boolean cacheTarget) {
        this.cacheTarget = cacheTarget;
    }

    private class PropertiesMonitor
    implements ServicePropertiesChangeListener {
        private PropertiesMonitor() {
        }

        @Override
        public void propertiesChange(ServicePropertiesChangeEvent event) {
            OsgiServiceFactoryBean.this.serviceProperties = event.getServiceProperties();
            Dictionary dictionary = OsgiServiceFactoryBean.this.mergeServiceProperties(OsgiServiceFactoryBean.this.serviceProperties, OsgiServiceFactoryBean.this.beanName);
            if (OsgiServiceFactoryBean.this.serviceRegistration != null) {
                OsgiServiceFactoryBean.this.serviceRegistration.setProperties(dictionary);
            }
        }
    }

    private class Executor
    implements ExporterInternalActions {
        private Executor() {
        }

        @Override
        public void registerService() {
            OsgiServiceFactoryBean.this.registerService();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void registerServiceAtStartup(boolean register) {
            Object object = OsgiServiceFactoryBean.this.lock;
            synchronized (object) {
                OsgiServiceFactoryBean.this.registerAtStartup = register;
            }
        }

        @Override
        public void unregisterService() {
            OsgiServiceFactoryBean.this.unregisterService();
        }

        @Override
        public void callUnregisterOnStartup() {
            OsgiServiceFactoryBean.this.resolver.startupUnregisterIfPossible();
        }
    }
}

