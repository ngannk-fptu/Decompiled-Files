/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  antlr.collections.AST
 */
package org.hibernate.hql.spi.id;

import antlr.RecognitionException;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.spi.id.IdTableInfo;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.InsertSelect;
import org.hibernate.sql.Select;
import org.hibernate.sql.SelectValues;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

public abstract class AbstractTableBasedBulkIdHandler {
    private final SessionFactoryImplementor sessionFactory;
    private final HqlSqlWalker walker;

    public AbstractTableBasedBulkIdHandler(SessionFactoryImplementor sessionFactory, HqlSqlWalker walker) {
        this.sessionFactory = sessionFactory;
        this.walker = walker;
    }

    protected SessionFactoryImplementor factory() {
        return this.sessionFactory;
    }

    protected HqlSqlWalker walker() {
        return this.walker;
    }

    public abstract Queryable getTargetedQueryable();

    protected ProcessedWhereClause processWhereClause(AST whereClause) {
        if (whereClause.getNumberOfChildren() != 0) {
            try {
                SqlGenerator sqlGenerator = new SqlGenerator(this.sessionFactory);
                sqlGenerator.whereClause(whereClause);
                String userWhereClause = sqlGenerator.getSQL().substring(7);
                List<ParameterSpecification> idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
                return new ProcessedWhereClause(userWhereClause, idSelectParameterSpecifications);
            }
            catch (RecognitionException e) {
                throw new HibernateException("Unable to generate id select for DML operation", e);
            }
        }
        return ProcessedWhereClause.NO_WHERE_CLAUSE;
    }

    protected String generateIdInsertSelect(String tableAlias, IdTableInfo idTableInfo, ProcessedWhereClause whereClause) {
        Select select = this.generateIdSelect(tableAlias, whereClause);
        InsertSelect insert = new InsertSelect(this.walker.getDialect());
        if (this.sessionFactory.getSessionFactoryOptions().isCommentsEnabled()) {
            insert.setComment("insert-select for " + this.getTargetedQueryable().getEntityName() + " ids");
        }
        insert.setTableName(idTableInfo.getQualifiedIdTableName());
        insert.setSelect(select);
        return insert.toStatementString();
    }

    Select generateIdSelect(String tableAlias, ProcessedWhereClause whereClause) {
        return AbstractTableBasedBulkIdHandler.generateIdSelect(tableAlias, whereClause.getUserWhereClauseFragment(), this.walker.getDialect(), this.getTargetedQueryable(), this::addAnyExtraIdSelectValues);
    }

    public static String generateIdSelect(String tableAlias, String whereClause, Dialect dialect, Queryable queryable) {
        return AbstractTableBasedBulkIdHandler.generateIdSelect(tableAlias, whereClause, dialect, queryable, selectValues -> {}).toStatementString();
    }

    private static Select generateIdSelect(String tableAlias, String whereClause, Dialect dialect, Queryable queryable, Consumer<SelectValues> addAnyExtraIdSelectValues) {
        Select select = new Select(dialect);
        SelectValues selectClause = new SelectValues(dialect).addColumns(tableAlias, queryable.getIdentifierColumnNames(), queryable.getIdentifierColumnNames());
        addAnyExtraIdSelectValues.accept(selectClause);
        select.setSelectClause(selectClause.render());
        String rootTableName = queryable.getTableName();
        String fromJoinFragment = queryable.fromJoinFragment(tableAlias, true, false);
        String whereJoinFragment = queryable.whereJoinFragment(tableAlias, true, false);
        select.setFromClause(rootTableName + ' ' + tableAlias + fromJoinFragment);
        if (whereJoinFragment == null) {
            whereJoinFragment = "";
        } else if ((whereJoinFragment = whereJoinFragment.trim()).startsWith("and")) {
            whereJoinFragment = whereJoinFragment.substring(4);
        }
        if (!whereClause.isEmpty() && !whereJoinFragment.isEmpty()) {
            whereJoinFragment = whereJoinFragment + " and ";
        }
        select.setWhereClause(whereJoinFragment + whereClause);
        return select;
    }

    protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
    }

    protected String generateIdSubselect(Queryable persister, IdTableInfo idTableInfo) {
        return "select " + String.join((CharSequence)", ", persister.getIdentifierColumnNames()) + " from " + idTableInfo.getQualifiedIdTableName();
    }

    protected String generateIdSubselect(Queryable persister, AbstractCollectionPersister cPersister, IdTableInfo idTableInfo) {
        String[] columnNames = AbstractTableBasedBulkIdHandler.getKeyColumnNames(persister, cPersister);
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append("select ");
        AbstractTableBasedBulkIdHandler.appendJoined(", ", columnNames, selectBuilder);
        return selectBuilder.append(" from ").append(idTableInfo.getQualifiedIdTableName()).toString();
    }

    protected static String[] getKeyColumnNames(Queryable persister, AbstractCollectionPersister cPersister) {
        String[] columnNames;
        Type keyType = cPersister.getKeyType();
        if (keyType.isComponentType()) {
            ComponentType componentType = (ComponentType)keyType;
            ArrayList columns = new ArrayList(componentType.getPropertyNames().length);
            for (String propertyName : componentType.getPropertyNames()) {
                Collections.addAll(columns, persister.toColumns(propertyName));
            }
            columnNames = columns.toArray(StringHelper.EMPTY_STRINGS);
        } else {
            columnNames = persister.getIdentifierColumnNames();
        }
        return columnNames;
    }

    protected static void appendJoined(String delimiter, String[] parts, StringBuilder sb) {
        sb.append(parts[0]);
        for (int i = 1; i < parts.length; ++i) {
            sb.append(delimiter);
            sb.append(parts[i]);
        }
    }

    protected void prepareForUse(Queryable persister, SharedSessionContractImplementor session) {
    }

    protected void releaseFromUse(Queryable persister, SharedSessionContractImplementor session) {
    }

    protected static class ProcessedWhereClause {
        public static final ProcessedWhereClause NO_WHERE_CLAUSE = new ProcessedWhereClause();
        private final String userWhereClauseFragment;
        private final List<ParameterSpecification> idSelectParameterSpecifications;

        private ProcessedWhereClause() {
            this("", Collections.emptyList());
        }

        public ProcessedWhereClause(String userWhereClauseFragment, List<ParameterSpecification> idSelectParameterSpecifications) {
            this.userWhereClauseFragment = userWhereClauseFragment;
            this.idSelectParameterSpecifications = idSelectParameterSpecifications;
        }

        public String getUserWhereClauseFragment() {
            return this.userWhereClauseFragment;
        }

        public List<ParameterSpecification> getIdSelectParameterSpecifications() {
            return this.idSelectParameterSpecifications;
        }
    }
}

