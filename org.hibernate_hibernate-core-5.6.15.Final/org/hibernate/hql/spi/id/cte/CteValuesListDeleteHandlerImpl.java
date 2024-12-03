/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.cte;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.cte.AbstractCteValuesListBulkIdHandler;
import org.hibernate.hql.spi.id.cte.CteValuesListBuilder;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.sql.Delete;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public class CteValuesListDeleteHandlerImpl
extends AbstractCteValuesListBulkIdHandler
implements MultiTableBulkIdStrategy.DeleteHandler {
    private final List<String> deletes = new ArrayList<String>();

    public CteValuesListDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        this(factory, walker, null, null);
    }

    public CteValuesListDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker, String catalog, String schema) {
        super(factory, walker, catalog, schema);
        String idSubselect = this.generateIdSubselect(this.getTargetedQueryable());
        for (Type type : this.getTargetedQueryable().getPropertyTypes()) {
            if (!type.isCollectionType()) continue;
            CollectionType cType = (CollectionType)type;
            AbstractCollectionPersister cPersister = (AbstractCollectionPersister)factory.getMetamodel().collectionPersister(cType.getRole());
            if (!cPersister.isManyToMany()) continue;
            this.deletes.add(this.generateDelete(cPersister.getTableName(), cPersister.getKeyColumnNames(), this.generateIdSubselect(idSubselect, this.getTargetedQueryable(), cPersister), "bulk delete - m2m join table cleanup"));
        }
        String[] tableNames = this.getTargetedQueryable().getConstraintOrderedTableNameClosure();
        String[][] columnNames = this.getTargetedQueryable().getContraintOrderedTableKeyColumnClosure();
        for (int i = 0; i < tableNames.length; ++i) {
            this.deletes.add(this.generateDelete(tableNames[i], columnNames[i], idSubselect, "bulk delete"));
        }
    }

    @Override
    public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        CteValuesListBuilder values = this.prepareCteStatement(session, queryParameters);
        if (!values.getIds().isEmpty()) {
            for (String deleteSuffix : this.deletes) {
                if (deleteSuffix == null) continue;
                String delete = values.toStatement(deleteSuffix);
                try {
                    PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(delete, false);
                    try {
                        int pos = 1;
                        for (Object[] result : values.getIds()) {
                            for (Object column : result) {
                                ps.setObject(pos++, column);
                            }
                        }
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

    private String generateDelete(String tableName, String[] columnNames, String idSubselect, String comment) {
        Delete delete = new Delete().setTableName(tableName).setWhere("(" + String.join((CharSequence)", ", columnNames) + ") in (" + idSubselect + ")");
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment(comment);
        }
        return delete.toStatementString();
    }

    @Override
    public String[] getSqlStatements() {
        return this.deletes.toArray(new String[this.deletes.size()]);
    }
}

