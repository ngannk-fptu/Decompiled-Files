/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexNature;

public interface PluralAttributeIndexSource {
    public PluralAttributeIndexNature getNature();

    public HibernateTypeSource getTypeInformation();

    public String getXmlNodeName();
}

