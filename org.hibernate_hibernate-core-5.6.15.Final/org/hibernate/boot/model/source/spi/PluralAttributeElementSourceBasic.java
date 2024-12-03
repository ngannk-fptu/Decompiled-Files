/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;

public interface PluralAttributeElementSourceBasic
extends PluralAttributeElementSource,
RelationalValueSourceContainer,
ImplicitBasicColumnNameSource {
    public HibernateTypeSource getExplicitHibernateTypeSource();
}

