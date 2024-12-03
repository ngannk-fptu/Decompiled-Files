/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.JDBCException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.AbstractRestrictableStatement;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.spi.id.AbstractTableBasedBulkIdHandler;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;

public abstract class AbstractIdsBulkIdHandler
extends AbstractTableBasedBulkIdHandler {
    private final Queryable targetedPersister;
    private final String idSelect;
    private final List<ParameterSpecification> idSelectParameterSpecifications;

    public AbstractIdsBulkIdHandler(SessionFactoryImplementor sessionFactory, HqlSqlWalker walker) {
        super(sessionFactory, walker);
        AbstractRestrictableStatement statement = (AbstractRestrictableStatement)walker.getAST();
        FromElement fromElement = statement.getFromClause().getFromElement();
        this.targetedPersister = fromElement.getQueryable();
        AbstractTableBasedBulkIdHandler.ProcessedWhereClause processedWhereClause = this.processWhereClause(statement.getWhereClause());
        this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
        String bulkTargetAlias = fromElement.getTableAlias();
        this.idSelect = this.generateIdSelect(bulkTargetAlias, processedWhereClause).toStatementString();
    }

    @Override
    public Queryable getTargetedQueryable() {
        return this.targetedPersister;
    }

    protected Dialect dialect() {
        return this.factory().getServiceRegistry().getService(JdbcServices.class).getDialect();
    }

    protected JDBCException convert(SQLException e, String message, String sql) {
        throw this.factory().getServiceRegistry().getService(JdbcServices.class).getSqlExceptionHelper().convert(e, message, sql);
    }

    protected List<Object[]> selectIds(SharedSessionContractImplementor session, QueryParameters queryParameters) {
        ArrayList<Object[]> ids = new ArrayList<Object[]>();
        try (PreparedStatement ps = session.getJdbcCoordinator().getStatementPreparer().prepareStatement(this.idSelect, false);){
            int position = 1;
            for (ParameterSpecification parameterSpecification : this.idSelectParameterSpecifications) {
                position += parameterSpecification.bind(ps, queryParameters, session, position);
            }
            Dialect dialect = session.getFactory().getServiceRegistry().getService(JdbcServices.class).getDialect();
            ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract(ps);
            while (rs.next()) {
                Object[] result = new Object[this.targetedPersister.getIdentifierColumnNames().length];
                for (String columnName : this.targetedPersister.getIdentifierColumnNames()) {
                    Object column;
                    int columnIndex = rs.findColumn(StringHelper.unquote(columnName, dialect));
                    result[columnIndex - 1] = column = rs.getObject(columnIndex);
                }
                ids.add(result);
            }
        }
        catch (SQLException e) {
            throw this.convert(e, "could not select ids for bulk operation", this.idSelect);
        }
        return ids;
    }
}

