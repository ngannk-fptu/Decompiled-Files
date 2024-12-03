/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.ByteBuddy
 *  net.bytebuddy.ClassFileVersion
 *  net.bytebuddy.TypeCache
 *  net.bytebuddy.TypeCache$SimpleKey
 *  net.bytebuddy.TypeCache$Sort
 *  net.bytebuddy.asm.AsmVisitorWrapper
 *  net.bytebuddy.asm.AsmVisitorWrapper$ForDeclaredMethods
 *  net.bytebuddy.asm.MemberSubstitution
 *  net.bytebuddy.description.method.MethodDescription
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.dynamic.DynamicType$Unloaded
 *  net.bytebuddy.dynamic.scaffold.TypeValidation
 *  net.bytebuddy.implementation.FieldAccessor
 *  net.bytebuddy.implementation.FieldAccessor$PropertyConfigurable
 *  net.bytebuddy.implementation.MethodDelegation
 *  net.bytebuddy.implementation.bytecode.assign.Assigner
 *  net.bytebuddy.implementation.bytecode.assign.Assigner$Typing
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatchers
 *  net.bytebuddy.pool.TypePool
 */
package org.hibernate.bytecode.internal.bytebuddy;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Function;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.TypeCache;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.MemberSubstitution;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.internal.bytebuddy.HibernateMethodLookupDispatcher;
import org.hibernate.bytecode.spi.ClassLoadingStrategyHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.proxy.ProxyConfiguration;

public final class ByteBuddyState {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ByteBuddyState.class);
    private static final boolean DEBUG = false;
    private final ByteBuddy byteBuddy;
    private static final ProxyDefinitionHelpers proxyDefinitionHelpers = new ProxyDefinitionHelpers();
    private final ClassRewriter classRewriter;
    private final TypeCache<TypeCache.SimpleKey> proxyCache;
    private final TypeCache<TypeCache.SimpleKey> basicProxyCache;

    ByteBuddyState() {
        this(ClassFileVersion.ofThisVm((ClassFileVersion)ClassFileVersion.JAVA_V8));
    }

    ByteBuddyState(ClassFileVersion classFileVersion) {
        this.byteBuddy = new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED);
        this.proxyCache = new TypeCache(TypeCache.Sort.WEAK);
        this.basicProxyCache = new TypeCache(TypeCache.Sort.WEAK);
        this.classRewriter = System.getSecurityManager() != null ? new SecurityManagerClassRewriter() : new StandardClassRewriter();
    }

    public Class<?> loadProxy(Class<?> referenceClass, TypeCache.SimpleKey cacheKey, Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return this.load(referenceClass, this.proxyCache, cacheKey, makeProxyFunction);
    }

    Class<?> loadBasicProxy(Class<?> referenceClass, TypeCache.SimpleKey cacheKey, Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return this.load(referenceClass, this.basicProxyCache, cacheKey, makeProxyFunction);
    }

    public Class<?> load(Class<?> referenceClass, Function<ByteBuddy, DynamicType.Builder<?>> makeClassFunction) {
        return this.make(makeClassFunction.apply(this.byteBuddy)).load(referenceClass.getClassLoader(), ClassLoadingStrategyHelper.resolveClassLoadingStrategy(referenceClass)).getLoaded();
    }

    public byte[] rewrite(TypePool typePool, String className, Function<ByteBuddy, DynamicType.Builder<?>> rewriteClassFunction) {
        DynamicType.Builder<?> builder = rewriteClassFunction.apply(this.byteBuddy);
        if (builder == null) {
            return null;
        }
        return this.make(typePool, builder).getBytes();
    }

    public ProxyDefinitionHelpers getProxyDefinitionHelpers() {
        return proxyDefinitionHelpers;
    }

    void clearState() {
        this.proxyCache.clear();
        this.basicProxyCache.clear();
    }

    private Class<?> load(Class<?> referenceClass, TypeCache<TypeCache.SimpleKey> cache, TypeCache.SimpleKey cacheKey, Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return cache.findOrInsert(referenceClass.getClassLoader(), (Object)cacheKey, () -> this.make((DynamicType.Builder)makeProxyFunction.apply(this.byteBuddy)).load(referenceClass.getClassLoader(), ClassLoadingStrategyHelper.resolveClassLoadingStrategy(referenceClass)).getLoaded(), cache);
    }

    public DynamicType.Unloaded<?> make(Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return this.make(makeProxyFunction.apply(this.byteBuddy));
    }

    public DynamicType.Unloaded<?> make(TypePool typePool, Function<ByteBuddy, DynamicType.Builder<?>> makeProxyFunction) {
        return this.make(typePool, makeProxyFunction.apply(this.byteBuddy));
    }

    private DynamicType.Unloaded<?> make(DynamicType.Builder<?> builder) {
        return this.make(null, builder);
    }

    private DynamicType.Unloaded<?> make(TypePool typePool, DynamicType.Builder<?> builder) {
        this.classRewriter.installReflectionMethodVisitors(builder);
        DynamicType.Unloaded unloadedClass = typePool != null ? builder.make(typePool) : builder.make();
        this.classRewriter.registerAuthorizedClass(unloadedClass);
        return unloadedClass;
    }

    private static class StandardClassRewriter
    implements ClassRewriter {
        private StandardClassRewriter() {
        }

        @Override
        public void installReflectionMethodVisitors(DynamicType.Builder<?> builder) {
        }

        @Override
        public void registerAuthorizedClass(DynamicType.Unloaded<?> unloadedClass) {
        }
    }

    private static class SecurityManagerClassRewriter
    implements ClassRewriter {
        private final AsmVisitorWrapper.ForDeclaredMethods getDeclaredMethodMemberSubstitution = SecurityManagerClassRewriter.getDeclaredMethodMemberSubstitution();
        private final AsmVisitorWrapper.ForDeclaredMethods getMethodMemberSubstitution = SecurityManagerClassRewriter.getMethodMemberSubstitution();

        private SecurityManagerClassRewriter() {
        }

        @Override
        public void installReflectionMethodVisitors(DynamicType.Builder<?> builder) {
            builder = builder.visit((AsmVisitorWrapper)this.getDeclaredMethodMemberSubstitution);
            builder = builder.visit((AsmVisitorWrapper)this.getMethodMemberSubstitution);
        }

        @Override
        public void registerAuthorizedClass(DynamicType.Unloaded<?> unloadedClass) {
            HibernateMethodLookupDispatcher.registerAuthorizedClass(unloadedClass.getTypeDescription().getName());
        }

        private static AsmVisitorWrapper.ForDeclaredMethods getDeclaredMethodMemberSubstitution() {
            return MemberSubstitution.relaxed().method((ElementMatcher)ElementMatchers.is((Method)AccessController.doPrivileged(new GetDeclaredMethodAction((Class)Class.class, "getDeclaredMethod", new Class[]{String.class, Class[].class})))).replaceWith(AccessController.doPrivileged(new GetDeclaredMethodAction((Class)HibernateMethodLookupDispatcher.class, "getDeclaredMethod", new Class[]{Class.class, String.class, Class[].class}))).on((ElementMatcher)ElementMatchers.isTypeInitializer());
        }

        private static AsmVisitorWrapper.ForDeclaredMethods getMethodMemberSubstitution() {
            return MemberSubstitution.relaxed().method((ElementMatcher)ElementMatchers.is((Method)AccessController.doPrivileged(new GetDeclaredMethodAction((Class)Class.class, "getMethod", new Class[]{String.class, Class[].class})))).replaceWith(AccessController.doPrivileged(new GetDeclaredMethodAction((Class)HibernateMethodLookupDispatcher.class, "getMethod", new Class[]{Class.class, String.class, Class[].class}))).on((ElementMatcher)ElementMatchers.isTypeInitializer());
        }
    }

    private static interface ClassRewriter {
        public void installReflectionMethodVisitors(DynamicType.Builder<?> var1);

        public void registerAuthorizedClass(DynamicType.Unloaded<?> var1);
    }

    public static class ProxyDefinitionHelpers {
        private final ElementMatcher<? super MethodDescription> groovyGetMetaClassFilter = ElementMatchers.isSynthetic().and((ElementMatcher)ElementMatchers.named((String)"getMetaClass").and((ElementMatcher)ElementMatchers.returns(td -> "groovy.lang.MetaClass".equals(td.getName()))));
        private final ElementMatcher<? super MethodDescription> virtualNotFinalizerFilter = ElementMatchers.isVirtual().and((ElementMatcher)ElementMatchers.not((ElementMatcher)ElementMatchers.isFinalizer()));
        private final ElementMatcher<? super MethodDescription> proxyNonInterceptedMethodFilter = ElementMatchers.nameStartsWith((String)"$$_hibernate_").and((ElementMatcher)ElementMatchers.isVirtual()).and((ElementMatcher)ElementMatchers.not((ElementMatcher)ElementMatchers.nameStartsWith((String)"$$_hibernate_read_"))).and((ElementMatcher)ElementMatchers.not((ElementMatcher)ElementMatchers.nameStartsWith((String)"$$_hibernate_write_")));
        private final MethodDelegation delegateToInterceptorDispatcherMethodDelegation;
        private final FieldAccessor.PropertyConfigurable interceptorFieldAccessor;

        private ProxyDefinitionHelpers() {
            PrivilegedAction<MethodDelegation> delegateToInterceptorDispatcherMethodDelegationPrivilegedAction = new PrivilegedAction<MethodDelegation>(){

                @Override
                public MethodDelegation run() {
                    return MethodDelegation.to(ProxyConfiguration.InterceptorDispatcher.class);
                }
            };
            this.delegateToInterceptorDispatcherMethodDelegation = System.getSecurityManager() != null ? AccessController.doPrivileged(delegateToInterceptorDispatcherMethodDelegationPrivilegedAction) : (MethodDelegation)delegateToInterceptorDispatcherMethodDelegationPrivilegedAction.run();
            PrivilegedAction<FieldAccessor.PropertyConfigurable> interceptorFieldAccessorPrivilegedAction = new PrivilegedAction<FieldAccessor.PropertyConfigurable>(){

                @Override
                public FieldAccessor.PropertyConfigurable run() {
                    return FieldAccessor.ofField((String)"$$_hibernate_interceptor").withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);
                }
            };
            this.interceptorFieldAccessor = System.getSecurityManager() != null ? AccessController.doPrivileged(interceptorFieldAccessorPrivilegedAction) : (FieldAccessor.PropertyConfigurable)interceptorFieldAccessorPrivilegedAction.run();
        }

        public ElementMatcher<? super MethodDescription> getGroovyGetMetaClassFilter() {
            return this.groovyGetMetaClassFilter;
        }

        public ElementMatcher<? super MethodDescription> getVirtualNotFinalizerFilter() {
            return this.virtualNotFinalizerFilter;
        }

        public ElementMatcher<? super MethodDescription> getProxyNonInterceptedMethodFilter() {
            return this.proxyNonInterceptedMethodFilter;
        }

        public MethodDelegation getDelegateToInterceptorDispatcherMethodDelegation() {
            return this.delegateToInterceptorDispatcherMethodDelegation;
        }

        public FieldAccessor.PropertyConfigurable getInterceptorFieldAccessor() {
            return this.interceptorFieldAccessor;
        }
    }

    private static class GetDeclaredMethodAction
    implements PrivilegedAction<Method> {
        private final Class<?> clazz;
        private final String methodName;
        private final Class<?>[] parameterTypes;

        private GetDeclaredMethodAction(Class<?> clazz, String methodName, Class<?> ... parameterTypes) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public Method run() {
            try {
                Method method = this.clazz.getDeclaredMethod(this.methodName, this.parameterTypes);
                return method;
            }
            catch (NoSuchMethodException e) {
                throw new HibernateException("Unable to prepare getDeclaredMethod()/getMethod() substitution", e);
            }
        }
    }
}

