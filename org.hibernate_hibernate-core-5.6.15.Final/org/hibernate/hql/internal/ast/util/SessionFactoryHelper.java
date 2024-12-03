/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.NameGenerator;
import org.hibernate.hql.internal.ast.DetailedSemanticException;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.persister.collection.CollectionPropertyMapping;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class SessionFactoryHelper {
    private final SessionFactoryImplementor sfi;
    private final Map<String, PropertyMapping> collectionPropertyMappingByRole;

    public SessionFactoryHelper(SessionFactoryImplementor sfi) {
        this.sfi = sfi;
        this.collectionPropertyMappingByRole = new HashMap<String, PropertyMapping>();
    }

    public SessionFactoryImplementor getFactory() {
        return this.sfi;
    }

    public boolean hasPhysicalDiscriminatorColumn(Queryable persister) {
        String discrimColumnName;
        return persister.getDiscriminatorType() != null && (discrimColumnName = persister.getDiscriminatorColumnName()) != null && !"clazz_".equals(discrimColumnName);
    }

    public String getImportedClassName(String className) {
        return this.sfi.getMetamodel().getImportedClassName(className);
    }

    public Queryable findQueryableUsingImports(String className) {
        return SessionFactoryHelper.findQueryableUsingImports(this.sfi, className);
    }

    public static Queryable findQueryableUsingImports(SessionFactoryImplementor sfi, String className) {
        String importedClassName = sfi.getMetamodel().getImportedClassName(className);
        if (importedClassName == null) {
            return null;
        }
        try {
            return (Queryable)sfi.getMetamodel().entityPersister(importedClassName);
        }
        catch (MappingException me) {
            return null;
        }
    }

    public EntityPersister findEntityPersisterByName(String name) throws MappingException {
        EntityPersister persister = this.sfi.getMetamodel().entityPersisters().get(name);
        if (persister != null) {
            return persister;
        }
        String importedClassName = this.sfi.getMetamodel().getImportedClassName(name);
        if (importedClassName == null) {
            return null;
        }
        return this.sfi.getMetamodel().entityPersister(importedClassName);
    }

    public EntityPersister requireClassPersister(String name) throws SemanticException {
        EntityPersister cp;
        try {
            cp = this.findEntityPersisterByName(name);
            if (cp == null) {
                throw new QuerySyntaxException(name + " is not mapped");
            }
        }
        catch (MappingException e) {
            throw new DetailedSemanticException(e.getMessage(), (Throwable)((Object)e));
        }
        return cp;
    }

    public QueryableCollection getCollectionPersister(String role) {
        try {
            return (QueryableCollection)this.sfi.getMetamodel().collectionPersister(role);
        }
        catch (ClassCastException cce) {
            throw new QueryException("collection is not queryable: " + role);
        }
        catch (Exception e) {
            throw new QueryException("collection not found: " + role);
        }
    }

    public QueryableCollection requireQueryableCollection(String role) throws QueryException {
        try {
            QueryableCollection queryableCollection = (QueryableCollection)this.sfi.getMetamodel().collectionPersister(role);
            if (queryableCollection != null) {
                this.collectionPropertyMappingByRole.put(role, new CollectionPropertyMapping(queryableCollection));
            }
            return queryableCollection;
        }
        catch (ClassCastException cce) {
            throw new QueryException("collection role is not queryable: " + role);
        }
        catch (Exception e) {
            throw new QueryException("collection role not found: " + role);
        }
    }

    public PropertyMapping getCollectionPropertyMapping(String role) {
        return this.collectionPropertyMappingByRole.get(role);
    }

    public String[] getCollectionElementColumns(String role, String roleAlias) {
        return this.getCollectionPropertyMapping(role).toColumns(roleAlias, "elements");
    }

    public JoinSequence createJoinSequence() {
        return new JoinSequence(this.sfi);
    }

    public JoinSequence createJoinSequence(boolean implicit, AssociationType associationType, String tableAlias, JoinType joinType, String[] columns) {
        JoinSequence joinSequence = this.createJoinSequence();
        joinSequence.setUseThetaStyle(implicit);
        joinSequence.addJoin(associationType, tableAlias, joinType, columns);
        return joinSequence;
    }

    public JoinSequence createJoinSequence(boolean implicit, AssociationType associationType, String tableAlias, JoinType joinType, String[][] columns) {
        JoinSequence joinSequence = this.createJoinSequence();
        joinSequence.setUseThetaStyle(implicit);
        joinSequence.addJoin(associationType, tableAlias, joinType, columns);
        return joinSequence;
    }

    public JoinSequence createCollectionJoinSequence(QueryableCollection collPersister, String collectionName) {
        JoinSequence joinSequence = this.createJoinSequence();
        joinSequence.setRoot(collPersister, collectionName);
        joinSequence.setUseThetaStyle(true);
        return joinSequence;
    }

    public String getIdentifierOrUniqueKeyPropertyName(EntityType entityType) {
        try {
            return entityType.getIdentifierOrUniqueKeyPropertyName(this.sfi);
        }
        catch (MappingException me) {
            throw new QueryException((Exception)((Object)me));
        }
    }

    public int getColumnSpan(Type type) {
        return type.getColumnSpan(this.sfi);
    }

    public String getAssociatedEntityName(CollectionType collectionType) {
        return collectionType.getAssociatedEntityName(this.sfi);
    }

    private Type getElementType(CollectionType collectionType) {
        return collectionType.getElementType(this.sfi);
    }

    public AssociationType getElementAssociationType(CollectionType collectionType) {
        return (AssociationType)this.getElementType(collectionType);
    }

    public SQLFunction findSQLFunction(String functionName) {
        return this.sfi.getSqlFunctionRegistry().findSQLFunction(functionName);
    }

    private SQLFunction requireSQLFunction(String functionName) {
        SQLFunction f = this.findSQLFunction(functionName);
        if (f == null) {
            throw new QueryException("Unable to find SQL function: " + functionName);
        }
        return f;
    }

    public Type findFunctionReturnType(String functionName, AST first) {
        SQLFunction sqlFunction = this.requireSQLFunction(functionName);
        return this.findFunctionReturnType(functionName, sqlFunction, first);
    }

    public Type findFunctionReturnType(String functionName, SQLFunction sqlFunction, AST firstArgument) {
        Type argumentType = null;
        if (firstArgument != null) {
            if ("cast".equals(functionName)) {
                argumentType = this.sfi.getTypeResolver().heuristicType(firstArgument.getNextSibling().getText());
            } else if (SqlNode.class.isInstance(firstArgument)) {
                argumentType = ((SqlNode)firstArgument).getDataType();
            }
        }
        return sqlFunction.getReturnType(argumentType, this.sfi);
    }

    public String[][] generateColumnNames(Type[] sqlResultTypes) {
        return NameGenerator.generateColumnNames(sqlResultTypes, this.sfi);
    }

    public boolean isStrictJPAQLComplianceEnabled() {
        return this.sfi.getSessionFactoryOptions().getJpaCompliance().isJpaQueryComplianceEnabled();
    }
}

