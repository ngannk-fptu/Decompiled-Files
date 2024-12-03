/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.insert;

import org.hibernate.dialect.Dialect;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.sql.Insert;

public class InsertSelectIdentityInsert
extends IdentifierGeneratingInsert {
    protected String identityColumnName;

    @Override
    public Insert addIdentityColumn(String columnName) {
        this.identityColumnName = columnName;
        return super.addIdentityColumn(columnName);
    }

    public InsertSelectIdentityInsert(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String toStatementString() {
        return this.getDialect().getIdentityColumnSupport().appendIdentitySelectToInsert(this.identityColumnName, super.toStatementString());
    }
}

