/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 */
package org.hibernate.hql.internal.ast.exec;

import antlr.RecognitionException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.exec.BasicExecutor;
import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;
import org.hibernate.hql.spi.id.AbstractTableBasedBulkIdHandler;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Update;

public class IdSubselectUpdateExecutor
extends BasicExecutor {
    private final Queryable persister;
    private final String sql;
    private final List<ParameterSpecification> parameterSpecifications;

    @Override
    public Queryable getPersister() {
        return this.persister;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public List<ParameterSpecification> getParameterSpecifications() {
        return this.parameterSpecifications;
    }

    public IdSubselectUpdateExecutor(HqlSqlWalker walker) {
        String whereClause;
        this.persister = walker.getFinalFromClause().getFromElement().getQueryable();
        Dialect dialect = walker.getDialect();
        UpdateStatement updateStatement = (UpdateStatement)walker.getAST();
        ArrayList<AssignmentSpecification> assignments = walker.getAssignmentSpecifications();
        if (updateStatement.getWhereClause().getNumberOfChildren() == 0) {
            whereClause = "";
        } else {
            try {
                SqlGenerator gen = new SqlGenerator(walker.getSessionFactoryHelper().getFactory());
                gen.whereClause(updateStatement.getWhereClause());
                gen.getParseErrorHandler().throwQueryException();
                whereClause = gen.getSQL().substring(7);
            }
            catch (RecognitionException e) {
                throw new HibernateException("Unable to generate id select for DML operation", e);
            }
        }
        String tableAlias = updateStatement.getFromClause().getFromElement().getTableAlias();
        String idSelect = AbstractTableBasedBulkIdHandler.generateIdSelect(tableAlias, whereClause, dialect, this.persister);
        String[] tableNames = this.persister.getConstraintOrderedTableNameClosure();
        String[][] columnNames = this.persister.getContraintOrderedTableKeyColumnClosure();
        int[] affectedTables = IntStream.range(0, tableNames.length).filter(table -> assignments.stream().anyMatch(assign -> assign.affectsTable(tableNames[table]))).toArray();
        if (affectedTables.length > 1) {
            throw new AssertionFailure("more than one affected table");
        }
        int affectedTable = affectedTables[0];
        String tableName = tableNames[affectedTable];
        String idColumnNames = String.join((CharSequence)", ", columnNames[affectedTable]);
        Update update = new Update(dialect).setTableName(tableName);
        if (dialect instanceof MySQLDialect) {
            String selectedIdColumns = String.join((CharSequence)", ", this.persister.getIdentifierColumnNames());
            update.setWhere("(" + idColumnNames + ") in (select " + (String)selectedIdColumns + " from (" + idSelect + ") as ht_ids)");
        } else {
            update.setWhere("(" + idColumnNames + ") in (" + idSelect + ")");
        }
        for (AssignmentSpecification assignment : assignments) {
            update.appendAssignmentFragment(assignment.getSqlAssignmentFragment());
        }
        this.sql = update.toStatementString();
        try {
            SqlGenerator gen = new SqlGenerator(walker.getSessionFactoryHelper().getFactory());
            gen.statement(walker.getAST());
            this.parameterSpecifications = gen.getCollectedParameters();
        }
        catch (RecognitionException e) {
            throw QuerySyntaxException.convert(e);
        }
    }
}

