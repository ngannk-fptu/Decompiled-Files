/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.AbstractPostInsertGenerator;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.AbstractSelectingDelegate;
import org.hibernate.id.insert.IdentifierGeneratingInsert;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class SelectGenerator
extends AbstractPostInsertGenerator {
    private String uniqueKeyPropertyName;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        this.uniqueKeyPropertyName = params.getProperty("key");
    }

    @Override
    public InsertGeneratedIdentifierDelegate getInsertGeneratedIdentifierDelegate(PostInsertIdentityPersister persister, Dialect dialect, boolean isGetGeneratedKeysEnabled) throws HibernateException {
        return new SelectGeneratorDelegate(persister, dialect, this.uniqueKeyPropertyName);
    }

    private static String determineNameOfPropertyToUse(PostInsertIdentityPersister persister, String supplied) {
        if (supplied != null) {
            return supplied;
        }
        int[] naturalIdPropertyIndices = persister.getNaturalIdentifierProperties();
        if (naturalIdPropertyIndices == null) {
            throw new IdentifierGenerationException("no natural-id property defined; need to specify [key] in generator parameters");
        }
        if (naturalIdPropertyIndices.length > 1) {
            throw new IdentifierGenerationException("select generator does not currently support composite natural-id properties; need to specify [key] in generator parameters");
        }
        if (persister.getEntityMetamodel().isNaturalIdentifierInsertGenerated()) {
            throw new IdentifierGenerationException("natural-id also defined as insert-generated; need to specify [key] in generator parameters");
        }
        return persister.getPropertyNames()[naturalIdPropertyIndices[0]];
    }

    public static class SelectGeneratorDelegate
    extends AbstractSelectingDelegate
    implements InsertGeneratedIdentifierDelegate {
        private final PostInsertIdentityPersister persister;
        private final Dialect dialect;
        private final String uniqueKeyPropertyName;
        private final Type uniqueKeyType;
        private final Type idType;
        private final String idSelectString;

        private SelectGeneratorDelegate(PostInsertIdentityPersister persister, Dialect dialect, String suppliedUniqueKeyPropertyName) {
            super(persister);
            this.persister = persister;
            this.dialect = dialect;
            this.uniqueKeyPropertyName = SelectGenerator.determineNameOfPropertyToUse(persister, suppliedUniqueKeyPropertyName);
            this.idSelectString = persister.getSelectByUniqueKeyString(this.uniqueKeyPropertyName);
            this.uniqueKeyType = persister.getPropertyType(this.uniqueKeyPropertyName);
            this.idType = persister.getIdentifierType();
        }

        @Override
        public IdentifierGeneratingInsert prepareIdentifierGeneratingInsert(SqlStringGenerationContext context) {
            return new IdentifierGeneratingInsert(this.dialect);
        }

        @Override
        protected String getSelectSQL() {
            return this.idSelectString;
        }

        @Override
        protected void bindParameters(SharedSessionContractImplementor session, PreparedStatement ps, Object entity) throws SQLException {
            Object uniqueKeyValue = this.persister.getPropertyValue(entity, this.uniqueKeyPropertyName);
            this.uniqueKeyType.nullSafeSet(ps, uniqueKeyValue, 1, session);
        }

        @Override
        protected Serializable getResult(SharedSessionContractImplementor session, ResultSet rs, Object entity) throws SQLException {
            if (!rs.next()) {
                throw new IdentifierGenerationException("the inserted row could not be located by the unique key: " + this.uniqueKeyPropertyName);
            }
            return (Serializable)this.idType.nullSafeGet(rs, this.persister.getRootTableKeyColumnNames(), session, entity);
        }
    }
}

