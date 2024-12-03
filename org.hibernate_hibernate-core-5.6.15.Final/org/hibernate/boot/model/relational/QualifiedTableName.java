/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedNameImpl;

public class QualifiedTableName
extends QualifiedNameImpl {
    public QualifiedTableName(Identifier catalogName, Identifier schemaName, Identifier tableName) {
        super(catalogName, schemaName, tableName);
    }

    public QualifiedTableName(Namespace.Name schemaName, Identifier tableName) {
        super(schemaName, tableName);
    }

    public Identifier getTableName() {
        return this.getObjectName();
    }

    public QualifiedTableName quote() {
        Identifier tableName;
        Identifier schemaName;
        Identifier catalogName = this.getCatalogName();
        if (catalogName != null) {
            catalogName = new Identifier(catalogName.getText(), true);
        }
        if ((schemaName = this.getSchemaName()) != null) {
            schemaName = new Identifier(schemaName.getText(), true);
        }
        if ((tableName = this.getTableName()) != null) {
            tableName = new Identifier(tableName.getText(), true);
        }
        return new QualifiedTableName(catalogName, schemaName, tableName);
    }
}

