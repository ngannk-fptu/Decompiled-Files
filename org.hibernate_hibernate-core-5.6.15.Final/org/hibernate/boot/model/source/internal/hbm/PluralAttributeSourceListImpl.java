/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.IndexedPluralAttributeSource;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSequentialIndexSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;

public class PluralAttributeSourceListImpl
extends AbstractPluralAttributeSourceImpl
implements IndexedPluralAttributeSource {
    private final JaxbHbmListType jaxbListMapping;
    private final PluralAttributeSequentialIndexSource indexSource;

    public PluralAttributeSourceListImpl(MappingDocument sourceMappingDocument, JaxbHbmListType jaxbListMapping, AttributeSourceContainer container) {
        super(sourceMappingDocument, jaxbListMapping, container);
        this.jaxbListMapping = jaxbListMapping;
        this.indexSource = jaxbListMapping.getListIndex() != null ? new PluralAttributeSequentialIndexSourceImpl(this.sourceMappingDocument(), jaxbListMapping.getListIndex()) : new PluralAttributeSequentialIndexSourceImpl(this.sourceMappingDocument(), jaxbListMapping.getIndex());
    }

    @Override
    public PluralAttributeIndexSource getIndexSource() {
        return this.indexSource;
    }

    @Override
    public PluralAttributeNature getNature() {
        return PluralAttributeNature.LIST;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.LIST;
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbListMapping.getNode();
    }
}

