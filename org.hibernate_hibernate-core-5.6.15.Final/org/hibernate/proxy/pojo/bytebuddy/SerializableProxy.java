/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.pojo.bytebuddy;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.cfg.Environment;
import org.hibernate.proxy.AbstractSerializableProxy;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor;
import org.hibernate.type.CompositeType;

public final class SerializableProxy
extends AbstractSerializableProxy {
    private final Class persistentClass;
    private final Class[] interfaces;
    private final String identifierGetterMethodName;
    private final Class identifierGetterMethodClass;
    private final String identifierSetterMethodName;
    private final Class identifierSetterMethodClass;
    private final Class[] identifierSetterMethodParams;
    private final CompositeType componentIdType;

    @Deprecated
    public SerializableProxy(String entityName, Class persistentClass, Class[] interfaces, Serializable id, Boolean readOnly, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType) {
        this(entityName, persistentClass, interfaces, id, readOnly, null, false, getIdentifierMethod, setIdentifierMethod, componentIdType);
    }

    public SerializableProxy(String entityName, Class persistentClass, Class[] interfaces, Serializable id, Boolean readOnly, String sessionFactoryUuid, boolean allowLoadOutsideTransaction, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType) {
        super(entityName, id, readOnly, sessionFactoryUuid, allowLoadOutsideTransaction);
        this.persistentClass = persistentClass;
        this.interfaces = interfaces;
        if (getIdentifierMethod != null) {
            this.identifierGetterMethodName = getIdentifierMethod.getName();
            this.identifierGetterMethodClass = getIdentifierMethod.getDeclaringClass();
        } else {
            this.identifierGetterMethodName = null;
            this.identifierGetterMethodClass = null;
        }
        if (setIdentifierMethod != null) {
            this.identifierSetterMethodName = setIdentifierMethod.getName();
            this.identifierSetterMethodClass = setIdentifierMethod.getDeclaringClass();
            this.identifierSetterMethodParams = setIdentifierMethod.getParameterTypes();
        } else {
            this.identifierSetterMethodName = null;
            this.identifierSetterMethodClass = null;
            this.identifierSetterMethodParams = null;
        }
        this.componentIdType = componentIdType;
    }

    @Override
    protected String getEntityName() {
        return super.getEntityName();
    }

    @Override
    protected Serializable getId() {
        return super.getId();
    }

    protected Class getPersistentClass() {
        return this.persistentClass;
    }

    protected Class[] getInterfaces() {
        return this.interfaces;
    }

    protected String getIdentifierGetterMethodName() {
        return this.identifierGetterMethodName;
    }

    protected Class getIdentifierGetterMethodClass() {
        return this.identifierGetterMethodClass;
    }

    protected String getIdentifierSetterMethodName() {
        return this.identifierSetterMethodName;
    }

    protected Class getIdentifierSetterMethodClass() {
        return this.identifierSetterMethodClass;
    }

    protected Class[] getIdentifierSetterMethodParams() {
        return this.identifierSetterMethodParams;
    }

    protected CompositeType getComponentIdType() {
        return this.componentIdType;
    }

    private Object readResolve() {
        BytecodeProvider bytecodeProvider = Environment.getBytecodeProvider();
        if (!(bytecodeProvider instanceof BytecodeProviderImpl)) {
            throw new IllegalStateException("The bytecode provider is not ByteBuddy, unable to deserialize a ByteBuddy proxy.");
        }
        HibernateProxy proxy = ((BytecodeProviderImpl)bytecodeProvider).getByteBuddyProxyHelper().deserializeProxy(this);
        this.afterDeserialization((ByteBuddyInterceptor)proxy.getHibernateLazyInitializer());
        return proxy;
    }
}

