/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.ByteBuddy
 *  net.bytebuddy.NamingStrategy
 *  net.bytebuddy.NamingStrategy$SuffixingRandom
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver
 *  net.bytebuddy.NamingStrategy$SuffixingRandom$BaseNameResolver$ForFixedValue
 *  net.bytebuddy.TypeCache$SimpleKey
 *  net.bytebuddy.description.modifier.ModifierContributor$ForField
 *  net.bytebuddy.description.modifier.Visibility
 *  net.bytebuddy.description.type.TypeDefinition
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.description.type.TypeDescription$ForLoadedType
 *  net.bytebuddy.description.type.TypeList$Generic$ForLoadedTypes
 *  net.bytebuddy.dynamic.DynamicType$Builder
 *  net.bytebuddy.dynamic.DynamicType$Unloaded
 *  net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy
 *  net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy$Default
 *  net.bytebuddy.implementation.Implementation
 *  net.bytebuddy.implementation.SuperMethodCall
 *  net.bytebuddy.pool.TypePool
 */
package org.hibernate.proxy.pojo.bytebuddy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.Function;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.pool.TypePool;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.internal.bytebuddy.ByteBuddyState;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyConfiguration;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.hibernate.proxy.pojo.bytebuddy.SerializableProxy;

public class ByteBuddyProxyHelper
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ByteBuddyProxyHelper.class);
    private static final String PROXY_NAMING_SUFFIX = Environment.useLegacyProxyClassnames() ? "HibernateProxy$" : "HibernateProxy";
    private final ByteBuddyState byteBuddyState;

    public ByteBuddyProxyHelper(ByteBuddyState byteBuddyState) {
        this.byteBuddyState = byteBuddyState;
    }

    public Class buildProxy(Class persistentClass, Class[] interfaces) {
        HashSet<Class> key = new HashSet<Class>();
        if (interfaces.length == 1) {
            key.add(persistentClass);
        }
        key.addAll(Arrays.asList(interfaces));
        return this.byteBuddyState.loadProxy(persistentClass, new TypeCache.SimpleKey(key), this.proxyBuilder((TypeDefinition)TypeDescription.ForLoadedType.of((Class)persistentClass), (Collection<? extends TypeDefinition>)new TypeList.Generic.ForLoadedTypes((Type[])interfaces)));
    }

    @Deprecated
    public DynamicType.Unloaded<?> buildUnloadedProxy(Class<?> persistentClass, Class<?>[] interfaces) {
        return this.byteBuddyState.make(this.proxyBuilder((TypeDefinition)TypeDescription.ForLoadedType.of(persistentClass), (Collection<? extends TypeDefinition>)new TypeList.Generic.ForLoadedTypes((Type[])interfaces)));
    }

    public DynamicType.Unloaded<?> buildUnloadedProxy(TypePool typePool, TypeDefinition persistentClass, Collection<? extends TypeDefinition> interfaces) {
        return this.byteBuddyState.make(typePool, this.proxyBuilder(persistentClass, interfaces));
    }

    private Function<ByteBuddy, DynamicType.Builder<?>> proxyBuilder(TypeDefinition persistentClass, Collection<? extends TypeDefinition> interfaces) {
        ByteBuddyState.ProxyDefinitionHelpers helpers = this.byteBuddyState.getProxyDefinitionHelpers();
        return byteBuddy -> byteBuddy.ignore(helpers.getGroovyGetMetaClassFilter()).with((NamingStrategy)new NamingStrategy.SuffixingRandom(PROXY_NAMING_SUFFIX, (NamingStrategy.SuffixingRandom.BaseNameResolver)new NamingStrategy.SuffixingRandom.BaseNameResolver.ForFixedValue(persistentClass.getTypeName()))).subclass((TypeDefinition)(interfaces.size() == 1 ? persistentClass : TypeDescription.OBJECT), (ConstructorStrategy)ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING).implement(interfaces).method(helpers.getVirtualNotFinalizerFilter()).intercept((Implementation)helpers.getDelegateToInterceptorDispatcherMethodDelegation()).method(helpers.getProxyNonInterceptedMethodFilter()).intercept((Implementation)SuperMethodCall.INSTANCE).defineField("$$_hibernate_interceptor", ProxyConfiguration.Interceptor.class, new ModifierContributor.ForField[]{Visibility.PRIVATE}).implement(new Type[]{ProxyConfiguration.class}).intercept((Implementation)helpers.getInterceptorFieldAccessor());
    }

    public HibernateProxy deserializeProxy(SerializableProxy serializableProxy) {
        ByteBuddyInterceptor interceptor = new ByteBuddyInterceptor(serializableProxy.getEntityName(), serializableProxy.getPersistentClass(), serializableProxy.getInterfaces(), serializableProxy.getId(), ByteBuddyProxyHelper.resolveIdGetterMethod(serializableProxy), ByteBuddyProxyHelper.resolveIdSetterMethod(serializableProxy), serializableProxy.getComponentIdType(), null, ReflectHelper.overridesEquals(serializableProxy.getPersistentClass()));
        try {
            Class proxyClass = this.buildProxy(serializableProxy.getPersistentClass(), serializableProxy.getInterfaces());
            HibernateProxy proxy = (HibernateProxy)proxyClass.newInstance();
            ((ProxyConfiguration)((Object)proxy)).$$_hibernate_set_interceptor(interceptor);
            return proxy;
        }
        catch (Throwable t) {
            String message = LOG.bytecodeEnhancementFailed(serializableProxy.getEntityName());
            LOG.error(message, t);
            throw new HibernateException(message, t);
        }
    }

    private static Method resolveIdGetterMethod(SerializableProxy serializableProxy) {
        if (serializableProxy.getIdentifierGetterMethodName() == null) {
            return null;
        }
        try {
            return serializableProxy.getIdentifierGetterMethodClass().getDeclaredMethod(serializableProxy.getIdentifierGetterMethodName(), new Class[0]);
        }
        catch (NoSuchMethodException e) {
            throw new HibernateException(String.format(Locale.ENGLISH, "Unable to deserialize proxy [%s, %s]; could not locate id getter method [%s] on entity class [%s]", serializableProxy.getEntityName(), serializableProxy.getId(), serializableProxy.getIdentifierGetterMethodName(), serializableProxy.getIdentifierGetterMethodClass()));
        }
    }

    private static Method resolveIdSetterMethod(SerializableProxy serializableProxy) {
        if (serializableProxy.getIdentifierSetterMethodName() == null) {
            return null;
        }
        try {
            return serializableProxy.getIdentifierSetterMethodClass().getDeclaredMethod(serializableProxy.getIdentifierSetterMethodName(), serializableProxy.getIdentifierSetterMethodParams());
        }
        catch (NoSuchMethodException e) {
            throw new HibernateException(String.format(Locale.ENGLISH, "Unable to deserialize proxy [%s, %s]; could not locate id setter method [%s] on entity class [%s]", serializableProxy.getEntityName(), serializableProxy.getId(), serializableProxy.getIdentifierSetterMethodName(), serializableProxy.getIdentifierSetterMethodClass()));
        }
    }
}

