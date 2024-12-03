/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.util.Map;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
import org.hibernate.hql.internal.ast.tree.SqlFragment;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.JoinProcessor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.param.CollectionFilterKeyParameterSpecification;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.type.Type;

public class SyntheticAndFactory
implements HqlSqlTokenTypes {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SyntheticAndFactory.class);
    private HqlSqlWalker hqlSqlWalker;
    private AST thetaJoins;
    private AST filters;

    public SyntheticAndFactory(HqlSqlWalker hqlSqlWalker) {
        this.hqlSqlWalker = hqlSqlWalker;
    }

    private Node create(int tokenType, String text) {
        return (Node)this.hqlSqlWalker.getASTFactory().create(tokenType, text);
    }

    public void addWhereFragment(JoinFragment joinFragment, String whereFragment, QueryNode query, FromElement fromElement, HqlSqlWalker hqlSqlWalker) {
        AST where;
        if (whereFragment == null) {
            return;
        }
        if (!fromElement.useWhereFragment() && !joinFragment.hasThetaJoins()) {
            return;
        }
        if ((whereFragment = whereFragment.trim()).isEmpty()) {
            return;
        }
        if (whereFragment.startsWith("and")) {
            whereFragment = whereFragment.substring(4);
        }
        LOG.debugf("Using unprocessed WHERE-fragment [%s]", whereFragment);
        SqlFragment fragment = (SqlFragment)this.create(150, whereFragment);
        fragment.setJoinFragment(joinFragment);
        fragment.setFromElement(fromElement);
        if (fromElement.getIndexCollectionSelectorParamSpec() != null) {
            fragment.addEmbeddedParameter(fromElement.getIndexCollectionSelectorParamSpec());
            fromElement.setIndexCollectionSelectorParamSpec(null);
        }
        if (hqlSqlWalker.isFilter() && whereFragment.indexOf(63) >= 0) {
            Type collectionFilterKeyType = hqlSqlWalker.getSessionFactoryHelper().requireQueryableCollection(hqlSqlWalker.getCollectionFilterRole()).getKeyType();
            CollectionFilterKeyParameterSpecification paramSpec = new CollectionFilterKeyParameterSpecification(hqlSqlWalker.getCollectionFilterRole(), collectionFilterKeyType);
            fragment.addEmbeddedParameter(paramSpec);
        }
        JoinProcessor.processDynamicFilterParameters(whereFragment, fragment, hqlSqlWalker);
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Using processed WHERE-fragment [%s]", fragment.getText());
        }
        if (fragment.getFromElement().isFilter() || fragment.hasFilterCondition()) {
            if (this.filters == null) {
                where = query.getWhereClause();
                this.filters = this.create(154, "{filter conditions}");
                ASTUtil.insertChild(where, this.filters);
            }
            this.filters.addChild((AST)fragment);
        } else {
            if (this.thetaJoins == null) {
                where = query.getWhereClause();
                this.thetaJoins = this.create(153, "{theta joins}");
                if (this.filters == null) {
                    ASTUtil.insertChild(where, this.thetaJoins);
                } else {
                    ASTUtil.insertSibling(this.thetaJoins, this.filters);
                }
            }
            this.thetaJoins.addChild((AST)fragment);
        }
    }

    public void addDiscriminatorWhereFragment(RestrictableStatement statement, Queryable persister, Map enabledFilters, String alias) {
        String whereFragment = persister.filterFragment(alias, enabledFilters).trim();
        if (whereFragment != null && whereFragment.isEmpty()) {
            return;
        }
        if (whereFragment.startsWith("and")) {
            whereFragment = whereFragment.substring(4);
        }
        whereFragment = StringHelper.replace(whereFragment, persister.generateFilterConditionAlias(alias) + '.', "");
        SqlFragment discrimNode = (SqlFragment)this.create(150, whereFragment);
        JoinProcessor.processDynamicFilterParameters(whereFragment, discrimNode, this.hqlSqlWalker);
        if (statement.getWhereClause().getNumberOfChildren() == 0) {
            statement.getWhereClause().setFirstChild((AST)discrimNode);
        } else {
            Node and = this.create(6, "{and}");
            AST currentFirstChild = statement.getWhereClause().getFirstChild();
            and.setFirstChild((AST)discrimNode);
            and.addChild(currentFirstChild);
            statement.getWhereClause().setFirstChild((AST)and);
        }
    }
}

