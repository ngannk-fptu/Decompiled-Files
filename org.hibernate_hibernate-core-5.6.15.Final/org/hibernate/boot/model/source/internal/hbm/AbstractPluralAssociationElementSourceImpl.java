/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.AssociationSource;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;

public abstract class AbstractPluralAssociationElementSourceImpl
extends AbstractHbmSourceNode
implements AssociationSource {
    private final PluralAttributeSource pluralAttributeSource;

    public AbstractPluralAssociationElementSourceImpl(MappingDocument mappingDocument, PluralAttributeSource pluralAttributeSource) {
        super(mappingDocument);
        this.pluralAttributeSource = pluralAttributeSource;
    }

    @Override
    public AttributeSource getAttributeSource() {
        return this.pluralAttributeSource;
    }

    @Override
    public boolean isMappedBy() {
        return false;
    }
}

