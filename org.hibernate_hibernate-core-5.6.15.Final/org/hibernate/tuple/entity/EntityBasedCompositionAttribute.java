/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.component.AbstractCompositionAttribute;
import org.hibernate.type.CompositeType;

public class EntityBasedCompositionAttribute
extends AbstractCompositionAttribute
implements CompositionDefinition {
    public EntityBasedCompositionAttribute(EntityPersister source, SessionFactoryImplementor factory, int attributeNumber, String attributeName, CompositeType attributeType, BaselineAttributeInformation baselineInfo) {
        super(source, factory, attributeNumber, attributeName, attributeType, 0, baselineInfo);
    }

    @Override
    protected EntityPersister locateOwningPersister() {
        return (EntityPersister)this.getSource();
    }
}

