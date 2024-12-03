/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.identity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.AbstractReturningDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;

public class GetGeneratedKeysDelegate
extends AbstractReturningDelegate
implements InsertGeneratedIdentifierDelegate {
    private final PostInsertIdentityPersister persister;
    private final Dialect dialect;

    public GetGeneratedKeysDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
        super(persister);
        this.persister = persister;
        this.dialect = dialect;
    }

    @Override
    public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert(SqlStringGenerationContext context) {
        IdentifierGeneratingInsert insert = new IdentifierGeneratingInsert(this.dialect);
        insert.addIdentityColumn(this.persister.getRootTableKeyColumnNames()[0]);
        return insert;
    }

    @Override
    protected PreparedStatement prepare(String insertSQL, SharedSessionContractImplementor session) throws SQLException {
        return session.getJdbcCoordinator().getStatementPreparer().prepareStatement(insertSQL, 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Serializable executeAndExtract(PreparedStatement insert, SharedSessionContractImplementor session) throws SQLException {
        session.getJdbcCoordinator().getResultSetReturn().executeUpdate(insert);
        ResultSet rs = null;
        try {
            rs = insert.getGeneratedKeys();
            Serializable serializable = IdentifierGeneratorHelper.getGeneratedIdentity(rs, this.persister.getRootTableKeyColumnNames()[0], this.persister.getIdentifierType(), session.getJdbcServices().getJdbcEnvironment().getDialect());
            return serializable;
        }
        finally {
            if (rs != null) {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, insert);
            }
        }
    }
}

