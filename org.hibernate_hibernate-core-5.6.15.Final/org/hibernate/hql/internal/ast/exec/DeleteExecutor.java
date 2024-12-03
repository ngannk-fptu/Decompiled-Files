/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast.exec;

import antlr.RecognitionException;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.exec.BasicExecutor;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.Delete;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class DeleteExecutor
extends BasicExecutor {
    private static final Logger LOG = Logger.getLogger(DeleteExecutor.class);
    private final String sql;
    private final List<ParameterSpecification> parameterSpecifications;
    private final Queryable persister;
    private final List<String> deletes = new ArrayList<String>();

    @Override
    Queryable getPersister() {
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

    public DeleteExecutor(HqlSqlWalker walker) {
        this.persister = walker.getFinalFromClause().getFromElement().getQueryable();
        SessionFactoryImplementor factory = walker.getSessionFactoryHelper().getFactory();
        try {
            SqlGenerator gen = new SqlGenerator(factory);
            gen.statement(walker.getAST());
            this.sql = gen.getSQL();
            gen.getParseErrorHandler().throwQueryException();
        }
        catch (RecognitionException e) {
            throw QuerySyntaxException.convert(e);
        }
        try {
            String idSubselectWhere;
            DeleteStatement deleteStatement = (DeleteStatement)walker.getAST();
            if (deleteStatement.hasWhereClause()) {
                AST whereClause = deleteStatement.getWhereClause();
                SqlGenerator gen = new SqlGenerator(factory);
                gen.whereClause(whereClause);
                this.parameterSpecifications = gen.getCollectedParameters();
                String sql = gen.getSQL();
                idSubselectWhere = sql.length() > 7 ? sql : "";
            } else {
                this.parameterSpecifications = new ArrayList<ParameterSpecification>();
                idSubselectWhere = "";
            }
            boolean commentsEnabled = factory.getSessionFactoryOptions().isCommentsEnabled();
            MetamodelImplementor metamodel = factory.getMetamodel();
            boolean notSupportingTuplesInSubqueries = !walker.getDialect().supportsTuplesInSubqueries();
            for (Type type : this.persister.getPropertyTypes()) {
                String[] columnNames;
                CollectionType cType;
                CollectionPersister cPersister;
                if (!type.isCollectionType() || !(cPersister = metamodel.collectionPersister((cType = (CollectionType)type).getRole())).isManyToMany()) continue;
                Type keyType = cPersister.getKeyType();
                if (keyType.isComponentType()) {
                    ComponentType componentType = (ComponentType)keyType;
                    ArrayList columns = new ArrayList(componentType.getPropertyNames().length);
                    try {
                        for (String propertyName : componentType.getPropertyNames()) {
                            Collections.addAll(columns, this.persister.toColumns(propertyName));
                        }
                        columnNames = columns.toArray(new String[0]);
                    }
                    catch (MappingException e) {
                        columnNames = this.persister.getIdentifierColumnNames();
                    }
                } else {
                    columnNames = this.persister.getIdentifierColumnNames();
                }
                if (columnNames.length > 1 && notSupportingTuplesInSubqueries) {
                    LOG.warn((Object)"This dialect is unable to cascade the delete into the many-to-many join table when the entity has multiple primary keys.  Either properly setup cascading on the constraints or manually clear the associations prior to deleting the entities.");
                    continue;
                }
                Joinable joinable = (Joinable)((Object)cPersister);
                StringBuilder whereBuilder = new StringBuilder();
                whereBuilder.append('(');
                DeleteExecutor.append(", ", joinable.getKeyColumnNames(), whereBuilder);
                whereBuilder.append(") in (select ");
                DeleteExecutor.append(", ", columnNames, whereBuilder);
                String where = whereBuilder.append(" from ").append(this.persister.getTableName()).append(idSubselectWhere).append(")").toString();
                Delete delete = new Delete().setTableName(joinable.getTableName()).setWhere(where);
                if (commentsEnabled) {
                    delete.setComment("delete FKs in join table");
                }
                this.deletes.add(delete.toStatementString());
            }
        }
        catch (RecognitionException e) {
            throw new HibernateException("Unable to delete the FKs in the join table!", e);
        }
    }

    private static void append(String delimiter, String[] parts, StringBuilder sb) {
        sb.append(parts[0]);
        for (int i = 1; i < parts.length; ++i) {
            sb.append(delimiter);
            sb.append(parts[i]);
        }
    }

    @Override
    public int execute(QueryParameters parameters, SharedSessionContractImplementor session) throws HibernateException {
        for (String delete : this.deletes) {
            this.doExecute(delete, parameters, this.parameterSpecifications, session);
        }
        return super.execute(parameters, session);
    }
}

