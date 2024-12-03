/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.entity.AbstractEntityBasedAttribute;
import org.hibernate.type.Type;

public class EntityBasedBasicAttribute
extends AbstractEntityBasedAttribute {
    public EntityBasedBasicAttribute(EntityPersister source, SessionFactoryImplementor factory, int attributeNumber, String attributeName, Type attributeType, BaselineAttributeInformation baselineInfo) {
        super(source, factory, attributeNumber, attributeName, attributeType, baselineInfo);
    }
}

