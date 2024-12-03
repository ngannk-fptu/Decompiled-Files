/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.naming.ImplicitDiscriminatorColumnNameSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;

public interface DiscriminatorSource
extends ImplicitDiscriminatorColumnNameSource {
    public RelationalValueSource getDiscriminatorRelationalValueSource();

    public String getExplicitHibernateTypeName();

    public boolean isForced();

    public boolean isInserted();
}

