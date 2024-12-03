/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.springframework.beans.factory.support;

import java.beans.ConstructorProperties;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AutowireUtils;
import org.springframework.beans.factory.support.BeanDefinitionValueResolver;
import org.springframework.beans.factory.support.ImplicitlyAppearedSingletonException;
import org.springframework.beans.factory.support.InstantiationStrategy;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

class ConstructorResolver {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint = new NamedThreadLocal("Current injection point");
    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final Log logger;

    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.logger = beanFactory.getLogger();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {
        Object[] argsToUse;
        Constructor<?> constructorToUse;
        BeanWrapperImpl bw;
        block38: {
            void var18_26;
            int minNrOfArgs;
            Constructor<?> uniqueCandidate;
            bw = new BeanWrapperImpl();
            this.beanFactory.initBeanWrapper(bw);
            constructorToUse = null;
            ArgumentsHolder argsHolderToUse = null;
            argsToUse = null;
            if (explicitArgs != null) {
                argsToUse = explicitArgs;
            } else {
                Object[] argsToResolve = null;
                Object object = mbd.constructorArgumentLock;
                synchronized (object) {
                    constructorToUse = (Constructor<?>)mbd.resolvedConstructorOrFactoryMethod;
                    if (constructorToUse != null && mbd.constructorArgumentsResolved && (argsToUse = mbd.resolvedConstructorArguments) == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
                if (argsToResolve != null) {
                    argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
                }
            }
            if (constructorToUse != null && argsToUse != null) break block38;
            Constructor<?>[] candidates = chosenCtors;
            if (candidates == null) {
                Class<?> beanClass = mbd.getBeanClass();
                try {
                    candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
                }
                catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                }
            }
            if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues() && (uniqueCandidate = candidates[0]).getParameterCount() == 0) {
                Object ex = mbd.constructorArgumentLock;
                synchronized (ex) {
                    mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                    mbd.constructorArgumentsResolved = true;
                    mbd.resolvedConstructorArguments = EMPTY_ARGS;
                }
                bw.setBeanInstance(this.instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
                return bw;
            }
            boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
            ConstructorArgumentValues resolvedValues = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }
            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            LinkedHashSet ambiguousConstructors = null;
            ArrayDeque<UnsatisfiedDependencyException> causes = null;
            Constructor<?>[] constructorArray = candidates;
            int n = constructorArray.length;
            boolean bl = false;
            while (var18_26 < n) {
                block37: {
                    int typeDiffWeight;
                    ArgumentsHolder argsHolder;
                    Class<?>[] paramTypes;
                    Constructor<?> candidate;
                    block36: {
                        candidate = constructorArray[var18_26];
                        int parameterCount = candidate.getParameterCount();
                        if (constructorToUse != null && argsToUse != null && argsToUse.length > parameterCount) break;
                        if (parameterCount < minNrOfArgs) break block37;
                        paramTypes = candidate.getParameterTypes();
                        if (resolvedValues != null) {
                            try {
                                ParameterNameDiscoverer pnd;
                                String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
                                if (paramNames == null && (pnd = this.beanFactory.getParameterNameDiscoverer()) != null) {
                                    paramNames = pnd.getParameterNames(candidate);
                                }
                                argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, this.getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
                                break block36;
                            }
                            catch (UnsatisfiedDependencyException ex) {
                                if (this.logger.isTraceEnabled()) {
                                    this.logger.trace((Object)("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex));
                                }
                                if (causes == null) {
                                    causes = new ArrayDeque<UnsatisfiedDependencyException>(1);
                                }
                                causes.add(ex);
                                break block37;
                            }
                        }
                        if (parameterCount != explicitArgs.length) break block37;
                        argsHolder = new ArgumentsHolder(explicitArgs);
                    }
                    int n2 = typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        constructorToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousConstructors = null;
                    } else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                        if (ambiguousConstructors == null) {
                            ambiguousConstructors = new LinkedHashSet();
                            ambiguousConstructors.add(constructorToUse);
                        }
                        ambiguousConstructors.add(candidate);
                    }
                }
                ++var18_26;
            }
            if (constructorToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex = (UnsatisfiedDependencyException)causes.removeLast();
                    for (Exception exception : causes) {
                        this.beanFactory.onSuppressedException(exception);
                    }
                    throw ex;
                }
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor on bean class [" + mbd.getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
            }
            if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found on bean class [" + mbd.getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
            }
            if (explicitArgs == null && argsHolderToUse != null) {
                argsHolderToUse.storeCache(mbd, constructorToUse);
            }
        }
        Assert.state(argsToUse != null, "Unresolved constructor arguments");
        bw.setBeanInstance(this.instantiate(beanName, mbd, constructorToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {
        try {
            InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> strategy.instantiate(mbd, beanName, (BeanFactory)this.beanFactory, constructorToUse, argsToUse), this.beanFactory.getAccessControlContext());
            }
            return strategy.instantiate(mbd, beanName, (BeanFactory)this.beanFactory, constructorToUse, argsToUse);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via constructor failed", ex);
        }
    }

    public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
        boolean isStatic;
        Class<?> factoryClass;
        if (mbd.getFactoryBeanName() != null) {
            factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
            isStatic = false;
        } else {
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Assert.state(factoryClass != null, "Unresolvable factory class");
        factoryClass = ClassUtils.getUserClass(factoryClass);
        Method[] candidates = this.getCandidateMethods(factoryClass, mbd);
        Method uniqueCandidate = null;
        for (Method candidate : candidates) {
            if (Modifier.isStatic(candidate.getModifiers()) != isStatic || !mbd.isFactoryMethod(candidate)) continue;
            if (uniqueCandidate == null) {
                uniqueCandidate = candidate;
                continue;
            }
            if (!this.isParamMismatch(uniqueCandidate, candidate)) continue;
            uniqueCandidate = null;
            break;
        }
        mbd.factoryMethodToIntrospect = uniqueCandidate;
    }

    private boolean isParamMismatch(Method uniqueCandidate, Method candidate) {
        int candidateParameterCount;
        int uniqueCandidateParameterCount = uniqueCandidate.getParameterCount();
        return uniqueCandidateParameterCount != (candidateParameterCount = candidate.getParameterCount()) || !Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes());
    }

    private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(() -> mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods());
        }
        return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getAllDeclaredMethods(factoryClass) : factoryClass.getMethods();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
        boolean isStatic;
        Class<?> factoryClass;
        Object factoryBean;
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        String factoryBeanName = mbd.getFactoryBeanName();
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }
            factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
                throw new ImplicitlyAppearedSingletonException();
            }
            this.beanFactory.registerDependentBean(factoryBeanName, beanName);
            factoryClass = factoryBean.getClass();
            isStatic = false;
        } else {
            if (!mbd.hasBeanClass()) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
            }
            factoryBean = null;
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Method factoryMethodToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            Object object = mbd.constructorArgumentLock;
            synchronized (object) {
                factoryMethodToUse = (Method)mbd.resolvedConstructorOrFactoryMethod;
                if (factoryMethodToUse != null && mbd.constructorArgumentsResolved && (argsToUse = mbd.resolvedConstructorArguments) == null) {
                    argsToResolve = mbd.preparedConstructorArguments;
                }
            }
            if (argsToResolve != null) {
                argsToUse = this.resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve);
            }
        }
        if (factoryMethodToUse == null || argsToUse == null) {
            int minNrOfArgs;
            Method uniqueCandidate;
            factoryClass = ClassUtils.getUserClass(factoryClass);
            List<Object> candidates = null;
            if (mbd.isFactoryMethodUnique) {
                if (factoryMethodToUse == null) {
                    factoryMethodToUse = mbd.getResolvedFactoryMethod();
                }
                if (factoryMethodToUse != null) {
                    candidates = Collections.singletonList(factoryMethodToUse);
                }
            }
            if (candidates == null) {
                Method[] rawCandidates;
                candidates = new ArrayList();
                for (Method candidate : rawCandidates = this.getCandidateMethods(factoryClass, mbd)) {
                    if (Modifier.isStatic(candidate.getModifiers()) != isStatic || !mbd.isFactoryMethod(candidate)) continue;
                    candidates.add(candidate);
                }
            }
            if (candidates.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues() && (uniqueCandidate = (Method)candidates.get(0)).getParameterCount() == 0) {
                mbd.factoryMethodToIntrospect = uniqueCandidate;
                Object object = mbd.constructorArgumentLock;
                synchronized (object) {
                    mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                    mbd.constructorArgumentsResolved = true;
                    mbd.resolvedConstructorArguments = EMPTY_ARGS;
                }
                bw.setBeanInstance(this.instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
                return bw;
            }
            if (candidates.size() > 1) {
                candidates.sort(AutowireUtils.EXECUTABLE_COMPARATOR);
            }
            ConstructorArgumentValues resolvedValues = null;
            boolean bl = mbd.getResolvedAutowireMode() == 3;
            int minTypeDiffWeight = Integer.MAX_VALUE;
            LinkedHashSet<Method> ambiguousFactoryMethods = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else if (mbd.hasConstructorArgumentValues()) {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = this.resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            } else {
                minNrOfArgs = 0;
            }
            ArrayDeque<UnsatisfiedDependencyException> causes = null;
            for (Method method : candidates) {
                int typeDiffWeight;
                ArgumentsHolder argsHolder;
                int n = method.getParameterCount();
                if (n < minNrOfArgs) continue;
                Object[] paramTypes = method.getParameterTypes();
                if (explicitArgs != null) {
                    if (paramTypes.length != explicitArgs.length) continue;
                    argsHolder = new ArgumentsHolder(explicitArgs);
                } else {
                    try {
                        String[] paramNames = null;
                        ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                        if (pnd != null) {
                            paramNames = pnd.getParameterNames(method);
                        }
                        argsHolder = this.createArgumentArray(beanName, mbd, resolvedValues, bw, (Class<?>[])paramTypes, paramNames, method, bl, candidates.size() == 1);
                    }
                    catch (UnsatisfiedDependencyException ex) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace((Object)("Ignoring factory method [" + method + "] of bean '" + beanName + "': " + ex));
                        }
                        if (causes == null) {
                            causes = new ArrayDeque<UnsatisfiedDependencyException>(1);
                        }
                        causes.add(ex);
                        continue;
                    }
                }
                int n2 = typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight((Class<?>[])paramTypes) : argsHolder.getAssignabilityWeight((Class<?>[])paramTypes);
                if (typeDiffWeight < minTypeDiffWeight) {
                    factoryMethodToUse = method;
                    argsHolderToUse = argsHolder;
                    argsToUse = argsHolder.arguments;
                    minTypeDiffWeight = typeDiffWeight;
                    ambiguousFactoryMethods = null;
                    continue;
                }
                if (factoryMethodToUse == null || typeDiffWeight != minTypeDiffWeight || mbd.isLenientConstructorResolution() || paramTypes.length != factoryMethodToUse.getParameterCount() || Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) continue;
                if (ambiguousFactoryMethods == null) {
                    ambiguousFactoryMethods = new LinkedHashSet<Method>();
                    ambiguousFactoryMethods.add(factoryMethodToUse);
                }
                ambiguousFactoryMethods.add(method);
            }
            if (factoryMethodToUse == null || argsToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex = (UnsatisfiedDependencyException)causes.removeLast();
                    for (Exception exception : causes) {
                        this.beanFactory.onSuppressedException(exception);
                    }
                    throw ex;
                }
                ArrayList<String> argTypes = new ArrayList<String>(minNrOfArgs);
                if (explicitArgs != null) {
                    for (Object arg : explicitArgs) {
                        argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
                    }
                } else if (resolvedValues != null) {
                    LinkedHashSet<ConstructorArgumentValues.ValueHolder> linkedHashSet = new LinkedHashSet<ConstructorArgumentValues.ValueHolder>(resolvedValues.getArgumentCount());
                    linkedHashSet.addAll(resolvedValues.getIndexedArgumentValues().values());
                    linkedHashSet.addAll(resolvedValues.getGenericArgumentValues());
                    for (ConstructorArgumentValues.ValueHolder value : linkedHashSet) {
                        String argType = value.getType() != null ? ClassUtils.getShortName(value.getType()) : (value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null");
                        argTypes.add(argType);
                    }
                }
                String string = StringUtils.collectionToCommaDelimitedString(argTypes);
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found on class [" + factoryClass.getName() + "]: " + (mbd.getFactoryBeanName() != null ? "factory bean '" + mbd.getFactoryBeanName() + "'; " : "") + "factory method '" + mbd.getFactoryMethodName() + "(" + string + ")'. Check that a method with the specified name " + (minNrOfArgs > 0 ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
            }
            if (Void.TYPE == factoryMethodToUse.getReturnType()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd.getFactoryMethodName() + "' on class [" + factoryClass.getName() + "]: needs to have a non-void return type!");
            }
            if (ambiguousFactoryMethods != null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found on class [" + factoryClass.getName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
            }
            if (explicitArgs == null && argsHolderToUse != null) {
                mbd.factoryMethodToIntrospect = factoryMethodToUse;
                argsHolderToUse.storeCache(mbd, factoryMethodToUse);
            }
        }
        bw.setBeanInstance(this.instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, @Nullable Object factoryBean, Method factoryMethod, Object[] args) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(() -> this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this.beanFactory, factoryBean, factoryMethod, args), this.beanFactory.getAccessControlContext());
            }
            return this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, (BeanFactory)this.beanFactory, factoryBean, factoryMethod, args);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean instantiation via factory method failed", ex);
        }
    }

    private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        int minNrOfArgs = cargs.getArgumentCount();
        for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
            ConstructorArgumentValues.ValueHolder valueHolder;
            int index = entry.getKey();
            if (index < 0) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
            }
            if (index + 1 > minNrOfArgs) {
                minNrOfArgs = index + 1;
            }
            if ((valueHolder = entry.getValue()).isConverted()) {
                resolvedValues.addIndexedArgumentValue(index, valueHolder);
                continue;
            }
            Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
            ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
            resolvedValueHolder.setSource(valueHolder);
            resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
        }
        for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
            if (valueHolder.isConverted()) {
                resolvedValues.addGenericArgumentValue(valueHolder);
                continue;
            }
            Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
            ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
            resolvedValueHolder.setSource(valueHolder);
            resolvedValues.addGenericArgumentValue(resolvedValueHolder);
        }
        return minNrOfArgs;
    }

    private ArgumentsHolder createArgumentArray(String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues, BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable, boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
        HashSet<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<ConstructorArgumentValues.ValueHolder>(paramTypes.length);
        LinkedHashSet<String> allAutowiredBeanNames = new LinkedHashSet<String>(paramTypes.length * 2);
        for (int paramIndex = 0; paramIndex < paramTypes.length; ++paramIndex) {
            Class<?> paramType = paramTypes[paramIndex];
            String paramName = paramNames != null ? paramNames[paramIndex] : "";
            ConstructorArgumentValues.ValueHolder valueHolder = null;
            if (!(resolvedValues == null || (valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders)) != null || autowiring && paramTypes.length != resolvedValues.getArgumentCount())) {
                valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
            }
            if (valueHolder != null) {
                Object convertedValue;
                usedValueHolders.add(valueHolder);
                Object originalValue = valueHolder.getValue();
                if (valueHolder.isConverted()) {
                    args.preparedArguments[paramIndex] = convertedValue = valueHolder.getConvertedValue();
                } else {
                    MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
                    try {
                        convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
                    }
                    catch (TypeMismatchException ex) {
                        throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
                    }
                    Object sourceHolder = valueHolder.getSource();
                    if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
                        Object sourceValue = ((ConstructorArgumentValues.ValueHolder)sourceHolder).getValue();
                        args.resolveNecessary = true;
                        args.preparedArguments[paramIndex] = sourceValue;
                    }
                }
                args.arguments[paramIndex] = convertedValue;
                args.rawArguments[paramIndex] = originalValue;
                continue;
            }
            MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
            if (!autowiring) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Ambiguous argument values for parameter of type [" + paramType.getName() + "] - did you specify the correct bean references as arguments?");
            }
            try {
                ConstructorDependencyDescriptor desc = new ConstructorDependencyDescriptor(methodParam, true);
                LinkedHashSet<String> autowiredBeanNames = new LinkedHashSet<String>(2);
                Object arg = this.resolveAutowiredArgument(desc, paramType, beanName, autowiredBeanNames, converter, fallback);
                if (arg != null) {
                    this.setShortcutIfPossible(desc, paramType, autowiredBeanNames);
                }
                allAutowiredBeanNames.addAll(autowiredBeanNames);
                args.rawArguments[paramIndex] = arg;
                args.arguments[paramIndex] = arg;
                args.preparedArguments[paramIndex] = desc;
                args.resolveNecessary = true;
                continue;
            }
            catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), ex);
            }
        }
        this.registerDependentBeans(executable, beanName, allAutowiredBeanNames);
        return args;
    }

    private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, Executable executable, Object[] argsToResolve) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        Class<?>[] paramTypes = executable.getParameterTypes();
        Object[] resolvedArgs = new Object[argsToResolve.length];
        for (int argIndex = 0; argIndex < argsToResolve.length; ++argIndex) {
            Object argValue = argsToResolve[argIndex];
            Class<?> paramType = paramTypes[argIndex];
            boolean convertNecessary = false;
            if (argValue instanceof ConstructorDependencyDescriptor) {
                ConstructorDependencyDescriptor descriptor = (ConstructorDependencyDescriptor)argValue;
                try {
                    argValue = this.resolveAutowiredArgument(descriptor, paramType, beanName, null, converter, true);
                }
                catch (BeansException ex) {
                    LinkedHashSet<String> autowiredBeanNames = null;
                    if (descriptor.hasShortcut()) {
                        descriptor.setShortcut(null);
                        autowiredBeanNames = new LinkedHashSet<String>(2);
                    }
                    this.logger.debug((Object)"Failed to resolve cached argument", (Throwable)ex);
                    argValue = this.resolveAutowiredArgument(descriptor, paramType, beanName, autowiredBeanNames, converter, true);
                    if (autowiredBeanNames != null && !descriptor.hasShortcut()) {
                        if (argValue != null) {
                            this.setShortcutIfPossible(descriptor, paramType, autowiredBeanNames);
                        }
                        this.registerDependentBeans(executable, beanName, autowiredBeanNames);
                    }
                }
            } else if (argValue instanceof BeanMetadataElement) {
                argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
                convertNecessary = true;
            } else if (argValue instanceof String) {
                argValue = this.beanFactory.evaluateBeanDefinitionString((String)argValue, mbd);
                convertNecessary = true;
            }
            if (convertNecessary) {
                MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
                try {
                    argValue = converter.convertIfNecessary(argValue, paramType, methodParam);
                }
                catch (TypeMismatchException ex) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
                }
            }
            resolvedArgs[argIndex] = argValue;
        }
        return resolvedArgs;
    }

    private Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        Class<?> userClass = ClassUtils.getUserClass(declaringClass);
        if (userClass != declaringClass) {
            try {
                return userClass.getDeclaredConstructor(constructor.getParameterTypes());
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return constructor;
    }

    @Nullable
    Object resolveAutowiredArgument(DependencyDescriptor descriptor, Class<?> paramType, String beanName, @Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {
        if (InjectionPoint.class.isAssignableFrom(paramType)) {
            InjectionPoint injectionPoint = (InjectionPoint)currentInjectionPoint.get();
            if (injectionPoint == null) {
                throw new IllegalStateException("No current InjectionPoint available for " + descriptor);
            }
            return injectionPoint;
        }
        try {
            return this.beanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        }
        catch (NoUniqueBeanDefinitionException ex) {
            throw ex;
        }
        catch (NoSuchBeanDefinitionException ex) {
            if (fallback) {
                if (paramType.isArray()) {
                    return Array.newInstance(paramType.getComponentType(), 0);
                }
                if (CollectionFactory.isApproximableCollectionType(paramType)) {
                    return CollectionFactory.createCollection(paramType, 0);
                }
                if (CollectionFactory.isApproximableMapType(paramType)) {
                    return CollectionFactory.createMap(paramType, 0);
                }
            }
            throw ex;
        }
    }

    private void setShortcutIfPossible(ConstructorDependencyDescriptor descriptor, Class<?> paramType, Set<String> autowiredBeanNames) {
        String autowiredBeanName;
        if (autowiredBeanNames.size() == 1 && this.beanFactory.containsBean(autowiredBeanName = autowiredBeanNames.iterator().next()) && this.beanFactory.isTypeMatch(autowiredBeanName, paramType)) {
            descriptor.setShortcut(autowiredBeanName);
        }
    }

    private void registerDependentBeans(Executable executable, String beanName, Set<String> autowiredBeanNames) {
        for (String autowiredBeanName : autowiredBeanNames) {
            this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
            if (!this.logger.isDebugEnabled()) continue;
            this.logger.debug((Object)("Autowiring by type from bean name '" + beanName + "' via " + (executable instanceof Constructor ? "constructor" : "factory method") + " to bean named '" + autowiredBeanName + "'"));
        }
    }

    static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
        InjectionPoint old = (InjectionPoint)currentInjectionPoint.get();
        if (injectionPoint != null) {
            currentInjectionPoint.set(injectionPoint);
        } else {
            currentInjectionPoint.remove();
        }
        return old;
    }

    private static class ConstructorDependencyDescriptor
    extends DependencyDescriptor {
        @Nullable
        private volatile String shortcut;

        public ConstructorDependencyDescriptor(MethodParameter methodParameter, boolean required) {
            super(methodParameter, required);
        }

        public void setShortcut(@Nullable String shortcut) {
            this.shortcut = shortcut;
        }

        public boolean hasShortcut() {
            return this.shortcut != null;
        }

        @Override
        public Object resolveShortcut(BeanFactory beanFactory) {
            String shortcut = this.shortcut;
            return shortcut != null ? beanFactory.getBean(shortcut, this.getDependencyType()) : null;
        }
    }

    private static class ConstructorPropertiesChecker {
        private ConstructorPropertiesChecker() {
        }

        @Nullable
        public static String[] evaluate(Constructor<?> candidate, int paramCount) {
            ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
            if (cp != null) {
                String[] names = cp.value();
                if (names.length != paramCount) {
                    throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
                }
                return names;
            }
            return null;
        }
    }

    private static class ArgumentsHolder {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }

        public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
            int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return Math.min(rawTypeDiffWeight, typeDiffWeight);
        }

        public int getAssignabilityWeight(Class<?>[] paramTypes) {
            int i2;
            for (i2 = 0; i2 < paramTypes.length; ++i2) {
                if (ClassUtils.isAssignableValue(paramTypes[i2], this.arguments[i2])) continue;
                return Integer.MAX_VALUE;
            }
            for (i2 = 0; i2 < paramTypes.length; ++i2) {
                if (ClassUtils.isAssignableValue(paramTypes[i2], this.rawArguments[i2])) continue;
                return 0x7FFFFDFF;
            }
            return 0x7FFFFBFF;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
            Object object = mbd.constructorArgumentLock;
            synchronized (object) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                } else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }
            }
        }
    }
}

