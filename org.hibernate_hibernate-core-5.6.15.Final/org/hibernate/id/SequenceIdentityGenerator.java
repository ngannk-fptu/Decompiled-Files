/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.insert.AbstractReturningDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.Insert;
import org.hibernate.type.Type;

@Deprecated
public class SequenceIdentityGenerator
extends SequenceGenerator
implements PostInsertIdentifierGenerator {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SequenceIdentityGenerator.class);

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        return IdentifierGeneratorHelper.POST_INSERT_INDICATOR;
    }

    @Override
    public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister persister, Dialect dialect, boolean isGetGeneratedKeysEnabled) throws HibernateException {
        return new Delegate(persister, this.getPhysicalSequenceName());
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(type, params, serviceRegistry);
    }

    public static class NoCommentsInsert
    extends IdentifierGeneratingInsert {
        public NoCommentsInsert(Dialect dialect) {
            super(dialect);
        }

        @Override
        public Insert setComment(String comment) {
            LOG.disallowingInsertStatementComment();
            return this;
        }
    }

    public static class Delegate
    extends AbstractReturningDelegate {
        private final QualifiedName physicalSequenceName;
        private final String[] keyColumns;

        public Delegate(PostInsertIdentityPersister persister, QualifiedName physicalSequenceName) {
            super(persister);
            this.physicalSequenceName = physicalSequenceName;
            this.keyColumns = this.getPersister().getRootTableKeyColumnNames();
            if (this.keyColumns.length > 1) {
                throw new HibernateException("sequence-identity generator cannot be used with with multi-column keys");
            }
        }

        @Override
        public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert(SqlStringGenerationContext context) {
            Dialect dialect = context.getDialect();
            NoCommentsInsert insert = new NoCommentsInsert(dialect);
            String sequenceNextValFragment = dialect.getSelectSequenceNextValString(context.format(this.physicalSequenceName));
            insert.addColumn(this.getPersister().getRootTableKeyColumnNames()[0], sequenceNextValFragment);
            return insert;
        }

        @Override
        protected PreparedStatement prepare(String insertSQL, SharedSessionContractImplementor session) throws SQLException {
            return session.getJdbcCoordinator().getStatementPreparer().prepareStatement(insertSQL, this.keyColumns);
        }

        @Override
        protected Serializable executeAndExtract(PreparedStatement insert, SharedSessionContractImplementor session) throws SQLException {
            session.getJdbcCoordinator().getResultSetReturn().executeUpdate(insert);
            return IdentifierGeneratorHelper.getGeneratedIdentity(insert.getGeneratedKeys(), this.getPersister().getRootTableKeyColumnNames()[0], this.getPersister().getIdentifierType(), session.getJdbcServices().getJdbcEnvironment().getDialect());
        }
    }
}

