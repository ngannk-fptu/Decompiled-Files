/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import java.lang.reflect.Method;
import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.component.ComponentTuplizer;

public abstract class AbstractComponentTuplizer
implements ComponentTuplizer {
    protected final Getter[] getters;
    protected final Setter[] setters;
    protected final int propertySpan;
    protected final Instantiator instantiator;
    protected final boolean hasCustomAccessors;

    protected abstract Instantiator buildInstantiator(Component var1);

    protected abstract Getter buildGetter(Component var1, Property var2);

    protected abstract Setter buildSetter(Component var1, Property var2);

    protected AbstractComponentTuplizer(Component component) {
        this.setComponentClass(component);
        this.propertySpan = component.getPropertySpan();
        this.getters = new Getter[this.propertySpan];
        this.setters = new Setter[this.propertySpan];
        Iterator iter = component.getPropertyIterator();
        boolean foundCustomAccessor = false;
        int i = 0;
        while (iter.hasNext()) {
            Property prop = (Property)iter.next();
            this.getters[i] = this.buildGetter(component, prop);
            this.setters[i] = this.buildSetter(component, prop);
            if (!prop.isBasicPropertyAccessor()) {
                foundCustomAccessor = true;
            }
            ++i;
        }
        this.hasCustomAccessors = foundCustomAccessor;
        this.instantiator = this.buildInstantiator(component);
    }

    @Override
    public Object getPropertyValue(Object component, int i) throws HibernateException {
        return this.getters[i].get(component);
    }

    @Override
    public Object[] getPropertyValues(Object component) throws HibernateException {
        Object[] values = new Object[this.propertySpan];
        for (int i = 0; i < this.propertySpan; ++i) {
            values[i] = this.getPropertyValue(component, i);
        }
        return values;
    }

    @Override
    public boolean isInstance(Object object) {
        return this.instantiator.isInstance(object);
    }

    @Override
    public void setPropertyValues(Object component, Object[] values) throws HibernateException {
        for (int i = 0; i < this.propertySpan; ++i) {
            this.setters[i].set(component, values[i], null);
        }
    }

    @Override
    public Object instantiate() throws HibernateException {
        return this.instantiator.instantiate();
    }

    @Override
    public Object getParent(Object component) {
        return null;
    }

    @Override
    public boolean hasParentProperty() {
        return false;
    }

    @Override
    public boolean isMethodOf(Method method) {
        return false;
    }

    @Override
    public void setParent(Object component, Object parent, SessionFactoryImplementor factory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Getter getGetter(int i) {
        return this.getters[i];
    }

    protected void setComponentClass(Component component) {
    }
}

