/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.pojo.bytebuddy;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.proxy.ProxyConfiguration;
import org.hibernate.proxy.pojo.BasicLazyInitializer;
import org.hibernate.proxy.pojo.bytebuddy.SerializableProxy;
import org.hibernate.type.CompositeType;

public class ByteBuddyInterceptor
extends BasicLazyInitializer
implements ProxyConfiguration.Interceptor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ByteBuddyInterceptor.class);
    private final Class[] interfaces;

    public ByteBuddyInterceptor(String entityName, Class persistentClass, Class[] interfaces, Serializable id, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType, SharedSessionContractImplementor session, boolean overridesEquals) {
        super(entityName, persistentClass, id, getIdentifierMethod, setIdentifierMethod, componentIdType, session, overridesEquals);
        this.interfaces = interfaces;
    }

    @Override
    public Object intercept(Object proxy, Method thisMethod, Object[] args) throws Throwable {
        Object result = this.invoke(thisMethod, args, proxy);
        if (result == INVOKE_IMPLEMENTATION) {
            Object target = this.getImplementation();
            try {
                Object returnValue;
                if (ReflectHelper.isPublic(this.persistentClass, thisMethod)) {
                    if (!thisMethod.getDeclaringClass().isInstance(target)) {
                        throw new ClassCastException(target.getClass().getName() + " incompatible with " + thisMethod.getDeclaringClass().getName());
                    }
                    returnValue = thisMethod.invoke(target, args);
                } else {
                    thisMethod.setAccessible(true);
                    returnValue = thisMethod.invoke(target, args);
                }
                if (returnValue == target) {
                    if (returnValue.getClass().isInstance(proxy)) {
                        return proxy;
                    }
                    LOG.narrowingProxy(returnValue.getClass());
                }
                return returnValue;
            }
            catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
        return result;
    }

    @Override
    protected Object serializableProxy() {
        return new SerializableProxy(this.getEntityName(), this.persistentClass, this.interfaces, this.getInternalIdentifier(), this.isReadOnlySettingAvailable() ? Boolean.valueOf(this.isReadOnly()) : this.isReadOnlyBeforeAttachedToSession(), this.getSessionFactoryUuid(), this.isAllowLoadOutsideTransaction(), this.getIdentifierMethod, this.setIdentifierMethod, this.componentIdType);
    }
}

