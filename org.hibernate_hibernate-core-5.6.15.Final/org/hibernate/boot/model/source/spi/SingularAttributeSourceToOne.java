/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AssociationSource;
import org.hibernate.boot.model.source.spi.CascadeStyleSource;
import org.hibernate.boot.model.source.spi.FetchCharacteristicsSingularAssociation;
import org.hibernate.boot.model.source.spi.FetchableAttributeSource;
import org.hibernate.boot.model.source.spi.ForeignKeyContributingSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;
import org.hibernate.type.ForeignKeyDirection;

public interface SingularAttributeSourceToOne
extends SingularAttributeSource,
ForeignKeyContributingSource,
FetchableAttributeSource,
AssociationSource,
CascadeStyleSource {
    public String getReferencedEntityAttributeName();

    @Override
    public String getReferencedEntityName();

    public ForeignKeyDirection getForeignKeyDirection();

    @Override
    public FetchCharacteristicsSingularAssociation getFetchCharacteristics();

    public boolean isUnique();

    public Boolean isEmbedXml();
}

