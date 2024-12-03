/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.AbstractPostInsertGenerator;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.AbstractReturningDelegate;
import org.hibernate.id.insert.AbstractSelectingDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.id.insert.InsertSelectIdentityInsert;

public class IdentityGenerator
extends AbstractPostInsertGenerator {
    @Override
    public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister persister, Dialect dialect, boolean isGetGeneratedKeysEnabled) throws HibernateException {
        if (isGetGeneratedKeysEnabled) {
            return dialect.getIdentityColumnSupport().buildGetGeneratedKeysDelegate(persister, dialect);
        }
        if (dialect.getIdentityColumnSupport().supportsInsertSelectIdentity()) {
            return new InsertSelectDelegate(persister, dialect);
        }
        return new BasicDelegate(persister, dialect);
    }

    public static class BasicDelegate
    extends AbstractSelectingDelegate
    implements InsertGeneratedIdentifierDelegate {
        private final PostInsertIdentityPersister persister;
        private final Dialect dialect;

        public BasicDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
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
        protected String getSelectSQL() {
            return this.persister.getIdentitySelectString();
        }

        @Override
        protected Serializable getResult(SharedSessionContractImplementor session, ResultSet rs, Object object) throws SQLException {
            return IdentifierGeneratorHelper.getGeneratedIdentity(rs, this.persister.getRootTableKeyColumnNames()[0], this.persister.getIdentifierType(), session.getJdbcServices().getJdbcEnvironment().getDialect());
        }
    }

    public static class InsertSelectDelegate
    extends AbstractReturningDelegate
    implements InsertGeneratedIdentifierDelegate {
        private final PostInsertIdentityPersister persister;
        private final Dialect dialect;

        public InsertSelectDelegate(PostInsertIdentityPersister persister, Dialect dialect) {
            super(persister);
            this.persister = persister;
            this.dialect = dialect;
        }

        @Override
        public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert(SqlStringGenerationContext context) {
            InsertSelectIdentityInsert insert = new InsertSelectIdentityInsert(this.dialect);
            insert.addIdentityColumn(this.persister.getRootTableKeyColumnNames()[0]);
            return insert;
        }

        @Override
        protected PreparedStatement prepare(String insertSQL, SharedSessionContractImplementor session) throws SQLException {
            return session.getJdbcCoordinator().getStatementPreparer().prepareStatement(insertSQL, 2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Serializable executeAndExtract(PreparedStatement insert, SharedSessionContractImplementor session) throws SQLException {
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().execute(insert);
            try {
                Serializable serializable = IdentifierGeneratorHelper.getGeneratedIdentity(rs, this.persister.getRootTableKeyColumnNames()[0], this.persister.getIdentifierType(), session.getJdbcServices().getJdbcEnvironment().getDialect());
                return serializable;
            }
            finally {
                session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(rs, insert);
            }
        }

        public Serializable determineGeneratedIdentifier(SharedSessionContractImplementor session, Object entity) {
            throw new AssertionFailure("insert statement returns generated value");
        }
    }
}

