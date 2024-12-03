/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.GetGeneratedKeysDelegate;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.PostInsertIdentityPersister;

public class Oracle12cGetGeneratedKeysDelegate
extends GetGeneratedKeysDelegate {
    private String[] keyColumns = this.getPersister().getRootTableKeyColumnNames();

    public Oracle12cGetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
        super(persister, dialect);
        if (this.keyColumns.length > 1) {
            throw new HibernateException("Identity generator cannot be used with multi-column keys");
        }
    }

    @Override
    protected PreparedStatement prepare(String insertSQL, SharedSessionContractImplementor session) throws SQLException {
        return session.getJdbcCoordinator().getStatementPreparer().prepareStatement(insertSQL, this.keyColumns);
    }
}

