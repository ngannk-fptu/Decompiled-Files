/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.boot.model.naming.Identifier;

public interface QualifiedName {
    public Identifier getCatalogName();

    public Identifier getSchemaName();

    public Identifier getObjectName();

    public String render();
}

