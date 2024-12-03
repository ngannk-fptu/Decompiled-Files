/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.FetchCharacteristics;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.ForeignKeyContributingSource;
import org.hibernate.boot.model.source.spi.Orderable;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceAssociation;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;

public interface PluralAttributeElementSourceManyToMany
extends PluralAttributeElementSourceAssociation,
RelationalValueSourceContainer,
ForeignKeyContributingSource,
Orderable {
    @Override
    public String getReferencedEntityName();

    public String getReferencedEntityAttributeName();

    @Override
    public boolean isIgnoreNotFound();

    @Override
    public String getExplicitForeignKeyName();

    public boolean isUnique();

    public FilterSource[] getFilterSources();

    public String getWhere();

    public FetchCharacteristics getFetchCharacteristics();
}

