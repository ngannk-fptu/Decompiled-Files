/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.AbstractInlineIdsBulkIdHandler;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Update;

public abstract class AbstractInlineIdsUpdateHandlerImpl
extends AbstractInlineIdsBulkIdHandler
implements MultiTableBulkIdStrategy.UpdateHandler {
    private Map<Integer, String> updates;
    private ParameterSpecification[][] assignmentParameterSpecifications;

    public AbstractInlineIdsUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        super(factory, walker);
    }

    @Override
    public String[] getSqlStatements() {
        if (this.updates == null) {
            return ArrayHelper.EMPTY_STRING_ARRAY;
        }
        return this.updates.values().toArray(new String[this.updates.values().size()]);
    }

    @Override
    public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        IdsClauseBuilder values = this.prepareInlineStatement(session, queryParameters);
        this.updates = new LinkedHashMap<Integer, String>();
        if (!values.getIds().isEmpty()) {
            Queryable targetedQueryable = this.getTargetedQueryable();
            String[] tableNames = targetedQueryable.getConstraintOrderedTableNameClosure();
            String[][] columnNames = targetedQueryable.getContraintOrderedTableKeyColumnClosure();
            String idSubselect = values.toStatement();
            this.assignmentParameterSpecifications = new ParameterSpecification[tableNames.length][];
            for (int tableIndex = 0; tableIndex < tableNames.length; ++tableIndex) {
                boolean affected = false;
                ArrayList parameterList = new ArrayList();
                Update update = this.generateUpdate(tableNames[tableIndex], columnNames[tableIndex], idSubselect, "bulk update");
                ArrayList<AssignmentSpecification> assignmentSpecifications = this.walker().getAssignmentSpecifications();
                for (AssignmentSpecification assignmentSpecification : assignmentSpecifications) {
                    if (!assignmentSpecification.affectsTable(tableNames[tableIndex])) continue;
                    affected = true;
                    update.appendAssignmentFragment(assignmentSpecification.getSqlAssignmentFragment());
                    if (assignmentSpecification.getParameters() == null) continue;
                    Collections.addAll(parameterList, assignmentSpecification.getParameters());
                }
                if (!affected) continue;
                this.updates.put(tableIndex, update.toStatementString());
                this.assignmentParameterSpecifications[tableIndex] = parameterList.toArray(new ParameterSpecification[parameterList.size()]);
            }
            JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
            for (Map.Entry<Integer, String> updateEntry : this.updates.entrySet()) {
                int i = updateEntry.getKey();
                String update = updateEntry.getValue();
                if (update == null) continue;
                try {
                    PreparedStatement ps = jdbcCoordinator.getStatementPreparer().prepareStatement(update, false);
                    try {
                        int position = 1;
                        if (this.assignmentParameterSpecifications[i] != null) {
                            for (ParameterSpecification assignmentParameterSpecification : this.assignmentParameterSpecifications[i]) {
                                position += assignmentParameterSpecification.bind(ps, queryParameters, session, position);
                            }
                        }
                        jdbcCoordinator.getResultSetReturn().executeUpdate(ps);
                    }
                    finally {
                        if (ps == null) continue;
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    throw this.convert(e, "error performing bulk update", update);
                }
            }
        }
        return values.getIds().size();
    }

    protected Update generateUpdate(String tableName, String[] columnNames, String idSubselect, String comment) {
        Update update = new Update(this.factory().getServiceRegistry().getService(JdbcServices.class).getDialect()).setTableName(tableName).setWhere("(" + String.join((CharSequence)", ", columnNames) + ") in (" + idSubselect + ")");
        if (this.factory().getSessionFactoryOptions().isCommentsEnabled()) {
            update.setComment(comment);
        }
        return update;
    }
}

