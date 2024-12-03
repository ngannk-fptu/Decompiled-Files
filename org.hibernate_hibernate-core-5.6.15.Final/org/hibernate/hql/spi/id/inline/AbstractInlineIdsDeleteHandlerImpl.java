/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsBulkIdHandler;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Delete;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public abstract class AbstractInlineIdsDeleteHandlerImpl
extends AbstractInlineIdsBulkIdHandler
implements MultiTableBulkIdStrategy.DeleteHandler {
    private List<String> deletes;

    public AbstractInlineIdsDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        super(factory, walker);
    }

    @Override
    public String[] getSqlStatements() {
        if (this.deletes == null || this.deletes.isEmpty()) {
            return ArrayHelper.EMPTY_STRING_ARRAY;
        }
        return this.deletes.toArray(new String[this.deletes.size()]);
    }

    @Override
    public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        IdsClauseBuilder values = this.prepareInlineStatement(session, queryParameters);
        this.deletes = new ArrayList<String>();
        if (!values.getIds().isEmpty()) {
            String idSubselect = values.toStatement();
            for (Type type : this.getTargetedQueryable().getPropertyTypes()) {
                if (!type.isCollectionType()) continue;
                CollectionType cType = (CollectionType)type;
                AbstractCollectionPersister cPersister = (AbstractCollectionPersister)this.factory().getMetamodel().collectionPersister(cType.getRole());
                if (!cPersister.isManyToMany()) continue;
                this.deletes.add(this.generateDelete(cPersister.getTableName(), cPersister.getKeyColumnNames(), this.generateIdSubselect(idSubselect, this.getTargetedQueryable(), cPersister), "bulk delete - m2m join table cleanup").toStatementString());
            }
            String[] tableNames = this.getTargetedQueryable().getConstraintOrderedTableNameClosure();
            String[][] columnNames = this.getTargetedQueryable().getContraintOrderedTableKeyColumnClosure();
            for (int i = 0; i < tableNames.length; ++i) {
                this.deletes.add(this.generateDelete(tableNames[i], columnNames[i], idSubselect, "bulk delete").toStatementString());
            }
            for (String delete : this.deletes) {
                if (delete == null) continue;
                try {
                    PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(delete, false);
                    try {
                        session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                    }
                    finally {
                        if (ps == null) continue;
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    throw this.convert(e, "error performing bulk delete", delete);
                }
            }
        }
        return values.getIds().size();
    }

    protected String generateIdSubselect(String idSubselect, Queryable persister, AbstractCollectionPersister cPersister) {
        Object[] columnNames = AbstractInlineIdsDeleteHandlerImpl.getKeyColumnNames(persister, cPersister);
        if (Arrays.equals(this.getTargetedQueryable().getIdentifierColumnNames(), columnNames)) {
            return idSubselect;
        }
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append("select ");
        AbstractInlineIdsDeleteHandlerImpl.appendJoined(", ", (String[])columnNames, selectBuilder);
        selectBuilder.append(" from ").append(this.getTargetedQueryable().getTableName());
        selectBuilder.append(" tmp where (");
        AbstractInlineIdsDeleteHandlerImpl.appendJoined(", ", this.getTargetedQueryable().getIdentifierColumnNames(), selectBuilder);
        selectBuilder.append(") in (");
        selectBuilder.append(idSubselect);
        selectBuilder.append(")");
        return selectBuilder.toString();
    }

    protected Delete generateDelete(String tableName, String[] columnNames, String idSubselect, String comment) {
        Delete delete = new Delete().setTableName(tableName).setWhere("(" + String.join((CharSequence)", ", columnNames) + ") in (" + idSubselect + ")");
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment(comment);
        }
        return delete;
    }
}

