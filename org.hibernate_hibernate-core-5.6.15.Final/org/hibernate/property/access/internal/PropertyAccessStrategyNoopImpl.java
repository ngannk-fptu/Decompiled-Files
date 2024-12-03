/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public class PropertyAccessStrategyNoopImpl
implements PropertyAccessStrategy {
    public static final PropertyAccessStrategyNoopImpl INSTANCE = new PropertyAccessStrategyNoopImpl();

    @Override
    public PropertyAccess buildPropertyAccess(Class containerJavaType, String propertyName) {
        return PropertyAccessNoopImpl.INSTANCE;
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
        public static final GetterImpl INSTANCE = new GetterImpl();

        private GetterImpl() {
        }

        @Override
        public Object get(Object owner) {
            return null;
        }

        @Override
        public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
            return null;
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

    private static class PropertyAccessNoopImpl
    implements PropertyAccess {
        public static final PropertyAccessNoopImpl INSTANCE = new PropertyAccessNoopImpl();

        private PropertyAccessNoopImpl() {
        }

        @Override
        public PropertyAccessStrategy getPropertyAccessStrategy() {
            return INSTANCE;
        }

        @Override
        public Getter getGetter() {
            return GetterImpl.INSTANCE;
        }

        @Override
        public Setter getSetter() {
            return SetterImpl.INSTANCE;
        }
    }
}

