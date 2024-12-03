/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.component.AbstractCompositionAttribute;
import org.hibernate.type.CompositeType;

public class CompositionBasedCompositionAttribute
extends AbstractCompositionAttribute {
    public CompositionBasedCompositionAttribute(AbstractCompositionAttribute source, SessionFactoryImplementor sessionFactory, int entityBasedAttributeNumber, String attributeName, CompositeType attributeType, int columnStartPosition, BaselineAttributeInformation baselineInfo) {
        super(source, sessionFactory, entityBasedAttributeNumber, attributeName, attributeType, columnStartPosition, baselineInfo);
    }

    @Override
    protected EntityPersister locateOwningPersister() {
        AbstractCompositionAttribute source = (AbstractCompositionAttribute)this.getSource();
        if (EntityDefinition.class.isInstance(source.getSource())) {
            return ((EntityDefinition)EntityDefinition.class.cast(source.getSource())).getEntityPersister();
        }
        return ((AbstractCompositionAttribute)AbstractCompositionAttribute.class.cast(source.getSource())).locateOwningPersister();
    }
}

