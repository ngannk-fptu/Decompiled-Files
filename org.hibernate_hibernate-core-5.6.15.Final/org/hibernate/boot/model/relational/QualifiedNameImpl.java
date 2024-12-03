/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;

public class QualifiedNameImpl
extends QualifiedNameParser.NameParts
implements QualifiedName {
    public QualifiedNameImpl(Namespace.Name schemaName, Identifier objectName) {
        this(schemaName.getCatalog(), schemaName.getSchema(), objectName);
    }

    public QualifiedNameImpl(Identifier catalogName, Identifier schemaName, Identifier objectName) {
        super(catalogName, schemaName, objectName);
    }
}

