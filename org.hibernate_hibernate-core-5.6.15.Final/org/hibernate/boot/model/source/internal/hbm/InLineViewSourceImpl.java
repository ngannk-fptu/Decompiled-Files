/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.InLineViewSource;

public class InLineViewSourceImpl
extends AbstractHbmSourceNode
implements InLineViewSource {
    private final String schemaName;
    private final String catalogName;
    private final String selectStatement;
    private final String logicalName;
    private final String comment;

    public InLineViewSourceImpl(MappingDocument mappingDocument, String schemaName, String catalogName, String selectStatement, String logicalName, String comment) {
        super(mappingDocument);
        this.schemaName = this.determineSchemaName(mappingDocument, schemaName);
        this.catalogName = this.determineCatalogName(mappingDocument, catalogName);
        this.selectStatement = selectStatement;
        this.logicalName = logicalName;
        this.comment = comment;
    }

    @Override
    public String getExplicitSchemaName() {
        return this.schemaName;
    }

    @Override
    public String getExplicitCatalogName() {
        return this.catalogName;
    }

    @Override
    public String getSelectStatement() {
        return this.selectStatement;
    }

    @Override
    public String getLogicalName() {
        return this.logicalName;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    private String determineCatalogName(MappingDocument mappingDocument, String catalogName) {
        return catalogName != null ? catalogName : mappingDocument.getDocumentRoot().getCatalog();
    }

    private String determineSchemaName(MappingDocument mappingDocument, String schemaName) {
        return schemaName != null ? schemaName : mappingDocument.getDocumentRoot().getSchema();
    }
}

