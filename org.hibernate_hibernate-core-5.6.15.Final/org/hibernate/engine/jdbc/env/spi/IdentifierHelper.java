/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.spi;

import org.hibernate.boot.model.naming.Identifier;

public interface IdentifierHelper {
    public Identifier normalizeQuoting(Identifier var1);

    public Identifier toIdentifier(String var1);

    public Identifier toIdentifier(String var1, boolean var2);

    public Identifier applyGlobalQuoting(String var1);

    public boolean isReservedWord(String var1);

    public String toMetaDataCatalogName(Identifier var1);

    public String toMetaDataSchemaName(Identifier var1);

    public String toMetaDataObjectName(Identifier var1);
}

