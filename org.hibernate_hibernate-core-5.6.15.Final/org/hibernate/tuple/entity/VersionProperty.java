/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.VersionValue;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.type.Type;

public class VersionProperty
extends AbstractNonIdentifierAttribute {
    private final VersionValue unsavedValue;

    public VersionProperty(EntityPersister source, SessionFactoryImplementor sessionFactory, int attributeNumber, String attributeName, Type attributeType, BaselineAttributeInformation attributeInformation, VersionValue unsavedValue) {
        super(source, sessionFactory, attributeNumber, attributeName, attributeType, attributeInformation);
        this.unsavedValue = unsavedValue;
    }

    public VersionValue getUnsavedValue() {
        return this.unsavedValue;
    }
}

