/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.NamingStrategy
 *  net.bytebuddy.NamingStrategy$SuffixingRandom
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver$ForFixedValue
 *  net.bytebuddy.TypeCache$SimpleKey
 *  net.bytebuddy.description.modifier.ModifierContributor$ForField
 *  net.bytebuddy.description.modifier.Visibility
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
 *  net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy$Default
 *  net.bytebuddy.implementation.Implementation
 */
package org.hibernate.bytecode.internal.bytebuddy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.internal.bytebuddy.ByteBuddyState;
import org.hibernate.bytecode.internal.bytebuddy.PassThroughInterceptor;
import org.hibernate.bytecode.spi.BasicProxyFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.proxy.ProxyConfiguration;

public class BasicProxyFactoryImpl
implements BasicProxyFactory {
    private static final Class[] NO_INTERFACES = new Class[0];
    private static final String PROXY_NAMING_SUFFIX = Environment.useLegacyProxyClassnames() ? "HibernateBasicProxy$" : "HibernateBasicProxy";
    private final Class proxyClass;
    private final ProxyConfiguration.Interceptor interceptor;
    private final Constructor proxyClassConstructor;

    public BasicProxyFactoryImpl(Class superClass, Class interfaceClass, ByteBuddyState byteBuddyState) {
        if (superClass == null && interfaceClass == null) {
            throw new AssertionFailure("attempting to build proxy without any superclass or interfaces");
        }
        if (superClass != null && interfaceClass != null) {
            throw new AssertionFailure("Ambiguous call: we assume invocation with EITHER a superClass OR an interfaceClass");
        }
        Class superClassOrMainInterface = superClass != null ? superClass : interfaceClass;
        TypeCache.SimpleKey cacheKey = new TypeCache.SimpleKey(superClassOrMainInterface, new Class[0]);
        this.proxyClass = byteBuddyState.loadBasicProxy(superClassOrMainInterface, cacheKey, byteBuddy -> {
            Type[] typeArray;
            DynamicType.Builder builder = byteBuddy.with((NamingStrategy)new NamingStrategy.SuffixingRandom(PROXY_NAMING_SUFFIX, (NamingStrategy.SuffixingRandom.BaseNameResolver)new NamingStrategy.SuffixingRandom.BaseNameResolver.ForFixedValue(superClassOrMainInterface.getName()))).subclass(superClass == null ? Object.class : superClass, (ConstructorStrategy)ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR);
            if (interfaceClass == null) {
                typeArray = NO_INTERFACES;
            } else {
                Class[] classArray = new Class[1];
                typeArray = classArray;
                classArray[0] = interfaceClass;
            }
            return builder.implement(typeArray).defineField("$$_hibernate_interceptor", ProxyConfiguration.Interceptor.class, new ModifierContributor.ForField[]{Visibility.PRIVATE}).method(byteBuddyState.getProxyDefinitionHelpers().getVirtualNotFinalizerFilter()).intercept((Implementation)byteBuddyState.getProxyDefinitionHelpers().getDelegateToInterceptorDispatcherMethodDelegation()).implement(new Type[]{ProxyConfiguration.class}).intercept((Implementation)byteBuddyState.getProxyDefinitionHelpers().getInterceptorFieldAccessor());
        });
        this.interceptor = new PassThroughInterceptor(this.proxyClass.getName());
        try {
            this.proxyClassConstructor = this.proxyClass.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionFailure("Could not access default constructor from newly generated basic proxy");
        }
    }

    @Override
    public Object getProxy() {
        try {
            ProxyConfiguration proxy = (ProxyConfiguration)this.proxyClassConstructor.newInstance(new Object[0]);
            proxy.$$_hibernate_set_interceptor(this.interceptor);
            return proxy;
        }
        catch (Throwable t) {
            throw new HibernateException("Unable to instantiate proxy instance", t);
        }
    }

    public boolean isInstance(Object object) {
        return this.proxyClass.isInstance(object);
    }
}

