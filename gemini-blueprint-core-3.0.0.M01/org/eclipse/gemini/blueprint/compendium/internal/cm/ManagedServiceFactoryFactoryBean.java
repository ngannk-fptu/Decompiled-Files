/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.service.cm.ConfigurationException
 *  org.osgi.service.cm.ManagedServiceFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.compendium.internal.cm.CMUtils;
import org.eclipse.gemini.blueprint.compendium.internal.cm.ManagedFactoryDisposableInvoker;
import org.eclipse.gemini.blueprint.compendium.internal.cm.UpdateCallback;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.exporter.OsgiServiceRegistrationListener;
import org.eclipse.gemini.blueprint.service.exporter.support.DefaultInterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.ExportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.service.exporter.support.InterfaceDetector;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ManagedServiceFactoryFactoryBean
implements InitializingBean,
BeanClassLoaderAware,
BeanFactoryAware,
BundleContextAware,
DisposableBean,
FactoryBean<Collection> {
    private static final Log log = LogFactory.getLog(ManagedServiceFactoryFactoryBean.class);
    private final Object monitor = new Object();
    private String factoryPid;
    private BundleContext bundleContext;
    private DefaultListableBeanFactory beanFactory;
    private RootBeanDefinition templateDefinition;
    private BeanFactory owningBeanFactory;
    private ServiceRegistration configurationWatcher;
    private final DynamicCollection serviceRegistrations = new DynamicCollection(8);
    private final Collection<ServiceRegistration> userReturnedCollection = Collections.unmodifiableCollection(this.serviceRegistrations);
    private final Map<String, OsgiServiceFactoryBean> serviceExporters = new ConcurrentHashMap<String, OsgiServiceFactoryBean>(8);
    private OsgiServiceRegistrationListener[] listeners = new OsgiServiceRegistrationListener[0];
    private InterfaceDetector detector = DefaultInterfaceDetector.DISABLED;
    private ExportContextClassLoaderEnum ccl = ExportContextClassLoaderEnum.UNMANAGED;
    private Class<?>[] interfaces;
    private ClassLoader classLoader;
    private boolean autowireOnUpdate = false;
    private String updateMethod;
    private UpdateCallback updateCallback;
    public Map initialInjectionProperties;
    private boolean destroyed = false;
    private volatile Map serviceProperties;
    private volatile DestructionInvokerCache destructionInvokerFactory;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() throws Exception {
        Object object = this.monitor;
        synchronized (object) {
            Assert.notNull((Object)this.factoryPid, (String)"factoryPid required");
            Assert.notNull((Object)this.bundleContext, (String)"bundleContext is required");
            Assert.notNull((Object)this.templateDefinition, (String)"templateDefinition is required");
            Assert.isTrue((!DefaultInterfaceDetector.DISABLED.equals(this.detector) || !ObjectUtils.isEmpty((Object[])this.interfaces) ? 1 : 0) != 0, (String)"No service interface(s) specified and auto-export discovery disabled; change at least one of these properties");
        }
        this.processTemplateDefinition();
        this.createEmbeddedBeanFactory();
        this.updateCallback = CMUtils.createCallback(this.autowireOnUpdate, this.updateMethod, (BeanFactory)this.beanFactory);
        this.registerService();
    }

    private void processTemplateDefinition() {
        this.templateDefinition.setScope("singleton");
        String destroyMethod = this.templateDefinition.getDestroyMethodName();
        this.templateDefinition.setDestroyMethodName(null);
        this.templateDefinition.registerExternallyManagedDestroyMethod("destroy");
        this.destructionInvokerFactory = new DestructionInvokerCache(destroyMethod);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() throws Exception {
        Object object = this.monitor;
        synchronized (object) {
            this.destroyed = true;
            OsgiServiceUtils.unregisterService(this.configurationWatcher);
            this.configurationWatcher = null;
            this.destroyFactory();
        }
        this.destructionInvokerFactory.cache.clear();
        this.destructionInvokerFactory = null;
        object = this.serviceRegistrations;
        synchronized (object) {
            this.serviceRegistrations.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createEmbeddedBeanFactory() {
        Object object = this.monitor;
        synchronized (object) {
            DefaultListableBeanFactory bf = new DefaultListableBeanFactory(this.owningBeanFactory);
            if (this.owningBeanFactory instanceof ConfigurableBeanFactory) {
                bf.copyConfigurationFrom((ConfigurableBeanFactory)this.owningBeanFactory);
            }
            bf.setBeanClassLoader(this.classLoader);
            bf.addBeanPostProcessor((BeanPostProcessor)new InitialInjectionProcessor());
            this.beanFactory = bf;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void registerService() {
        Object object = this.monitor;
        synchronized (object) {
            Hashtable<String, String> props = new Hashtable<String, String>(2);
            ((Dictionary)props).put("service.pid", this.factoryPid);
            this.configurationWatcher = this.bundleContext.registerService(ManagedServiceFactory.class.getName(), (Object)new ConfigurationWatcher(), props);
        }
    }

    private void destroyFactory() {
        if (this.beanFactory != null) {
            String[] singletonBeans;
            for (String sigletonName : singletonBeans = this.beanFactory.getSingletonNames()) {
                Object singleton = this.beanFactory.getBean(sigletonName);
                this.beanFactory.removeBeanDefinition(sigletonName);
                ManagedFactoryDisposableInvoker invoker = this.destructionInvokerFactory.getInvoker(singleton.getClass());
                invoker.destroy(sigletonName, singleton, ManagedFactoryDisposableInvoker.DestructionCodes.BUNDLE_STOPPING);
            }
            this.beanFactory = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createOrUpdate(String pid, Map props) {
        Object object = this.monitor;
        synchronized (object) {
            if (this.destroyed) {
                return;
            }
            if (this.beanFactory.containsBean(pid)) {
                this.updateInstance(pid, props);
            } else {
                this.createInstance(pid, props);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void createInstance(String pid, Map props) {
        Object object = this.monitor;
        synchronized (object) {
            if (this.destroyed) {
                return;
            }
            this.beanFactory.registerBeanDefinition(pid, (BeanDefinition)this.templateDefinition);
            this.initialInjectionProperties = props;
            Object bean = this.beanFactory.getBean(pid);
            this.registerService(pid, bean);
        }
    }

    private void registerService(String pid, Object bean) {
        OsgiServiceFactoryBean exporter = this.createExporter(pid, bean);
        this.serviceExporters.put(pid, exporter);
        try {
            this.serviceRegistrations.add(exporter.getObject());
        }
        catch (Exception ex) {
            throw new BeanCreationException("Cannot publish bean for pid " + pid, (Throwable)ex);
        }
    }

    private OsgiServiceFactoryBean createExporter(String beanName, Object bean) {
        OsgiServiceFactoryBean exporter = new OsgiServiceFactoryBean();
        exporter.setInterfaceDetector(this.detector);
        exporter.setBeanClassLoader(this.classLoader);
        exporter.setBeanName(beanName);
        exporter.setBundleContext(this.bundleContext);
        exporter.setExportContextClassLoader(this.ccl);
        exporter.setInterfaces(this.interfaces);
        exporter.setListeners(this.listeners);
        exporter.setTarget(bean);
        Properties props = new Properties();
        if (this.serviceProperties != null) {
            props.putAll((Map<?, ?>)this.serviceProperties);
        }
        props.put("service.pid", beanName);
        exporter.setServiceProperties(props);
        try {
            exporter.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new BeanCreationException("Cannot publish bean for pid " + beanName, (Throwable)ex);
        }
        return exporter;
    }

    private void updateInstance(String pid, Map props) {
        if (this.updateCallback != null) {
            Object instance = this.beanFactory.getBean(pid);
            this.updateCallback.update(instance, props);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void destroyInstance(String pid) {
        Object object = this.monitor;
        synchronized (object) {
            if (this.destroyed) {
                return;
            }
            if (this.beanFactory.containsBeanDefinition(pid)) {
                this.unregisterService(pid);
                Object singleton = this.beanFactory.getBean(pid);
                this.beanFactory.removeBeanDefinition(pid);
                ManagedFactoryDisposableInvoker invoker = this.destructionInvokerFactory.getInvoker(singleton.getClass());
                invoker.destroy(pid, singleton, ManagedFactoryDisposableInvoker.DestructionCodes.CM_ENTRY_DELETED);
            }
        }
    }

    private void unregisterService(String pid) {
        OsgiServiceFactoryBean exporterFactory = this.serviceExporters.remove(pid);
        if (exporterFactory != null) {
            ServiceRegistration registration = null;
            try {
                registration = exporterFactory.getObject();
            }
            catch (Exception ex) {
                log.error((Object)("Could not retrieve registration for pid " + pid), (Throwable)ex);
            }
            if (log.isTraceEnabled()) {
                log.trace((Object)("Unpublishing bean for pid " + pid + " w/ registration " + registration));
            }
            this.serviceRegistrations.remove(registration);
            exporterFactory.destroy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Object object = this.monitor;
        synchronized (object) {
            this.owningBeanFactory = beanFactory;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBundleContext(BundleContext bundleContext) {
        Object object = this.monitor;
        synchronized (object) {
            this.bundleContext = bundleContext;
        }
    }

    public Collection getObject() throws Exception {
        return this.userReturnedCollection;
    }

    public Class<Collection> getObjectType() {
        return Collection.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setListeners(OsgiServiceRegistrationListener[] listeners) {
        if (listeners != null) {
            this.listeners = listeners;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFactoryPid(String factoryPid) {
        Object object = this.monitor;
        synchronized (object) {
            this.factoryPid = factoryPid;
        }
    }

    public void setTemplateDefinition(BeanDefinition[] templateDefinition) {
        if (templateDefinition != null && templateDefinition.length > 0) {
            this.templateDefinition = new RootBeanDefinition();
            this.templateDefinition.overrideFrom(templateDefinition[0]);
        } else {
            this.templateDefinition = null;
        }
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setInterfaceDetector(InterfaceDetector detector) {
        this.detector = detector;
    }

    public void setExportContextClassLoader(ExportContextClassLoaderEnum ccl) {
        this.ccl = ccl;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setAutowireOnUpdate(boolean autowireOnUpdate) {
        this.autowireOnUpdate = autowireOnUpdate;
    }

    public void setUpdateMethod(String updateMethod) {
        this.updateMethod = updateMethod;
    }

    public void setServiceProperties(Map serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    private class DestructionInvokerCache {
        private final String methodName;
        final ConcurrentMap<Class<?>, ManagedFactoryDisposableInvoker> cache = new ConcurrentHashMap(4);

        DestructionInvokerCache(String methodName) {
            this.methodName = methodName;
        }

        ManagedFactoryDisposableInvoker getInvoker(Class<?> type) {
            ManagedFactoryDisposableInvoker invoker = (ManagedFactoryDisposableInvoker)this.cache.get(type);
            if (invoker == null) {
                invoker = new ManagedFactoryDisposableInvoker(type, this.methodName);
                this.cache.put(type, invoker);
            }
            return invoker;
        }
    }

    private class InitialInjectionProcessor
    implements BeanPostProcessor {
        private InitialInjectionProcessor() {
        }

        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            CMUtils.applyMapOntoInstance(bean, ManagedServiceFactoryFactoryBean.this.initialInjectionProperties, (AbstractBeanFactory)ManagedServiceFactoryFactoryBean.this.beanFactory);
            if (log.isTraceEnabled()) {
                log.trace((Object)("Applying initial injection for managed bean " + beanName));
            }
            return bean;
        }
    }

    private class ConfigurationWatcher
    implements ManagedServiceFactory {
        private ConfigurationWatcher() {
        }

        public void deleted(String pid) {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Configuration [" + pid + "] has been deleted"));
            }
            ManagedServiceFactoryFactoryBean.this.destroyInstance(pid);
        }

        public String getName() {
            return "Spring DM managed-service-factory support";
        }

        public void updated(String pid, Dictionary props) throws ConfigurationException {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Configuration [" + pid + "] has been updated with properties " + props));
            }
            ManagedServiceFactoryFactoryBean.this.createOrUpdate(pid, new MapBasedDictionary(props));
        }
    }
}

