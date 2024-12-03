/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotations
 *  org.springframework.core.annotation.MergedAnnotations$SearchStrategy
 *  org.springframework.core.log.LogMessage
 */
package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Provider;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.AutowireUtils;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.ConstructorResolver;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CompositeIterator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DefaultListableBeanFactory
extends AbstractAutowireCapableBeanFactory
implements ConfigurableListableBeanFactory,
BeanDefinitionRegistry,
Serializable {
    @Nullable
    private static Class<?> javaxInjectProviderClass;
    private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories;
    @Nullable
    private String serializationId;
    private boolean allowBeanDefinitionOverriding = true;
    private boolean allowEagerClassLoading = true;
    @Nullable
    private Comparator<Object> dependencyComparator;
    private AutowireCandidateResolver autowireCandidateResolver = SimpleAutowireCandidateResolver.INSTANCE;
    private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap(16);
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>(256);
    private final Map<String, BeanDefinitionHolder> mergedBeanDefinitionHolders = new ConcurrentHashMap<String, BeanDefinitionHolder>(256);
    private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap(64);
    private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap(64);
    private volatile List<String> beanDefinitionNames = new ArrayList<String>(256);
    private volatile Set<String> manualSingletonNames = new LinkedHashSet<String>(16);
    @Nullable
    private volatile String[] frozenBeanDefinitionNames;
    private volatile boolean configurationFrozen;

    public DefaultListableBeanFactory() {
    }

    public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
    }

    public void setSerializationId(@Nullable String serializationId) {
        if (serializationId != null) {
            serializableFactories.put(serializationId, new WeakReference<DefaultListableBeanFactory>(this));
        } else if (this.serializationId != null) {
            serializableFactories.remove(this.serializationId);
        }
        this.serializationId = serializationId;
    }

    @Nullable
    public String getSerializationId() {
        return this.serializationId;
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public boolean isAllowBeanDefinitionOverriding() {
        return this.allowBeanDefinitionOverriding;
    }

    public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
        this.allowEagerClassLoading = allowEagerClassLoading;
    }

    public boolean isAllowEagerClassLoading() {
        return this.allowEagerClassLoading;
    }

    public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
        this.dependencyComparator = dependencyComparator;
    }

    @Nullable
    public Comparator<Object> getDependencyComparator() {
        return this.dependencyComparator;
    }

    public void setAutowireCandidateResolver(AutowireCandidateResolver autowireCandidateResolver) {
        Assert.notNull((Object)autowireCandidateResolver, "AutowireCandidateResolver must not be null");
        if (autowireCandidateResolver instanceof BeanFactoryAware) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ((BeanFactoryAware)((Object)autowireCandidateResolver)).setBeanFactory(this);
                    return null;
                }, this.getAccessControlContext());
            } else {
                ((BeanFactoryAware)((Object)autowireCandidateResolver)).setBeanFactory(this);
            }
        }
        this.autowireCandidateResolver = autowireCandidateResolver;
    }

    public AutowireCandidateResolver getAutowireCandidateResolver() {
        return this.autowireCandidateResolver;
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        super.copyConfigurationFrom(otherFactory);
        if (otherFactory instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory)otherFactory;
            this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
            this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
            this.dependencyComparator = otherListableFactory.dependencyComparator;
            this.setAutowireCandidateResolver(otherListableFactory.getAutowireCandidateResolver().cloneIfNecessary());
            this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
        }
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return this.getBean(requiredType, (Object[])null);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object ... args) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        T resolved = this.resolveBean(ResolvableType.forRawClass(requiredType), args, false);
        if (resolved == null) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        return resolved;
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
        Assert.notNull(requiredType, "Required type must not be null");
        return this.getBeanProvider(ResolvableType.forRawClass(requiredType));
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
        return new BeanObjectProvider<T>(){

            @Override
            public T getObject() throws BeansException {
                Object resolved = DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
                if (resolved == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return resolved;
            }

            @Override
            public T getObject(Object ... args) throws BeansException {
                Object resolved = DefaultListableBeanFactory.this.resolveBean(requiredType, args, false);
                if (resolved == null) {
                    throw new NoSuchBeanDefinitionException(requiredType);
                }
                return resolved;
            }

            @Override
            @Nullable
            public T getIfAvailable() throws BeansException {
                return DefaultListableBeanFactory.this.resolveBean(requiredType, null, false);
            }

            @Override
            @Nullable
            public T getIfUnique() throws BeansException {
                return DefaultListableBeanFactory.this.resolveBean(requiredType, null, true);
            }

            @Override
            public Stream<T> stream() {
                return Arrays.stream(DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType)).map(name -> DefaultListableBeanFactory.this.getBean((String)name)).filter(bean2 -> !(bean2 instanceof NullBean));
            }

            @Override
            public Stream<T> orderedStream() {
                String[] beanNames = DefaultListableBeanFactory.this.getBeanNamesForTypedStream(requiredType);
                if (beanNames.length == 0) {
                    return Stream.empty();
                }
                LinkedHashMap<String, Object> matchingBeans = new LinkedHashMap<String, Object>(beanNames.length);
                for (String beanName : beanNames) {
                    Object beanInstance = DefaultListableBeanFactory.this.getBean(beanName);
                    if (beanInstance instanceof NullBean) continue;
                    matchingBeans.put(beanName, beanInstance);
                }
                Stream stream = matchingBeans.values().stream();
                return stream.sorted(DefaultListableBeanFactory.this.adaptOrderComparator(matchingBeans));
            }
        };
    }

    @Nullable
    private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
        NamedBeanHolder<T> namedBean = this.resolveNamedBean(requiredType, args, nonUniqueAsNull);
        if (namedBean != null) {
            return namedBean.getBeanInstance();
        }
        BeanFactory parent = this.getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            return ((DefaultListableBeanFactory)parent).resolveBean(requiredType, args, nonUniqueAsNull);
        }
        if (parent != null) {
            ObjectProvider parentProvider = parent.getBeanProvider(requiredType);
            if (args != null) {
                return parentProvider.getObject(args);
            }
            return nonUniqueAsNull ? parentProvider.getIfUnique() : parentProvider.getIfAvailable();
        }
        return null;
    }

    private String[] getBeanNamesForTypedStream(ResolvableType requiredType) {
        return BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this, requiredType);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        Assert.notNull((Object)beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        String[] frozenNames = this.frozenBeanDefinitionNames;
        if (frozenNames != null) {
            return (String[])frozenNames.clone();
        }
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type) {
        return this.getBeanNamesForType(type, true, true);
    }

    @Override
    public String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        Class<?> resolved = type.resolve();
        if (resolved != null && !type.hasGenerics()) {
            return this.getBeanNamesForType(resolved, includeNonSingletons, allowEagerInit);
        }
        return this.doGetBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type) {
        return this.getBeanNamesForType(type, true, true);
    }

    @Override
    public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
        if (!this.isConfigurationFrozen() || type == null || !allowEagerInit) {
            return this.doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
        }
        Map<Class<?>, String[]> cache = includeNonSingletons ? this.allBeanNamesByType : this.singletonBeanNamesByType;
        String[] resolvedBeanNames = cache.get(type);
        if (resolvedBeanNames != null) {
            return resolvedBeanNames;
        }
        resolvedBeanNames = this.doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
        if (ClassUtils.isCacheSafe(type, this.getBeanClassLoader())) {
            cache.put(type, resolvedBeanNames);
        }
        return resolvedBeanNames;
    }

    private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
        ArrayList<String> result = new ArrayList<String>();
        for (String beanName : this.beanDefinitionNames) {
            if (this.isAlias(beanName)) continue;
            try {
                boolean isNonLazyDecorated;
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                if (mbd.isAbstract() || !allowEagerInit && (!mbd.hasBeanClass() && mbd.isLazyInit() && !this.isAllowEagerClassLoading() || this.requiresEagerInitForType(mbd.getFactoryBeanName()))) continue;
                boolean isFactoryBean = this.isFactoryBean(beanName, mbd);
                BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
                boolean matchFound = false;
                boolean allowFactoryBeanInit = allowEagerInit || this.containsSingleton(beanName);
                boolean bl = isNonLazyDecorated = dbd != null && !mbd.isLazyInit();
                if (!isFactoryBean) {
                    if (includeNonSingletons || this.isSingleton(beanName, mbd, dbd)) {
                        matchFound = this.isTypeMatch(beanName, type, allowFactoryBeanInit);
                    }
                } else {
                    if (includeNonSingletons || isNonLazyDecorated || allowFactoryBeanInit && this.isSingleton(beanName, mbd, dbd)) {
                        matchFound = this.isTypeMatch(beanName, type, allowFactoryBeanInit);
                    }
                    if (!matchFound) {
                        beanName = "&" + beanName;
                        matchFound = this.isTypeMatch(beanName, type, allowFactoryBeanInit);
                    }
                }
                if (!matchFound) continue;
                result.add(beanName);
            }
            catch (BeanDefinitionStoreException | CannotLoadBeanClassException ex) {
                if (allowEagerInit) {
                    throw ex;
                }
                LogMessage message = ex instanceof CannotLoadBeanClassException ? LogMessage.format((String)"Ignoring bean class loading failure for bean '%s'", (Object)beanName) : LogMessage.format((String)"Ignoring unresolvable metadata in bean definition '%s'", (Object)beanName);
                this.logger.trace(message, ex);
                this.onSuppressedException(ex);
            }
            catch (NoSuchBeanDefinitionException ex) {}
        }
        for (String beanName : this.manualSingletonNames) {
            try {
                if (this.isFactoryBean(beanName)) {
                    if ((includeNonSingletons || this.isSingleton(beanName)) && this.isTypeMatch(beanName, type)) {
                        result.add(beanName);
                        continue;
                    }
                    beanName = "&" + beanName;
                }
                if (!this.isTypeMatch(beanName, type)) continue;
                result.add(beanName);
            }
            catch (NoSuchBeanDefinitionException ex) {
                this.logger.trace(LogMessage.format((String)"Failed to check manually registered singleton with name '%s'", (Object)beanName), ex);
            }
        }
        return StringUtils.toStringArray(result);
    }

    private boolean isSingleton(String beanName, RootBeanDefinition mbd, @Nullable BeanDefinitionHolder dbd) {
        return dbd != null ? mbd.isSingleton() : this.isSingleton(beanName);
    }

    private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
        return factoryBeanName != null && this.isFactoryBean(factoryBeanName) && !this.containsSingleton(factoryBeanName);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return this.getBeansOfType(type, true, true);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
        String[] beanNames = this.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(beanNames.length);
        for (String beanName : beanNames) {
            try {
                Object beanInstance = this.getBean(beanName);
                if (beanInstance instanceof NullBean) continue;
                result.put(beanName, beanInstance);
            }
            catch (BeanCreationException ex) {
                BeanCreationException bce;
                String exBeanName;
                Throwable rootCause = ex.getMostSpecificCause();
                if (rootCause instanceof BeanCurrentlyInCreationException && (exBeanName = (bce = (BeanCreationException)rootCause).getBeanName()) != null && this.isCurrentlyInCreation(exBeanName)) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Ignoring match to currently created bean '" + exBeanName + "': " + ex.getMessage());
                    }
                    this.onSuppressedException(ex);
                    continue;
                }
                throw ex;
            }
        }
        return result;
    }

    @Override
    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        ArrayList<String> result = new ArrayList<String>();
        for (String beanName : this.beanDefinitionNames) {
            BeanDefinition bd = this.beanDefinitionMap.get(beanName);
            if (bd == null || bd.isAbstract() || this.findAnnotationOnBean(beanName, annotationType) == null) continue;
            result.add(beanName);
        }
        for (String beanName : this.manualSingletonNames) {
            if (result.contains(beanName) || this.findAnnotationOnBean(beanName, annotationType) == null) continue;
            result.add(beanName);
        }
        return StringUtils.toStringArray(result);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        String[] beanNames = this.getBeanNamesForAnnotation(annotationType);
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(beanNames.length);
        for (String beanName : beanNames) {
            Object beanInstance = this.getBean(beanName);
            if (beanInstance instanceof NullBean) continue;
            result.put(beanName, beanInstance);
        }
        return result;
    }

    @Override
    @Nullable
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
        return (A)((Annotation)this.findMergedAnnotationOnBean(beanName, annotationType).synthesize(MergedAnnotation::isPresent).orElse(null));
    }

    private <A extends Annotation> MergedAnnotation<A> findMergedAnnotationOnBean(String beanName, Class<A> annotationType) {
        MergedAnnotation annotation;
        Class<?> beanType = this.getType(beanName);
        if (beanType != null && (annotation = MergedAnnotations.from(beanType, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType)).isPresent()) {
            return annotation;
        }
        if (this.containsBeanDefinition(beanName)) {
            MergedAnnotation annotation2;
            Class<?> beanClass;
            RootBeanDefinition bd = this.getMergedLocalBeanDefinition(beanName);
            if (bd.hasBeanClass() && (beanClass = bd.getBeanClass()) != beanType && (annotation2 = MergedAnnotations.from(beanClass, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType)).isPresent()) {
                return annotation2;
            }
            Method factoryMethod = bd.getResolvedFactoryMethod();
            if (factoryMethod != null && (annotation2 = MergedAnnotations.from((AnnotatedElement)factoryMethod, (MergedAnnotations.SearchStrategy)MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(annotationType)).isPresent()) {
                return annotation2;
            }
        }
        return MergedAnnotation.missing();
    }

    @Override
    public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
        Assert.notNull(dependencyType, "Dependency type must not be null");
        if (autowiredValue != null) {
            if (!(autowiredValue instanceof ObjectFactory) && !dependencyType.isInstance(autowiredValue)) {
                throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
            }
            this.resolvableDependencies.put(dependencyType, autowiredValue);
        }
    }

    @Override
    public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
        return this.isAutowireCandidate(beanName, descriptor, this.getAutowireCandidateResolver());
    }

    protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
        String bdName = BeanFactoryUtils.transformedBeanName(beanName);
        if (this.containsBeanDefinition(bdName)) {
            return this.isAutowireCandidate(beanName, this.getMergedLocalBeanDefinition(bdName), descriptor, resolver);
        }
        if (this.containsSingleton(beanName)) {
            return this.isAutowireCandidate(beanName, new RootBeanDefinition(this.getType(beanName)), descriptor, resolver);
        }
        BeanFactory parent = this.getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            return ((DefaultListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor, resolver);
        }
        if (parent instanceof ConfigurableListableBeanFactory) {
            return ((ConfigurableListableBeanFactory)parent).isAutowireCandidate(beanName, descriptor);
        }
        return true;
    }

    protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
        String bdName = BeanFactoryUtils.transformedBeanName(beanName);
        this.resolveBeanClass(mbd, bdName, new Class[0]);
        if (mbd.isFactoryMethodUnique && mbd.factoryMethodToIntrospect == null) {
            new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
        }
        BeanDefinitionHolder holder = beanName.equals(bdName) ? this.mergedBeanDefinitionHolders.computeIfAbsent(beanName, key -> new BeanDefinitionHolder(mbd, beanName, this.getAliases(bdName))) : new BeanDefinitionHolder(mbd, beanName, this.getAliases(bdName));
        return resolver.isAutowireCandidate(holder, descriptor);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bd;
    }

    @Override
    public Iterator<String> getBeanNamesIterator() {
        CompositeIterator<String> iterator = new CompositeIterator<String>();
        iterator.add(this.beanDefinitionNames.iterator());
        iterator.add(this.manualSingletonNames.iterator());
        return iterator;
    }

    @Override
    protected void clearMergedBeanDefinition(String beanName) {
        super.clearMergedBeanDefinition(beanName);
        this.mergedBeanDefinitionHolders.remove(beanName);
    }

    @Override
    public void clearMetadataCache() {
        super.clearMetadataCache();
        this.mergedBeanDefinitionHolders.clear();
        this.clearByTypeCache();
    }

    @Override
    public void freezeConfiguration() {
        this.configurationFrozen = true;
        this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override
    public boolean isConfigurationFrozen() {
        return this.configurationFrozen;
    }

    @Override
    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName);
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Pre-instantiating singletons in " + this);
        }
        ArrayList<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
        for (String beanName : beanNames) {
            RootBeanDefinition bd = this.getMergedLocalBeanDefinition(beanName);
            if (bd.isAbstract() || !bd.isSingleton() || bd.isLazyInit()) continue;
            if (this.isFactoryBean(beanName)) {
                boolean isEagerInit;
                Object bean2 = this.getBean("&" + beanName);
                if (!(bean2 instanceof FactoryBean)) continue;
                FactoryBean factory = (FactoryBean)bean2;
                if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                    isEagerInit = AccessController.doPrivileged(((SmartFactoryBean)factory)::isEagerInit, this.getAccessControlContext());
                } else {
                    boolean bl = isEagerInit = factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit();
                }
                if (!isEagerInit) continue;
                this.getBean(beanName);
                continue;
            }
            this.getBean(beanName);
        }
        for (String beanName : beanNames) {
            Object singletonInstance = this.getSingleton(beanName);
            if (!(singletonInstance instanceof SmartInitializingSingleton)) continue;
            SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton)singletonInstance;
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    smartSingleton.afterSingletonsInstantiated();
                    return null;
                }, this.getAccessControlContext());
                continue;
            }
            smartSingleton.afterSingletonsInstantiated();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        BeanDefinition existingDefinition;
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull((Object)beanDefinition, "BeanDefinition must not be null");
        if (beanDefinition instanceof AbstractBeanDefinition) {
            try {
                ((AbstractBeanDefinition)beanDefinition).validate();
            }
            catch (BeanDefinitionValidationException ex) {
                throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName, "Validation of bean definition failed", ex);
            }
        }
        if ((existingDefinition = this.beanDefinitionMap.get(beanName)) != null) {
            if (!this.isAllowBeanDefinitionOverriding()) {
                throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
            }
            if (existingDefinition.getRole() < beanDefinition.getRole()) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (!beanDefinition.equals(existingDefinition)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (this.logger.isTraceEnabled()) {
                this.logger.trace("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
            }
            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            if (this.hasBeanCreationStarted()) {
                Map<String, BeanDefinition> map = this.beanDefinitionMap;
                synchronized (map) {
                    this.beanDefinitionMap.put(beanName, beanDefinition);
                    ArrayList<String> updatedDefinitions = new ArrayList<String>(this.beanDefinitionNames.size() + 1);
                    updatedDefinitions.addAll(this.beanDefinitionNames);
                    updatedDefinitions.add(beanName);
                    this.beanDefinitionNames = updatedDefinitions;
                    this.removeManualSingletonName(beanName);
                }
            } else {
                this.beanDefinitionMap.put(beanName, beanDefinition);
                this.beanDefinitionNames.add(beanName);
                this.removeManualSingletonName(beanName);
            }
            this.frozenBeanDefinitionNames = null;
        }
        if (existingDefinition != null || this.containsSingleton(beanName)) {
            this.resetBeanDefinition(beanName);
        } else if (this.isConfigurationFrozen()) {
            this.clearByTypeCache();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        Assert.hasText(beanName, "'beanName' must not be empty");
        BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        }
        if (this.hasBeanCreationStarted()) {
            Map<String, BeanDefinition> map = this.beanDefinitionMap;
            synchronized (map) {
                ArrayList<String> updatedDefinitions = new ArrayList<String>(this.beanDefinitionNames);
                updatedDefinitions.remove(beanName);
                this.beanDefinitionNames = updatedDefinitions;
            }
        } else {
            this.beanDefinitionNames.remove(beanName);
        }
        this.frozenBeanDefinitionNames = null;
        this.resetBeanDefinition(beanName);
    }

    protected void resetBeanDefinition(String beanName) {
        this.clearMergedBeanDefinition(beanName);
        this.destroySingleton(beanName);
        for (BeanPostProcessor processor : this.getBeanPostProcessors()) {
            if (!(processor instanceof MergedBeanDefinitionPostProcessor)) continue;
            ((MergedBeanDefinitionPostProcessor)processor).resetBeanDefinition(beanName);
        }
        for (String bdName : this.beanDefinitionNames) {
            BeanDefinition bd;
            if (beanName.equals(bdName) || (bd = this.beanDefinitionMap.get(bdName)) == null || !beanName.equals(bd.getParentName())) continue;
            this.resetBeanDefinition(bdName);
        }
    }

    @Override
    protected boolean allowAliasOverriding() {
        return this.isAllowBeanDefinitionOverriding();
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        this.updateManualSingletonNames(set -> set.add(beanName), set -> !this.beanDefinitionMap.containsKey(beanName));
        this.clearByTypeCache();
    }

    @Override
    public void destroySingletons() {
        super.destroySingletons();
        this.updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
        this.clearByTypeCache();
    }

    @Override
    public void destroySingleton(String beanName) {
        super.destroySingleton(beanName);
        this.removeManualSingletonName(beanName);
        this.clearByTypeCache();
    }

    private void removeManualSingletonName(String beanName) {
        this.updateManualSingletonNames(set -> set.remove(beanName), set -> set.contains(beanName));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
        if (this.hasBeanCreationStarted()) {
            Map<String, BeanDefinition> map = this.beanDefinitionMap;
            synchronized (map) {
                if (condition.test(this.manualSingletonNames)) {
                    LinkedHashSet<String> updatedSingletons = new LinkedHashSet<String>(this.manualSingletonNames);
                    action.accept(updatedSingletons);
                    this.manualSingletonNames = updatedSingletons;
                }
            }
        } else if (condition.test(this.manualSingletonNames)) {
            action.accept(this.manualSingletonNames);
        }
    }

    private void clearByTypeCache() {
        this.allBeanNamesByType.clear();
        this.singletonBeanNamesByType.clear();
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        Assert.notNull(requiredType, "Required type must not be null");
        NamedBeanHolder<T> namedBean = this.resolveNamedBean(ResolvableType.forRawClass(requiredType), null, false);
        if (namedBean != null) {
            return namedBean;
        }
        BeanFactory parent = this.getParentBeanFactory();
        if (parent instanceof AutowireCapableBeanFactory) {
            return ((AutowireCapableBeanFactory)parent).resolveNamedBean(requiredType);
        }
        throw new NoSuchBeanDefinitionException(requiredType);
    }

    @Nullable
    private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {
        Assert.notNull((Object)requiredType, "Required type must not be null");
        String[] candidateNames = this.getBeanNamesForType(requiredType);
        if (candidateNames.length > 1) {
            ArrayList<String> autowireCandidates = new ArrayList<String>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (this.containsBeanDefinition(beanName) && !this.getBeanDefinition(beanName).isAutowireCandidate()) continue;
                autowireCandidates.add(beanName);
            }
            if (!autowireCandidates.isEmpty()) {
                candidateNames = StringUtils.toStringArray(autowireCandidates);
            }
        }
        if (candidateNames.length == 1) {
            String beanName = candidateNames[0];
            return new NamedBeanHolder(beanName, this.getBean(beanName, requiredType.toClass(), args));
        }
        if (candidateNames.length > 1) {
            LinkedHashMap<String, Object> candidates = new LinkedHashMap<String, Object>(candidateNames.length);
            for (String beanName : candidateNames) {
                if (this.containsSingleton(beanName) && args == null) {
                    Object beanInstance = this.getBean(beanName);
                    candidates.put(beanName, beanInstance instanceof NullBean ? null : beanInstance);
                    continue;
                }
                candidates.put(beanName, this.getType(beanName));
            }
            String candidateName = this.determinePrimaryCandidate(candidates, requiredType.toClass());
            if (candidateName == null) {
                candidateName = this.determineHighestPriorityCandidate(candidates, requiredType.toClass());
            }
            if (candidateName != null) {
                Object beanInstance = candidates.get(candidateName);
                if (beanInstance == null || beanInstance instanceof Class) {
                    beanInstance = this.getBean(candidateName, requiredType.toClass(), args);
                }
                return new NamedBeanHolder(candidateName, beanInstance);
            }
            if (!nonUniqueAsNull) {
                throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
            }
        }
        return null;
    }

    @Override
    @Nullable
    public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        descriptor.initParameterNameDiscovery(this.getParameterNameDiscoverer());
        if (Optional.class == descriptor.getDependencyType()) {
            return this.createOptionalDependency(descriptor, requestingBeanName, new Object[0]);
        }
        if (ObjectFactory.class == descriptor.getDependencyType() || ObjectProvider.class == descriptor.getDependencyType()) {
            return new DependencyObjectProvider(descriptor, requestingBeanName);
        }
        if (javaxInjectProviderClass == descriptor.getDependencyType()) {
            return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
        }
        Object result = this.getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName);
        if (result == null) {
            result = this.doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Nullable
    public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object result;
            Object instanceCandidate;
            String autowiredBeanName;
            Object shortcut = descriptor.resolveShortcut(this);
            if (shortcut != null) {
                Object object = shortcut;
                return object;
            }
            Class<?> type = descriptor.getDependencyType();
            Object value = this.getAutowireCandidateResolver().getSuggestedValue(descriptor);
            if (value != null) {
                BeanDefinition bd;
                if (value instanceof String) {
                    String strVal = this.resolveEmbeddedValue((String)value);
                    bd = beanName != null && this.containsBean(beanName) ? this.getMergedBeanDefinition(beanName) : null;
                    value = this.evaluateBeanDefinitionString(strVal, bd);
                }
                TypeConverter converter = typeConverter != null ? typeConverter : this.getTypeConverter();
                try {
                    bd = (BeanDefinition)converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
                    return bd;
                }
                catch (UnsupportedOperationException ex) {
                    Object obj = descriptor.getField() != null ? converter.convertIfNecessary(value, type, descriptor.getField()) : converter.convertIfNecessary(value, type, descriptor.getMethodParameter());
                    ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
                    return obj;
                }
            }
            Object multipleBeans = this.resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
            if (multipleBeans != null) {
                Object ex = multipleBeans;
                return ex;
            }
            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans.isEmpty()) {
                if (this.isRequired(descriptor)) {
                    this.raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                Object var11_17 = null;
                return var11_17;
            }
            if (matchingBeans.size() > 1) {
                autowiredBeanName = this.determineAutowireCandidate(matchingBeans, descriptor);
                if (autowiredBeanName == null) {
                    if (this.isRequired(descriptor) || !this.indicatesMultipleBeans(type)) {
                        Object object = descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                        return object;
                    }
                    Object var13_20 = null;
                    return var13_20;
                }
                instanceCandidate = matchingBeans.get(autowiredBeanName);
            } else {
                Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
                autowiredBeanName = entry.getKey();
                instanceCandidate = entry.getValue();
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(autowiredBeanName);
            }
            if (instanceCandidate instanceof Class) {
                instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
            }
            if ((result = instanceCandidate) instanceof NullBean) {
                if (this.isRequired(descriptor)) {
                    this.raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                result = null;
            }
            if (!ClassUtils.isAssignableValue(type, result)) {
                throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
            }
            Object object = result;
            return object;
            {
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
        }
    }

    @Nullable
    private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
        Class<?> type = descriptor.getDependencyType();
        if (descriptor instanceof StreamDependencyDescriptor) {
            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, type, descriptor);
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            Stream<Object> stream = matchingBeans.keySet().stream().map(name -> descriptor.resolveCandidate((String)name, type, this)).filter(bean2 -> !(bean2 instanceof NullBean));
            if (((StreamDependencyDescriptor)descriptor).isOrdered()) {
                stream = stream.sorted(this.adaptOrderComparator(matchingBeans));
            }
            return stream;
        }
        if (type.isArray()) {
            Comparator<Object> comparator;
            TypeConverter converter;
            Object result;
            Class<?> componentType = type.getComponentType();
            ResolvableType resolvableType = descriptor.getResolvableType();
            Class<?> resolvedArrayType = resolvableType.resolve(type);
            if (resolvedArrayType != type) {
                componentType = resolvableType.getComponentType().resolve();
            }
            if (componentType == null) {
                return null;
            }
            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, componentType, new MultiElementDescriptor(descriptor));
            if (matchingBeans.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            if ((result = (converter = typeConverter != null ? typeConverter : this.getTypeConverter()).convertIfNecessary(matchingBeans.values(), resolvedArrayType)) instanceof Object[] && (comparator = this.adaptDependencyComparator(matchingBeans)) != null) {
                Arrays.sort((Object[])result, comparator);
            }
            return result;
        }
        if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
            Comparator<Object> comparator;
            TypeConverter converter;
            Object result;
            Class<?> elementType = descriptor.getResolvableType().asCollection().resolveGeneric(new int[0]);
            if (elementType == null) {
                return null;
            }
            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, elementType, new MultiElementDescriptor(descriptor));
            if (matchingBeans.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            if ((result = (converter = typeConverter != null ? typeConverter : this.getTypeConverter()).convertIfNecessary(matchingBeans.values(), type)) instanceof List && ((List)result).size() > 1 && (comparator = this.adaptDependencyComparator(matchingBeans)) != null) {
                ((List)result).sort(comparator);
            }
            return result;
        }
        if (Map.class == type) {
            ResolvableType mapType = descriptor.getResolvableType().asMap();
            Class<?> keyType = mapType.resolveGeneric(0);
            if (String.class != keyType) {
                return null;
            }
            Class<?> valueType = mapType.resolveGeneric(1);
            if (valueType == null) {
                return null;
            }
            Map<String, Object> matchingBeans = this.findAutowireCandidates(beanName, valueType, new MultiElementDescriptor(descriptor));
            if (matchingBeans.isEmpty()) {
                return null;
            }
            if (autowiredBeanNames != null) {
                autowiredBeanNames.addAll(matchingBeans.keySet());
            }
            return matchingBeans;
        }
        return null;
    }

    private boolean isRequired(DependencyDescriptor descriptor) {
        return this.getAutowireCandidateResolver().isRequired(descriptor);
    }

    private boolean indicatesMultipleBeans(Class<?> type) {
        return type.isArray() || type.isInterface() && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }

    @Nullable
    private Comparator<Object> adaptDependencyComparator(Map<String, ?> matchingBeans) {
        Comparator<Object> comparator = this.getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            return ((OrderComparator)comparator).withSourceProvider(this.createFactoryAwareOrderSourceProvider(matchingBeans));
        }
        return comparator;
    }

    private Comparator<Object> adaptOrderComparator(Map<String, ?> matchingBeans) {
        Comparator<Object> dependencyComparator = this.getDependencyComparator();
        OrderComparator comparator = dependencyComparator instanceof OrderComparator ? (OrderComparator)dependencyComparator : OrderComparator.INSTANCE;
        return comparator.withSourceProvider(this.createFactoryAwareOrderSourceProvider(matchingBeans));
    }

    private OrderComparator.OrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, ?> beans2) {
        IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap<Object, String>();
        beans2.forEach((beanName, instance) -> instancesToBeanNames.put(instance, (String)beanName));
        return new FactoryAwareOrderSourceProvider(instancesToBeanNames);
    }

    protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
        String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)this, requiredType, true, descriptor.isEager());
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>(candidateNames.length);
        for (Map.Entry<Class<?>, Object> classObjectEntry : this.resolvableDependencies.entrySet()) {
            Class<?> autowiringType = classObjectEntry.getKey();
            if (!autowiringType.isAssignableFrom(requiredType)) continue;
            Object autowiringValue = classObjectEntry.getValue();
            if (!requiredType.isInstance(autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType))) continue;
            result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
            break;
        }
        for (String candidate : candidateNames) {
            if (this.isSelfReference(beanName, candidate) || !this.isAutowireCandidate(candidate, descriptor)) continue;
            this.addCandidateEntry(result, candidate, descriptor, requiredType);
        }
        if (result.isEmpty()) {
            boolean multiple = this.indicatesMultipleBeans(requiredType);
            DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
            for (String candidate : candidateNames) {
                if (this.isSelfReference(beanName, candidate) || !this.isAutowireCandidate(candidate, fallbackDescriptor) || multiple && !this.getAutowireCandidateResolver().hasQualifier(descriptor)) continue;
                this.addCandidateEntry(result, candidate, descriptor, requiredType);
            }
            if (result.isEmpty() && !multiple) {
                for (String candidate : candidateNames) {
                    if (!this.isSelfReference(beanName, candidate) || descriptor instanceof MultiElementDescriptor && beanName.equals(candidate) || !this.isAutowireCandidate(candidate, fallbackDescriptor)) continue;
                    this.addCandidateEntry(result, candidate, descriptor, requiredType);
                }
            }
        }
        return result;
    }

    private void addCandidateEntry(Map<String, Object> candidates, String candidateName, DependencyDescriptor descriptor, Class<?> requiredType) {
        if (descriptor instanceof MultiElementDescriptor) {
            Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
            if (!(beanInstance instanceof NullBean)) {
                candidates.put(candidateName, beanInstance);
            }
        } else if (this.containsSingleton(candidateName) || descriptor instanceof StreamDependencyDescriptor && ((StreamDependencyDescriptor)descriptor).isOrdered()) {
            Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
            candidates.put(candidateName, beanInstance instanceof NullBean ? null : beanInstance);
        } else {
            candidates.put(candidateName, this.getType(candidateName));
        }
    }

    @Nullable
    protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
        Class<?> requiredType = descriptor.getDependencyType();
        String primaryCandidate = this.determinePrimaryCandidate(candidates, requiredType);
        if (primaryCandidate != null) {
            return primaryCandidate;
        }
        String priorityCandidate = this.determineHighestPriorityCandidate(candidates, requiredType);
        if (priorityCandidate != null) {
            return priorityCandidate;
        }
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            String candidateName = entry.getKey();
            Object beanInstance = entry.getValue();
            if ((beanInstance == null || !this.resolvableDependencies.containsValue(beanInstance)) && !this.matchesBeanName(candidateName, descriptor.getDependencyName())) continue;
            return candidateName;
        }
        return null;
    }

    @Nullable
    protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String primaryBeanName = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            Object beanInstance;
            String candidateBeanName = entry.getKey();
            if (!this.isPrimary(candidateBeanName, beanInstance = entry.getValue())) continue;
            if (primaryBeanName != null) {
                boolean candidateLocal = this.containsBeanDefinition(candidateBeanName);
                boolean primaryLocal = this.containsBeanDefinition(primaryBeanName);
                if (candidateLocal && primaryLocal) {
                    throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "more than one 'primary' bean found among candidates: " + candidates.keySet());
                }
                if (!candidateLocal) continue;
                primaryBeanName = candidateBeanName;
                continue;
            }
            primaryBeanName = candidateBeanName;
        }
        return primaryBeanName;
    }

    @Nullable
    protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
        String highestPriorityBeanName = null;
        Integer highestPriority = null;
        for (Map.Entry<String, Object> entry : candidates.entrySet()) {
            Integer candidatePriority;
            String candidateBeanName = entry.getKey();
            Object beanInstance = entry.getValue();
            if (beanInstance == null || (candidatePriority = this.getPriority(beanInstance)) == null) continue;
            if (highestPriorityBeanName != null) {
                if (candidatePriority.equals(highestPriority)) {
                    throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(), "Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());
                }
                if (candidatePriority >= highestPriority) continue;
                highestPriorityBeanName = candidateBeanName;
                highestPriority = candidatePriority;
                continue;
            }
            highestPriorityBeanName = candidateBeanName;
            highestPriority = candidatePriority;
        }
        return highestPriorityBeanName;
    }

    protected boolean isPrimary(String beanName, Object beanInstance) {
        String transformedBeanName = this.transformedBeanName(beanName);
        if (this.containsBeanDefinition(transformedBeanName)) {
            return this.getMergedLocalBeanDefinition(transformedBeanName).isPrimary();
        }
        BeanFactory parent = this.getParentBeanFactory();
        return parent instanceof DefaultListableBeanFactory && ((DefaultListableBeanFactory)parent).isPrimary(transformedBeanName, beanInstance);
    }

    @Nullable
    protected Integer getPriority(Object beanInstance) {
        Comparator<Object> comparator = this.getDependencyComparator();
        if (comparator instanceof OrderComparator) {
            return ((OrderComparator)comparator).getPriority(beanInstance);
        }
        return null;
    }

    protected boolean matchesBeanName(String beanName, @Nullable String candidateName) {
        return candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(this.getAliases(beanName), candidateName));
    }

    private boolean isSelfReference(@Nullable String beanName, @Nullable String candidateName) {
        return beanName != null && candidateName != null && (beanName.equals(candidateName) || this.containsBeanDefinition(candidateName) && beanName.equals(this.getMergedLocalBeanDefinition(candidateName).getFactoryBeanName()));
    }

    private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
        this.checkBeanNotOfRequiredType(type, descriptor);
        throw new NoSuchBeanDefinitionException(resolvableType, "expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: " + ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
    }

    private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
        for (String beanName : this.beanDefinitionNames) {
            try {
                Object beanInstance;
                Class<?> beanType;
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                Class<?> targetType = mbd.getTargetType();
                if (targetType == null || !type.isAssignableFrom(targetType) || !this.isAutowireCandidate(beanName, mbd, descriptor, this.getAutowireCandidateResolver()) || (beanType = (beanInstance = this.getSingleton(beanName, false)) != null && beanInstance.getClass() != NullBean.class ? beanInstance.getClass() : this.predictBeanType(beanName, mbd, new Class[0])) == null || type.isAssignableFrom(beanType)) continue;
                throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
            }
            catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            }
        }
        BeanFactory parent = this.getParentBeanFactory();
        if (parent instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory)parent).checkBeanNotOfRequiredType(type, descriptor);
        }
    }

    private Optional<?> createOptionalDependency(DependencyDescriptor descriptor, @Nullable String beanName, final Object ... args) {
        NestedDependencyDescriptor descriptorToUse = new NestedDependencyDescriptor(descriptor){

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                return !ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName, args) : super.resolveCandidate(beanName, requiredType, beanFactory);
            }
        };
        Object result = this.doResolveDependency(descriptorToUse, beanName, null, null);
        return result instanceof Optional ? (Optional<Object>)result : Optional.ofNullable(result);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
        sb.append(": defining beans [");
        sb.append(StringUtils.collectionToCommaDelimitedString(this.beanDefinitionNames));
        sb.append("]; ");
        BeanFactory parent = this.getParentBeanFactory();
        if (parent == null) {
            sb.append("root of factory hierarchy");
        } else {
            sb.append("parent: ").append(ObjectUtils.identityToString(parent));
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.serializationId != null) {
            return new SerializedBeanFactoryReference(this.serializationId);
        }
        throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
    }

    static {
        try {
            javaxInjectProviderClass = ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            javaxInjectProviderClass = null;
        }
        serializableFactories = new ConcurrentHashMap<String, Reference<DefaultListableBeanFactory>>(8);
    }

    private class FactoryAwareOrderSourceProvider
    implements OrderComparator.OrderSourceProvider {
        private final Map<Object, String> instancesToBeanNames;

        public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
            this.instancesToBeanNames = instancesToBeanNames;
        }

        @Override
        @Nullable
        public Object getOrderSource(Object obj) {
            Class<?> targetType;
            String beanName = this.instancesToBeanNames.get(obj);
            if (beanName == null || !DefaultListableBeanFactory.this.containsBeanDefinition(beanName)) {
                return null;
            }
            RootBeanDefinition beanDefinition = DefaultListableBeanFactory.this.getMergedLocalBeanDefinition(beanName);
            ArrayList<GenericDeclaration> sources = new ArrayList<GenericDeclaration>(2);
            Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
            if (factoryMethod != null) {
                sources.add(factoryMethod);
            }
            if ((targetType = beanDefinition.getTargetType()) != null && targetType != obj.getClass()) {
                sources.add(targetType);
            }
            return sources.toArray();
        }
    }

    private class Jsr330Factory
    implements Serializable {
        private Jsr330Factory() {
        }

        public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
            return new Jsr330Provider(descriptor, beanName);
        }

        private class Jsr330Provider
        extends DependencyObjectProvider
        implements Provider<Object> {
            public Jsr330Provider(@Nullable DependencyDescriptor descriptor, String beanName) {
                super(descriptor, beanName);
            }

            @Nullable
            public Object get() throws BeansException {
                return this.getValue();
            }
        }
    }

    private class DependencyObjectProvider
    implements BeanObjectProvider<Object> {
        private final DependencyDescriptor descriptor;
        private final boolean optional;
        @Nullable
        private final String beanName;

        public DependencyObjectProvider(@Nullable DependencyDescriptor descriptor, String beanName) {
            this.descriptor = new NestedDependencyDescriptor(descriptor);
            this.optional = this.descriptor.getDependencyType() == Optional.class;
            this.beanName = beanName;
        }

        @Override
        public Object getObject() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            Object result = DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override
        public Object getObject(final Object ... args) throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, args);
            }
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor){

                @Override
                public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
                    return beanFactory.getBean(beanName, args);
                }
            };
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            if (result == null) {
                throw new NoSuchBeanDefinitionException(this.descriptor.getResolvableType());
            }
            return result;
        }

        @Override
        @Nullable
        public Object getIfAvailable() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor){

                @Override
                public boolean isRequired() {
                    return false;
                }
            };
            return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
        }

        @Override
        @Nullable
        public Object getIfUnique() throws BeansException {
            DependencyDescriptor descriptorToUse = new DependencyDescriptor(this.descriptor){

                @Override
                public boolean isRequired() {
                    return false;
                }

                @Override
                @Nullable
                public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) {
                    return null;
                }
            };
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(descriptorToUse, this.beanName, new Object[0]);
            }
            return DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
        }

        @Nullable
        protected Object getValue() throws BeansException {
            if (this.optional) {
                return DefaultListableBeanFactory.this.createOptionalDependency(this.descriptor, this.beanName, new Object[0]);
            }
            return DefaultListableBeanFactory.this.doResolveDependency(this.descriptor, this.beanName, null, null);
        }

        @Override
        public Stream<Object> stream() {
            return this.resolveStream(false);
        }

        @Override
        public Stream<Object> orderedStream() {
            return this.resolveStream(true);
        }

        private Stream<Object> resolveStream(boolean ordered) {
            StreamDependencyDescriptor descriptorToUse = new StreamDependencyDescriptor(this.descriptor, ordered);
            Object result = DefaultListableBeanFactory.this.doResolveDependency(descriptorToUse, this.beanName, null, null);
            return result instanceof Stream ? (Stream<Object>)result : Stream.of(result);
        }
    }

    private static interface BeanObjectProvider<T>
    extends ObjectProvider<T>,
    Serializable {
    }

    private static class StreamDependencyDescriptor
    extends DependencyDescriptor {
        private final boolean ordered;

        public StreamDependencyDescriptor(DependencyDescriptor original, boolean ordered) {
            super(original);
            this.ordered = ordered;
        }

        public boolean isOrdered() {
            return this.ordered;
        }
    }

    private static class MultiElementDescriptor
    extends NestedDependencyDescriptor {
        public MultiElementDescriptor(DependencyDescriptor original) {
            super(original);
        }
    }

    private static class NestedDependencyDescriptor
    extends DependencyDescriptor {
        public NestedDependencyDescriptor(DependencyDescriptor original) {
            super(original);
            this.increaseNestingLevel();
        }
    }

    private static class SerializedBeanFactoryReference
    implements Serializable {
        private final String id;

        public SerializedBeanFactoryReference(String id) {
            this.id = id;
        }

        private Object readResolve() {
            Object result;
            Reference ref = (Reference)serializableFactories.get(this.id);
            if (ref != null && (result = ref.get()) != null) {
                return result;
            }
            DefaultListableBeanFactory dummyFactory = new DefaultListableBeanFactory();
            dummyFactory.serializationId = this.id;
            return dummyFactory;
        }
    }
}

