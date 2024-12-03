/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.cte;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.cte.AbstractCteValuesListBulkIdHandler;
import org.hibernate.hql.spi.id.cte.CteValuesListBuilder;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.sql.Update;

public class CteValuesListUpdateHandlerImpl
extends AbstractCteValuesListBulkIdHandler
implements MultiTableBulkIdStrategy.UpdateHandler {
    private final String[] updates;
    private final ParameterSpecification[][] assignmentParameterSpecifications;

    public CteValuesListUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
        this(factory, walker, null, null);
    }

    public CteValuesListUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker, String catalog, String schema) {
        super(factory, walker, catalog, schema);
        String[] tableNames = this.getTargetedQueryable().getConstraintOrderedTableNameClosure();
        String[][] columnNames = this.getTargetedQueryable().getContraintOrderedTableKeyColumnClosure();
        String idSubselect = this.generateIdSubselect(this.getTargetedQueryable());
        this.updates = new String[tableNames.length];
        this.assignmentParameterSpecifications = new ParameterSpecification[tableNames.length][];
        for (int tableIndex = 0; tableIndex < tableNames.length; ++tableIndex) {
            boolean affected = false;
            ArrayList parameterList = new ArrayList();
            Update update = new Update(factory.getServiceRegistry().getService(JdbcServices.class).getDialect()).setTableName(tableNames[tableIndex]).setWhere("(" + String.join((CharSequence)", ", columnNames[tableIndex]) + ") in (" + idSubselect + ")");
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
            this.assignmentParameterSpecifications[tableIndex] = parameterList.toArray(new ParameterSpecification[parameterList.size()]);
        }
    }

    @Override
    public String[] getSqlStatements() {
        return this.updates;
    }

    @Override
    public int execute(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        CteValuesListBuilder values = this.prepareCteStatement(session, queryParameters);
        if (!values.getIds().isEmpty()) {
            for (int i = 0; i < this.updates.length; ++i) {
                String updateSuffix = this.updates[i];
                if (updateSuffix == null) continue;
                String update = values.toStatement(updateSuffix);
                try (PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(update, false);){
                    int position = 1;
                    for (Object[] result : values.getIds()) {
                        for (Object column : result) {
                            ps.setObject(position++, column);
                        }
                    }
                    if (this.assignmentParameterSpecifications[i] != null) {
                        for (ParameterSpecification assignmentParameterSpecification : this.assignmentParameterSpecifications[i]) {
                            position += assignmentParameterSpecification.bind(ps, queryParameters, session, position);
                        }
                    }
                    session.getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                    continue;
                }
                catch (SQLException e) {
                    throw this.convert(e, "error performing bulk update", update);
                }
            }
        }
        return values.getIds().size();
    }
}

