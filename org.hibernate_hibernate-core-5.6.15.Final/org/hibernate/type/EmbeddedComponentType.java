/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.lang.reflect.Method;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.tuple.component.ComponentMetamodel;
import org.hibernate.type.ComponentType;
import org.hibernate.type.TypeFactory;

public class EmbeddedComponentType
extends ComponentType {
    @Deprecated
    public EmbeddedComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
        super(metamodel);
    }

    public EmbeddedComponentType(ComponentMetamodel metamodel) {
        super(metamodel);
    }

    @Override
    public boolean isEmbedded() {
        return true;
    }

    @Override
    public boolean isMethodOf(Method method) {
        return this.componentTuplizer.isMethodOf(method);
    }

    @Override
    public Object instantiate(Object parent, SharedSessionContractImplementor session) throws HibernateException {
        boolean useParent = parent != null && super.getReturnedClass().isInstance(parent);
        return useParent ? parent : super.instantiate(parent, session);
    }
}

