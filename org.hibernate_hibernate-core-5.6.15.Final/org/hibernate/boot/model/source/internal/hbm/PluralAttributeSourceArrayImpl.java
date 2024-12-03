/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmArrayType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSequentialIndexSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSourceArray;

public class PluralAttributeSourceArrayImpl
extends AbstractPluralAttributeSourceImpl
implements PluralAttributeSourceArray {
    private final JaxbHbmArrayType jaxbArrayMapping;
    private final PluralAttributeSequentialIndexSource indexSource;

    public PluralAttributeSourceArrayImpl(MappingDocument sourceMappingDocument, JaxbHbmArrayType jaxbArrayMapping, AttributeSourceContainer container) {
        super(sourceMappingDocument, jaxbArrayMapping, container);
        this.jaxbArrayMapping = jaxbArrayMapping;
        this.indexSource = jaxbArrayMapping.getListIndex() != null ? new PluralAttributeSequentialIndexSourceImpl(this.sourceMappingDocument(), jaxbArrayMapping.getListIndex()) : new PluralAttributeSequentialIndexSourceImpl(this.sourceMappingDocument(), jaxbArrayMapping.getIndex());
    }

    @Override
    public PluralAttributeIndexSource getIndexSource() {
        return this.indexSource;
    }

    @Override
    public PluralAttributeNature getNature() {
        return PluralAttributeNature.ARRAY;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.ARRAY;
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbArrayMapping.getNode();
    }

    @Override
    public String getElementClass() {
        return this.jaxbArrayMapping.getElementClass();
    }
}

