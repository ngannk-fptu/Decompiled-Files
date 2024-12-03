/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToManyCollectionElementType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAssociationElementSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.PluralAttributeElementNature;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceOneToMany;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeElementSourceOneToManyImpl
extends AbstractPluralAssociationElementSourceImpl
implements PluralAttributeElementSourceOneToMany {
    private final JaxbHbmOneToManyCollectionElementType jaxbOneToManyElement;
    private final String referencedEntityName;
    private final String cascadeString;

    public PluralAttributeElementSourceOneToManyImpl(MappingDocument mappingDocument, PluralAttributeSource pluralAttributeSource, JaxbHbmOneToManyCollectionElementType jaxbOneToManyElement, String cascadeString) {
        super(mappingDocument, pluralAttributeSource);
        this.jaxbOneToManyElement = jaxbOneToManyElement;
        this.cascadeString = cascadeString;
        this.referencedEntityName = StringHelper.isNotEmpty(jaxbOneToManyElement.getEntityName()) ? jaxbOneToManyElement.getEntityName() : mappingDocument.qualifyClassName(jaxbOneToManyElement.getClazz());
    }

    @Override
    public PluralAttributeElementNature getNature() {
        return PluralAttributeElementNature.ONE_TO_MANY;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    @Override
    public boolean isIgnoreNotFound() {
        return this.jaxbOneToManyElement.getNotFound() != null && "ignore".equalsIgnoreCase(this.jaxbOneToManyElement.getNotFound().value());
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbOneToManyElement.getNode();
    }
}

