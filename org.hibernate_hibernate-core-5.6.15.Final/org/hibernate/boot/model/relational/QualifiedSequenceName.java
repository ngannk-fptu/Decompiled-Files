/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedNameImpl;

public class QualifiedSequenceName
extends QualifiedNameImpl {
    public QualifiedSequenceName(Identifier catalogName, Identifier schemaName, Identifier sequenceName) {
        super(catalogName, schemaName, sequenceName);
    }

    public QualifiedSequenceName(Namespace.Name schemaName, Identifier sequenceName) {
        super(schemaName, sequenceName);
    }

    public Identifier getSequenceName() {
        return this.getObjectName();
    }
}

