/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import org.hibernate.InstantiationException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.bytecode.spi.ReflectionOptimizer;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.Component;
import org.hibernate.tuple.Instantiator;

public class PojoInstantiator
implements Instantiator,
Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(PojoInstantiator.class.getName());
    private transient Constructor constructor;
    private final Class mappedClass;
    private final transient ReflectionOptimizer.InstantiationOptimizer optimizer;
    private final boolean embeddedIdentifier;
    private final boolean isAbstract;

    public PojoInstantiator(Class mappedClass, ReflectionOptimizer.InstantiationOptimizer optimizer, boolean embeddedIdentifier) {
        this.mappedClass = mappedClass;
        this.optimizer = optimizer;
        this.embeddedIdentifier = embeddedIdentifier;
        this.isAbstract = ReflectHelper.isAbstractClass(mappedClass);
        try {
            this.constructor = ReflectHelper.getDefaultConstructor(mappedClass);
        }
        catch (PropertyNotFoundException pnfe) {
            LOG.noDefaultConstructor(mappedClass.getName());
            this.constructor = null;
        }
    }

    public PojoInstantiator(Component component, ReflectionOptimizer.InstantiationOptimizer optimizer) {
        this(component.getComponentClass(), optimizer);
    }

    public PojoInstantiator(Class componentClass, ReflectionOptimizer.InstantiationOptimizer optimizer) {
        this.mappedClass = componentClass;
        this.isAbstract = ReflectHelper.isAbstractClass(this.mappedClass);
        this.optimizer = optimizer;
        this.embeddedIdentifier = false;
        try {
            this.constructor = ReflectHelper.getDefaultConstructor(this.mappedClass);
        }
        catch (PropertyNotFoundException pnfe) {
            LOG.noDefaultConstructor(this.mappedClass.getName());
            this.constructor = null;
        }
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.constructor = ReflectHelper.getDefaultConstructor(this.mappedClass);
    }

    @Override
    public Object instantiate() {
        if (this.isAbstract) {
            throw new InstantiationException("Cannot instantiate abstract class or interface: ", this.mappedClass);
        }
        if (this.optimizer != null) {
            return this.optimizer.newInstance();
        }
        if (this.constructor == null) {
            throw new InstantiationException("No default constructor for entity: ", this.mappedClass);
        }
        try {
            return this.applyInterception(this.constructor.newInstance(null));
        }
        catch (Exception e) {
            throw new InstantiationException("Could not instantiate entity: ", this.mappedClass, e);
        }
    }

    protected Object applyInterception(Object entity) {
        return entity;
    }

    @Override
    public Object instantiate(Serializable id) {
        boolean useEmbeddedIdentifierInstanceAsEntity = this.embeddedIdentifier && id != null && id.getClass().equals(this.mappedClass);
        return useEmbeddedIdentifierInstanceAsEntity ? id : this.instantiate();
    }

    @Override
    public boolean isInstance(Object object) {
        return this.mappedClass.isInstance(object);
    }
}

