/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy
 *  org.springframework.cglib.core.CodeGenerationException
 *  org.springframework.cglib.core.GeneratorStrategy
 *  org.springframework.cglib.core.NamingPolicy
 *  org.springframework.cglib.core.SpringNamingPolicy
 *  org.springframework.cglib.proxy.Callback
 *  org.springframework.cglib.proxy.CallbackFilter
 *  org.springframework.cglib.proxy.Dispatcher
 *  org.springframework.cglib.proxy.Enhancer
 *  org.springframework.cglib.proxy.Factory
 *  org.springframework.cglib.proxy.MethodInterceptor
 *  org.springframework.cglib.proxy.MethodProxy
 *  org.springframework.cglib.proxy.NoOp
 *  org.springframework.core.KotlinDetector
 *  org.springframework.core.SmartClassLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.GeneratorStrategy;
import org.springframework.cglib.core.NamingPolicy;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.core.KotlinDetector;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

class CglibAopProxy
implements AopProxy,
Serializable {
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;
    protected static final Log logger = LogFactory.getLog(CglibAopProxy.class);
    private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap();
    protected final AdvisedSupport advised;
    @Nullable
    protected Object[] constructorArgs;
    @Nullable
    protected Class<?>[] constructorArgTypes;
    private final transient AdvisedDispatcher advisedDispatcher;
    private transient Map<Method, Integer> fixedInterceptorMap = Collections.emptyMap();
    private transient int fixedInterceptorOffset;

    public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull((Object)config, (String)"AdvisedSupport must not be null");
        if (config.getAdvisorCount() == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
            throw new AopConfigException("No advisors and no TargetSource specified");
        }
        this.advised = config;
        this.advisedDispatcher = new AdvisedDispatcher(this.advised);
    }

    public void setConstructorArguments(@Nullable Object[] constructorArgs, @Nullable Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length + ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }

    @Override
    public Object getProxy() {
        return this.getProxy(null);
    }

    @Override
    public Object getProxy(@Nullable ClassLoader classLoader) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Creating CGLIB proxy: " + this.advised.getTargetSource()));
        }
        try {
            Class<?> rootClass = this.advised.getTargetClass();
            Assert.state((rootClass != null ? 1 : 0) != 0, (String)"Target class must be available for creating a CGLIB proxy");
            Class<?> proxySuperClass = rootClass;
            if (rootClass.getName().contains("$$")) {
                Class<?>[] additionalInterfaces;
                proxySuperClass = rootClass.getSuperclass();
                for (Class<?> additionalInterface : additionalInterfaces = rootClass.getInterfaces()) {
                    this.advised.addInterface(additionalInterface);
                }
            }
            this.validateClassIfNecessary(proxySuperClass, classLoader);
            Enhancer enhancer = this.createEnhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
                if (classLoader instanceof SmartClassLoader && ((SmartClassLoader)classLoader).isClassReloadable(proxySuperClass)) {
                    enhancer.setUseCache(false);
                }
            }
            enhancer.setSuperclass(proxySuperClass);
            enhancer.setInterfaces((Class[])AopProxyUtils.completeProxiedInterfaces(this.advised));
            enhancer.setNamingPolicy((NamingPolicy)SpringNamingPolicy.INSTANCE);
            enhancer.setStrategy((GeneratorStrategy)new ClassLoaderAwareGeneratorStrategy(classLoader));
            Callback[] callbacks = this.getCallbacks(rootClass);
            Class[] types = new Class[callbacks.length];
            for (int x = 0; x < types.length; ++x) {
                types[x] = callbacks[x].getClass();
            }
            enhancer.setCallbackFilter((CallbackFilter)new ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
            enhancer.setCallbackTypes(types);
            return this.createProxyClassAndInstance(enhancer, callbacks);
        }
        catch (IllegalArgumentException | CodeGenerationException ex) {
            throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() + ": Common causes of this problem include using a final class or a non-visible class", ex);
        }
        catch (Throwable ex) {
            throw new AopConfigException("Unexpected AOP exception", ex);
        }
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        return this.constructorArgs != null && this.constructorArgTypes != null ? enhancer.create((Class[])this.constructorArgTypes, this.constructorArgs) : enhancer.create();
    }

    protected Enhancer createEnhancer() {
        return new Enhancer();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void validateClassIfNecessary(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader) {
        if (!this.advised.isOptimize() && logger.isInfoEnabled()) {
            Map<Class<?>, Boolean> map = validatedClasses;
            synchronized (map) {
                if (!validatedClasses.containsKey(proxySuperClass)) {
                    this.doValidateClass(proxySuperClass, proxyClassLoader, ClassUtils.getAllInterfacesForClassAsSet(proxySuperClass));
                    validatedClasses.put(proxySuperClass, Boolean.TRUE);
                }
            }
        }
    }

    private void doValidateClass(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader, Set<Class<?>> ifcs) {
        if (proxySuperClass != Object.class) {
            Method[] methods;
            for (Method method : methods = proxySuperClass.getDeclaredMethods()) {
                int mod = method.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isPrivate(mod)) continue;
                if (Modifier.isFinal(mod)) {
                    if (logger.isInfoEnabled() && CglibAopProxy.implementsInterface(method, ifcs)) {
                        logger.info((Object)("Unable to proxy interface-implementing method [" + method + "] because it is marked as final: Consider using interface-based JDK proxies instead!"));
                    }
                    if (!logger.isDebugEnabled()) continue;
                    logger.debug((Object)("Final method [" + method + "] cannot get proxied via CGLIB: Calls to this method will NOT be routed to the target instance and might lead to NPEs against uninitialized fields in the proxy instance."));
                    continue;
                }
                if (!logger.isDebugEnabled() || Modifier.isPublic(mod) || Modifier.isProtected(mod) || proxyClassLoader == null || proxySuperClass.getClassLoader() == proxyClassLoader) continue;
                logger.debug((Object)("Method [" + method + "] is package-visible across different ClassLoaders and cannot get proxied via CGLIB: Declare this method as public or protected if you need to support invocations through the proxy."));
            }
            this.doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader, ifcs);
        }
    }

    private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
        Callback[] callbacks;
        boolean isFrozen = this.advised.isFrozen();
        boolean exposeProxy = this.advised.isExposeProxy();
        boolean isStatic = this.advised.getTargetSource().isStatic();
        DynamicAdvisedInterceptor aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
        Serializable targetInterceptor = exposeProxy ? (isStatic ? new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) : new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource())) : (isStatic ? new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) : new DynamicUnadvisedInterceptor(this.advised.getTargetSource()));
        Serializable targetDispatcher = isStatic ? new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();
        Callback[] mainCallbacks = new Callback[]{aopInterceptor, targetInterceptor, new SerializableNoOp(), targetDispatcher, this.advisedDispatcher, new EqualsInterceptor(this.advised), new HashCodeInterceptor(this.advised)};
        if (isStatic && isFrozen) {
            Method[] methods = rootClass.getMethods();
            Callback[] fixedCallbacks = new Callback[methods.length];
            this.fixedInterceptorMap = CollectionUtils.newHashMap((int)methods.length);
            for (int x = 0; x < methods.length; ++x) {
                Method method = methods[x];
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, rootClass);
                fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
                this.fixedInterceptorMap.put(method, x);
            }
            callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
            System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
            System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
            this.fixedInterceptorOffset = mainCallbacks.length;
        } else {
            callbacks = mainCallbacks;
        }
        return callbacks;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof CglibAopProxy && AopProxyUtils.equalsInProxy(this.advised, ((CglibAopProxy)other).advised);
    }

    public int hashCode() {
        return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
    }

    private static boolean implementsInterface(Method method, Set<Class<?>> ifcs) {
        for (Class<?> ifc : ifcs) {
            if (!ClassUtils.hasMethod(ifc, (Method)method)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    private static Object invokeMethod(@Nullable Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        try {
            return methodProxy.invoke(target, args);
        }
        catch (CodeGenerationException ex) {
            CglibMethodInvocation.logFastClassGenerationFailure(method);
            return AopUtils.invokeJoinpointUsingReflection(target, method, args);
        }
    }

    @Nullable
    private static Object processReturnType(Object proxy, @Nullable Object target, Method method, @Nullable Object returnValue) {
        if (returnValue != null && returnValue == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            returnValue = proxy;
        }
        Class<?> returnType = method.getReturnType();
        if (returnValue == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
        }
        return returnValue;
    }

    private static class ProxyCallbackFilter
    implements CallbackFilter {
        private final AdvisedSupport advised;
        private final Map<Method, Integer> fixedInterceptorMap;
        private final int fixedInterceptorOffset;

        public ProxyCallbackFilter(AdvisedSupport advised, Map<Method, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }

        public int accept(Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                logger.trace((Object)"Found finalize() method - using NO_OVERRIDE");
                return 2;
            }
            if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Method is declared on Advised interface: " + method));
                }
                return 4;
            }
            if (AopUtils.isEqualsMethod(method)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Found 'equals' method: " + method));
                }
                return 5;
            }
            if (AopUtils.isHashCodeMethod(method)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Found 'hashCode' method: " + method));
                }
                return 6;
            }
            Class<?> targetClass = this.advised.getTargetClass();
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            boolean haveAdvice = !chain.isEmpty();
            boolean isFrozen = this.advised.isFrozen();
            boolean exposeProxy = this.advised.isExposeProxy();
            boolean isStatic = this.advised.getTargetSource().isStatic();
            if (haveAdvice || !isFrozen) {
                if (exposeProxy) {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Must expose proxy on advised method: " + method));
                    }
                    return 0;
                }
                if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(method)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace((Object)("Method has advice and optimizations are enabled: " + method));
                    }
                    int index = this.fixedInterceptorMap.get(method);
                    return index + this.fixedInterceptorOffset;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Unable to apply any optimizations to advised method: " + method));
                }
                return 0;
            }
            if (exposeProxy || !isStatic) {
                return 1;
            }
            Class<?> returnType = method.getReturnType();
            if (targetClass != null && returnType.isAssignableFrom(targetClass)) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Method return type is assignable from target type and may therefore return 'this' - using INVOKE_TARGET: " + method));
                }
                return 1;
            }
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Method return type ensures 'this' cannot be returned - using DISPATCH_TARGET: " + method));
            }
            return 3;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ProxyCallbackFilter)) {
                return false;
            }
            ProxyCallbackFilter otherCallbackFilter = (ProxyCallbackFilter)other;
            AdvisedSupport otherAdvised = otherCallbackFilter.advised;
            if (this.advised.isFrozen() != otherAdvised.isFrozen()) {
                return false;
            }
            if (this.advised.isExposeProxy() != otherAdvised.isExposeProxy()) {
                return false;
            }
            if (this.advised.isOpaque() != otherAdvised.isOpaque()) {
                return false;
            }
            if (this.advised.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
                return false;
            }
            if (!AopProxyUtils.equalsProxiedInterfaces(this.advised, otherAdvised)) {
                return false;
            }
            if (this.advised.getAdvisorCount() != otherAdvised.getAdvisorCount()) {
                return false;
            }
            Advisor[] thisAdvisors = this.advised.getAdvisors();
            Advisor[] thatAdvisors = otherAdvised.getAdvisors();
            for (int i = 0; i < thisAdvisors.length; ++i) {
                Advisor thisAdvisor = thisAdvisors[i];
                Advisor thatAdvisor = thatAdvisors[i];
                if (!ProxyCallbackFilter.equalsAdviceClasses(thisAdvisor, thatAdvisor)) {
                    return false;
                }
                if (ProxyCallbackFilter.equalsPointcuts(thisAdvisor, thatAdvisor)) continue;
                return false;
            }
            return true;
        }

        private static boolean equalsAdviceClasses(Advisor a, Advisor b) {
            return a.getAdvice().getClass() == b.getAdvice().getClass();
        }

        private static boolean equalsPointcuts(Advisor a, Advisor b) {
            return !(a instanceof PointcutAdvisor) || b instanceof PointcutAdvisor && ObjectUtils.nullSafeEquals((Object)((PointcutAdvisor)a).getPointcut(), (Object)((PointcutAdvisor)b).getPointcut());
        }

        public int hashCode() {
            Advisor[] advisors;
            int hashCode = 0;
            for (Advisor advisor : advisors = this.advised.getAdvisors()) {
                Advice advice = advisor.getAdvice();
                hashCode = 13 * hashCode + advice.getClass().hashCode();
            }
            return hashCode;
        }
    }

    private static class CglibMethodInvocation
    extends ReflectiveMethodInvocation {
        @Nullable
        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method, Object[] arguments, @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
            super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
            this.methodProxy = CglibMethodInvocation.isMethodProxyCompatible(method) ? methodProxy : null;
        }

        @Override
        @Nullable
        public Object proceed() throws Throwable {
            try {
                return super.proceed();
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                if (ReflectionUtils.declaresException((Method)this.getMethod(), ex.getClass()) || KotlinDetector.isKotlinType(this.getMethod().getDeclaringClass())) {
                    throw ex;
                }
                throw new UndeclaredThrowableException(ex);
            }
        }

        @Override
        protected Object invokeJoinpoint() throws Throwable {
            if (this.methodProxy != null) {
                try {
                    return this.methodProxy.invoke(this.target, this.arguments);
                }
                catch (CodeGenerationException ex) {
                    CglibMethodInvocation.logFastClassGenerationFailure(this.method);
                }
            }
            return super.invokeJoinpoint();
        }

        static boolean isMethodProxyCompatible(Method method) {
            return Modifier.isPublic(method.getModifiers()) && method.getDeclaringClass() != Object.class && !AopUtils.isEqualsMethod(method) && !AopUtils.isHashCodeMethod(method) && !AopUtils.isToStringMethod(method);
        }

        static void logFastClassGenerationFailure(Method method) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to generate CGLIB fast class for method: " + method));
            }
        }
    }

    private static class DynamicAdvisedInterceptor
    implements MethodInterceptor,
    Serializable {
        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            boolean setProxyContext = false;
            Object target = null;
            TargetSource targetSource = this.advised.getTargetSource();
            try {
                Object retVal;
                Class<?> targetClass;
                List<Object> chain;
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }
                if ((chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass = (target = targetSource.getTarget()) != null ? target.getClass() : null)).isEmpty() && CglibMethodInvocation.isMethodProxyCompatible(method)) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = CglibAopProxy.invokeMethod(target, method, argsToUse, methodProxy);
                } else {
                    retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
                }
                Object object = retVal = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                return object;
            }
            finally {
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
            }
        }

        public boolean equals(@Nullable Object other) {
            return this == other || other instanceof DynamicAdvisedInterceptor && this.advised.equals(((DynamicAdvisedInterceptor)other).advised);
        }

        public int hashCode() {
            return this.advised.hashCode();
        }
    }

    private static class FixedChainStaticTargetInterceptor
    implements MethodInterceptor,
    Serializable {
        private final List<Object> adviceChain;
        @Nullable
        private final Object target;
        @Nullable
        private final Class<?> targetClass;

        public FixedChainStaticTargetInterceptor(List<Object> adviceChain, @Nullable Object target, @Nullable Class<?> targetClass) {
            this.adviceChain = adviceChain;
            this.target = target;
            this.targetClass = targetClass;
        }

        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            CglibMethodInvocation invocation = new CglibMethodInvocation(proxy, this.target, method, args, this.targetClass, this.adviceChain, methodProxy);
            Object retVal = invocation.proceed();
            retVal = CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
            return retVal;
        }
    }

    private static class HashCodeInterceptor
    implements MethodInterceptor,
    Serializable {
        private final AdvisedSupport advised;

        public HashCodeInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            return CglibAopProxy.class.hashCode() * 13 + this.advised.getTargetSource().hashCode();
        }
    }

    private static class EqualsInterceptor
    implements MethodInterceptor,
    Serializable {
        private final AdvisedSupport advised;

        public EqualsInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            Object other = args[0];
            if (proxy == other) {
                return true;
            }
            if (other instanceof Factory) {
                Callback callback = ((Factory)other).getCallback(5);
                if (!(callback instanceof EqualsInterceptor)) {
                    return false;
                }
                AdvisedSupport otherAdvised = ((EqualsInterceptor)callback).advised;
                return AopProxyUtils.equalsInProxy(this.advised, otherAdvised);
            }
            return false;
        }
    }

    private static class AdvisedDispatcher
    implements Dispatcher,
    Serializable {
        private final AdvisedSupport advised;

        public AdvisedDispatcher(AdvisedSupport advised) {
            this.advised = advised;
        }

        public Object loadObject() {
            return this.advised;
        }
    }

    private static class StaticDispatcher
    implements Dispatcher,
    Serializable {
        @Nullable
        private final Object target;

        public StaticDispatcher(@Nullable Object target) {
            this.target = target;
        }

        @Nullable
        public Object loadObject() {
            return this.target;
        }
    }

    private static class DynamicUnadvisedExposedInterceptor
    implements MethodInterceptor,
    Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedExposedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            Object target = this.targetSource.getTarget();
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = CglibAopProxy.invokeMethod(target, method, args, methodProxy);
                Object object = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                return object;
            }
            finally {
                AopContext.setCurrentProxy(oldProxy);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
            }
        }
    }

    private static class DynamicUnadvisedInterceptor
    implements MethodInterceptor,
    Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.targetSource.getTarget();
            try {
                Object retVal = CglibAopProxy.invokeMethod(target, method, args, methodProxy);
                Object object = CglibAopProxy.processReturnType(proxy, target, method, retVal);
                return object;
            }
            finally {
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
            }
        }
    }

    private static class StaticUnadvisedExposedInterceptor
    implements MethodInterceptor,
    Serializable {
        @Nullable
        private final Object target;

        public StaticUnadvisedExposedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = CglibAopProxy.invokeMethod(this.target, method, args, methodProxy);
                Object object = CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
                return object;
            }
            finally {
                AopContext.setCurrentProxy(oldProxy);
            }
        }
    }

    private static class StaticUnadvisedInterceptor
    implements MethodInterceptor,
    Serializable {
        @Nullable
        private final Object target;

        public StaticUnadvisedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal = CglibAopProxy.invokeMethod(this.target, method, args, methodProxy);
            return CglibAopProxy.processReturnType(proxy, this.target, method, retVal);
        }
    }

    public static class SerializableNoOp
    implements NoOp,
    Serializable {
    }
}

