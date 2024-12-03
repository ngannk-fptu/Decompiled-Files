/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.Orderable;
import org.hibernate.boot.model.source.spi.PluralAttributeNature;
import org.hibernate.boot.model.source.spi.Sortable;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeSourceSetImpl
extends AbstractPluralAttributeSourceImpl
implements Orderable,
Sortable {
    private final JaxbHbmSetType jaxbSet;

    public PluralAttributeSourceSetImpl(MappingDocument sourceMappingDocument, JaxbHbmSetType jaxbSet, AttributeSourceContainer container) {
        super(sourceMappingDocument, jaxbSet, container);
        this.jaxbSet = jaxbSet;
    }

    @Override
    public PluralAttributeNature getNature() {
        return PluralAttributeNature.SET;
    }

    @Override
    public boolean isSorted() {
        String comparatorName = this.getComparatorName();
        return StringHelper.isNotEmpty(comparatorName) && !comparatorName.equals("unsorted");
    }

    @Override
    public String getComparatorName() {
        return this.jaxbSet.getSort();
    }

    @Override
    public boolean isOrdered() {
        return StringHelper.isNotEmpty(this.getOrder());
    }

    @Override
    public String getOrder() {
        return this.jaxbSet.getOrderBy();
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.SET;
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbSet.getNode();
    }
}

