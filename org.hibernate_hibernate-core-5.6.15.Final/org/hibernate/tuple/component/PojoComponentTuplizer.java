/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.io.Serializable;
import java.lang.reflect.Method;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.spi.BasicProxyFactory;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.bytecode.spi.ProxyFactoryFactory;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.PojoInstantiator;
import org.hibernate.tuple.component.AbstractComponentTuplizer;

public class PojoComponentTuplizer
extends AbstractComponentTuplizer {
    private Class componentClass;
    private ReflectionOptimizer optimizer;
    private final Getter parentGetter;
    private final Setter parentSetter;

    public PojoComponentTuplizer(Component component) {
        super(component);
        String[] getterNames = new String[this.propertySpan];
        String[] setterNames = new String[this.propertySpan];
        Class[] propTypes = new Class[this.propertySpan];
        for (int i = 0; i < this.propertySpan; ++i) {
            getterNames[i] = this.getters[i].getMethodName();
            setterNames[i] = this.setters[i].getMethodName();
            propTypes[i] = this.getters[i].getReturnType();
        }
        String parentPropertyName = component.getParentProperty();
        if (parentPropertyName == null) {
            this.parentSetter = null;
            this.parentGetter = null;
        } else {
            PropertyAccess propertyAccess = PropertyAccessStrategyBasicImpl.INSTANCE.buildPropertyAccess(this.componentClass, parentPropertyName);
            this.parentSetter = propertyAccess.getSetter();
            this.parentGetter = propertyAccess.getGetter();
        }
        if (this.hasCustomAccessors || !Environment.useReflectionOptimizer()) {
            this.optimizer = null;
        } else {
            BytecodeProvider bytecodeProvider = component.getServiceRegistry().getService(BytecodeProvider.class);
            this.optimizer = bytecodeProvider.getReflectionOptimizer(this.componentClass, getterNames, setterNames, propTypes);
        }
    }

    @Override
    public Class getMappedClass() {
        return this.componentClass;
    }

    @Override
    public Object[] getPropertyValues(Object component) throws HibernateException {
        if (component == PropertyAccessStrategyBackRefImpl.UNKNOWN) {
            return new Object[this.propertySpan];
        }
        if (this.optimizer != null && this.optimizer.getAccessOptimizer() != null) {
            return this.optimizer.getAccessOptimizer().getPropertyValues(component);
        }
        return super.getPropertyValues(component);
    }

    @Override
    public void setPropertyValues(Object component, Object[] values) throws HibernateException {
        if (this.optimizer != null && this.optimizer.getAccessOptimizer() != null) {
            this.optimizer.getAccessOptimizer().setPropertyValues(component, values);
        } else {
            super.setPropertyValues(component, values);
        }
    }

    @Override
    public Object getParent(Object component) {
        return this.parentGetter.get(component);
    }

    @Override
    public boolean hasParentProperty() {
        return this.parentGetter != null;
    }

    @Override
    public boolean isMethodOf(Method method) {
        for (int i = 0; i < this.propertySpan; ++i) {
            Method getterMethod = this.getters[i].getMethod();
            if (getterMethod == null || !getterMethod.equals(method)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
        this.parentSetter.set(component, parent, factory);
    }

    @Override
    protected Instantiator buildInstantiator(Component component) {
        if (component.isEmbedded() && ReflectHelper.isAbstractClass(this.componentClass)) {
            ProxyFactoryFactory proxyFactoryFactory = component.getServiceRegistry().getService(ProxyFactoryFactory.class);
            return new ProxiedInstantiator(this.componentClass, proxyFactoryFactory);
        }
        if (this.optimizer == null) {
            return new PojoInstantiator(this.componentClass, null);
        }
        return new PojoInstantiator(this.componentClass, this.optimizer.getInstantiationOptimizer());
    }

    @Override
    protected Getter buildGetter(Component component, Property prop) {
        return prop.getGetter(this.componentClass);
    }

    @Override
    protected Setter buildSetter(Component component, Property prop) {
        return prop.getSetter(this.componentClass);
    }

    @Override
    protected void setComponentClass(Component component) {
        this.componentClass = component.getComponentClass();
    }

    private static class ProxiedInstantiator
    implements Instantiator {
        private final Class proxiedClass;
        private final BasicProxyFactory factory;

        public ProxiedInstantiator(Class componentClass, ProxyFactoryFactory proxyFactoryFactory) {
            this.proxiedClass = componentClass;
            this.factory = proxyFactoryFactory.buildBasicProxyFactory(this.proxiedClass);
        }

        @Override
        public Object instantiate(Serializable id) {
            throw new AssertionFailure("ProxiedInstantiator can only be used to instantiate component");
        }

        @Override
        public Object instantiate() {
            return this.factory.getProxy();
        }

        @Override
        public boolean isInstance(Object object) {
            return this.proxiedClass.isInstance(object);
        }
    }
}

