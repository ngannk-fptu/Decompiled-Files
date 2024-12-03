/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.TargetSource
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.scope.ScopedProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.aop.target.LazyInitTargetSource
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.CannotLoadBeanClassException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.jmx.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.management.DynamicMBean;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Constants;
import org.springframework.jmx.export.MBeanExportException;
import org.springframework.jmx.export.MBeanExportOperations;
import org.springframework.jmx.export.MBeanExporterListener;
import org.springframework.jmx.export.NotificationListenerBean;
import org.springframework.jmx.export.SpringModelMBean;
import org.springframework.jmx.export.UnableToRegisterMBeanException;
import org.springframework.jmx.export.assembler.AutodetectCapableMBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.SimpleReflectiveMBeanInfoAssembler;
import org.springframework.jmx.export.naming.KeyNamingStrategy;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.export.notification.ModelMBeanNotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.MBeanRegistrationSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

public class MBeanExporter
extends MBeanRegistrationSupport
implements MBeanExportOperations,
BeanClassLoaderAware,
BeanFactoryAware,
InitializingBean,
SmartInitializingSingleton,
DisposableBean {
    public static final int AUTODETECT_NONE = 0;
    public static final int AUTODETECT_MBEAN = 1;
    public static final int AUTODETECT_ASSEMBLER = 2;
    public static final int AUTODETECT_ALL = 3;
    private static final String WILDCARD = "*";
    private static final String MR_TYPE_OBJECT_REFERENCE = "ObjectReference";
    private static final String CONSTANT_PREFIX_AUTODETECT = "AUTODETECT_";
    private static final Constants constants = new Constants(MBeanExporter.class);
    @Nullable
    private Map<String, Object> beans;
    @Nullable
    private Integer autodetectMode;
    private boolean allowEagerInit = false;
    private MBeanInfoAssembler assembler = new SimpleReflectiveMBeanInfoAssembler();
    private ObjectNamingStrategy namingStrategy = new KeyNamingStrategy();
    private boolean ensureUniqueRuntimeObjectNames = true;
    private boolean exposeManagedResourceClassLoader = true;
    private Set<String> excludedBeans = new HashSet<String>();
    @Nullable
    private MBeanExporterListener[] listeners;
    @Nullable
    private NotificationListenerBean[] notificationListeners;
    private final Map<NotificationListenerBean, ObjectName[]> registeredNotificationListeners = new LinkedHashMap<NotificationListenerBean, ObjectName[]>();
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private ListableBeanFactory beanFactory;

    public void setBeans(Map<String, Object> beans2) {
        this.beans = beans2;
    }

    public void setAutodetect(boolean autodetect) {
        this.autodetectMode = autodetect ? 3 : 0;
    }

    public void setAutodetectMode(int autodetectMode) {
        if (!constants.getValues(CONSTANT_PREFIX_AUTODETECT).contains(autodetectMode)) {
            throw new IllegalArgumentException("Only values of autodetect constants allowed");
        }
        this.autodetectMode = autodetectMode;
    }

    public void setAutodetectModeName(String constantName) {
        if (!constantName.startsWith(CONSTANT_PREFIX_AUTODETECT)) {
            throw new IllegalArgumentException("Only autodetect constants allowed");
        }
        this.autodetectMode = (Integer)constants.asNumber(constantName);
    }

    public void setAllowEagerInit(boolean allowEagerInit) {
        this.allowEagerInit = allowEagerInit;
    }

    public void setAssembler(MBeanInfoAssembler assembler) {
        this.assembler = assembler;
    }

    public void setNamingStrategy(ObjectNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    public void setEnsureUniqueRuntimeObjectNames(boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }

    public void setExposeManagedResourceClassLoader(boolean exposeManagedResourceClassLoader) {
        this.exposeManagedResourceClassLoader = exposeManagedResourceClassLoader;
    }

    public void setExcludedBeans(String ... excludedBeans) {
        this.excludedBeans.clear();
        Collections.addAll(this.excludedBeans, excludedBeans);
    }

    public void addExcludedBean(String excludedBean) {
        Assert.notNull((Object)excludedBean, (String)"ExcludedBean must not be null");
        this.excludedBeans.add(excludedBean);
    }

    public void setListeners(MBeanExporterListener ... listeners) {
        this.listeners = listeners;
    }

    public void setNotificationListeners(NotificationListenerBean ... notificationListeners) {
        this.notificationListeners = notificationListeners;
    }

    public void setNotificationListenerMappings(Map<?, ? extends NotificationListener> listeners) {
        Assert.notNull(listeners, (String)"'listeners' must not be null");
        ArrayList notificationListeners = new ArrayList(listeners.size());
        listeners.forEach((key, listener) -> {
            NotificationListenerBean bean2 = new NotificationListenerBean((NotificationListener)listener);
            if (key != null && !WILDCARD.equals(key)) {
                bean2.setMappedObjectName(key);
            }
            notificationListeners.add(bean2);
        });
        this.notificationListeners = notificationListeners.toArray(new NotificationListenerBean[0]);
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ListableBeanFactory) {
            this.beanFactory = (ListableBeanFactory)beanFactory;
        } else {
            this.logger.debug((Object)"MBeanExporter not running in a ListableBeanFactory: autodetection of MBeans not available.");
        }
    }

    public void afterPropertiesSet() {
        if (this.server == null) {
            this.server = JmxUtils.locateMBeanServer();
        }
    }

    public void afterSingletonsInstantiated() {
        try {
            this.logger.debug((Object)"Registering beans for JMX exposure on startup");
            this.registerBeans();
            this.registerNotificationListeners();
        }
        catch (RuntimeException ex) {
            this.unregisterNotificationListeners();
            this.unregisterBeans();
            throw ex;
        }
    }

    public void destroy() {
        this.logger.debug((Object)"Unregistering JMX-exposed beans on shutdown");
        this.unregisterNotificationListeners();
        this.unregisterBeans();
    }

    @Override
    public ObjectName registerManagedResource(Object managedResource) throws MBeanExportException {
        ObjectName objectName;
        Assert.notNull((Object)managedResource, (String)"Managed resource must not be null");
        try {
            objectName = this.getObjectName(managedResource, null);
            if (this.ensureUniqueRuntimeObjectNames) {
                objectName = JmxUtils.appendIdentityToObjectName(objectName, managedResource);
            }
        }
        catch (Throwable ex) {
            throw new MBeanExportException("Unable to generate ObjectName for MBean [" + managedResource + "]", ex);
        }
        this.registerManagedResource(managedResource, objectName);
        return objectName;
    }

    @Override
    public void registerManagedResource(Object managedResource, ObjectName objectName) throws MBeanExportException {
        Assert.notNull((Object)managedResource, (String)"Managed resource must not be null");
        Assert.notNull((Object)objectName, (String)"ObjectName must not be null");
        try {
            if (this.isMBean(managedResource.getClass())) {
                this.doRegister(managedResource, objectName);
            } else {
                ModelMBean mbean = this.createAndConfigureMBean(managedResource, managedResource.getClass().getName());
                this.doRegister(mbean, objectName);
                this.injectNotificationPublisherIfNecessary(managedResource, mbean, objectName);
            }
        }
        catch (JMException ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + managedResource + "] with object name [" + objectName + "]", ex);
        }
    }

    @Override
    public void unregisterManagedResource(ObjectName objectName) {
        Assert.notNull((Object)objectName, (String)"ObjectName must not be null");
        this.doUnregister(objectName);
    }

    protected void registerBeans() {
        int mode;
        if (this.beans == null) {
            this.beans = new HashMap<String, Object>();
            if (this.autodetectMode == null) {
                this.autodetectMode = 3;
            }
        }
        int n = mode = this.autodetectMode != null ? this.autodetectMode : 0;
        if (mode != 0) {
            if (this.beanFactory == null) {
                throw new MBeanExportException("Cannot autodetect MBeans if not running in a BeanFactory");
            }
            if (mode == 1 || mode == 3) {
                this.logger.debug((Object)"Autodetecting user-defined JMX MBeans");
                this.autodetect(this.beans, (beanClass, beanName) -> this.isMBean(beanClass));
            }
            if ((mode == 2 || mode == 3) && this.assembler instanceof AutodetectCapableMBeanInfoAssembler) {
                this.autodetect(this.beans, ((AutodetectCapableMBeanInfoAssembler)this.assembler)::includeBean);
            }
        }
        if (!this.beans.isEmpty()) {
            this.beans.forEach((beanName, instance) -> this.registerBeanNameOrInstance(instance, (String)beanName));
        }
    }

    protected boolean isBeanDefinitionLazyInit(ListableBeanFactory beanFactory, String beanName) {
        return beanFactory instanceof ConfigurableListableBeanFactory && beanFactory.containsBeanDefinition(beanName) && ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(beanName).isLazyInit();
    }

    protected ObjectName registerBeanNameOrInstance(Object mapValue, String beanKey) throws MBeanExportException {
        try {
            if (mapValue instanceof String) {
                if (this.beanFactory == null) {
                    throw new MBeanExportException("Cannot resolve bean names if not running in a BeanFactory");
                }
                String beanName = (String)mapValue;
                if (this.isBeanDefinitionLazyInit(this.beanFactory, beanName)) {
                    ObjectName objectName = this.registerLazyInit(beanName, beanKey);
                    this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName);
                    return objectName;
                }
                Object bean2 = this.beanFactory.getBean(beanName);
                ObjectName objectName = this.registerBeanInstance(bean2, beanKey);
                this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName);
                return objectName;
            }
            if (this.beanFactory != null) {
                Map beansOfSameType = this.beanFactory.getBeansOfType(mapValue.getClass(), false, this.allowEagerInit);
                for (Map.Entry entry : beansOfSameType.entrySet()) {
                    if (entry.getValue() != mapValue) continue;
                    String beanName = (String)entry.getKey();
                    ObjectName objectName = this.registerBeanInstance(mapValue, beanKey);
                    this.replaceNotificationListenerBeanNameKeysIfNecessary(beanName, objectName);
                    return objectName;
                }
            }
            return this.registerBeanInstance(mapValue, beanKey);
        }
        catch (Throwable ex) {
            throw new UnableToRegisterMBeanException("Unable to register MBean [" + mapValue + "] with key '" + beanKey + "'", ex);
        }
    }

    private void replaceNotificationListenerBeanNameKeysIfNecessary(String beanName, ObjectName objectName) {
        if (this.notificationListeners != null) {
            for (NotificationListenerBean notificationListener : this.notificationListeners) {
                notificationListener.replaceObjectName(beanName, objectName);
            }
        }
    }

    private ObjectName registerBeanInstance(Object bean2, String beanKey) throws JMException {
        ObjectName objectName = this.getObjectName(bean2, beanKey);
        Object mbeanToExpose = null;
        if (this.isMBean(bean2.getClass())) {
            mbeanToExpose = bean2;
        } else {
            DynamicMBean adaptedBean = this.adaptMBeanIfPossible(bean2);
            if (adaptedBean != null) {
                mbeanToExpose = adaptedBean;
            }
        }
        if (mbeanToExpose != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Located MBean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]"));
            }
            this.doRegister(mbeanToExpose, objectName);
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Located managed bean '" + beanKey + "': registering with JMX server as MBean [" + objectName + "]"));
            }
            ModelMBean mbean = this.createAndConfigureMBean(bean2, beanKey);
            this.doRegister(mbean, objectName);
            this.injectNotificationPublisherIfNecessary(bean2, mbean, objectName);
        }
        return objectName;
    }

    private ObjectName registerLazyInit(String beanName, String beanKey) throws JMException {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory set");
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setFrozen(true);
        if (this.isMBean(this.beanFactory.getType(beanName))) {
            LazyInitTargetSource targetSource = new LazyInitTargetSource();
            targetSource.setTargetBeanName(beanName);
            targetSource.setBeanFactory((BeanFactory)this.beanFactory);
            proxyFactory.setTargetSource((TargetSource)targetSource);
            Object proxy = proxyFactory.getProxy(this.beanClassLoader);
            ObjectName objectName = this.getObjectName(proxy, beanKey);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Located MBean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName + "]"));
            }
            this.doRegister(proxy, objectName);
            return objectName;
        }
        NotificationPublisherAwareLazyTargetSource targetSource = new NotificationPublisherAwareLazyTargetSource();
        targetSource.setTargetBeanName(beanName);
        targetSource.setBeanFactory((BeanFactory)this.beanFactory);
        proxyFactory.setTargetSource((TargetSource)targetSource);
        Object proxy = proxyFactory.getProxy(this.beanClassLoader);
        ObjectName objectName = this.getObjectName(proxy, beanKey);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Located simple bean '" + beanKey + "': registering with JMX server as lazy-init MBean [" + objectName + "]"));
        }
        ModelMBean mbean = this.createAndConfigureMBean(proxy, beanKey);
        targetSource.setModelMBean(mbean);
        targetSource.setObjectName(objectName);
        this.doRegister(mbean, objectName);
        return objectName;
    }

    protected ObjectName getObjectName(Object bean2, @Nullable String beanKey) throws MalformedObjectNameException {
        if (bean2 instanceof SelfNaming) {
            return ((SelfNaming)bean2).getObjectName();
        }
        return this.namingStrategy.getObjectName(bean2, beanKey);
    }

    protected boolean isMBean(@Nullable Class<?> beanClass) {
        return JmxUtils.isMBean(beanClass);
    }

    @Nullable
    protected DynamicMBean adaptMBeanIfPossible(Object bean2) throws JMException {
        Class targetClass = AopUtils.getTargetClass((Object)bean2);
        if (targetClass != bean2.getClass()) {
            Class<?> ifc = JmxUtils.getMXBeanInterface(targetClass);
            if (ifc != null) {
                if (!ifc.isInstance(bean2)) {
                    throw new NotCompliantMBeanException("Managed bean [" + bean2 + "] has a target class with an MXBean interface but does not expose it in the proxy");
                }
                return new StandardMBean(bean2, ifc, true);
            }
            ifc = JmxUtils.getMBeanInterface(targetClass);
            if (ifc != null) {
                if (!ifc.isInstance(bean2)) {
                    throw new NotCompliantMBeanException("Managed bean [" + bean2 + "] has a target class with an MBean interface but does not expose it in the proxy");
                }
                return new StandardMBean(bean2, ifc);
            }
        }
        return null;
    }

    protected ModelMBean createAndConfigureMBean(Object managedResource, String beanKey) throws MBeanExportException {
        try {
            ModelMBean mbean = this.createModelMBean();
            mbean.setModelMBeanInfo(this.getMBeanInfo(managedResource, beanKey));
            mbean.setManagedResource(managedResource, MR_TYPE_OBJECT_REFERENCE);
            return mbean;
        }
        catch (Throwable ex) {
            throw new MBeanExportException("Could not create ModelMBean for managed resource [" + managedResource + "] with key '" + beanKey + "'", ex);
        }
    }

    protected ModelMBean createModelMBean() throws MBeanException {
        return this.exposeManagedResourceClassLoader ? new SpringModelMBean() : new RequiredModelMBean();
    }

    private ModelMBeanInfo getMBeanInfo(Object managedBean, String beanKey) throws JMException {
        ModelMBeanInfo info = this.assembler.getMBeanInfo(managedBean, beanKey);
        if (this.logger.isInfoEnabled() && ObjectUtils.isEmpty((Object[])info.getAttributes()) && ObjectUtils.isEmpty((Object[])info.getOperations())) {
            this.logger.info((Object)("Bean with key '" + beanKey + "' has been registered as an MBean but has no exposed attributes or operations"));
        }
        return info;
    }

    private void autodetect(Map<String, Object> beans2, AutodetectCallback callback) {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory set");
        LinkedHashSet beanNames = new LinkedHashSet(this.beanFactory.getBeanDefinitionCount());
        Collections.addAll(beanNames, this.beanFactory.getBeanDefinitionNames());
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            Collections.addAll(beanNames, ((ConfigurableBeanFactory)this.beanFactory).getSingletonNames());
        }
        for (String beanName : beanNames) {
            if (this.isExcluded(beanName) || this.isBeanDefinitionAbstract(this.beanFactory, beanName)) continue;
            try {
                Class beanClass = this.beanFactory.getType(beanName);
                if (beanClass == null || !callback.include(beanClass, beanName)) continue;
                boolean lazyInit = this.isBeanDefinitionLazyInit(this.beanFactory, beanName);
                Object beanInstance = null;
                if (!lazyInit && !beanClass.isInstance(beanInstance = this.beanFactory.getBean(beanName))) continue;
                if (!(ScopedProxyUtils.isScopedTarget((String)beanName) || beans2.containsValue(beanName) || beanInstance != null && CollectionUtils.containsInstance(beans2.values(), (Object)beanInstance))) {
                    beans2.put(beanName, beanInstance != null ? beanInstance : beanName);
                    if (!this.logger.isDebugEnabled()) continue;
                    this.logger.debug((Object)("Bean with name '" + beanName + "' has been autodetected for JMX exposure"));
                    continue;
                }
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace((Object)("Bean with name '" + beanName + "' is already registered for JMX exposure"));
            }
            catch (CannotLoadBeanClassException ex) {
                if (!this.allowEagerInit) continue;
                throw ex;
            }
        }
    }

    private boolean isExcluded(String beanName) {
        return this.excludedBeans.contains(beanName) || beanName.startsWith("&") && this.excludedBeans.contains(beanName.substring("&".length()));
    }

    private boolean isBeanDefinitionAbstract(ListableBeanFactory beanFactory, String beanName) {
        return beanFactory instanceof ConfigurableListableBeanFactory && beanFactory.containsBeanDefinition(beanName) && ((ConfigurableListableBeanFactory)beanFactory).getBeanDefinition(beanName).isAbstract();
    }

    private void injectNotificationPublisherIfNecessary(Object managedResource, @Nullable ModelMBean modelMBean, @Nullable ObjectName objectName) {
        if (managedResource instanceof NotificationPublisherAware && modelMBean != null && objectName != null) {
            ((NotificationPublisherAware)managedResource).setNotificationPublisher(new ModelMBeanNotificationPublisher(modelMBean, objectName, managedResource));
        }
    }

    private void registerNotificationListeners() throws MBeanExportException {
        if (this.notificationListeners != null) {
            Assert.state((this.server != null ? 1 : 0) != 0, (String)"No MBeanServer available");
            for (NotificationListenerBean bean2 : this.notificationListeners) {
                try {
                    ObjectName[] mappedObjectNames = bean2.getResolvedObjectNames();
                    if (mappedObjectNames == null) {
                        mappedObjectNames = this.getRegisteredObjectNames();
                    }
                    if (this.registeredNotificationListeners.put(bean2, mappedObjectNames) != null) continue;
                    for (ObjectName mappedObjectName : mappedObjectNames) {
                        this.server.addNotificationListener(mappedObjectName, bean2.getNotificationListener(), bean2.getNotificationFilter(), bean2.getHandback());
                    }
                }
                catch (Throwable ex) {
                    throw new MBeanExportException("Unable to register NotificationListener", ex);
                }
            }
        }
    }

    private void unregisterNotificationListeners() {
        if (this.server != null) {
            this.registeredNotificationListeners.forEach((bean2, mappedObjectNames) -> {
                for (ObjectName mappedObjectName : mappedObjectNames) {
                    try {
                        this.server.removeNotificationListener(mappedObjectName, bean2.getNotificationListener(), bean2.getNotificationFilter(), bean2.getHandback());
                    }
                    catch (Throwable ex) {
                        if (!this.logger.isDebugEnabled()) continue;
                        this.logger.debug((Object)"Unable to unregister NotificationListener", ex);
                    }
                }
            });
        }
        this.registeredNotificationListeners.clear();
    }

    @Override
    protected void onRegister(ObjectName objectName) {
        this.notifyListenersOfRegistration(objectName);
    }

    @Override
    protected void onUnregister(ObjectName objectName) {
        this.notifyListenersOfUnregistration(objectName);
    }

    private void notifyListenersOfRegistration(ObjectName objectName) {
        if (this.listeners != null) {
            for (MBeanExporterListener listener : this.listeners) {
                listener.mbeanRegistered(objectName);
            }
        }
    }

    private void notifyListenersOfUnregistration(ObjectName objectName) {
        if (this.listeners != null) {
            for (MBeanExporterListener listener : this.listeners) {
                listener.mbeanUnregistered(objectName);
            }
        }
    }

    private class NotificationPublisherAwareLazyTargetSource
    extends LazyInitTargetSource {
        @Nullable
        private ModelMBean modelMBean;
        @Nullable
        private ObjectName objectName;

        private NotificationPublisherAwareLazyTargetSource() {
        }

        public void setModelMBean(ModelMBean modelMBean) {
            this.modelMBean = modelMBean;
        }

        public void setObjectName(ObjectName objectName) {
            this.objectName = objectName;
        }

        @Nullable
        public Object getTarget() {
            try {
                return super.getTarget();
            }
            catch (RuntimeException ex) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info((Object)("Failed to retrieve target for JMX-exposed bean [" + this.objectName + "]: " + ex));
                }
                throw ex;
            }
        }

        protected void postProcessTargetObject(Object targetObject) {
            MBeanExporter.this.injectNotificationPublisherIfNecessary(targetObject, this.modelMBean, this.objectName);
        }
    }

    @FunctionalInterface
    private static interface AutodetectCallback {
        public boolean include(Class<?> var1, String var2);
    }
}

