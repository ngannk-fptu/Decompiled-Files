/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.springframework.core.DefaultParameterNameDiscoverer
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.core.NativeDetector
 *  org.springframework.core.ParameterNameDiscoverer
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$MethodCallback
 *  org.springframework.util.ReflectionUtils$MethodFilter
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.AutowiredPropertyMarker;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.AutowireUtils;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.BeanDefinitionValueResolver;
import org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy;
import org.springframework.beans.factory.support.ConstructorResolver;
import org.springframework.beans.factory.support.DisposableBeanAdapter;
import org.springframework.beans.factory.support.ImplicitlyAppearedSingletonException;
import org.springframework.beans.factory.support.InstantiationStrategy;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.NativeDetector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractAutowireCapableBeanFactory
extends AbstractBeanFactory
implements AutowireCapableBeanFactory {
    private InstantiationStrategy instantiationStrategy;
    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private boolean allowCircularReferences = true;
    private boolean allowRawInjectionDespiteWrapping = false;
    private final Set<Class<?>> ignoredDependencyTypes = new HashSet();
    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet();
    private final NamedThreadLocal<String> currentlyCreatedBean = new NamedThreadLocal("Currently created bean");
    private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();
    private final ConcurrentMap<Class<?>, Method[]> factoryMethodCandidateCache = new ConcurrentHashMap();
    private final ConcurrentMap<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache = new ConcurrentHashMap();

    public AbstractAutowireCapableBeanFactory() {
        this.ignoreDependencyInterface(BeanNameAware.class);
        this.ignoreDependencyInterface(BeanFactoryAware.class);
        this.ignoreDependencyInterface(BeanClassLoaderAware.class);
        this.instantiationStrategy = NativeDetector.inNativeImage() ? new SimpleInstantiationStrategy() : new CglibSubclassingInstantiationStrategy();
    }

    public AbstractAutowireCapableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this();
        this.setParentBeanFactory(parentBeanFactory);
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    protected InstantiationStrategy getInstantiationStrategy() {
        return this.instantiationStrategy;
    }

    public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    public boolean isAllowCircularReferences() {
        return this.allowCircularReferences;
    }

    public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
        this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
    }

    public boolean isAllowRawInjectionDespiteWrapping() {
        return this.allowRawInjectionDespiteWrapping;
    }

    public void ignoreDependencyType(Class<?> type) {
        this.ignoredDependencyTypes.add(type);
    }

    public void ignoreDependencyInterface(Class<?> ifc) {
        this.ignoredDependencyInterfaces.add(ifc);
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
            AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory)otherFactory;
            this.instantiationStrategy = otherAutowireFactory.instantiationStrategy;
            this.allowCircularReferences = otherAutowireFactory.allowCircularReferences;
            this.ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
            this.ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
        }
    }

    @Override
    public <T> T createBean(Class<T> beanClass) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass);
        bd.setScope("prototype");
        bd.allowCaching = ClassUtils.isCacheSafe(beanClass, (ClassLoader)this.getBeanClassLoader());
        return (T)this.createBean(beanClass.getName(), bd, null);
    }

    @Override
    public void autowireBean(Object existingBean) {
        RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass((Object)existingBean));
        bd.setScope("prototype");
        bd.allowCaching = ClassUtils.isCacheSafe(bd.getBeanClass(), (ClassLoader)this.getBeanClassLoader());
        BeanWrapperImpl bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(bd.getBeanClass().getName(), bd, bw);
    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        BeanDefinition mbd = this.getMergedBeanDefinition(beanName);
        AbstractBeanDefinition bd = null;
        if (mbd instanceof RootBeanDefinition) {
            RootBeanDefinition rbd = (RootBeanDefinition)mbd;
            AbstractBeanDefinition abstractBeanDefinition = bd = rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition();
        }
        if (bd == null) {
            bd = new RootBeanDefinition(mbd);
        }
        if (!bd.isPrototype()) {
            bd.setScope("prototype");
            ((RootBeanDefinition)bd).allowCaching = ClassUtils.isCacheSafe((Class)ClassUtils.getUserClass((Object)existingBean), (ClassLoader)this.getBeanClassLoader());
        }
        BeanWrapperImpl bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(beanName, (RootBeanDefinition)bd, bw);
        return this.initializeBean(beanName, existingBean, (RootBeanDefinition)bd);
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        return this.createBean(beanClass.getName(), bd, null);
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
        bd.setScope("prototype");
        if (bd.getResolvedAutowireMode() == 3) {
            return this.autowireConstructor(beanClass.getName(), bd, null, null).getWrappedInstance();
        }
        Object bean = System.getSecurityManager() != null ? AccessController.doPrivileged(() -> this.getInstantiationStrategy().instantiate(bd, null, this), this.getAccessControlContext()) : this.getInstantiationStrategy().instantiate(bd, null, this);
        this.populateBean(beanClass.getName(), bd, new BeanWrapperImpl(bean));
        return bean;
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        if (autowireMode == 3) {
            throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
        }
        RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass((Object)existingBean), autowireMode, dependencyCheck);
        bd.setScope("prototype");
        BeanWrapperImpl bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.populateBean(bd.getBeanClass().getName(), bd, bw);
    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        this.markBeanAsCreated(beanName);
        BeanDefinition bd = this.getMergedBeanDefinition(beanName);
        BeanWrapperImpl bw = new BeanWrapperImpl(existingBean);
        this.initBeanWrapper(bw);
        this.applyPropertyValues(beanName, bd, bw, bd.getPropertyValues());
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) {
        return this.initializeBean(beanName, existingBean, null);
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public void destroyBean(Object existingBean) {
        new DisposableBeanAdapter(existingBean, this.getBeanPostProcessorCache().destructionAware, this.getAccessControlContext()).destroy();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object resolveBeanByName(String name, DependencyDescriptor descriptor) {
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object obj = this.getBean(name, descriptor.getDependencyType());
            return obj;
        }
        finally {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
        }
    }

    @Override
    @Nullable
    public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException {
        return this.resolveDependency(descriptor, requestingBeanName, null, null);
    }

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Creating instance of bean '" + beanName + "'"));
        }
        RootBeanDefinition mbdToUse = mbd;
        Class<?> resolvedClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
        }
        try {
            mbdToUse.prepareMethodOverrides();
        }
        catch (BeanDefinitionValidationException ex) {
            throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", (Throwable)((Object)ex));
        }
        try {
            Object bean = this.resolveBeforeInstantiation(beanName, mbdToUse);
            if (bean != null) {
                return bean;
            }
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", ex);
        }
        try {
            Object beanInstance = this.doCreateBean(beanName, mbdToUse, args);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Finished creating instance of bean '" + beanName + "'"));
            }
            return beanInstance;
        }
        catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        Object earlySingletonReference;
        boolean earlySingletonExposure;
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = (BeanWrapper)this.factoryBeanInstanceCache.remove(beanName);
        }
        if (instanceWrapper == null) {
            instanceWrapper = this.createBeanInstance(beanName, mbd, args);
        }
        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }
        Object object = mbd.postProcessingLock;
        synchronized (object) {
            if (!mbd.postProcessed) {
                try {
                    this.applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                }
                catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", ex);
                }
                mbd.postProcessed = true;
            }
        }
        boolean bl = earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references"));
            }
            this.addSingletonFactory(beanName, () -> this.getEarlyBeanReference(beanName, mbd, bean));
        }
        Object exposedObject = bean;
        try {
            this.populateBean(beanName, mbd, instanceWrapper);
            exposedObject = this.initializeBean(beanName, exposedObject, mbd);
        }
        catch (Throwable ex) {
            if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException)((Object)ex)).getBeanName())) {
                throw (BeanCreationException)((Object)ex);
            }
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
        }
        if (earlySingletonExposure && (earlySingletonReference = this.getSingleton(beanName, false)) != null) {
            if (exposedObject == bean) {
                exposedObject = earlySingletonReference;
            } else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                String[] dependentBeans = this.getDependentBeans(beanName);
                LinkedHashSet<String> actualDependentBeans = new LinkedHashSet<String>(dependentBeans.length);
                for (String dependentBean : dependentBeans) {
                    if (this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) continue;
                    actualDependentBeans.add(dependentBean);
                }
                if (!actualDependentBeans.isEmpty()) {
                    throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                }
            }
        }
        try {
            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
        }
        catch (BeanDefinitionValidationException ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", (Throwable)((Object)ex));
        }
        return exposedObject;
    }

    @Override
    @Nullable
    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?> ... typesToMatch) {
        Class<?> targetType = this.determineTargetType(beanName, mbd, typesToMatch);
        if (targetType != null && !mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            boolean matchingOnlyFactoryBean = typesToMatch.length == 1 && typesToMatch[0] == FactoryBean.class;
            for (SmartInstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().smartInstantiationAware) {
                Class<?> predicted = bp.predictBeanType(targetType, beanName);
                if (predicted == null || matchingOnlyFactoryBean && !FactoryBean.class.isAssignableFrom(predicted)) continue;
                return predicted;
            }
        }
        return targetType;
    }

    @Nullable
    protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?> ... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType == null) {
            Class<?> clazz = targetType = mbd.getFactoryMethodName() != null ? this.getTypeForFactoryMethod(beanName, mbd, typesToMatch) : this.resolveBeanClass(mbd, beanName, typesToMatch);
            if (ObjectUtils.isEmpty((Object[])typesToMatch) || this.getTempClassLoader() == null) {
                mbd.resolvedTargetType = targetType;
            }
        }
        return targetType;
    }

    @Nullable
    protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?> ... typesToMatch) {
        ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
        if (cachedReturnType != null) {
            return cachedReturnType.resolve();
        }
        Class commonType = null;
        Method uniqueCandidate = mbd.factoryMethodToIntrospect;
        if (uniqueCandidate == null) {
            Method[] candidates;
            Class factoryClass;
            boolean isStatic = true;
            String factoryBeanName = mbd.getFactoryBeanName();
            if (factoryBeanName != null) {
                if (factoryBeanName.equals(beanName)) {
                    throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
                }
                factoryClass = this.getType(factoryBeanName);
                isStatic = false;
            } else {
                factoryClass = this.resolveBeanClass(mbd, beanName, typesToMatch);
            }
            if (factoryClass == null) {
                return null;
            }
            factoryClass = ClassUtils.getUserClass(factoryClass);
            int minNrOfArgs = mbd.hasConstructorArgumentValues() ? mbd.getConstructorArgumentValues().getArgumentCount() : 0;
            for (Method candidate : candidates = this.factoryMethodCandidateCache.computeIfAbsent(factoryClass, clazz -> ReflectionUtils.getUniqueDeclaredMethods((Class)clazz, (ReflectionUtils.MethodFilter)ReflectionUtils.USER_DECLARED_METHODS))) {
                if (Modifier.isStatic(candidate.getModifiers()) != isStatic || !mbd.isFactoryMethod(candidate) || candidate.getParameterCount() < minNrOfArgs) continue;
                if (candidate.getTypeParameters().length > 0) {
                    try {
                        Class<?>[] paramTypes = candidate.getParameterTypes();
                        String[] paramNames = null;
                        ParameterNameDiscoverer pnd = this.getParameterNameDiscoverer();
                        if (pnd != null) {
                            paramNames = pnd.getParameterNames(candidate);
                        }
                        ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
                        HashSet<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<ConstructorArgumentValues.ValueHolder>(paramTypes.length);
                        Object[] args = new Object[paramTypes.length];
                        for (int i = 0; i < args.length; ++i) {
                            ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], paramNames != null ? paramNames[i] : null, usedValueHolders);
                            if (valueHolder == null) {
                                valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
                            }
                            if (valueHolder == null) continue;
                            args[i] = valueHolder.getValue();
                            usedValueHolders.add(valueHolder);
                        }
                        Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(candidate, args, this.getBeanClassLoader());
                        uniqueCandidate = commonType == null && returnType == candidate.getReturnType() ? candidate : null;
                        if ((commonType = ClassUtils.determineCommonAncestor(returnType, (Class)commonType)) == null) {
                            return null;
                        }
                    }
                    catch (Throwable ex) {
                        if (!this.logger.isDebugEnabled()) continue;
                        this.logger.debug((Object)("Failed to resolve generic return type for factory method: " + ex));
                    }
                    continue;
                }
                uniqueCandidate = commonType == null ? candidate : null;
                commonType = ClassUtils.determineCommonAncestor(candidate.getReturnType(), commonType);
                if (commonType != null) continue;
                return null;
            }
            mbd.factoryMethodToIntrospect = uniqueCandidate;
            if (commonType == null) {
                return null;
            }
        }
        mbd.factoryMethodReturnType = cachedReturnType = uniqueCandidate != null ? ResolvableType.forMethodReturnType((Method)uniqueCandidate) : ResolvableType.forClass(commonType);
        return cachedReturnType.resolve();
    }

    @Override
    protected ResolvableType getTypeForFactoryBean(String beanName, RootBeanDefinition mbd, boolean allowInit) {
        ResolvableType beanType;
        ResolvableType result = this.getTypeForFactoryBeanFromAttributes(mbd);
        if (result != ResolvableType.NONE) {
            return result;
        }
        ResolvableType resolvableType = beanType = mbd.hasBeanClass() ? ResolvableType.forClass(mbd.getBeanClass()) : ResolvableType.NONE;
        if (mbd.getInstanceSupplier() != null) {
            result = this.getFactoryBeanGeneric(mbd.targetType);
            if (result.resolve() != null) {
                return result;
            }
            result = this.getFactoryBeanGeneric(beanType);
            if (result.resolve() != null) {
                return result;
            }
        }
        String factoryBeanName = mbd.getFactoryBeanName();
        String factoryMethodName = mbd.getFactoryMethodName();
        if (factoryBeanName != null) {
            if (factoryMethodName != null) {
                Class<?> factoryBeanClass;
                BeanDefinition factoryBeanDefinition = this.getBeanDefinition(factoryBeanName);
                if (factoryBeanDefinition instanceof AbstractBeanDefinition && ((AbstractBeanDefinition)factoryBeanDefinition).hasBeanClass()) {
                    factoryBeanClass = ((AbstractBeanDefinition)factoryBeanDefinition).getBeanClass();
                } else {
                    RootBeanDefinition fbmbd = this.getMergedBeanDefinition(factoryBeanName, factoryBeanDefinition);
                    factoryBeanClass = this.determineTargetType(factoryBeanName, fbmbd, new Class[0]);
                }
                if (factoryBeanClass != null && (result = this.getTypeForFactoryBeanFromMethod(factoryBeanClass, factoryMethodName)).resolve() != null) {
                    return result;
                }
            }
            if (!this.isBeanEligibleForMetadataCaching(factoryBeanName)) {
                return ResolvableType.NONE;
            }
        }
        if (allowInit) {
            FactoryBean<?> factoryBean;
            FactoryBean<?> factoryBean2 = factoryBean = mbd.isSingleton() ? this.getSingletonFactoryBeanForTypeCheck(beanName, mbd) : this.getNonSingletonFactoryBeanForTypeCheck(beanName, mbd);
            if (factoryBean != null) {
                Class<?> type = this.getTypeForFactoryBean(factoryBean);
                if (type != null) {
                    return ResolvableType.forClass(type);
                }
                return super.getTypeForFactoryBean(beanName, mbd, true);
            }
        }
        if (factoryBeanName == null && mbd.hasBeanClass() && factoryMethodName != null) {
            return this.getTypeForFactoryBeanFromMethod(mbd.getBeanClass(), factoryMethodName);
        }
        result = this.getFactoryBeanGeneric(beanType);
        if (result.resolve() != null) {
            return result;
        }
        return ResolvableType.NONE;
    }

    private ResolvableType getFactoryBeanGeneric(@Nullable ResolvableType type) {
        if (type == null) {
            return ResolvableType.NONE;
        }
        return type.as(FactoryBean.class).getGeneric(new int[0]);
    }

    private ResolvableType getTypeForFactoryBeanFromMethod(Class<?> beanClass, String factoryMethodName) {
        Class factoryBeanClass = ClassUtils.getUserClass(beanClass);
        FactoryBeanMethodTypeFinder finder = new FactoryBeanMethodTypeFinder(factoryMethodName);
        ReflectionUtils.doWithMethods((Class)factoryBeanClass, (ReflectionUtils.MethodCallback)finder, (ReflectionUtils.MethodFilter)ReflectionUtils.USER_DECLARED_METHODS);
        return finder.getResult();
    }

    @Override
    @Deprecated
    @Nullable
    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        return this.getTypeForFactoryBean(beanName, mbd, true).resolve();
    }

    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            for (SmartInstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().smartInstantiationAware) {
                exposedObject = bp.getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        Object object = this.getSingletonMutex();
        synchronized (object) {
            Object instance;
            BeanWrapper bw = (BeanWrapper)this.factoryBeanInstanceCache.get(beanName);
            if (bw != null) {
                return (FactoryBean)bw.getWrappedInstance();
            }
            Object beanInstance = this.getSingleton(beanName, false);
            if (beanInstance instanceof FactoryBean) {
                return (FactoryBean)beanInstance;
            }
            if (this.isSingletonCurrentlyInCreation(beanName) || mbd.getFactoryBeanName() != null && this.isSingletonCurrentlyInCreation(mbd.getFactoryBeanName())) {
                return null;
            }
            try {
                this.beforeSingletonCreation(beanName);
                instance = this.resolveBeforeInstantiation(beanName, mbd);
                if (instance == null) {
                    bw = this.createBeanInstance(beanName, mbd, null);
                    instance = bw.getWrappedInstance();
                }
            }
            catch (UnsatisfiedDependencyException ex) {
                throw ex;
            }
            catch (BeanCreationException ex) {
                if (ex.contains(LinkageError.class)) {
                    throw ex;
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Bean creation exception on singleton FactoryBean type check: " + (Object)((Object)ex)));
                }
                this.onSuppressedException((Exception)((Object)ex));
                FactoryBean<?> factoryBean = null;
                return factoryBean;
            }
            finally {
                this.afterSingletonCreation(beanName);
            }
            FactoryBean<?> fb = this.getFactoryBean(beanName, instance);
            if (bw != null) {
                this.factoryBeanInstanceCache.put(beanName, bw);
            }
            return fb;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
        Object instance;
        if (this.isPrototypeCurrentlyInCreation(beanName)) {
            return null;
        }
        try {
            this.beforePrototypeCreation(beanName);
            instance = this.resolveBeforeInstantiation(beanName, mbd);
            if (instance == null) {
                BeanWrapper bw = this.createBeanInstance(beanName, mbd, null);
                instance = bw.getWrappedInstance();
            }
        }
        catch (UnsatisfiedDependencyException ex) {
            throw ex;
        }
        catch (BeanCreationException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Bean creation exception on non-singleton FactoryBean type check: " + (Object)((Object)ex)));
            }
            this.onSuppressedException((Exception)((Object)ex));
            FactoryBean<?> factoryBean = null;
            return factoryBean;
        }
        finally {
            this.afterPrototypeCreation(beanName);
        }
        return this.getFactoryBean(beanName, instance);
    }

    protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
        for (MergedBeanDefinitionPostProcessor processor : this.getBeanPostProcessorCache().mergedDefinition) {
            processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
        }
    }

    @Nullable
    protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            Class<?> targetType;
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors() && (targetType = this.determineTargetType(beanName, mbd, new Class[0])) != null && (bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, beanName)) != null) {
                bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
            }
            mbd.beforeInstantiationResolved = bean != null;
        }
        return bean;
    }

    @Nullable
    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (InstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().instantiationAware) {
            Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
        Class<?> beanClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        }
        Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
        if (instanceSupplier != null) {
            return this.obtainFromSupplier(instanceSupplier, beanName);
        }
        if (mbd.getFactoryMethodName() != null) {
            return this.instantiateUsingFactoryMethod(beanName, mbd, args);
        }
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            Object object = mbd.constructorArgumentLock;
            synchronized (object) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        if (resolved) {
            if (autowireNecessary) {
                return this.autowireConstructor(beanName, mbd, null, null);
            }
            return this.instantiateBean(beanName, mbd);
        }
        Constructor<?>[] ctors = this.determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        if (ctors != null || mbd.getResolvedAutowireMode() == 3 || mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty((Object[])args)) {
            return this.autowireConstructor(beanName, mbd, ctors, args);
        }
        ctors = mbd.getPreferredConstructors();
        if (ctors != null) {
            return this.autowireConstructor(beanName, mbd, ctors, null);
        }
        return this.instantiateBean(beanName, mbd);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
        Object instance;
        String outerBean = (String)this.currentlyCreatedBean.get();
        this.currentlyCreatedBean.set((Object)beanName);
        try {
            instance = instanceSupplier.get();
        }
        finally {
            if (outerBean != null) {
                this.currentlyCreatedBean.set((Object)outerBean);
            } else {
                this.currentlyCreatedBean.remove();
            }
        }
        if (instance == null) {
            instance = new NullBean();
        }
        BeanWrapperImpl bw = new BeanWrapperImpl(instance);
        this.initBeanWrapper(bw);
        return bw;
    }

    @Override
    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
        String currentlyCreatedBean = (String)this.currentlyCreatedBean.get();
        if (currentlyCreatedBean != null) {
            this.registerDependentBean(beanName, currentlyCreatedBean);
        }
        return super.getObjectForBeanInstance(beanInstance, name, beanName, mbd);
    }

    @Nullable
    protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass != null && this.hasInstantiationAwareBeanPostProcessors()) {
            for (SmartInstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().smartInstantiationAware) {
                Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
                if (ctors == null) continue;
                return ctors;
            }
        }
        return null;
    }

    protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
        try {
            Object beanInstance = System.getSecurityManager() != null ? AccessController.doPrivileged(() -> this.getInstantiationStrategy().instantiate(mbd, beanName, this), this.getAccessControlContext()) : this.getInstantiationStrategy().instantiate(mbd, beanName, this);
            BeanWrapperImpl bw = new BeanWrapperImpl(beanInstance);
            this.initBeanWrapper(bw);
            return bw;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
        }
    }

    protected BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
        return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
    }

    protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
        return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
    }

    protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
        if (bw == null) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
            return;
        }
        if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
            for (InstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().instantiationAware) {
                if (bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) continue;
                return;
            }
        }
        PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
        int resolvedAutowireMode = mbd.getResolvedAutowireMode();
        if (resolvedAutowireMode == 1 || resolvedAutowireMode == 2) {
            MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
            if (resolvedAutowireMode == 1) {
                this.autowireByName(beanName, mbd, bw, newPvs);
            }
            if (resolvedAutowireMode == 2) {
                this.autowireByType(beanName, mbd, bw, newPvs);
            }
            pvs = newPvs;
        }
        boolean hasInstAwareBpps = this.hasInstantiationAwareBeanPostProcessors();
        boolean needsDepCheck = mbd.getDependencyCheck() != 0;
        PropertyDescriptor[] filteredPds = null;
        if (hasInstAwareBpps) {
            if (pvs == null) {
                pvs = mbd.getPropertyValues();
            }
            for (InstantiationAwareBeanPostProcessor bp : this.getBeanPostProcessorCache().instantiationAware) {
                PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
                if (pvsToUse == null) {
                    if (filteredPds == null) {
                        filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                    }
                    if ((pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName)) == null) {
                        return;
                    }
                }
                pvs = pvsToUse;
            }
        }
        if (needsDepCheck) {
            if (filteredPds == null) {
                filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
            }
            this.checkDependencies(beanName, mbd, filteredPds, pvs);
        }
        if (pvs != null) {
            this.applyPropertyValues(beanName, mbd, bw, pvs);
        }
    }

    protected void autowireByName(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        String[] propertyNames;
        for (String propertyName : propertyNames = this.unsatisfiedNonSimpleProperties(mbd, bw)) {
            if (this.containsBean(propertyName)) {
                Object bean = this.getBean(propertyName);
                pvs.add(propertyName, bean);
                this.registerDependentBean(propertyName, beanName);
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace((Object)("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'"));
                continue;
            }
            if (!this.logger.isTraceEnabled()) continue;
            this.logger.trace((Object)("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found"));
        }
    }

    protected void autowireByType(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
        TypeConverter converter = this.getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        String[] propertyNames = this.unsatisfiedNonSimpleProperties(mbd, bw);
        LinkedHashSet autowiredBeanNames = new LinkedHashSet(propertyNames.length * 2);
        for (String propertyName : propertyNames) {
            try {
                boolean eager;
                PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                if (Object.class == pd.getPropertyType()) continue;
                MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                AutowireByTypeDependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager = !(bw.getWrappedInstance() instanceof PriorityOrdered));
                Object autowiredArgument = this.resolveDependency(desc, beanName, autowiredBeanNames, converter);
                if (autowiredArgument != null) {
                    pvs.add(propertyName, autowiredArgument);
                }
                for (String autowiredBeanName : autowiredBeanNames) {
                    this.registerDependentBean(autowiredBeanName, beanName);
                    if (!this.logger.isTraceEnabled()) continue;
                    this.logger.trace((Object)("Autowiring by type from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + autowiredBeanName + "'"));
                }
                autowiredBeanNames.clear();
            }
            catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
            }
        }
    }

    protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
        PropertyDescriptor[] pds;
        TreeSet<String> result = new TreeSet<String>();
        MutablePropertyValues pvs = mbd.getPropertyValues();
        for (PropertyDescriptor pd : pds = bw.getPropertyDescriptors()) {
            if (pd.getWriteMethod() == null || this.isExcludedFromDependencyCheck(pd) || pvs.contains(pd.getName()) || BeanUtils.isSimpleProperty(pd.getPropertyType())) continue;
            result.add(pd.getName());
        }
        return StringUtils.toStringArray(result);
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw, boolean cache) {
        PropertyDescriptor[] filtered = (PropertyDescriptor[])this.filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
        if (filtered == null) {
            PropertyDescriptor[] existing;
            filtered = this.filterPropertyDescriptorsForDependencyCheck(bw);
            if (cache && (existing = this.filteredPropertyDescriptorsCache.putIfAbsent(bw.getWrappedClass(), filtered)) != null) {
                filtered = existing;
            }
        }
        return filtered;
    }

    protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
        ArrayList<PropertyDescriptor> pds = new ArrayList<PropertyDescriptor>(Arrays.asList(bw.getPropertyDescriptors()));
        pds.removeIf(this::isExcludedFromDependencyCheck);
        return pds.toArray(new PropertyDescriptor[0]);
    }

    protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
        return AutowireUtils.isExcludedFromDependencyCheck(pd) || this.ignoredDependencyTypes.contains(pd.getPropertyType()) || AutowireUtils.isSetterDefinedInInterface(pd, this.ignoredDependencyInterfaces);
    }

    protected void checkDependencies(String beanName, AbstractBeanDefinition mbd, PropertyDescriptor[] pds, @Nullable PropertyValues pvs) throws UnsatisfiedDependencyException {
        int dependencyCheck = mbd.getDependencyCheck();
        for (PropertyDescriptor pd : pds) {
            boolean unsatisfied;
            if (pd.getWriteMethod() == null || pvs != null && pvs.contains(pd.getName())) continue;
            boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
            boolean bl = unsatisfied = dependencyCheck == 3 || isSimple && dependencyCheck == 2 || !isSimple && dependencyCheck == 1;
            if (!unsatisfied) continue;
            throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(), "Set this property value or disable dependency checking for this bean.");
        }
    }

    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        List<PropertyValue> original;
        if (pvs.isEmpty()) {
            return;
        }
        if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
            ((BeanWrapperImpl)bw).setSecurityContext(this.getAccessControlContext());
        }
        MutablePropertyValues mpvs = null;
        if (pvs instanceof MutablePropertyValues) {
            mpvs = (MutablePropertyValues)pvs;
            if (mpvs.isConverted()) {
                try {
                    bw.setPropertyValues(mpvs);
                    return;
                }
                catch (BeansException ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", (Throwable)((Object)ex));
                }
            }
            original = mpvs.getPropertyValueList();
        } else {
            original = Arrays.asList(pvs.getPropertyValues());
        }
        TypeConverter converter = this.getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
        ArrayList<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
        boolean resolveNecessary = false;
        for (PropertyValue pv : original) {
            boolean convertible;
            Object resolvedValue;
            if (pv.isConverted()) {
                deepCopy.add(pv);
                continue;
            }
            String propertyName = pv.getName();
            Object originalValue = pv.getValue();
            if (originalValue == AutowiredPropertyMarker.INSTANCE) {
                Method writeMethod = bw.getPropertyDescriptor(propertyName).getWriteMethod();
                if (writeMethod == null) {
                    throw new IllegalArgumentException("Autowire marker for property without write method: " + pv);
                }
                originalValue = new DependencyDescriptor(new MethodParameter(writeMethod, 0), true);
            }
            Object convertedValue = resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
            boolean bl = convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
            if (convertible) {
                convertedValue = this.convertForProperty(resolvedValue, propertyName, bw, converter);
            }
            if (resolvedValue == originalValue) {
                if (convertible) {
                    pv.setConvertedValue(convertedValue);
                }
                deepCopy.add(pv);
                continue;
            }
            if (convertible && originalValue instanceof TypedStringValue && !((TypedStringValue)originalValue).isDynamic() && !(convertedValue instanceof Collection) && !ObjectUtils.isArray((Object)convertedValue)) {
                pv.setConvertedValue(convertedValue);
                deepCopy.add(pv);
                continue;
            }
            resolveNecessary = true;
            deepCopy.add(new PropertyValue(pv, convertedValue));
        }
        if (mpvs != null && !resolveNecessary) {
            mpvs.setConverted();
        }
        try {
            bw.setPropertyValues(new MutablePropertyValues(deepCopy));
        }
        catch (BeansException ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", (Throwable)((Object)ex));
        }
    }

    @Nullable
    private Object convertForProperty(@Nullable Object value, String propertyName, BeanWrapper bw, TypeConverter converter) {
        if (converter instanceof BeanWrapperImpl) {
            return ((BeanWrapperImpl)converter).convertForProperty(value, propertyName);
        }
        PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
        MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
        return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
    }

    protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                this.invokeAwareMethods(beanName, bean);
                return null;
            }, this.getAccessControlContext());
        } else {
            this.invokeAwareMethods(beanName, bean);
        }
        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }
        try {
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", ex);
        }
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }
        return wrappedBean;
    }

    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            ClassLoader bcl;
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware)bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware && (bcl = this.getBeanClassLoader()) != null) {
                ((BeanClassLoaderAware)bean).setBeanClassLoader(bcl);
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware)bean).setBeanFactory(this);
            }
        }
    }

    protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd) throws Throwable {
        String initMethodName;
        boolean isInitializingBean = bean instanceof InitializingBean;
        if (isInitializingBean && (mbd == null || !mbd.hasAnyExternallyManagedInitMethod("afterPropertiesSet"))) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Invoking afterPropertiesSet() on bean with name '" + beanName + "'"));
            }
            if (System.getSecurityManager() != null) {
                try {
                    AccessController.doPrivileged(() -> {
                        ((InitializingBean)bean).afterPropertiesSet();
                        return null;
                    }, this.getAccessControlContext());
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            } else {
                ((InitializingBean)bean).afterPropertiesSet();
            }
        }
        if (!(mbd == null || bean.getClass() == NullBean.class || !StringUtils.hasLength((String)(initMethodName = mbd.getInitMethodName())) || isInitializingBean && "afterPropertiesSet".equals(initMethodName) || mbd.hasAnyExternallyManagedInitMethod(initMethodName))) {
            this.invokeCustomInitMethod(beanName, bean, mbd);
        }
    }

    protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd) throws Throwable {
        Method initMethod;
        String initMethodName = mbd.getInitMethodName();
        Assert.state((initMethodName != null ? 1 : 0) != 0, (String)"No init method set");
        Method method = initMethod = mbd.isNonPublicAccessAllowed() ? BeanUtils.findMethod(bean.getClass(), initMethodName, new Class[0]) : ClassUtils.getMethodIfAvailable(bean.getClass(), (String)initMethodName, (Class[])new Class[0]);
        if (initMethod == null) {
            if (mbd.isEnforceInitMethod()) {
                throw new BeanDefinitionValidationException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("No default init method named '" + initMethodName + "' found on bean with name '" + beanName + "'"));
            }
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'"));
        }
        Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible((Method)initMethod, bean.getClass());
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                ReflectionUtils.makeAccessible((Method)methodToInvoke);
                return null;
            });
            try {
                AccessController.doPrivileged(() -> methodToInvoke.invoke(bean, new Object[0]), this.getAccessControlContext());
            }
            catch (PrivilegedActionException pae) {
                InvocationTargetException ex = (InvocationTargetException)pae.getException();
                throw ex.getTargetException();
            }
        }
        try {
            ReflectionUtils.makeAccessible((Method)methodToInvoke);
            methodToInvoke.invoke(bean, new Object[0]);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    @Override
    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
        return this.applyBeanPostProcessorsAfterInitialization(object, beanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void removeSingleton(String beanName) {
        Object object = this.getSingletonMutex();
        synchronized (object) {
            super.removeSingleton(beanName);
            this.factoryBeanInstanceCache.remove(beanName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void clearSingletonCache() {
        Object object = this.getSingletonMutex();
        synchronized (object) {
            super.clearSingletonCache();
            this.factoryBeanInstanceCache.clear();
        }
    }

    Log getLogger() {
        return this.logger;
    }

    private static class FactoryBeanMethodTypeFinder
    implements ReflectionUtils.MethodCallback {
        private final String factoryMethodName;
        private ResolvableType result = ResolvableType.NONE;

        FactoryBeanMethodTypeFinder(String factoryMethodName) {
            this.factoryMethodName = factoryMethodName;
        }

        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
            if (this.isFactoryBeanMethod(method)) {
                ResolvableType returnType = ResolvableType.forMethodReturnType((Method)method);
                ResolvableType candidate = returnType.as(FactoryBean.class).getGeneric(new int[0]);
                if (this.result == ResolvableType.NONE) {
                    this.result = candidate;
                } else {
                    Class commonAncestor;
                    Class resolvedResult = this.result.resolve();
                    if (!ObjectUtils.nullSafeEquals((Object)resolvedResult, (Object)(commonAncestor = ClassUtils.determineCommonAncestor((Class)candidate.resolve(), (Class)resolvedResult)))) {
                        this.result = ResolvableType.forClass((Class)commonAncestor);
                    }
                }
            }
        }

        private boolean isFactoryBeanMethod(Method method) {
            return method.getName().equals(this.factoryMethodName) && FactoryBean.class.isAssignableFrom(method.getReturnType());
        }

        ResolvableType getResult() {
            Class resolved = this.result.resolve();
            boolean foundResult = resolved != null && resolved != Object.class;
            return foundResult ? this.result : ResolvableType.NONE;
        }
    }

    private static class AutowireByTypeDependencyDescriptor
    extends DependencyDescriptor {
        public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
            super(methodParameter, false, eager);
        }

        @Override
        public String getDependencyName() {
            return null;
        }
    }
}

