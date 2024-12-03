/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.MergedAnnotation
 *  org.springframework.core.annotation.MergedAnnotation$Adapt
 *  org.springframework.core.annotation.MergedAnnotations
 */
package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.LookupOverride;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class AutowiredAnnotationBeanPostProcessor
extends InstantiationAwareBeanPostProcessorAdapter
implements MergedBeanDefinitionPostProcessor,
PriorityOrdered,
BeanFactoryAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new LinkedHashSet<Class<? extends Annotation>>(4);
    private String requiredParameterName = "required";
    private boolean requiredParameterValue = true;
    private int order = 0x7FFFFFFD;
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;
    private final Set<String> lookupMethodsChecked = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache = new ConcurrentHashMap(256);
    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);

    public AutowiredAnnotationBeanPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.autowiredAnnotationTypes.add(Value.class);
        try {
            this.autowiredAnnotationTypes.add(ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
            this.logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
        Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.add(autowiredAnnotationType);
    }

    public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }

    public void setRequiredParameterName(String requiredParameterName) {
        this.requiredParameterName = requiredParameterName;
    }

    public void setRequiredParameterValue(boolean requiredParameterValue) {
        this.requiredParameterValue = requiredParameterValue;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = this.findAutowiringMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    @Override
    public void resetBeanDefinition(String beanName) {
        this.lookupMethodsChecked.remove(beanName);
        this.injectionMetadataCache.remove(beanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeanCreationException {
        Constructor<?>[] candidateConstructors;
        if (!this.lookupMethodsChecked.contains(beanName)) {
            if (AnnotationUtils.isCandidateClass(beanClass, Lookup.class)) {
                try {
                    Class<?> targetClass = beanClass;
                    do {
                        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                            Lookup lookup = method.getAnnotation(Lookup.class);
                            if (lookup != null) {
                                Assert.state(this.beanFactory != null, "No BeanFactory available");
                                LookupOverride override = new LookupOverride(method, lookup.value());
                                try {
                                    RootBeanDefinition mbd = (RootBeanDefinition)this.beanFactory.getMergedBeanDefinition(beanName);
                                    mbd.getMethodOverrides().addOverride(override);
                                }
                                catch (NoSuchBeanDefinitionException ex) {
                                    throw new BeanCreationException(beanName, "Cannot apply @Lookup to beans without corresponding bean definition");
                                }
                            }
                        });
                    } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
                }
                catch (IllegalStateException ex) {
                    throw new BeanCreationException(beanName, "Lookup method resolution failed", ex);
                }
            }
            this.lookupMethodsChecked.add(beanName);
        }
        if ((candidateConstructors = this.candidateConstructorsCache.get(beanClass)) == null) {
            Map<Class<?>, Constructor<?>[]> map = this.candidateConstructorsCache;
            synchronized (map) {
                candidateConstructors = this.candidateConstructorsCache.get(beanClass);
                if (candidateConstructors == null) {
                    Constructor<?>[] rawCandidates;
                    try {
                        rawCandidates = beanClass.getDeclaredConstructors();
                    }
                    catch (Throwable ex) {
                        throw new BeanCreationException(beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                    }
                    ArrayList candidates = new ArrayList(rawCandidates.length);
                    Constructor<?> requiredConstructor = null;
                    Constructor<?> defaultConstructor = null;
                    Constructor<?> primaryConstructor = BeanUtils.findPrimaryConstructor(beanClass);
                    int nonSyntheticConstructors = 0;
                    for (Constructor<?> candidate : rawCandidates) {
                        Class<?> userClass;
                        if (!candidate.isSynthetic()) {
                            ++nonSyntheticConstructors;
                        } else if (primaryConstructor != null) continue;
                        MergedAnnotation<?> ann = this.findAutowiredAnnotation(candidate);
                        if (ann == null && (userClass = ClassUtils.getUserClass(beanClass)) != beanClass) {
                            try {
                                Constructor<?> superCtor = userClass.getDeclaredConstructor(candidate.getParameterTypes());
                                ann = this.findAutowiredAnnotation(superCtor);
                            }
                            catch (NoSuchMethodException noSuchMethodException) {
                                // empty catch block
                            }
                        }
                        if (ann != null) {
                            if (requiredConstructor != null) {
                                throw new BeanCreationException(beanName, "Invalid autowire-marked constructor: " + candidate + ". Found constructor with 'required' Autowired annotation already: " + requiredConstructor);
                            }
                            boolean required = this.determineRequiredStatus(ann);
                            if (required) {
                                if (!candidates.isEmpty()) {
                                    throw new BeanCreationException(beanName, "Invalid autowire-marked constructors: " + candidates + ". Found constructor with 'required' Autowired annotation: " + candidate);
                                }
                                requiredConstructor = candidate;
                            }
                            candidates.add(candidate);
                            continue;
                        }
                        if (candidate.getParameterCount() != 0) continue;
                        defaultConstructor = candidate;
                    }
                    if (!candidates.isEmpty()) {
                        if (requiredConstructor == null) {
                            if (defaultConstructor != null) {
                                candidates.add(defaultConstructor);
                            } else if (candidates.size() == 1 && this.logger.isInfoEnabled()) {
                                this.logger.info("Inconsistent constructor declaration on bean with name '" + beanName + "': single autowire-marked constructor flagged as optional - this constructor is effectively required since there is no default constructor to fall back to: " + candidates.get(0));
                            }
                        }
                        candidateConstructors = candidates.toArray(new Constructor[0]);
                    } else {
                        candidateConstructors = rawCandidates.length == 1 && rawCandidates[0].getParameterCount() > 0 ? new Constructor[]{rawCandidates[0]} : (nonSyntheticConstructors == 2 && primaryConstructor != null && defaultConstructor != null && !primaryConstructor.equals(defaultConstructor) ? new Constructor[]{primaryConstructor, defaultConstructor} : (nonSyntheticConstructors == 1 && primaryConstructor != null ? new Constructor[]{primaryConstructor} : new Constructor[]{}));
                    }
                    this.candidateConstructorsCache.put(beanClass, candidateConstructors);
                }
            }
        }
        return candidateConstructors.length > 0 ? candidateConstructors : null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean2, String beanName) {
        InjectionMetadata metadata = this.findAutowiringMetadata(beanName, bean2.getClass(), pvs);
        try {
            metadata.inject(bean2, beanName, pvs);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }
        return pvs;
    }

    @Override
    @Deprecated
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean2, String beanName) {
        return this.postProcessProperties(pvs, bean2, beanName);
    }

    public void processInjection(Object bean2) throws BeanCreationException {
        Class<?> clazz = bean2.getClass();
        InjectionMetadata metadata = this.findAutowiringMetadata(clazz.getName(), clazz, null);
        try {
            metadata.inject(bean2, null, null);
        }
        catch (BeanCreationException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            Map<String, InjectionMetadata> map = this.injectionMetadataCache;
            synchronized (map) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = this.buildAutowiringMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
            return InjectionMetadata.EMPTY;
        }
        ArrayList<InjectionMetadata.InjectedElement> elements = new ArrayList<InjectionMetadata.InjectedElement>();
        Class<?> targetClass = clazz;
        do {
            ArrayList currElements = new ArrayList();
            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = this.findAutowiredAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Autowired annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    boolean required = this.determineRequiredStatus(ann);
                    currElements.add(new AutowiredFieldElement(field, required));
                }
            });
            ReflectionUtils.doWithLocalMethods(targetClass, method -> {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                MergedAnnotation<?> ann = this.findAutowiredAnnotation(bridgedMethod);
                if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("Autowired annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterCount() == 0 && this.logger.isInfoEnabled()) {
                        this.logger.info("Autowired annotation should only be used on methods with parameters: " + method);
                    }
                    boolean required = this.determineRequiredStatus(ann);
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    currElements.add(new AutowiredMethodElement(method, required, pd));
                }
            });
            elements.addAll(0, currElements);
        } while ((targetClass = targetClass.getSuperclass()) != null && targetClass != Object.class);
        return InjectionMetadata.forElements(elements, clazz);
    }

    @Nullable
    private MergedAnnotation<?> findAutowiredAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from((AnnotatedElement)ao);
        for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            MergedAnnotation annotation = annotations.get(type);
            if (!annotation.isPresent()) continue;
            return annotation;
        }
        return null;
    }

    protected boolean determineRequiredStatus(MergedAnnotation<?> ann) {
        return this.determineRequiredStatus((AnnotationAttributes)ann.asMap(mergedAnnotation -> new AnnotationAttributes(mergedAnnotation.getType()), new MergedAnnotation.Adapt[0]));
    }

    @Deprecated
    protected boolean determineRequiredStatus(AnnotationAttributes ann) {
        return !ann.containsKey(this.requiredParameterName) || this.requiredParameterValue == ann.getBoolean(this.requiredParameterName);
    }

    protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
        if (this.beanFactory == null) {
            throw new IllegalStateException("No BeanFactory configured - override the getBeanOfType method or specify the 'beanFactory' property");
        }
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
    }

    private void registerDependentBeans(@Nullable String beanName, Set<String> autowiredBeanNames) {
        if (beanName != null) {
            for (String autowiredBeanName : autowiredBeanNames) {
                if (this.beanFactory != null && this.beanFactory.containsBean(autowiredBeanName)) {
                    this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
                }
                if (!this.logger.isTraceEnabled()) continue;
                this.logger.trace("Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName + "'");
            }
        }
    }

    @Nullable
    private Object resolvedCachedArgument(@Nullable String beanName, @Nullable Object cachedArgument) {
        if (cachedArgument instanceof DependencyDescriptor) {
            DependencyDescriptor descriptor = (DependencyDescriptor)cachedArgument;
            Assert.state(this.beanFactory != null, "No BeanFactory available");
            return this.beanFactory.resolveDependency(descriptor, beanName, null, null);
        }
        return cachedArgument;
    }

    private static class ShortcutDependencyDescriptor
    extends DependencyDescriptor {
        private final String shortcut;
        private final Class<?> requiredType;

        public ShortcutDependencyDescriptor(DependencyDescriptor original, String shortcut, Class<?> requiredType) {
            super(original);
            this.shortcut = shortcut;
            this.requiredType = requiredType;
        }

        @Override
        public Object resolveShortcut(BeanFactory beanFactory) {
            return beanFactory.getBean(this.shortcut, this.requiredType);
        }
    }

    private class AutowiredMethodElement
    extends InjectionMetadata.InjectedElement {
        private final boolean required;
        private volatile boolean cached;
        @Nullable
        private volatile Object[] cachedMethodArguments;

        public AutowiredMethodElement(Method method, @Nullable boolean required, PropertyDescriptor pd) {
            super(method, pd);
            this.required = required;
        }

        @Override
        protected void inject(Object bean2, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Object[] arguments;
            if (this.checkPropertySkipping(pvs)) {
                return;
            }
            Method method = (Method)this.member;
            if (this.cached) {
                try {
                    arguments = this.resolveCachedArguments(beanName);
                }
                catch (NoSuchBeanDefinitionException ex) {
                    arguments = this.resolveMethodArguments(method, bean2, beanName);
                }
            } else {
                arguments = this.resolveMethodArguments(method, bean2, beanName);
            }
            if (arguments != null) {
                try {
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(bean2, arguments);
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }

        @Nullable
        private Object[] resolveCachedArguments(@Nullable String beanName) {
            Object[] cachedMethodArguments = this.cachedMethodArguments;
            if (cachedMethodArguments == null) {
                return null;
            }
            Object[] arguments = new Object[cachedMethodArguments.length];
            for (int i = 0; i < arguments.length; ++i) {
                arguments[i] = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, cachedMethodArguments[i]);
            }
            return arguments;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        private Object[] resolveMethodArguments(Method method, Object bean2, @Nullable String beanName) {
            int argumentCount = method.getParameterCount();
            Object[] arguments = new Object[argumentCount];
            DependencyDescriptor[] descriptors = new DependencyDescriptor[argumentCount];
            LinkedHashSet<String> autowiredBeans = new LinkedHashSet<String>(argumentCount);
            Assert.state(AutowiredAnnotationBeanPostProcessor.this.beanFactory != null, "No BeanFactory available");
            TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
            for (int i = 0; i < arguments.length; ++i) {
                MethodParameter methodParam = new MethodParameter(method, i);
                DependencyDescriptor currDesc = new DependencyDescriptor(methodParam, this.required);
                currDesc.setContainingClass(bean2.getClass());
                descriptors[i] = currDesc;
                try {
                    Object arg = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(currDesc, beanName, autowiredBeans, typeConverter);
                    if (arg == null && !this.required) {
                        arguments = null;
                        break;
                    }
                    arguments[i] = arg;
                    continue;
                }
                catch (BeansException ex) {
                    throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(methodParam), ex);
                }
            }
            AutowiredMethodElement autowiredMethodElement = this;
            synchronized (autowiredMethodElement) {
                if (!this.cached) {
                    if (arguments != null) {
                        DependencyDescriptor[] cachedMethodArguments = Arrays.copyOf(descriptors, arguments.length);
                        AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeans);
                        if (autowiredBeans.size() == argumentCount) {
                            Iterator it = autowiredBeans.iterator();
                            Class<?>[] paramTypes = method.getParameterTypes();
                            for (int i = 0; i < paramTypes.length; ++i) {
                                String autowiredBeanName = (String)it.next();
                                if (!AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) || !AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) continue;
                                cachedMethodArguments[i] = new ShortcutDependencyDescriptor(descriptors[i], autowiredBeanName, paramTypes[i]);
                            }
                        }
                        this.cachedMethodArguments = cachedMethodArguments;
                    } else {
                        this.cachedMethodArguments = null;
                    }
                    this.cached = true;
                }
            }
            return arguments;
        }
    }

    private class AutowiredFieldElement
    extends InjectionMetadata.InjectedElement {
        private final boolean required;
        private volatile boolean cached;
        @Nullable
        private volatile Object cachedFieldValue;

        public AutowiredFieldElement(Field field, boolean required) {
            super(field, null);
            this.required = required;
        }

        @Override
        protected void inject(Object bean2, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Object value;
            Field field = (Field)this.member;
            if (this.cached) {
                try {
                    value = AutowiredAnnotationBeanPostProcessor.this.resolvedCachedArgument(beanName, this.cachedFieldValue);
                }
                catch (NoSuchBeanDefinitionException ex) {
                    value = this.resolveFieldValue(field, bean2, beanName);
                }
            } else {
                value = this.resolveFieldValue(field, bean2, beanName);
            }
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean2, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        private Object resolveFieldValue(Field field, Object bean2, @Nullable String beanName) {
            Object value;
            DependencyDescriptor desc = new DependencyDescriptor(field, this.required);
            desc.setContainingClass(bean2.getClass());
            LinkedHashSet<String> autowiredBeanNames = new LinkedHashSet<String>(1);
            Assert.state(AutowiredAnnotationBeanPostProcessor.this.beanFactory != null, "No BeanFactory available");
            TypeConverter typeConverter = AutowiredAnnotationBeanPostProcessor.this.beanFactory.getTypeConverter();
            try {
                value = AutowiredAnnotationBeanPostProcessor.this.beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
            }
            catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
            }
            AutowiredFieldElement autowiredFieldElement = this;
            synchronized (autowiredFieldElement) {
                if (!this.cached) {
                    DependencyDescriptor cachedFieldValue = null;
                    if (value != null || this.required) {
                        cachedFieldValue = desc;
                        AutowiredAnnotationBeanPostProcessor.this.registerDependentBeans(beanName, autowiredBeanNames);
                        if (autowiredBeanNames.size() == 1) {
                            String autowiredBeanName = (String)autowiredBeanNames.iterator().next();
                            if (AutowiredAnnotationBeanPostProcessor.this.beanFactory.containsBean(autowiredBeanName) && AutowiredAnnotationBeanPostProcessor.this.beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
                                cachedFieldValue = new ShortcutDependencyDescriptor(desc, autowiredBeanName, field.getType());
                            }
                        }
                    }
                    this.cachedFieldValue = cachedFieldValue;
                    this.cached = true;
                }
            }
            return value;
        }
    }
}

