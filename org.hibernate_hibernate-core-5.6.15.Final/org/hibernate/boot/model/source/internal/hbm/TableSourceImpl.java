/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.TableSource;

public class TableSourceImpl
extends AbstractHbmSourceNode
implements TableSource {
    private final String explicitCatalog;
    private final String explicitSchema;
    private final String explicitTableName;
    private final String rowId;
    private final String comment;
    private final String checkConstraint;

    TableSourceImpl(MappingDocument mappingDocument, String explicitSchema, String explicitCatalog, String explicitTableName, String rowId, String comment, String checkConstraint) {
        super(mappingDocument);
        this.explicitCatalog = this.determineCatalogName(mappingDocument, explicitCatalog);
        this.explicitSchema = this.determineSchemaName(mappingDocument, explicitSchema);
        this.explicitTableName = explicitTableName;
        this.rowId = rowId;
        this.comment = comment;
        this.checkConstraint = checkConstraint;
    }

    @Override
    public String getExplicitCatalogName() {
        return this.explicitCatalog;
    }

    @Override
    public String getExplicitSchemaName() {
        return this.explicitSchema;
    }

    @Override
    public String getExplicitTableName() {
        return this.explicitTableName;
    }

    @Override
    public String getRowId() {
        return this.rowId;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public String getCheckConstraint() {
        return this.checkConstraint;
    }

    private String determineCatalogName(MappingDocument mappingDocument, String catalogName) {
        return catalogName != null ? catalogName : mappingDocument.getDocumentRoot().getCatalog();
    }

    private String determineSchemaName(MappingDocument mappingDocument, String schemaName) {
        return schemaName != null ? schemaName : mappingDocument.getDocumentRoot().getSchema();
    }
}

