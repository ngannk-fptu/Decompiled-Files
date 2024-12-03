/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.pojo;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.proxy.AbstractLazyInitializer;
import org.hibernate.type.CompositeType;

public abstract class BasicLazyInitializer
extends AbstractLazyInitializer {
    protected static final Object INVOKE_IMPLEMENTATION = new MarkerObject("INVOKE_IMPLEMENTATION");
    protected final Class persistentClass;
    protected final Method getIdentifierMethod;
    protected final Method setIdentifierMethod;
    protected final boolean overridesEquals;
    protected final CompositeType componentIdType;
    private Object replacement;

    protected BasicLazyInitializer(String entityName, Class persistentClass, Serializable id, Method getIdentifierMethod, Method setIdentifierMethod, CompositeType componentIdType, SharedSessionContractImplementor session, boolean overridesEquals) {
        super(entityName, id, session);
        this.persistentClass = persistentClass;
        this.getIdentifierMethod = getIdentifierMethod;
        this.setIdentifierMethod = setIdentifierMethod;
        this.componentIdType = componentIdType;
        this.overridesEquals = overridesEquals;
    }

    protected abstract Object serializableProxy();

    protected final Object invoke(Method method, Object[] args, Object proxy) throws Throwable {
        String methodName = method.getName();
        int params = args.length;
        if (params == 0) {
            if ("writeReplace".equals(methodName)) {
                return this.getReplacement();
            }
            if (!this.overridesEquals && "hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            }
            if (this.isUninitialized() && method.equals(this.getIdentifierMethod)) {
                return this.getIdentifier();
            }
            if ("getHibernateLazyInitializer".equals(methodName)) {
                return this;
            }
        } else if (params == 1) {
            if (!this.overridesEquals && "equals".equals(methodName)) {
                return args[0] == proxy;
            }
            if (method.equals(this.setIdentifierMethod)) {
                this.initialize();
                this.setIdentifier((Serializable)args[0]);
                return INVOKE_IMPLEMENTATION;
            }
        }
        if (this.componentIdType != null && this.componentIdType.isMethodOf(method)) {
            return method.invoke((Object)this.getIdentifier(), args);
        }
        return INVOKE_IMPLEMENTATION;
    }

    private Object getReplacement() {
        this.initializeWithoutLoadIfPossible();
        if (this.isUninitialized()) {
            if (this.replacement == null) {
                this.prepareForPossibleLoadingOutsideTransaction();
                this.replacement = this.serializableProxy();
            }
            return this.replacement;
        }
        return this.getTarget();
    }

    @Override
    public final Class getPersistentClass() {
        return this.persistentClass;
    }
}

