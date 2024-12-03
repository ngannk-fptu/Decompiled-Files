/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;

public abstract class AbstractHbmSourceNode {
    private final MappingDocument sourceMappingDocument;

    protected AbstractHbmSourceNode(MappingDocument sourceMappingDocument) {
        this.sourceMappingDocument = sourceMappingDocument;
    }

    protected MappingDocument sourceMappingDocument() {
        return this.sourceMappingDocument;
    }

    protected HbmLocalMetadataBuildingContext metadataBuildingContext() {
        return this.sourceMappingDocument;
    }

    protected Origin origin() {
        return this.sourceMappingDocument().getOrigin();
    }

    protected JaxbHbmHibernateMapping mappingRoot() {
        return this.sourceMappingDocument().getDocumentRoot();
    }
}

