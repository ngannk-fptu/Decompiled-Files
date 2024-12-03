/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.type.Type;

public abstract class AbstractEntityBasedAttribute
extends AbstractNonIdentifierAttribute {
    protected AbstractEntityBasedAttribute(EntityPersister source, SessionFactoryImplementor sessionFactory, int attributeNumber, String attributeName, Type attributeType, BaselineAttributeInformation attributeInformation) {
        super(source, sessionFactory, attributeNumber, attributeName, attributeType, attributeInformation);
    }

    @Override
    public EntityPersister getSource() {
        return (EntityPersister)super.getSource();
    }
}

