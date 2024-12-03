/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import java.util.Set;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Exportable;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedNameImpl;
import org.hibernate.boot.model.relational.SimpleAuxiliaryDatabaseObject;

public class NamedAuxiliaryDatabaseObject
extends SimpleAuxiliaryDatabaseObject
implements Exportable {
    private final String name;

    public NamedAuxiliaryDatabaseObject(String name, Namespace namespace, String createString, String dropString, Set<String> dialectScopes) {
        super(namespace, createString, dropString, dialectScopes);
        this.name = name;
    }

    public NamedAuxiliaryDatabaseObject(String name, Namespace namespace, String[] createStrings, String[] dropStrings, Set<String> dialectScopes) {
        super(namespace, createStrings, dropStrings, dialectScopes);
        this.name = name;
    }

    @Override
    public String getExportIdentifier() {
        return new QualifiedNameImpl(Identifier.toIdentifier(this.getCatalogName()), Identifier.toIdentifier(this.getSchemaName()), Identifier.toIdentifier(this.name)).render();
    }
}

