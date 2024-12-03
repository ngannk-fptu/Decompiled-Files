/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitAnyDiscriminatorColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitAnyKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitDiscriminatorColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitIdentifierColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitMapKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitPrimaryKeyJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitTenantIdColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;

public interface ImplicitNamingStrategy {
    public Identifier determinePrimaryTableName(ImplicitEntityNameSource var1);

    public Identifier determineJoinTableName(ImplicitJoinTableNameSource var1);

    public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource var1);

    public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource var1);

    public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource var1);

    public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource var1);

    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource var1);

    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource var1);

    public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource var1);

    public Identifier determineAnyDiscriminatorColumnName(ImplicitAnyDiscriminatorColumnNameSource var1);

    public Identifier determineAnyKeyColumnName(ImplicitAnyKeyColumnNameSource var1);

    public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource var1);

    public Identifier determineListIndexColumnName(ImplicitIndexColumnNameSource var1);

    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource var1);

    public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource var1);

    public Identifier determineIndexName(ImplicitIndexNameSource var1);
}

