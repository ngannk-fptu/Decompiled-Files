/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.spi.id;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.spi.id.AbstractTableBasedBulkIdHandler;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Delete;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class TableBasedDeleteHandlerImpl
extends AbstractTableBasedBulkIdHandler
implements MultiTableBulkIdStrategy.DeleteHandler {
    private static final Logger log = Logger.getLogger(TableBasedDeleteHandlerImpl.class);
    private final Queryable targetedPersister;
    private final String idInsertSelect;
    private final List<ParameterSpecification> idSelectParameterSpecifications;
    private final List<String> deletes;

    public TableBasedDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker, IdTableInfo idTableInfo) {
        super(factory, walker);
        DeleteStatement deleteStatement = (DeleteStatement)walker.getAST();
        FromElement fromElement = deleteStatement.getFromClause().getFromElement();
        this.targetedPersister = fromElement.getQueryable();
        String bulkTargetAlias = fromElement.getTableAlias();
        AbstractTableBasedBulkIdHandler.ProcessedWhereClause processedWhereClause = this.processWhereClause(deleteStatement.getWhereClause());
        this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
        this.idInsertSelect = this.generateIdInsertSelect(bulkTargetAlias, idTableInfo, processedWhereClause);
        log.tracev("Generated ID-INSERT-SELECT SQL (multi-table delete) : {0}", (Object)this.idInsertSelect);
        String idSubselect = this.generateIdSubselect(this.targetedPersister, idTableInfo);
        this.deletes = new ArrayList<String>();
        for (Type type : this.targetedPersister.getPropertyTypes()) {
            if (!type.isCollectionType()) continue;
            CollectionType cType = (CollectionType)type;
            AbstractCollectionPersister cPersister = (AbstractCollectionPersister)factory.getMetamodel().collectionPersister(cType.getRole());
            if (!cPersister.isManyToMany()) continue;
            this.deletes.add(this.generateDelete(cPersister.getTableName(), cPersister.getKeyColumnNames(), this.generateIdSubselect(this.targetedPersister, cPersister, idTableInfo), "bulk delete - m2m join table cleanup"));
        }
        String[] tableNames = this.targetedPersister.getConstraintOrderedTableNameClosure();
        String[][] columnNames = this.targetedPersister.getContraintOrderedTableKeyColumnClosure();
        for (int i = 0; i < tableNames.length; ++i) {
            this.deletes.add(this.generateDelete(tableNames[i], columnNames[i], idSubselect, "bulk delete"));
        }
    }

    private String generateDelete(String tableName, String[] columnNames, String idSubselect, String comment) {
        Delete delete = new Delete().setTableName(tableName).setWhere("(" + String.join((CharSequence)", ", columnNames) + ") IN (" + idSubselect + ")");
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            delete.setComment(comment);
        }
        return delete.toStatementString();
    }

    @Override
    public Queryable getTargetedQueryable() {
        return this.targetedPersister;
    }

    @Override
    public String[] getSqlStatements() {
        return this.deletes.toArray(new String[this.deletes.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        this.prepareForUse(this.targetedPersister, session);
        try {
            PreparedStatement ps = null;
            int resultCount = 0;
            try {
                try {
                    ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.idInsertSelect, false);
                    int position = 1;
                    position += this.handlePrependedParametersOnIdSelection(ps, session, position);
                    for (ParameterSpecification parameterSpecification : this.idSelectParameterSpecifications) {
                        position += parameterSpecification.bind(ps, queryParameters, session, position);
                    }
                    resultCount = session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                }
                finally {
                    if (ps != null) {
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
            }
            catch (SQLException e) {
                throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not insert/select ids for bulk delete", this.idInsertSelect);
            }
            for (String delete : this.deletes) {
                try {
                    try {
                        ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(delete, false);
                        this.handleAddedParametersOnDelete(ps, session);
                        session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                    }
                    finally {
                        if (ps == null) continue;
                        session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                        session.getJdbcCoordinator().afterStatementExecution();
                    }
                }
                catch (SQLException e) {
                    throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "error performing bulk delete", delete);
                }
            }
            int n = resultCount;
            return n;
        }
        finally {
            this.releaseFromUse(this.targetedPersister, session);
        }
    }

    protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SharedSessionContractImplementor session, int pos) throws SQLException {
        return 0;
    }

    protected void handleAddedParametersOnDelete(PreparedStatement ps, SharedSessionContractImplementor session) throws SQLException {
    }
}

