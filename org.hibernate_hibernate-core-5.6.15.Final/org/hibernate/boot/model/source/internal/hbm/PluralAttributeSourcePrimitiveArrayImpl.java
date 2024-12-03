/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPrimitiveArrayType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSequentialIndexSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSourceArray;

public class PluralAttributeSourcePrimitiveArrayImpl
extends AbstractPluralAttributeSourceImpl
implements PluralAttributeSourceArray {
    private final PluralAttributeSequentialIndexSource indexSource;
    private final JaxbHbmPrimitiveArrayType jaxbArrayMapping;

    public PluralAttributeSourcePrimitiveArrayImpl(MappingDocument sourceMappingDocument, JaxbHbmPrimitiveArrayType jaxbArrayMapping, AttributeSourceContainer container) {
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
        return XmlElementMetadata.PRIMITIVE_ARRAY;
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbArrayMapping.getNode();
    }

    @Override
    public String getElementClass() {
        return null;
    }
}

