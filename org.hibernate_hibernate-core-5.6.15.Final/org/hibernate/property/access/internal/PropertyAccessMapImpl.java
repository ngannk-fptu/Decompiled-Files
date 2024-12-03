/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.internal;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

public class PropertyAccessMapImpl
implements PropertyAccess {
    private final Getter getter;
    private final Setter setter;
    private final PropertyAccessStrategyMapImpl strategy;

    public PropertyAccessMapImpl(PropertyAccessStrategyMapImpl strategy, String propertyName) {
        this.strategy = strategy;
        this.getter = new GetterImpl(propertyName);
        this.setter = new SetterImpl(propertyName);
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
        return this.setter;
    }

    public static class SetterImpl
    implements Setter {
        private final String propertyName;

        public SetterImpl(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public void set(Object target, Object value, SessionFactoryImplementor factory) {
            ((Map)target).put(this.propertyName, value);
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

    public static class GetterImpl
    implements Getter {
        private final String propertyName;

        public GetterImpl(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public Object get(Object owner) {
            return ((Map)owner).get(this.propertyName);
        }

        @Override
        public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
            return this.get(owner);
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
}

