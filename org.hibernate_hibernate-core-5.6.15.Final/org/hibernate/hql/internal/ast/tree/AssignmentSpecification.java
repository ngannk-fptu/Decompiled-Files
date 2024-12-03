/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.collections.AST;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.entity.UnionSubclassEntityPersister;

public class AssignmentSpecification {
    private final Set tableNames;
    private final ParameterSpecification[] hqlParameters;
    private final AST eq;
    private final SessionFactoryImplementor factory;
    private String sqlAssignmentString;

    public AssignmentSpecification(AST eq, Queryable persister) {
        if (eq.getType() != 108) {
            throw new QueryException("assignment in set-clause not associated with equals");
        }
        this.eq = eq;
        this.factory = persister.getFactory();
        DotNode lhs = (DotNode)eq.getFirstChild();
        SqlNode rhs = (SqlNode)lhs.getNextSibling();
        this.validateLhs(lhs);
        String propertyPath = lhs.getPropertyPath();
        HashSet<String> temp = new HashSet<String>();
        if (persister instanceof UnionSubclassEntityPersister) {
            String[] tables = persister.getConstraintOrderedTableNameClosure();
            Collections.addAll(temp, tables);
        } else {
            temp.add(persister.getSubclassTableName(persister.getSubclassPropertyTableNumber(propertyPath)));
        }
        this.tableNames = Collections.unmodifiableSet(temp);
        if (rhs == null) {
            this.hqlParameters = new ParameterSpecification[0];
        } else if (AssignmentSpecification.isParam((AST)rhs)) {
            this.hqlParameters = new ParameterSpecification[]{((ParameterNode)rhs).getHqlParameterSpecification()};
        } else {
            List parameterList = ASTUtil.collectChildren((AST)rhs, new ASTUtil.IncludePredicate(){

                @Override
                public boolean include(AST node) {
                    return AssignmentSpecification.isParam(node);
                }
            });
            this.hqlParameters = new ParameterSpecification[parameterList.size()];
            Iterator itr = parameterList.iterator();
            int i = 0;
            while (itr.hasNext()) {
                this.hqlParameters[i++] = ((ParameterNode)itr.next()).getHqlParameterSpecification();
            }
        }
    }

    public boolean affectsTable(String tableName) {
        return this.tableNames.contains(tableName);
    }

    public ParameterSpecification[] getParameters() {
        return this.hqlParameters;
    }

    public String getSqlAssignmentFragment() {
        if (this.sqlAssignmentString == null) {
            try {
                SqlGenerator sqlGenerator = new SqlGenerator(this.factory);
                sqlGenerator.comparisonExpr(this.eq, false);
                this.sqlAssignmentString = sqlGenerator.getSQL();
            }
            catch (Throwable t) {
                throw new QueryException("cannot interpret set-clause assignment");
            }
        }
        return this.sqlAssignmentString;
    }

    private static boolean isParam(AST node) {
        return node.getType() == 132 || node.getType() == 156;
    }

    private void validateLhs(FromReferenceNode lhs) {
        if (!lhs.isResolved()) {
            throw new UnsupportedOperationException("cannot validate assignablity of unresolved node");
        }
        if (lhs.getDataType().isCollectionType()) {
            throw new QueryException("collections not assignable in update statements");
        }
        if (lhs.getDataType().isComponentType()) {
            throw new QueryException("Components currently not assignable in update statements");
        }
        if (lhs.getDataType().isEntityType()) {
            // empty if block
        }
        if (lhs.getImpliedJoin() != null || lhs.getFromElement().isImplied()) {
            throw new QueryException("Implied join paths are not assignable in update statements");
        }
    }
}

