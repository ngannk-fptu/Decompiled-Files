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
import java.util.Collections;
import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.hql.spi.id.AbstractTableBasedBulkIdHandler;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Update;
import org.jboss.logging.Logger;

public class TableBasedUpdateHandlerImpl
extends AbstractTableBasedBulkIdHandler
implements MultiTableBulkIdStrategy.UpdateHandler {
    private static final Logger log = Logger.getLogger(TableBasedUpdateHandlerImpl.class);
    private final Queryable targetedPersister;
    private final String idInsertSelect;
    private final List<ParameterSpecification> idSelectParameterSpecifications;
    private final String[] updates;
    private final ParameterSpecification[][] assignmentParameterSpecifications;

    public TableBasedUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker, IdTableInfo idTableInfo) {
        super(factory, walker);
        Dialect dialect = factory.getJdbcServices().getJdbcEnvironment().getDialect();
        UpdateStatement updateStatement = (UpdateStatement)walker.getAST();
        FromElement fromElement = updateStatement.getFromClause().getFromElement();
        this.targetedPersister = fromElement.getQueryable();
        String bulkTargetAlias = fromElement.getTableAlias();
        AbstractTableBasedBulkIdHandler.ProcessedWhereClause processedWhereClause = this.processWhereClause(updateStatement.getWhereClause());
        this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
        this.idInsertSelect = this.generateIdInsertSelect(bulkTargetAlias, idTableInfo, processedWhereClause);
        log.tracev("Generated ID-INSERT-SELECT SQL (multi-table update) : {0}", (Object)this.idInsertSelect);
        String[] tableNames = this.targetedPersister.getConstraintOrderedTableNameClosure();
        String[][] columnNames = this.targetedPersister.getContraintOrderedTableKeyColumnClosure();
        String idSubselect = this.generateIdSubselect(this.targetedPersister, idTableInfo);
        this.updates = new String[tableNames.length];
        this.assignmentParameterSpecifications = new ParameterSpecification[tableNames.length][];
        for (int tableIndex = 0; tableIndex < tableNames.length; ++tableIndex) {
            boolean affected = false;
            ArrayList parameterList = new ArrayList();
            Update update = new Update(dialect).setTableName(tableNames[tableIndex]).setWhere("(" + String.join((CharSequence)", ", columnNames[tableIndex]) + ") IN (" + idSubselect + ")");
            if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
                update.setComment("bulk update");
            }
            ArrayList<AssignmentSpecification> assignmentSpecifications = walker.getAssignmentSpecifications();
            for (AssignmentSpecification assignmentSpecification : assignmentSpecifications) {
                if (!assignmentSpecification.affectsTable(tableNames[tableIndex])) continue;
                affected = true;
                update.appendAssignmentFragment(assignmentSpecification.getSqlAssignmentFragment());
                if (assignmentSpecification.getParameters() == null) continue;
                Collections.addAll(parameterList, assignmentSpecification.getParameters());
            }
            if (!affected) continue;
            this.updates[tableIndex] = update.toStatementString();
            this.assignmentParameterSpecifications[tableIndex] = parameterList.toArray(new ParameterSpecification[0]);
        }
    }

    @Override
    public Queryable getTargetedQueryable() {
        return this.targetedPersister;
    }

    @Override
    public String[] getSqlStatements() {
        return this.updates;
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
                throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "could not insert/select ids for bulk update", this.idInsertSelect);
            }
            for (int i = 0; i < this.updates.length; ++i) {
                if (this.updates[i] == null) continue;
                try {
                    try {
                        ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.updates[i], false);
                        if (this.assignmentParameterSpecifications[i] != null) {
                            int position = 1;
                            for (ParameterSpecification assignmentParameterSpecification : this.assignmentParameterSpecifications[i]) {
                                position += assignmentParameterSpecification.bind(ps, queryParameters, session, position);
                            }
                            this.handleAddedParametersOnUpdate(ps, session, position);
                        }
                        session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                        continue;
                    }
                    finally {
                        if (ps != null) {
                            session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release(ps);
                            session.getJdbcCoordinator().afterStatementExecution();
                        }
                    }
                }
                catch (SQLException e) {
                    throw session.getJdbcServices().getSqlExceptionHelper().convert(e, "error performing bulk update", this.updates[i]);
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

    protected void handleAddedParametersOnUpdate(PreparedStatement ps, SharedSessionContractImplementor session, int position) throws SQLException {
    }
}

