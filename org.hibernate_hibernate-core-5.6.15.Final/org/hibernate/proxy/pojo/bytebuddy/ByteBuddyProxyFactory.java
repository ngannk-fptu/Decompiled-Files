/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.pojo.bytebuddy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyConfiguration;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyProxyHelper;
import org.hibernate.type.CompositeType;

public class ByteBuddyProxyFactory
implements ProxyFactory,
Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ByteBuddyProxyFactory.class);
    private final ByteBuddyProxyHelper byteBuddyProxyHelper;
    private Class persistentClass;
    private String entityName;
    private Class[] interfaces;
    private Method getIdentifierMethod;
    private Method setIdentifierMethod;
    private CompositeType componentIdType;
    private boolean overridesEquals;
    private Class proxyClass;

    public ByteBuddyProxyFactory(ByteBuddyProxyHelper byteBuddyProxyHelper) {
        this.byteBuddyProxyHelper = byteBuddyProxyHelper;
    }

    @Override
    public void postInstantiate(String entityName, Class persistentClass, Set<Class> interfaces, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType) throws HibernateException {
        this.entityName = entityName;
        this.persistentClass = persistentClass;
        this.interfaces = this.toArray(interfaces);
        this.getIdentifierMethod = getIdentifierMethod;
        this.setIdentifierMethod = setIdentifierMethod;
        this.componentIdType = componentIdType;
        this.overridesEquals = ReflectHelper.overridesEquals(persistentClass);
        this.proxyClass = this.byteBuddyProxyHelper.buildProxy(persistentClass, this.interfaces);
    }

    private Class[] toArray(Set<Class> interfaces) {
        if (interfaces == null) {
            return ArrayHelper.EMPTY_CLASS_ARRAY;
        }
        return interfaces.toArray(new Class[interfaces.size()]);
    }

    @Override
    public HibernateProxy getProxy(Serializable id, SharedSessionContractImplementor session) throws HibernateException {
        ByteBuddyInterceptor interceptor = new ByteBuddyInterceptor(this.entityName, this.persistentClass, this.interfaces, id, this.getIdentifierMethod, this.setIdentifierMethod, this.componentIdType, session, this.overridesEquals);
        try {
            HibernateProxy proxy = (HibernateProxy)this.proxyClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            ((ProxyConfiguration)((Object)proxy)).$$_hibernate_set_interceptor(interceptor);
            return proxy;
        }
        catch (NoSuchMethodException e) {
            String logMessage = LOG.bytecodeEnhancementFailedBecauseOfDefaultConstructor(this.entityName);
            LOG.error(logMessage, e);
            throw new HibernateException(logMessage, e);
        }
        catch (Throwable t) {
            String logMessage = LOG.bytecodeEnhancementFailed(this.entityName);
            LOG.error(logMessage, t);
            throw new HibernateException(logMessage, t);
        }
    }
}

