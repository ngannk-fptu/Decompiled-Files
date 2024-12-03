/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.AssertionFailure;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.IndexedPluralAttributeSource;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeMapKeyManyToAnySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeMapKeyManyToManySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeMapKeySourceBasicImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeMapKeySourceEmbeddedImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.Sortable;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeSourceMapImpl
extends AbstractPluralAttributeSourceImpl
implements IndexedPluralAttributeSource,
Sortable {
    private final String sorting;
    private final PluralAttributeIndexSource indexSource;
    private final String xmlNodeName;

    public PluralAttributeSourceMapImpl(MappingDocument sourceMappingDocument, JaxbHbmMapType jaxbMap, AttributeSourceContainer container) {
        super(sourceMappingDocument, jaxbMap, container);
        this.xmlNodeName = jaxbMap.getNode();
        this.sorting = PluralAttributeSourceMapImpl.interpretSorting(jaxbMap.getSort());
        if (jaxbMap.getMapKey() != null) {
            this.indexSource = new PluralAttributeMapKeySourceBasicImpl(sourceMappingDocument, jaxbMap.getMapKey());
        } else if (jaxbMap.getIndex() != null) {
            this.indexSource = new PluralAttributeMapKeySourceBasicImpl(sourceMappingDocument, jaxbMap.getIndex());
        } else if (jaxbMap.getCompositeMapKey() != null) {
            this.indexSource = new PluralAttributeMapKeySourceEmbeddedImpl(sourceMappingDocument, (AbstractPluralAttributeSourceImpl)this, jaxbMap.getCompositeMapKey());
        } else if (jaxbMap.getCompositeIndex() != null) {
            this.indexSource = new PluralAttributeMapKeySourceEmbeddedImpl(sourceMappingDocument, (AbstractPluralAttributeSourceImpl)this, jaxbMap.getCompositeIndex());
        } else if (jaxbMap.getMapKeyManyToMany() != null) {
            this.indexSource = new PluralAttributeMapKeyManyToManySourceImpl(sourceMappingDocument, this, jaxbMap.getMapKeyManyToMany());
        } else if (jaxbMap.getIndexManyToMany() != null) {
            this.indexSource = new PluralAttributeMapKeyManyToManySourceImpl(sourceMappingDocument, this, jaxbMap.getIndexManyToMany());
        } else if (jaxbMap.getIndexManyToAny() != null) {
            this.indexSource = new PluralAttributeMapKeyManyToAnySourceImpl(sourceMappingDocument, this, jaxbMap.getIndexManyToAny());
        } else {
            throw new AssertionFailure("No map key found");
        }
    }

    private static String interpretSorting(String sort) {
        if (StringHelper.isEmpty(sort)) {
            return null;
        }
        if ("unsorted".equals(sort)) {
            return null;
        }
        return sort;
    }

    @Override
    public PluralAttributeIndexSource getIndexSource() {
        return this.indexSource;
    }

    @Override
    public PluralAttributeNature getNature() {
        return PluralAttributeNature.MAP;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.MAP;
    }

    @Override
    public String getXmlNodeName() {
        return this.xmlNodeName;
    }

    @Override
    public boolean isSorted() {
        return this.sorting != null;
    }

    @Override
    public String getComparatorName() {
        return this.sorting;
    }
}

