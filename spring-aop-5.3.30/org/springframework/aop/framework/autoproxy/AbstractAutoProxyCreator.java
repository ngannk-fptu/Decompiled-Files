/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.PropertyValues
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
 *  org.springframework.core.SmartClassLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyProcessorSupport;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractAutoProxyCreator
extends ProxyProcessorSupport
implements SmartInstantiationAwareBeanPostProcessor,
BeanFactoryAware {
    @Nullable
    protected static final Object[] DO_NOT_PROXY = null;
    protected static final Object[] PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS = new Object[0];
    protected final Log logger = LogFactory.getLog(this.getClass());
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    private boolean freezeProxy = false;
    private String[] interceptorNames = new String[0];
    private boolean applyCommonInterceptorsFirst = true;
    @Nullable
    private TargetSourceCreator[] customTargetSourceCreators;
    @Nullable
    private BeanFactory beanFactory;
    private final Set<String> targetSourcedBeans = Collections.newSetFromMap(new ConcurrentHashMap(16));
    private final Map<Object, Object> earlyProxyReferences = new ConcurrentHashMap<Object, Object>(16);
    private final Map<Object, Class<?>> proxyTypes = new ConcurrentHashMap(16);
    private final Map<Object, Boolean> advisedBeans = new ConcurrentHashMap<Object, Boolean>(256);

    @Override
    public void setFrozen(boolean frozen) {
        this.freezeProxy = frozen;
    }

    @Override
    public boolean isFrozen() {
        return this.freezeProxy;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    public void setCustomTargetSourceCreators(TargetSourceCreator ... targetSourceCreators) {
        this.customTargetSourceCreators = targetSourceCreators;
    }

    public void setInterceptorNames(String ... interceptorNames) {
        this.interceptorNames = interceptorNames;
    }

    public void setApplyCommonInterceptorsFirst(boolean applyCommonInterceptorsFirst) {
        this.applyCommonInterceptorsFirst = applyCommonInterceptorsFirst;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Nullable
    public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
        if (this.proxyTypes.isEmpty()) {
            return null;
        }
        Object cacheKey = this.getCacheKey(beanClass, beanName);
        return this.proxyTypes.get(cacheKey);
    }

    @Nullable
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) {
        return null;
    }

    public Object getEarlyBeanReference(Object bean, String beanName) {
        Object cacheKey = this.getCacheKey(bean.getClass(), beanName);
        this.earlyProxyReferences.put(cacheKey, bean);
        return this.wrapIfNecessary(bean, beanName, cacheKey);
    }

    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        TargetSource targetSource;
        Object cacheKey = this.getCacheKey(beanClass, beanName);
        if (!StringUtils.hasLength((String)beanName) || !this.targetSourcedBeans.contains(beanName)) {
            if (this.advisedBeans.containsKey(cacheKey)) {
                return null;
            }
            if (this.isInfrastructureClass(beanClass) || this.shouldSkip(beanClass, beanName)) {
                this.advisedBeans.put(cacheKey, Boolean.FALSE);
                return null;
            }
        }
        if ((targetSource = this.getCustomTargetSource(beanClass, beanName)) != null) {
            if (StringUtils.hasLength((String)beanName)) {
                this.targetSourcedBeans.add(beanName);
            }
            Object[] specificInterceptors = this.getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
            Object proxy = this.createProxy(beanClass, beanName, specificInterceptors, targetSource);
            this.proxyTypes.put(cacheKey, proxy.getClass());
            return proxy;
        }
        return null;
    }

    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        return pvs;
    }

    public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
        Object cacheKey;
        if (bean != null && this.earlyProxyReferences.remove(cacheKey = this.getCacheKey(bean.getClass(), beanName)) != bean) {
            return this.wrapIfNecessary(bean, beanName, cacheKey);
        }
        return bean;
    }

    protected Object getCacheKey(Class<?> beanClass, @Nullable String beanName) {
        if (StringUtils.hasLength((String)beanName)) {
            return FactoryBean.class.isAssignableFrom(beanClass) ? "&" + beanName : beanName;
        }
        return beanClass;
    }

    protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
        if (StringUtils.hasLength((String)beanName) && this.targetSourcedBeans.contains(beanName)) {
            return bean;
        }
        if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
            return bean;
        }
        if (this.isInfrastructureClass(bean.getClass()) || this.shouldSkip(bean.getClass(), beanName)) {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return bean;
        }
        Object[] specificInterceptors = this.getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
        if (specificInterceptors != DO_NOT_PROXY) {
            this.advisedBeans.put(cacheKey, Boolean.TRUE);
            Object proxy = this.createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
            this.proxyTypes.put(cacheKey, proxy.getClass());
            return proxy;
        }
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal;
        boolean bl = retVal = Advice.class.isAssignableFrom(beanClass) || Pointcut.class.isAssignableFrom(beanClass) || Advisor.class.isAssignableFrom(beanClass) || AopInfrastructureBean.class.isAssignableFrom(beanClass);
        if (retVal && this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]"));
        }
        return retVal;
    }

    protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        return AutoProxyUtils.isOriginalInstance(beanName, beanClass);
    }

    @Nullable
    protected TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
        if (this.customTargetSourceCreators != null && this.beanFactory != null && this.beanFactory.containsBean(beanName)) {
            for (TargetSourceCreator tsc : this.customTargetSourceCreators) {
                TargetSource ts = tsc.getTargetSource(beanClass, beanName);
                if (ts == null) continue;
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("TargetSourceCreator [" + tsc + "] found custom TargetSource for bean with name '" + beanName + "'"));
                }
                return ts;
            }
        }
        return null;
    }

    protected Object createProxy(Class<?> beanClass, @Nullable String beanName, @Nullable Object[] specificInterceptors, TargetSource targetSource) {
        ClassLoader classLoader;
        if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
            AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory)this.beanFactory, beanName, beanClass);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        if (proxyFactory.isProxyTargetClass()) {
            if (Proxy.isProxyClass(beanClass) || ClassUtils.isLambdaClass(beanClass)) {
                for (Class<?> ifc : beanClass.getInterfaces()) {
                    proxyFactory.addInterface(ifc);
                }
            }
        } else if (this.shouldProxyTargetClass(beanClass, beanName)) {
            proxyFactory.setProxyTargetClass(true);
        } else {
            this.evaluateProxyInterfaces(beanClass, proxyFactory);
        }
        Advisor[] advisors = this.buildAdvisors(beanName, specificInterceptors);
        proxyFactory.addAdvisors(advisors);
        proxyFactory.setTargetSource(targetSource);
        this.customizeProxyFactory(proxyFactory);
        proxyFactory.setFrozen(this.freezeProxy);
        if (this.advisorsPreFiltered()) {
            proxyFactory.setPreFiltered(true);
        }
        if ((classLoader = this.getProxyClassLoader()) instanceof SmartClassLoader && classLoader != beanClass.getClassLoader()) {
            classLoader = ((SmartClassLoader)classLoader).getOriginalClassLoader();
        }
        return proxyFactory.getProxy(classLoader);
    }

    protected boolean shouldProxyTargetClass(Class<?> beanClass, @Nullable String beanName) {
        return this.beanFactory instanceof ConfigurableListableBeanFactory && AutoProxyUtils.shouldProxyTargetClass((ConfigurableListableBeanFactory)this.beanFactory, beanName);
    }

    protected boolean advisorsPreFiltered() {
        return false;
    }

    protected Advisor[] buildAdvisors(@Nullable String beanName, @Nullable Object[] specificInterceptors) {
        Advisor[] commonInterceptors = this.resolveInterceptorNames();
        ArrayList<Object> allInterceptors = new ArrayList<Object>();
        if (specificInterceptors != null) {
            if (specificInterceptors.length > 0) {
                allInterceptors.addAll(Arrays.asList(specificInterceptors));
            }
            if (commonInterceptors.length > 0) {
                if (this.applyCommonInterceptorsFirst) {
                    allInterceptors.addAll(0, Arrays.asList(commonInterceptors));
                } else {
                    allInterceptors.addAll(Arrays.asList(commonInterceptors));
                }
            }
        }
        if (this.logger.isTraceEnabled()) {
            int nrOfCommonInterceptors = commonInterceptors.length;
            int nrOfSpecificInterceptors = specificInterceptors != null ? specificInterceptors.length : 0;
            this.logger.trace((Object)("Creating implicit proxy for bean '" + beanName + "' with " + nrOfCommonInterceptors + " common interceptors and " + nrOfSpecificInterceptors + " specific interceptors"));
        }
        Advisor[] advisors = new Advisor[allInterceptors.size()];
        for (int i = 0; i < allInterceptors.size(); ++i) {
            advisors[i] = this.advisorAdapterRegistry.wrap(allInterceptors.get(i));
        }
        return advisors;
    }

    private Advisor[] resolveInterceptorNames() {
        BeanFactory bf = this.beanFactory;
        ConfigurableBeanFactory cbf = bf instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory)bf : null;
        ArrayList<Advisor> advisors = new ArrayList<Advisor>();
        for (String beanName : this.interceptorNames) {
            if (cbf != null && cbf.isCurrentlyInCreation(beanName)) continue;
            Assert.state((bf != null ? 1 : 0) != 0, (String)"BeanFactory required for resolving interceptor names");
            Object next = bf.getBean(beanName);
            advisors.add(this.advisorAdapterRegistry.wrap(next));
        }
        return advisors.toArray(new Advisor[0]);
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }

    @Nullable
    protected abstract Object[] getAdvicesAndAdvisorsForBean(Class<?> var1, String var2, @Nullable TargetSource var3) throws BeansException;
}

