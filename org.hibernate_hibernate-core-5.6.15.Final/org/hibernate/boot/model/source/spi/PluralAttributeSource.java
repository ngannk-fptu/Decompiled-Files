/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.CascadeStyleSource;
import org.hibernate.boot.model.source.spi.CollectionIdSource;
import org.hibernate.boot.model.source.spi.FetchCharacteristicsPluralAttribute;
import org.hibernate.boot.model.source.spi.FetchableAttributeSource;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSource;
import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;

public interface PluralAttributeSource
extends AttributeSource,
FetchableAttributeSource,
CascadeStyleSource {
    public PluralAttributeNature getNature();

    public CollectionIdSource getCollectionIdSource();

    public PluralAttributeKeySource getKeySource();

    public PluralAttributeElementSource getElementSource();

    public FilterSource[] getFilterSources();

    public TableSpecificationSource getCollectionTableSpecificationSource();

    public String getCollectionTableComment();

    public String getCollectionTableCheck();

    public String[] getSynchronizedTableNames();

    public Caching getCaching();

    public String getCustomPersisterClassName();

    public String getWhere();

    public boolean isInverse();

    public boolean isMutable();

    public String getCustomLoaderName();

    public CustomSql getCustomSqlInsert();

    public CustomSql getCustomSqlUpdate();

    public CustomSql getCustomSqlDelete();

    public CustomSql getCustomSqlDeleteAll();

    public String getMappedBy();

    public boolean usesJoinTable();

    @Override
    public FetchCharacteristicsPluralAttribute getFetchCharacteristics();
}

