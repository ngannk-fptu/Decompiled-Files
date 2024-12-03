/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.component;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.type.Type;

public class CompositeBasedBasicAttribute
extends AbstractNonIdentifierAttribute {
    protected CompositeBasedBasicAttribute(AttributeSource source, SessionFactoryImplementor sessionFactory, int attributeNumber, String attributeName, Type attributeType, BaselineAttributeInformation baselineInfo) {
        super(source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo);
    }
}

