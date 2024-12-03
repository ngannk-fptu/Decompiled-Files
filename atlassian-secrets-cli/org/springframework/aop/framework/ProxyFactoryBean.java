/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ProxyCreatorSupport;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.UnknownAdviceTypeException;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class ProxyFactoryBean
extends ProxyCreatorSupport
implements FactoryBean<Object>,
BeanClassLoaderAware,
BeanFactoryAware {
    public static final String GLOBAL_SUFFIX = "*";
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private String[] interceptorNames;
    @Nullable
    private String targetName;
    private boolean autodetectInterfaces = true;
    private boolean singleton = true;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;
    @Nullable
    private transient ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();
    private transient boolean classLoaderConfigured = false;
    @Nullable
    private transient BeanFactory beanFactory;
    private boolean advisorChainInitialized = false;
    @Nullable
    private Object singletonInstance;

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) throws ClassNotFoundException {
        this.setInterfaces(proxyInterfaces);
    }

    public void setInterceptorNames(String ... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setAutodetectInterfaces(boolean autodetectInterfaces) {
        this.autodetectInterfaces = autodetectInterfaces;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    @Override
    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    public void setProxyClassLoader(@Nullable ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
        this.classLoaderConfigured = classLoader != null;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (!this.classLoaderConfigured) {
            this.proxyClassLoader = classLoader;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.checkInterceptorNames();
    }

    @Override
    @Nullable
    public Object getObject() throws BeansException {
        this.initializeAdvisorChain();
        if (this.isSingleton()) {
            return this.getSingletonInstance();
        }
        if (this.targetName == null) {
            this.logger.warn("Using non-singleton proxies with singleton targets is often undesirable. Enable prototype proxies by setting the 'targetName' property.");
        }
        return this.newPrototypeInstance();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Class<?> getObjectType() {
        ProxyFactoryBean proxyFactoryBean = this;
        synchronized (proxyFactoryBean) {
            if (this.singletonInstance != null) {
                return this.singletonInstance.getClass();
            }
        }
        Class<?>[] ifcs = this.getProxiedInterfaces();
        if (ifcs.length == 1) {
            return ifcs[0];
        }
        if (ifcs.length > 1) {
            return this.createCompositeInterface(ifcs);
        }
        if (this.targetName != null && this.beanFactory != null) {
            return this.beanFactory.getType(this.targetName);
        }
        return this.getTargetClass();
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    protected Class<?> createCompositeInterface(Class<?>[] interfaces) {
        return ClassUtils.createCompositeInterface(interfaces, this.proxyClassLoader);
    }

    private synchronized Object getSingletonInstance() {
        if (this.singletonInstance == null) {
            this.targetSource = this.freshTargetSource();
            if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass()) {
                Class<?> targetClass = this.getTargetClass();
                if (targetClass == null) {
                    throw new FactoryBeanNotInitializedException("Cannot determine target class for proxy");
                }
                this.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
            }
            super.setFrozen(this.freezeProxy);
            this.singletonInstance = this.getProxy(this.createAopProxy());
        }
        return this.singletonInstance;
    }

    private synchronized Object newPrototypeInstance() {
        Class<?> targetClass;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating copy of prototype ProxyFactoryBean config: " + this);
        }
        ProxyCreatorSupport copy = new ProxyCreatorSupport(this.getAopProxyFactory());
        TargetSource targetSource = this.freshTargetSource();
        copy.copyConfigurationFrom(this, targetSource, this.freshAdvisorChain());
        if (this.autodetectInterfaces && this.getProxiedInterfaces().length == 0 && !this.isProxyTargetClass() && (targetClass = targetSource.getTargetClass()) != null) {
            copy.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
        }
        copy.setFrozen(this.freezeProxy);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Using ProxyCreatorSupport copy: " + copy);
        }
        return this.getProxy(copy.createAopProxy());
    }

    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy(this.proxyClassLoader);
    }

    private void checkInterceptorNames() {
        if (!ObjectUtils.isEmpty(this.interceptorNames)) {
            String finalName = this.interceptorNames[this.interceptorNames.length - 1];
            if (this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE && !finalName.endsWith(GLOBAL_SUFFIX) && !this.isNamedBeanAnAdvisorOrAdvice(finalName)) {
                this.targetName = finalName;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Bean with name '" + finalName + "' concluding interceptor chain is not an advisor class: treating it as a target or TargetSource");
                }
                String[] newNames = new String[this.interceptorNames.length - 1];
                System.arraycopy(this.interceptorNames, 0, newNames, 0, newNames.length);
                this.interceptorNames = newNames;
            }
        }
    }

    private boolean isNamedBeanAnAdvisorOrAdvice(String beanName) {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Class<?> namedBeanClass = this.beanFactory.getType(beanName);
        if (namedBeanClass != null) {
            return Advisor.class.isAssignableFrom(namedBeanClass) || Advice.class.isAssignableFrom(namedBeanClass);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Could not determine type of bean with name '" + beanName + "' - assuming it is neither an Advisor nor an Advice");
        }
        return false;
    }

    private synchronized void initializeAdvisorChain() throws AopConfigException, BeansException {
        if (this.advisorChainInitialized) {
            return;
        }
        if (!ObjectUtils.isEmpty(this.interceptorNames)) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve interceptor names " + Arrays.asList(this.interceptorNames));
            }
            if (this.interceptorNames[this.interceptorNames.length - 1].endsWith(GLOBAL_SUFFIX) && this.targetName == null && this.targetSource == EMPTY_TARGET_SOURCE) {
                throw new AopConfigException("Target required after globals");
            }
            for (String name : this.interceptorNames) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Configuring advisor or advice '" + name + "'");
                }
                if (name.endsWith(GLOBAL_SUFFIX)) {
                    if (!(this.beanFactory instanceof ListableBeanFactory)) {
                        throw new AopConfigException("Can only use global advisors or interceptors with a ListableBeanFactory");
                    }
                    this.addGlobalAdvisor((ListableBeanFactory)this.beanFactory, name.substring(0, name.length() - GLOBAL_SUFFIX.length()));
                    continue;
                }
                Object advice = this.singleton || this.beanFactory.isSingleton(name) ? this.beanFactory.getBean(name) : new PrototypePlaceholderAdvisor(name);
                this.addAdvisorOnChainCreation(advice, name);
            }
        }
        this.advisorChainInitialized = true;
    }

    private List<Advisor> freshAdvisorChain() {
        Advisor[] advisors = this.getAdvisors();
        ArrayList<Advisor> freshAdvisors = new ArrayList<Advisor>(advisors.length);
        for (Advisor advisor : advisors) {
            if (advisor instanceof PrototypePlaceholderAdvisor) {
                PrototypePlaceholderAdvisor pa = (PrototypePlaceholderAdvisor)advisor;
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Refreshing bean named '" + pa.getBeanName() + "'");
                }
                if (this.beanFactory == null) {
                    throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve prototype advisor '" + pa.getBeanName() + "'");
                }
                Object bean2 = this.beanFactory.getBean(pa.getBeanName());
                Advisor refreshedAdvisor = this.namedBeanToAdvisor(bean2);
                freshAdvisors.add(refreshedAdvisor);
                continue;
            }
            freshAdvisors.add(advisor);
        }
        return freshAdvisors;
    }

    private void addGlobalAdvisor(ListableBeanFactory beanFactory, String prefix) {
        String[] globalAdvisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Advisor.class);
        String[] globalInterceptorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, Interceptor.class);
        ArrayList<Object> beans2 = new ArrayList<Object>(globalAdvisorNames.length + globalInterceptorNames.length);
        HashMap<Object, String> names = new HashMap<Object, String>(beans2.size());
        for (String name : globalAdvisorNames) {
            Object bean2 = beanFactory.getBean(name);
            beans2.add(bean2);
            names.put(bean2, name);
        }
        for (String name : globalInterceptorNames) {
            Object bean2 = beanFactory.getBean(name);
            beans2.add(bean2);
            names.put(bean2, name);
        }
        AnnotationAwareOrderComparator.sort(beans2);
        for (Object e : beans2) {
            String name = (String)names.get(e);
            if (!name.startsWith(prefix)) continue;
            this.addAdvisorOnChainCreation(e, name);
        }
    }

    private void addAdvisorOnChainCreation(Object next, String name) {
        Advisor advisor = this.namedBeanToAdvisor(next);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Adding advisor with name '" + name + "'");
        }
        this.addAdvisor(advisor);
    }

    private TargetSource freshTargetSource() {
        Object target;
        if (this.targetName == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Not refreshing target: Bean name not specified in 'interceptorNames'.");
            }
            return this.targetSource;
        }
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory available anymore (probably due to serialization) - cannot resolve target with name '" + this.targetName + "'");
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Refreshing target with name '" + this.targetName + "'");
        }
        return (target = this.beanFactory.getBean(this.targetName)) instanceof TargetSource ? (TargetSource)target : new SingletonTargetSource(target);
    }

    private Advisor namedBeanToAdvisor(Object next) {
        try {
            return this.advisorAdapterRegistry.wrap(next);
        }
        catch (UnknownAdviceTypeException ex) {
            throw new AopConfigException("Unknown advisor type " + next.getClass() + "; Can only include Advisor or Advice type beans in interceptorNames chain except for last entry,which may also be target or TargetSource", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void adviceChanged() {
        super.adviceChanged();
        if (this.singleton) {
            this.logger.debug("Advice has changed; recaching singleton instance");
            ProxyFactoryBean proxyFactoryBean = this;
            synchronized (proxyFactoryBean) {
                this.singletonInstance = null;
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
    }

    private static class PrototypePlaceholderAdvisor
    implements Advisor,
    Serializable {
        private final String beanName;
        private final String message;

        public PrototypePlaceholderAdvisor(String beanName) {
            this.beanName = beanName;
            this.message = "Placeholder for prototype Advisor/Advice with bean name '" + beanName + "'";
        }

        public String getBeanName() {
            return this.beanName;
        }

        @Override
        public Advice getAdvice() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        @Override
        public boolean isPerInstance() {
            throw new UnsupportedOperationException("Cannot invoke methods: " + this.message);
        }

        public String toString() {
            return this.message;
        }
    }
}

