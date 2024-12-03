/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.query.qom;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.And;
import javax.jcr.query.qom.BindVariableValue;
import javax.jcr.query.qom.ChildNode;
import javax.jcr.query.qom.ChildNodeJoinCondition;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DescendantNode;
import javax.jcr.query.qom.DescendantNodeJoinCondition;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.EquiJoinCondition;
import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.FullTextSearchScore;
import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Length;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.LowerCase;
import javax.jcr.query.qom.NodeLocalName;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Not;
import javax.jcr.query.qom.Or;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyExistence;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.SameNode;
import javax.jcr.query.qom.SameNodeJoinCondition;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.Source;
import javax.jcr.query.qom.StaticOperand;
import javax.jcr.query.qom.UpperCase;

public interface QueryObjectModelFactory
extends QueryObjectModelConstants {
    public QueryObjectModel createQuery(Source var1, Constraint var2, Ordering[] var3, Column[] var4) throws InvalidQueryException, RepositoryException;

    public Selector selector(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public Join join(Source var1, Source var2, String var3, JoinCondition var4) throws InvalidQueryException, RepositoryException;

    public EquiJoinCondition equiJoinCondition(String var1, String var2, String var3, String var4) throws InvalidQueryException, RepositoryException;

    public SameNodeJoinCondition sameNodeJoinCondition(String var1, String var2, String var3) throws InvalidQueryException, RepositoryException;

    public ChildNodeJoinCondition childNodeJoinCondition(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public DescendantNodeJoinCondition descendantNodeJoinCondition(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public And and(Constraint var1, Constraint var2) throws InvalidQueryException, RepositoryException;

    public Or or(Constraint var1, Constraint var2) throws InvalidQueryException, RepositoryException;

    public Not not(Constraint var1) throws InvalidQueryException, RepositoryException;

    public Comparison comparison(DynamicOperand var1, String var2, StaticOperand var3) throws InvalidQueryException, RepositoryException;

    public PropertyExistence propertyExistence(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public FullTextSearch fullTextSearch(String var1, String var2, StaticOperand var3) throws InvalidQueryException, RepositoryException;

    public SameNode sameNode(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public ChildNode childNode(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public DescendantNode descendantNode(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public PropertyValue propertyValue(String var1, String var2) throws InvalidQueryException, RepositoryException;

    public Length length(PropertyValue var1) throws InvalidQueryException, RepositoryException;

    public NodeName nodeName(String var1) throws InvalidQueryException, RepositoryException;

    public NodeLocalName nodeLocalName(String var1) throws InvalidQueryException, RepositoryException;

    public FullTextSearchScore fullTextSearchScore(String var1) throws InvalidQueryException, RepositoryException;

    public LowerCase lowerCase(DynamicOperand var1) throws InvalidQueryException, RepositoryException;

    public UpperCase upperCase(DynamicOperand var1) throws InvalidQueryException, RepositoryException;

    public BindVariableValue bindVariable(String var1) throws InvalidQueryException, RepositoryException;

    public Literal literal(Value var1) throws InvalidQueryException, RepositoryException;

    public Ordering ascending(DynamicOperand var1) throws InvalidQueryException, RepositoryException;

    public Ordering descending(DynamicOperand var1) throws InvalidQueryException, RepositoryException;

    public Column column(String var1, String var2, String var3) throws InvalidQueryException, RepositoryException;
}

