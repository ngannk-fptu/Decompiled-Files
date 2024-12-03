/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.internal.PropertyAccessStrategyEmbeddedImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public class PropertyAccessEmbeddedImpl
implements PropertyAccess {
    private final PropertyAccessStrategyEmbeddedImpl strategy;
    private final GetterImpl getter;

    public PropertyAccessEmbeddedImpl(PropertyAccessStrategyEmbeddedImpl strategy, Class containerType, String propertyName) {
        this.strategy = strategy;
        this.getter = new GetterImpl(containerType);
    }

    @Override
    public PropertyAccessStrategy getPropertyAccessStrategy() {
        return this.strategy;
    }

    @Override
    public Getter getGetter() {
        return this.getter;
    }

    @Override
    public Setter getSetter() {
        return SetterImpl.INSTANCE;
    }

    private static class SetterImpl
    implements Setter {
        public static final SetterImpl INSTANCE = new SetterImpl();

        private SetterImpl() {
        }

        @Override
        public void set(Object target, Object value, SessionFactoryImplementor factory) {
        }

        @Override
        public String getMethodName() {
            return null;
        }

        @Override
        public Method getMethod() {
            return null;
        }
    }

    private static class GetterImpl
    implements Getter {
        private final Class containerType;

        public GetterImpl(Class containerType) {
            this.containerType = containerType;
        }

        @Override
        public Object get(Object owner) {
            return owner;
        }

        @Override
        public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
            return owner;
        }

        @Override
        public Class getReturnType() {
            return this.containerType;
        }

        @Override
        public Member getMember() {
            return null;
        }

        @Override
        public String getMethodName() {
            return null;
        }

        @Override
        public Method getMethod() {
            return null;
        }
    }
}

