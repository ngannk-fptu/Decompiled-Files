/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.relational;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedSequenceName;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;

public interface SqlStringGenerationContext {
    public Dialect getDialect();

    @Deprecated
    public IdentifierHelper getIdentifierHelper();

    public Identifier toIdentifier(String var1);

    public Identifier getDefaultCatalog();

    public Identifier catalogWithDefault(Identifier var1);

    public Identifier getDefaultSchema();

    public Identifier schemaWithDefault(Identifier var1);

    public String format(QualifiedTableName var1);

    public String formatWithoutDefaults(QualifiedTableName var1);

    public String format(QualifiedSequenceName var1);

    public String format(QualifiedName var1);

    public String formatWithoutCatalog(QualifiedSequenceName var1);
}

