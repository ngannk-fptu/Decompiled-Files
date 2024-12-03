/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.ToolingHintContextContainer;

public interface AttributeSource
extends ToolingHintContextContainer {
    public XmlElementMetadata getSourceType();

    public String getName();

    public boolean isSingular();

    public String getXmlNodeName();

    public AttributePath getAttributePath();

    public AttributeRole getAttributeRole();

    public HibernateTypeSource getTypeInformation();

    public String getPropertyAccessorName();

    public boolean isIncludedInOptimisticLocking();
}

