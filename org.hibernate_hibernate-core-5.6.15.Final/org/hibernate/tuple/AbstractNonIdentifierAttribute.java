/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.tuple.AbstractAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.type.Type;

public abstract class AbstractNonIdentifierAttribute
extends AbstractAttribute
implements NonIdentifierAttribute {
    private final AttributeSource source;
    private final SessionFactoryImplementor sessionFactory;
    private final int attributeNumber;
    private final BaselineAttributeInformation attributeInformation;

    protected AbstractNonIdentifierAttribute(AttributeSource source, SessionFactoryImplementor sessionFactory, int attributeNumber, String attributeName, Type attributeType, BaselineAttributeInformation attributeInformation) {
        super(attributeName, attributeType);
        this.source = source;
        this.sessionFactory = sessionFactory;
        this.attributeNumber = attributeNumber;
        this.attributeInformation = attributeInformation;
    }

    @Override
    public AttributeSource getSource() {
        return this.source();
    }

    protected AttributeSource source() {
        return this.source;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.sessionFactory;
    }

    protected int attributeNumber() {
        return this.attributeNumber;
    }

    @Override
    public boolean isLazy() {
        return this.attributeInformation.isLazy();
    }

    @Override
    public boolean isInsertable() {
        return this.attributeInformation.isInsertable();
    }

    @Override
    public boolean isUpdateable() {
        return this.attributeInformation.isUpdateable();
    }

    @Override
    public ValueGeneration getValueGenerationStrategy() {
        return this.attributeInformation.getValueGenerationStrategy();
    }

    @Override
    public boolean isNullable() {
        return this.attributeInformation.isNullable();
    }

    @Override
    public boolean isDirtyCheckable() {
        return this.attributeInformation.isDirtyCheckable();
    }

    @Override
    public boolean isDirtyCheckable(boolean hasUninitializedProperties) {
        return this.isDirtyCheckable();
    }

    @Override
    public boolean isVersionable() {
        return this.attributeInformation.isVersionable();
    }

    @Override
    public CascadeStyle getCascadeStyle() {
        return this.attributeInformation.getCascadeStyle();
    }

    @Override
    public FetchMode getFetchMode() {
        return this.attributeInformation.getFetchMode();
    }

    protected String loggableMetadata() {
        return "non-identifier";
    }

    public String toString() {
        return "Attribute(name=" + this.getName() + ", type=" + this.getType().getName() + " [" + this.loggableMetadata() + "])";
    }
}

