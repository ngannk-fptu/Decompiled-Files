/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.AttributeAccessor
 *  org.springframework.core.DecoratingClassLoader
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.log.LogMessage
 *  org.springframework.core.metrics.ApplicationStartup
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.beans.factory.support;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DisposableBeanAdapter;
import org.springframework.beans.factory.support.FactoryBeanRegistrySupport;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.ScopeNotActiveException;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.log.LogMessage;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

public abstract class AbstractBeanFactory
extends FactoryBeanRegistrySupport
implements ConfigurableBeanFactory {
    @Nullable
    private BeanFactory parentBeanFactory;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private ClassLoader tempClassLoader;
    private boolean cacheBeanMetadata = true;
    @Nullable
    private BeanExpressionResolver beanExpressionResolver;
    @Nullable
    private ConversionService conversionService;
    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet<PropertyEditorRegistrar>(4);
    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap(4);
    @Nullable
    private TypeConverter typeConverter;
    private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList<StringValueResolver>();
    private final List<BeanPostProcessor> beanPostProcessors = new BeanPostProcessorCacheAwareList();
    @Nullable
    private BeanPostProcessorCache beanPostProcessorCache;
    private final Map<String, Scope> scopes = new LinkedHashMap<String, Scope>(8);
    @Nullable
    private SecurityContextProvider securityContextProvider;
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<String, RootBeanDefinition>(256);
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new NamedThreadLocal("Prototype beans currently in creation");
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

    public AbstractBeanFactory() {
    }

    public AbstractBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.doGetBean(name, null, null, false);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return this.doGetBean(name, requiredType, null, false);
    }

    @Override
    public Object getBean(String name, Object ... args) throws BeansException {
        return this.doGetBean(name, null, args, false);
    }

    public <T> T getBean(String name, @Nullable Class<T> requiredType, Object ... args) throws BeansException {
        return this.doGetBean(name, requiredType, args, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
        Object beanInstance;
        block31: {
            String beanName = this.transformedBeanName(name);
            Object sharedInstance = this.getSingleton(beanName);
            if (sharedInstance != null && args == null) {
                if (this.logger.isTraceEnabled()) {
                    if (this.isSingletonCurrentlyInCreation(beanName)) {
                        this.logger.trace((Object)("Returning eagerly cached instance of singleton bean '" + beanName + "' that is not fully initialized yet - a consequence of a circular reference"));
                    } else {
                        this.logger.trace((Object)("Returning cached instance of singleton bean '" + beanName + "'"));
                    }
                }
                beanInstance = this.getObjectForBeanInstance(sharedInstance, name, beanName, null);
            } else {
                if (this.isPrototypeCurrentlyInCreation(beanName)) {
                    throw new BeanCurrentlyInCreationException(beanName);
                }
                BeanFactory parentBeanFactory = this.getParentBeanFactory();
                if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                    String nameToLookup = this.originalBeanName(name);
                    if (parentBeanFactory instanceof AbstractBeanFactory) {
                        return ((AbstractBeanFactory)parentBeanFactory).doGetBean(nameToLookup, requiredType, args, typeCheckOnly);
                    }
                    if (args != null) {
                        return (T)parentBeanFactory.getBean(nameToLookup, args);
                    }
                    if (requiredType != null) {
                        return parentBeanFactory.getBean(nameToLookup, requiredType);
                    }
                    return (T)parentBeanFactory.getBean(nameToLookup);
                }
                if (!typeCheckOnly) {
                    this.markBeanAsCreated(beanName);
                }
                StartupStep beanCreation = this.applicationStartup.start("spring.beans.instantiate").tag("beanName", name);
                try {
                    if (requiredType != null) {
                        beanCreation.tag("beanType", requiredType::toString);
                    }
                    RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                    this.checkMergedBeanDefinition(mbd, beanName, args);
                    String[] dependsOn = mbd.getDependsOn();
                    if (dependsOn != null) {
                        for (String dep : dependsOn) {
                            if (this.isDependent(beanName, dep)) {
                                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                            }
                            this.registerDependentBean(dep, beanName);
                            try {
                                this.getBean(dep);
                            }
                            catch (NoSuchBeanDefinitionException ex) {
                                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "'" + beanName + "' depends on missing bean '" + dep + "'", (Throwable)((Object)ex));
                            }
                        }
                    }
                    if (mbd.isSingleton()) {
                        sharedInstance = this.getSingleton(beanName, () -> {
                            try {
                                return this.createBean(beanName, mbd, args);
                            }
                            catch (BeansException ex) {
                                this.destroySingleton(beanName);
                                throw ex;
                            }
                        });
                        beanInstance = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                        break block31;
                    }
                    if (mbd.isPrototype()) {
                        Object prototypeInstance = null;
                        try {
                            this.beforePrototypeCreation(beanName);
                            prototypeInstance = this.createBean(beanName, mbd, args);
                        }
                        finally {
                            this.afterPrototypeCreation(beanName);
                        }
                        beanInstance = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                        break block31;
                    }
                    String scopeName = mbd.getScope();
                    if (!StringUtils.hasLength((String)scopeName)) {
                        throw new IllegalStateException("No scope name defined for bean '" + beanName + "'");
                    }
                    Scope scope = this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }
                    try {
                        Object scopedInstance = scope.get(beanName, () -> {
                            this.beforePrototypeCreation(beanName);
                            try {
                                Object object = this.createBean(beanName, mbd, args);
                                return object;
                            }
                            finally {
                                this.afterPrototypeCreation(beanName);
                            }
                        });
                        beanInstance = this.getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    }
                    catch (IllegalStateException ex) {
                        throw new ScopeNotActiveException(beanName, scopeName, ex);
                    }
                }
                catch (BeansException ex) {
                    beanCreation.tag("exception", ((Object)((Object)ex)).getClass().toString());
                    beanCreation.tag("message", String.valueOf(ex.getMessage()));
                    this.cleanupAfterBeanCreationFailure(beanName);
                    throw ex;
                }
                finally {
                    beanCreation.end();
                }
            }
        }
        return this.adaptBeanInstance(name, beanInstance, requiredType);
    }

    <T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
        if (requiredType != null && !requiredType.isInstance(bean)) {
            try {
                Object convertedBean = this.getTypeConverter().convertIfNecessary(bean, requiredType);
                if (convertedBean == null) {
                    throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
                }
                return (T)convertedBean;
            }
            catch (TypeMismatchException ex) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Failed to convert bean '" + name + "' to required type '" + ClassUtils.getQualifiedName(requiredType) + "'"), (Throwable)((Object)ex));
                }
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        }
        return (T)bean;
    }

    @Override
    public boolean containsBean(String name) {
        String beanName = this.transformedBeanName(name);
        if (this.containsSingleton(beanName) || this.containsBeanDefinition(beanName)) {
            return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(name);
        }
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        return parentBeanFactory != null && parentBeanFactory.containsBean(this.originalBeanName(name));
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            if (beanInstance instanceof FactoryBean) {
                return BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean)beanInstance).isSingleton();
            }
            return !BeanFactoryUtils.isFactoryDereference(name);
        }
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.isSingleton(this.originalBeanName(name));
        }
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton()) {
            if (this.isFactoryBean(beanName, mbd)) {
                if (BeanFactoryUtils.isFactoryDereference(name)) {
                    return true;
                }
                FactoryBean factoryBean = (FactoryBean)this.getBean("&" + beanName);
                return factoryBean.isSingleton();
            }
            return !BeanFactoryUtils.isFactoryDereference(name);
        }
        return false;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.isPrototype(this.originalBeanName(name));
        }
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (mbd.isPrototype()) {
            return !BeanFactoryUtils.isFactoryDereference(name) || this.isFactoryBean(beanName, mbd);
        }
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            return false;
        }
        if (this.isFactoryBean(beanName, mbd)) {
            FactoryBean fb = (FactoryBean)this.getBean("&" + beanName);
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype() || !fb.isSingleton(), this.getAccessControlContext());
            }
            return fb instanceof SmartFactoryBean && ((SmartFactoryBean)fb).isPrototype() || !fb.isSingleton();
        }
        return false;
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return this.isTypeMatch(name, typeToMatch, true);
    }

    protected boolean isTypeMatch(String name, ResolvableType typeToMatch, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class[] classArray;
        String beanName = this.transformedBeanName(name);
        boolean isFactoryDereference = BeanFactoryUtils.isFactoryDereference(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean) {
                if (!isFactoryDereference) {
                    Class<?> type = this.getTypeForFactoryBean((FactoryBean)beanInstance);
                    return type != null && typeToMatch.isAssignableFrom(type);
                }
                return typeToMatch.isInstance(beanInstance);
            }
            if (!isFactoryDereference) {
                if (typeToMatch.isInstance(beanInstance)) {
                    return true;
                }
                if (typeToMatch.hasGenerics() && this.containsBeanDefinition(beanName)) {
                    ResolvableType resolvableType;
                    RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                    Class<?> targetType = mbd.getTargetType();
                    if (targetType != null && targetType != ClassUtils.getUserClass((Object)beanInstance)) {
                        Class classToMatch = typeToMatch.resolve();
                        if (classToMatch != null && !classToMatch.isInstance(beanInstance)) {
                            return false;
                        }
                        if (typeToMatch.isAssignableFrom(targetType)) {
                            return true;
                        }
                    }
                    if ((resolvableType = mbd.targetType) == null) {
                        resolvableType = mbd.factoryMethodReturnType;
                    }
                    return resolvableType != null && typeToMatch.isAssignableFrom(resolvableType);
                }
            }
            return false;
        }
        if (this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.isTypeMatch(this.originalBeanName(name), typeToMatch);
        }
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
        Class<FactoryBean> classToMatch = typeToMatch.resolve();
        if (classToMatch == null) {
            classToMatch = FactoryBean.class;
        }
        if (FactoryBean.class == classToMatch) {
            Class[] classArray2 = new Class[1];
            classArray = classArray2;
            classArray2[0] = classToMatch;
        } else {
            Class[] classArray3 = new Class[2];
            classArray3[0] = FactoryBean.class;
            classArray = classArray3;
            classArray3[1] = classToMatch;
        }
        Class[] typesToMatch = classArray;
        Class<?> predictedType = null;
        if (!isFactoryDereference && dbd != null && this.isFactoryBean(beanName, mbd) && (!mbd.isLazyInit() || allowFactoryBeanInit)) {
            RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
            Class<?> targetType = this.predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
            if (targetType != null && !FactoryBean.class.isAssignableFrom(targetType)) {
                predictedType = targetType;
            }
        }
        if (predictedType == null && (predictedType = this.predictBeanType(beanName, mbd, typesToMatch)) == null) {
            return false;
        }
        ResolvableType beanType = null;
        if (FactoryBean.class.isAssignableFrom(predictedType) ? beanInstance == null && !isFactoryDereference && (predictedType = (beanType = this.getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit)).resolve()) == null : isFactoryDereference && ((predictedType = this.predictBeanType(beanName, mbd, FactoryBean.class)) == null || !FactoryBean.class.isAssignableFrom(predictedType))) {
            return false;
        }
        if (beanType == null) {
            ResolvableType definedType = mbd.targetType;
            if (definedType == null) {
                definedType = mbd.factoryMethodReturnType;
            }
            if (definedType != null && definedType.resolve() == predictedType) {
                beanType = definedType;
            }
        }
        if (beanType != null) {
            return typeToMatch.isAssignableFrom(beanType);
        }
        return typeToMatch.isAssignableFrom(predictedType);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return this.isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
    }

    @Override
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.getType(name, true);
    }

    @Override
    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        Class<?> beanClass;
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
                return this.getTypeForFactoryBean((FactoryBean)beanInstance);
            }
            return beanInstance.getClass();
        }
        BeanFactory parentBeanFactory = this.getParentBeanFactory();
        if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
            return parentBeanFactory.getType(this.originalBeanName(name));
        }
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
        if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
            RootBeanDefinition tbd = this.getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
            Class<?> targetClass = this.predictBeanType(dbd.getBeanName(), tbd, new Class[0]);
            if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                return targetClass;
            }
        }
        if ((beanClass = this.predictBeanType(beanName, mbd, new Class[0])) != null && FactoryBean.class.isAssignableFrom(beanClass)) {
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                return this.getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit).resolve();
            }
            return beanClass;
        }
        return !BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null;
    }

    @Override
    public String[] getAliases(String name) {
        BeanFactory parentBeanFactory;
        String beanName = this.transformedBeanName(name);
        ArrayList<String> aliases = new ArrayList<String>();
        boolean factoryPrefix = name.startsWith("&");
        String fullBeanName = beanName;
        if (factoryPrefix) {
            fullBeanName = "&" + beanName;
        }
        if (!fullBeanName.equals(name)) {
            aliases.add(fullBeanName);
        }
        String[] retrievedAliases = super.getAliases(beanName);
        String prefix = factoryPrefix ? "&" : "";
        for (String retrievedAlias : retrievedAliases) {
            String alias = prefix + retrievedAlias;
            if (alias.equals(name)) continue;
            aliases.add(alias);
        }
        if (!this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName) && (parentBeanFactory = this.getParentBeanFactory()) != null) {
            aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
        }
        return StringUtils.toStringArray(aliases);
    }

    @Override
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override
    public boolean containsLocalBean(String name) {
        String beanName = this.transformedBeanName(name);
        return !(!this.containsSingleton(beanName) && !this.containsBeanDefinition(beanName) || BeanFactoryUtils.isFactoryDereference(name) && !this.isFactoryBean(beanName));
    }

    @Override
    public void setParentBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
            throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
        }
        if (this == parentBeanFactory) {
            throw new IllegalStateException("Cannot set parent bean factory to self");
        }
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader();
    }

    @Override
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override
    public void setTempClassLoader(@Nullable ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }

    @Override
    @Nullable
    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }

    @Override
    public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
        this.cacheBeanMetadata = cacheBeanMetadata;
    }

    @Override
    public boolean isCacheBeanMetadata() {
        return this.cacheBeanMetadata;
    }

    @Override
    public void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver) {
        this.beanExpressionResolver = resolver;
    }

    @Override
    @Nullable
    public BeanExpressionResolver getBeanExpressionResolver() {
        return this.beanExpressionResolver;
    }

    @Override
    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        Assert.notNull((Object)registrar, (String)"PropertyEditorRegistrar must not be null");
        this.propertyEditorRegistrars.add(registrar);
    }

    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        Assert.notNull(requiredType, (String)"Required type must not be null");
        Assert.notNull(propertyEditorClass, (String)"PropertyEditor class must not be null");
        this.customEditors.put(requiredType, propertyEditorClass);
    }

    @Override
    public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
        this.registerCustomEditors(registry);
    }

    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return this.customEditors;
    }

    @Override
    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Nullable
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    @Override
    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = this.getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }
        SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        typeConverter.setConversionService(this.getConversionService());
        this.registerCustomEditors(typeConverter);
        return typeConverter;
    }

    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        Assert.notNull((Object)valueResolver, (String)"StringValueResolver must not be null");
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public boolean hasEmbeddedValueResolver() {
        return !this.embeddedValueResolvers.isEmpty();
    }

    @Override
    @Nullable
    public String resolveEmbeddedValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
            if (result != null) continue;
            return null;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        Assert.notNull((Object)beanPostProcessor, (String)"BeanPostProcessor must not be null");
        List<BeanPostProcessor> list = this.beanPostProcessors;
        synchronized (list) {
            this.beanPostProcessors.remove(beanPostProcessor);
            this.beanPostProcessors.add(beanPostProcessor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
        List<BeanPostProcessor> list = this.beanPostProcessors;
        synchronized (list) {
            this.beanPostProcessors.removeAll(beanPostProcessors);
            this.beanPostProcessors.addAll(beanPostProcessors);
        }
    }

    @Override
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    BeanPostProcessorCache getBeanPostProcessorCache() {
        List<BeanPostProcessor> list = this.beanPostProcessors;
        synchronized (list) {
            BeanPostProcessorCache bppCache = this.beanPostProcessorCache;
            if (bppCache == null) {
                bppCache = new BeanPostProcessorCache();
                for (BeanPostProcessor bpp : this.beanPostProcessors) {
                    if (bpp instanceof InstantiationAwareBeanPostProcessor) {
                        bppCache.instantiationAware.add((InstantiationAwareBeanPostProcessor)bpp);
                        if (bpp instanceof SmartInstantiationAwareBeanPostProcessor) {
                            bppCache.smartInstantiationAware.add((SmartInstantiationAwareBeanPostProcessor)bpp);
                        }
                    }
                    if (bpp instanceof DestructionAwareBeanPostProcessor) {
                        bppCache.destructionAware.add((DestructionAwareBeanPostProcessor)bpp);
                    }
                    if (!(bpp instanceof MergedBeanDefinitionPostProcessor)) continue;
                    bppCache.mergedDefinition.add((MergedBeanDefinitionPostProcessor)bpp);
                }
                this.beanPostProcessorCache = bppCache;
            }
            return bppCache;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void resetBeanPostProcessorCache() {
        List<BeanPostProcessor> list = this.beanPostProcessors;
        synchronized (list) {
            this.beanPostProcessorCache = null;
        }
    }

    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return !this.getBeanPostProcessorCache().instantiationAware.isEmpty();
    }

    protected boolean hasDestructionAwareBeanPostProcessors() {
        return !this.getBeanPostProcessorCache().destructionAware.isEmpty();
    }

    @Override
    public void registerScope(String scopeName, Scope scope) {
        Assert.notNull((Object)scopeName, (String)"Scope identifier must not be null");
        Assert.notNull((Object)scope, (String)"Scope must not be null");
        if ("singleton".equals(scopeName) || "prototype".equals(scopeName)) {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
        Scope previous = this.scopes.put(scopeName, scope);
        if (previous != null && previous != scope) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]"));
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Registering scope '" + scopeName + "' with implementation [" + scope + "]"));
        }
    }

    @Override
    public String[] getRegisteredScopeNames() {
        return StringUtils.toStringArray(this.scopes.keySet());
    }

    @Override
    @Nullable
    public Scope getRegisteredScope(String scopeName) {
        Assert.notNull((Object)scopeName, (String)"Scope identifier must not be null");
        return this.scopes.get(scopeName);
    }

    public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        Assert.notNull((Object)applicationStartup, (String)"applicationStartup should not be null");
        this.applicationStartup = applicationStartup;
    }

    @Override
    public ApplicationStartup getApplicationStartup() {
        return this.applicationStartup;
    }

    @Override
    public AccessControlContext getAccessControlContext() {
        return this.securityContextProvider != null ? this.securityContextProvider.getAccessControlContext() : AccessController.getContext();
    }

    @Override
    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        Assert.notNull((Object)otherFactory, (String)"BeanFactory must not be null");
        this.setBeanClassLoader(otherFactory.getBeanClassLoader());
        this.setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
        this.setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
        this.setConversionService(otherFactory.getConversionService());
        if (otherFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory)otherFactory;
            this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
            this.customEditors.putAll(otherAbstractFactory.customEditors);
            this.typeConverter = otherAbstractFactory.typeConverter;
            this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
            this.scopes.putAll(otherAbstractFactory.scopes);
            this.securityContextProvider = otherAbstractFactory.securityContextProvider;
        } else {
            String[] otherScopeNames;
            this.setTypeConverter(otherFactory.getTypeConverter());
            for (String scopeName : otherScopeNames = otherFactory.getRegisteredScopeNames()) {
                this.scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
            }
        }
    }

    @Override
    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        String beanName = this.transformedBeanName(name);
        if (!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }
        return this.getMergedLocalBeanDefinition(beanName);
    }

    @Override
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean;
        }
        if (!this.containsBeanDefinition(beanName) && this.getParentBeanFactory() instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)this.getParentBeanFactory()).isFactoryBean(name);
        }
        return this.isFactoryBean(beanName, this.getMergedLocalBeanDefinition(beanName));
    }

    @Override
    public boolean isActuallyInCreation(String beanName) {
        return this.isSingletonCurrentlyInCreation(beanName) || this.isPrototypeCurrentlyInCreation(beanName);
    }

    protected boolean isPrototypeCurrentlyInCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        return curVal != null && (curVal.equals(beanName) || curVal instanceof Set && ((Set)curVal).contains(beanName));
    }

    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            HashSet<String> beanNameSet = new HashSet<String>(2);
            beanNameSet.add((String)curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set beanNameSet = (Set)curVal;
            beanNameSet.add(beanName);
        }
    }

    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set beanNameSet = (Set)curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    @Override
    public void destroyBean(String beanName, Object beanInstance) {
        this.destroyBean(beanName, beanInstance, this.getMergedLocalBeanDefinition(beanName));
    }

    protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
        new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessorCache().destructionAware, this.getAccessControlContext()).destroy();
    }

    @Override
    public void destroyScopedBean(String beanName) {
        RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton() || mbd.isPrototype()) {
            throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
        }
        String scopeName = mbd.getScope();
        Scope scope = this.scopes.get(scopeName);
        if (scope == null) {
            throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
        }
        Object bean = scope.remove(beanName);
        if (bean != null) {
            this.destroyBean(beanName, bean, mbd);
        }
    }

    protected String transformedBeanName(String name) {
        return this.canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    protected String originalBeanName(String name) {
        String beanName = this.transformedBeanName(name);
        if (name.startsWith("&")) {
            beanName = "&" + beanName;
        }
        return beanName;
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        bw.setConversionService(this.getConversionService());
        this.registerCustomEditors(bw);
    }

    protected void registerCustomEditors(PropertyEditorRegistry registry) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            ((PropertyEditorRegistrySupport)registry).useConfigValueEditors();
        }
        if (!this.propertyEditorRegistrars.isEmpty()) {
            for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
                try {
                    registrar.registerCustomEditors(registry);
                }
                catch (BeanCreationException ex) {
                    BeanCreationException bce;
                    String bceBeanName;
                    Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException && (bceBeanName = (bce = (BeanCreationException)((Object)rootCause)).getBeanName()) != null && this.isCurrentlyInCreation(bceBeanName)) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug((Object)("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + ex.getBeanName() + "': " + ex.getMessage()));
                        }
                        this.onSuppressedException((Exception)((Object)ex));
                        continue;
                    }
                    throw ex;
                }
            }
        }
        if (!this.customEditors.isEmpty()) {
            this.customEditors.forEach((requiredType, editorClass) -> registry.registerCustomEditor((Class<?>)requiredType, (PropertyEditor)BeanUtils.instantiateClass(editorClass)));
        }
    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null && !mbd.stale) {
            return mbd;
        }
        return this.getMergedBeanDefinition(beanName, this.getBeanDefinition(beanName));
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
        return this.getMergedBeanDefinition(beanName, bd, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd) throws BeanDefinitionStoreException {
        Map<String, RootBeanDefinition> map = this.mergedBeanDefinitions;
        synchronized (map) {
            RootBeanDefinition mbd = null;
            RootBeanDefinition previous = null;
            if (containingBd == null) {
                mbd = this.mergedBeanDefinitions.get(beanName);
            }
            if (mbd == null || mbd.stale) {
                previous = mbd;
                if (bd.getParentName() == null) {
                    mbd = bd instanceof RootBeanDefinition ? ((RootBeanDefinition)bd).cloneBeanDefinition() : new RootBeanDefinition(bd);
                } else {
                    BeanDefinition pbd;
                    block15: {
                        try {
                            String parentBeanName = this.transformedBeanName(bd.getParentName());
                            if (!beanName.equals(parentBeanName)) {
                                pbd = this.getMergedBeanDefinition(parentBeanName);
                                break block15;
                            }
                            BeanFactory parent = this.getParentBeanFactory();
                            if (parent instanceof ConfigurableBeanFactory) {
                                pbd = ((ConfigurableBeanFactory)parent).getMergedBeanDefinition(parentBeanName);
                                break block15;
                            }
                            throw new NoSuchBeanDefinitionException(parentBeanName, "Parent name '" + parentBeanName + "' is equal to bean name '" + beanName + "': cannot be resolved without a ConfigurableBeanFactory parent");
                        }
                        catch (NoSuchBeanDefinitionException ex) {
                            throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName, "Could not resolve parent bean definition '" + bd.getParentName() + "'", (Throwable)((Object)ex));
                        }
                    }
                    mbd = new RootBeanDefinition(pbd);
                    mbd.overrideFrom(bd);
                }
                if (!StringUtils.hasLength((String)mbd.getScope())) {
                    mbd.setScope("singleton");
                }
                if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                    mbd.setScope(containingBd.getScope());
                }
                if (containingBd == null && this.isCacheBeanMetadata()) {
                    this.mergedBeanDefinitions.put(beanName, mbd);
                }
            }
            if (previous != null) {
                this.copyRelevantMergedBeanDefinitionCaches(previous, mbd);
            }
            return mbd;
        }
    }

    private void copyRelevantMergedBeanDefinitionCaches(RootBeanDefinition previous, RootBeanDefinition mbd) {
        if (ObjectUtils.nullSafeEquals((Object)mbd.getBeanClassName(), (Object)previous.getBeanClassName()) && ObjectUtils.nullSafeEquals((Object)mbd.getFactoryBeanName(), (Object)previous.getFactoryBeanName()) && ObjectUtils.nullSafeEquals((Object)mbd.getFactoryMethodName(), (Object)previous.getFactoryMethodName())) {
            ResolvableType targetType = mbd.targetType;
            ResolvableType previousTargetType = previous.targetType;
            if (targetType == null || targetType.equals((Object)previousTargetType)) {
                mbd.targetType = previousTargetType;
                mbd.isFactoryBean = previous.isFactoryBean;
                mbd.resolvedTargetType = previous.resolvedTargetType;
                mbd.factoryMethodReturnType = previous.factoryMethodReturnType;
                mbd.factoryMethodToIntrospect = previous.factoryMethodToIntrospect;
            }
        }
    }

    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args) throws BeanDefinitionStoreException {
        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException(beanName);
        }
    }

    protected void clearMergedBeanDefinition(String beanName) {
        RootBeanDefinition bd = this.mergedBeanDefinitions.get(beanName);
        if (bd != null) {
            bd.stale = true;
        }
    }

    public void clearMetadataCache() {
        this.mergedBeanDefinitions.forEach((beanName, bd) -> {
            if (!this.isBeanEligibleForMetadataCaching((String)beanName)) {
                bd.stale = true;
            }
        });
    }

    @Nullable
    protected Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?> ... typesToMatch) throws CannotLoadBeanClassException {
        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> this.doResolveBeanClass(mbd, typesToMatch), this.getAccessControlContext());
            }
            return this.doResolveBeanClass(mbd, typesToMatch);
        }
        catch (PrivilegedActionException pae) {
            ClassNotFoundException ex = (ClassNotFoundException)pae.getException();
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        }
    }

    @Nullable
    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?> ... typesToMatch) throws ClassNotFoundException {
        String className;
        ClassLoader tempClassLoader;
        ClassLoader beanClassLoader;
        ClassLoader dynamicLoader = beanClassLoader = this.getBeanClassLoader();
        boolean freshResolve = false;
        if (!ObjectUtils.isEmpty((Object[])typesToMatch) && (tempClassLoader = this.getTempClassLoader()) != null) {
            dynamicLoader = tempClassLoader;
            freshResolve = true;
            if (tempClassLoader instanceof DecoratingClassLoader) {
                DecoratingClassLoader dcl = (DecoratingClassLoader)tempClassLoader;
                for (Class<?> typeToMatch : typesToMatch) {
                    dcl.excludeClass(typeToMatch.getName());
                }
            }
        }
        if ((className = mbd.getBeanClassName()) != null) {
            Object evaluated = this.evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                if (evaluated instanceof Class) {
                    return (Class)evaluated;
                }
                if (evaluated instanceof String) {
                    className = (String)evaluated;
                    freshResolve = true;
                } else {
                    throw new IllegalStateException("Invalid class name expression result: " + evaluated);
                }
            }
            if (freshResolve) {
                block12: {
                    if (dynamicLoader != null) {
                        try {
                            return dynamicLoader.loadClass(className);
                        }
                        catch (ClassNotFoundException ex) {
                            if (!this.logger.isTraceEnabled()) break block12;
                            this.logger.trace((Object)("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex));
                        }
                    }
                }
                return ClassUtils.forName((String)className, (ClassLoader)dynamicLoader);
            }
        }
        return mbd.resolveBeanClass(beanClassLoader);
    }

    @Nullable
    protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
        String scopeName;
        if (this.beanExpressionResolver == null) {
            return value;
        }
        Scope scope = null;
        if (beanDefinition != null && (scopeName = beanDefinition.getScope()) != null) {
            scope = this.getRegisteredScope(scopeName);
        }
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }

    @Nullable
    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?> ... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return this.resolveBeanClass(mbd, beanName, typesToMatch);
    }

    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Boolean result = mbd.isFactoryBean;
        if (result == null) {
            Class<?> beanType = this.predictBeanType(beanName, mbd, FactoryBean.class);
            mbd.isFactoryBean = result = Boolean.valueOf(beanType != null && FactoryBean.class.isAssignableFrom(beanType));
        }
        return result;
    }

    protected ResolvableType getTypeForFactoryBean(String beanName, RootBeanDefinition mbd, boolean allowInit) {
        ResolvableType result = this.getTypeForFactoryBeanFromAttributes(mbd);
        if (result != ResolvableType.NONE) {
            return result;
        }
        if (allowInit && mbd.isSingleton()) {
            try {
                FactoryBean factoryBean = this.doGetBean("&" + beanName, FactoryBean.class, null, true);
                Class<?> objectType = this.getTypeForFactoryBean(factoryBean);
                return objectType != null ? ResolvableType.forClass(objectType) : ResolvableType.NONE;
            }
            catch (BeanCreationException ex) {
                if (ex.contains(BeanCurrentlyInCreationException.class)) {
                    this.logger.trace((Object)LogMessage.format((String)"Bean currently in creation on FactoryBean type check: %s", (Object)((Object)ex)));
                } else if (mbd.isLazyInit()) {
                    this.logger.trace((Object)LogMessage.format((String)"Bean creation exception on lazy FactoryBean type check: %s", (Object)((Object)ex)));
                } else {
                    this.logger.debug((Object)LogMessage.format((String)"Bean creation exception on eager FactoryBean type check: %s", (Object)((Object)ex)));
                }
                this.onSuppressedException((Exception)((Object)ex));
            }
        }
        return ResolvableType.NONE;
    }

    ResolvableType getTypeForFactoryBeanFromAttributes(AttributeAccessor attributes) {
        Object attribute = attributes.getAttribute("factoryBeanObjectType");
        if (attribute instanceof ResolvableType) {
            return (ResolvableType)attribute;
        }
        if (attribute instanceof Class) {
            return ResolvableType.forClass((Class)((Class)attribute));
        }
        return ResolvableType.NONE;
    }

    @Nullable
    @Deprecated
    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        return this.getTypeForFactoryBean(beanName, mbd, true).resolve();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            Map<String, RootBeanDefinition> map = this.mergedBeanDefinitions;
            synchronized (map) {
                if (!this.alreadyCreated.contains(beanName)) {
                    this.clearMergedBeanDefinition(beanName);
                    this.alreadyCreated.add(beanName);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void cleanupAfterBeanCreationFailure(String beanName) {
        Map<String, RootBeanDefinition> map = this.mergedBeanDefinitions;
        synchronized (map) {
            this.alreadyCreated.remove(beanName);
        }
    }

    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.alreadyCreated.contains(beanName);
    }

    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            this.removeSingleton(beanName);
            return true;
        }
        return false;
    }

    protected boolean hasBeanCreationStarted() {
        return !this.alreadyCreated.isEmpty();
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                return beanInstance;
            }
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
            }
            if (mbd != null) {
                mbd.isFactoryBean = true;
            }
            return beanInstance;
        }
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        Object object = null;
        if (mbd != null) {
            mbd.isFactoryBean = true;
        } else {
            object = this.getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            FactoryBean factory = (FactoryBean)beanInstance;
            if (mbd == null && this.containsBeanDefinition(beanName)) {
                mbd = this.getMergedLocalBeanDefinition(beanName);
            }
            boolean synthetic = mbd != null && mbd.isSynthetic();
            object = this.getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

    public boolean isBeanNameInUse(String beanName) {
        return this.isAlias(beanName) || this.containsLocalBean(beanName) || this.hasDependentBean(beanName);
    }

    protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
        return bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || this.hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(bean, this.getBeanPostProcessorCache().destructionAware));
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
        AccessControlContext acc;
        AccessControlContext accessControlContext = acc = System.getSecurityManager() != null ? this.getAccessControlContext() : null;
        if (!mbd.isPrototype() && this.requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                this.registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessorCache().destructionAware, acc));
            } else {
                Scope scope = this.scopes.get(mbd.getScope());
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
                }
                scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(bean, beanName, mbd, this.getBeanPostProcessorCache().destructionAware, acc));
            }
        }
    }

    protected abstract boolean containsBeanDefinition(String var1);

    protected abstract BeanDefinition getBeanDefinition(String var1) throws BeansException;

    protected abstract Object createBean(String var1, RootBeanDefinition var2, @Nullable Object[] var3) throws BeanCreationException;

    static class BeanPostProcessorCache {
        final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList<InstantiationAwareBeanPostProcessor>();
        final List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = new ArrayList<SmartInstantiationAwareBeanPostProcessor>();
        final List<DestructionAwareBeanPostProcessor> destructionAware = new ArrayList<DestructionAwareBeanPostProcessor>();
        final List<MergedBeanDefinitionPostProcessor> mergedDefinition = new ArrayList<MergedBeanDefinitionPostProcessor>();

        BeanPostProcessorCache() {
        }
    }

    private class BeanPostProcessorCacheAwareList
    extends CopyOnWriteArrayList<BeanPostProcessor> {
        private BeanPostProcessorCacheAwareList() {
        }

        @Override
        public BeanPostProcessor set(int index, BeanPostProcessor element) {
            BeanPostProcessor result = super.set(index, element);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return result;
        }

        @Override
        public boolean add(BeanPostProcessor o) {
            boolean success = super.add(o);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return success;
        }

        @Override
        public void add(int index, BeanPostProcessor element) {
            super.add(index, element);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
        }

        @Override
        public BeanPostProcessor remove(int index) {
            BeanPostProcessor result = (BeanPostProcessor)super.remove(index);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return result;
        }

        @Override
        public boolean remove(Object o) {
            boolean success = super.remove(o);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean success = super.removeAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean success = super.retainAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public boolean addAll(Collection<? extends BeanPostProcessor> c) {
            boolean success = super.addAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public boolean addAll(int index, Collection<? extends BeanPostProcessor> c) {
            boolean success = super.addAll(index, c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public boolean removeIf(Predicate<? super BeanPostProcessor> filter) {
            boolean success = super.removeIf(filter);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override
        public void replaceAll(UnaryOperator<BeanPostProcessor> operator) {
            super.replaceAll(operator);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
        }
    }
}

