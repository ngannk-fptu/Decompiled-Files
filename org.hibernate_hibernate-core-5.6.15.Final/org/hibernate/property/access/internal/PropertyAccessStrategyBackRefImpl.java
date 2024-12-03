/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public class PropertyAccessStrategyBackRefImpl
implements PropertyAccessStrategy {
    public static final Serializable UNKNOWN = new Serializable(){

        public String toString() {
            return "<unknown>";
        }

        public Object readResolve() {
            return UNKNOWN;
        }
    };
    private final String entityName;
    private final String propertyName;

    public PropertyAccessStrategyBackRefImpl(String collectionRole, String entityName) {
        this.entityName = entityName;
        this.propertyName = collectionRole.substring(entityName.length() + 1);
    }

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return new PropertyAccessBackRefImpl(this);
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
        private final String entityName;
        private final String propertyName;

        public GetterImpl(String entityName, String propertyName) {
            this.entityName = entityName;
            this.propertyName = propertyName;
        }

        @Override
        public Object get(Object owner) {
            return UNKNOWN;
        }

        @Override
        public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
            if (session == null) {
                return UNKNOWN;
            }
            return session.getPersistenceContextInternal().getOwnerId(this.entityName, this.propertyName, owner, mergeMap);
        }

        @Override
        public Class getReturnType() {
            return Object.class;
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

    private static class PropertyAccessBackRefImpl
    implements PropertyAccess {
        private PropertyAccessStrategyBackRefImpl strategy;
        private final GetterImpl getter;

        public PropertyAccessBackRefImpl(PropertyAccessStrategyBackRefImpl strategy) {
            this.strategy = strategy;
            this.getter = new GetterImpl(strategy.entityName, strategy.propertyName);
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
    }
}

