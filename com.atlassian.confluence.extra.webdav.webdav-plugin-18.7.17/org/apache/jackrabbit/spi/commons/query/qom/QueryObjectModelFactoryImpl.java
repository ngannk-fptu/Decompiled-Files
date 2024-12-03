/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

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
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.SameNode;
import javax.jcr.query.qom.SameNodeJoinCondition;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.Source;
import javax.jcr.query.qom.StaticOperand;
import javax.jcr.query.qom.UpperCase;
import org.apache.jackrabbit.commons.query.qom.JoinType;
import org.apache.jackrabbit.commons.query.qom.Operator;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AndImpl;
import org.apache.jackrabbit.spi.commons.query.qom.BindVariableValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ColumnImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ComparisonImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DescendantNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DescendantNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.EquiJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchScoreImpl;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.JoinImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LengthImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LiteralImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LowerCaseImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeLocalNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NotImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrderingImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyExistenceImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SourceImpl;
import org.apache.jackrabbit.spi.commons.query.qom.StaticOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.UpperCaseImpl;

public abstract class QueryObjectModelFactoryImpl
implements QueryObjectModelFactory {
    private final NamePathResolver resolver;

    public QueryObjectModelFactoryImpl(NamePathResolver resolver) {
        this.resolver = resolver;
    }

    protected abstract QueryObjectModel createQuery(QueryObjectModelTree var1) throws InvalidQueryException, RepositoryException;

    public QueryObjectModel createQuery(Selector selector, Constraint constraint, Ordering[] orderings, Column[] columns) throws InvalidQueryException, RepositoryException {
        return this.createQuery((Source)selector, constraint, orderings, columns);
    }

    @Override
    public QueryObjectModel createQuery(Source source, Constraint constraint, Ordering[] orderings, Column[] columns) throws InvalidQueryException, RepositoryException {
        ColumnImpl[] cols;
        OrderingImpl[] ords;
        if (source == null) {
            throw new InvalidQueryException("source must not be null");
        }
        if (!(source instanceof SourceImpl)) {
            throw new RepositoryException("Unknown Source implementation");
        }
        if (constraint != null && !(constraint instanceof ConstraintImpl)) {
            throw new RepositoryException("Unknown Constraint implementation");
        }
        if (orderings != null) {
            ords = new OrderingImpl[orderings.length];
            for (int i = 0; i < orderings.length; ++i) {
                if (!(orderings[i] instanceof OrderingImpl)) {
                    throw new RepositoryException("Unknown Ordering implementation");
                }
                ords[i] = (OrderingImpl)orderings[i];
            }
        } else {
            ords = OrderingImpl.EMPTY_ARRAY;
        }
        if (columns != null) {
            cols = new ColumnImpl[columns.length];
            for (int i = 0; i < columns.length; ++i) {
                if (!(columns[i] instanceof ColumnImpl)) {
                    throw new RepositoryException("Unknown Column implementation");
                }
                cols[i] = (ColumnImpl)columns[i];
            }
        } else {
            cols = ColumnImpl.EMPTY_ARRAY;
        }
        QueryObjectModelTree qomTree = new QueryObjectModelTree(this.resolver, (SourceImpl)source, (ConstraintImpl)constraint, ords, cols);
        return this.createQuery(qomTree);
    }

    public Selector selector(String nodeTypeName) throws InvalidQueryException, RepositoryException {
        Name ntName = this.checkNodeTypeName(nodeTypeName);
        return new SelectorImpl(this.resolver, ntName, ntName);
    }

    @Override
    public Selector selector(String nodeTypeName, String selectorName) throws InvalidQueryException, RepositoryException {
        return new SelectorImpl(this.resolver, this.checkNodeTypeName(nodeTypeName), this.checkSelectorName(selectorName));
    }

    @Override
    public Join join(Source left, Source right, String joinTypeName, JoinCondition joinCondition) throws InvalidQueryException, RepositoryException {
        if (!(left instanceof SourceImpl) || !(right instanceof SourceImpl)) {
            throw new RepositoryException("Unknown Source implementation");
        }
        if (!(joinCondition instanceof JoinConditionImpl)) {
            throw new RepositoryException("Unknown JoinCondition implementation");
        }
        return new JoinImpl(this.resolver, (SourceImpl)left, (SourceImpl)right, JoinType.getJoinTypeByName(joinTypeName), (JoinConditionImpl)joinCondition);
    }

    @Override
    public EquiJoinCondition equiJoinCondition(String selector1Name, String property1Name, String selector2Name, String property2Name) throws InvalidQueryException, RepositoryException {
        return new EquiJoinConditionImpl(this.resolver, this.checkSelectorName(selector1Name), this.checkPropertyName(property1Name), this.checkSelectorName(selector2Name), this.checkPropertyName(property2Name));
    }

    public SameNodeJoinCondition sameNodeJoinCondition(String selector1Name, String selector2Name) throws InvalidQueryException, RepositoryException {
        return new SameNodeJoinConditionImpl(this.resolver, this.checkSelectorName(selector1Name), this.checkSelectorName(selector2Name), null);
    }

    @Override
    public SameNodeJoinCondition sameNodeJoinCondition(String selector1Name, String selector2Name, String selector2Path) throws InvalidQueryException, RepositoryException {
        return new SameNodeJoinConditionImpl(this.resolver, this.checkSelectorName(selector1Name), this.checkSelectorName(selector2Name), this.checkPath(selector2Path));
    }

    @Override
    public ChildNodeJoinCondition childNodeJoinCondition(String childSelectorName, String parentSelectorName) throws InvalidQueryException, RepositoryException {
        return new ChildNodeJoinConditionImpl(this.resolver, this.checkSelectorName(childSelectorName), this.checkSelectorName(parentSelectorName));
    }

    @Override
    public DescendantNodeJoinCondition descendantNodeJoinCondition(String descendantSelectorName, String ancestorSelectorName) throws InvalidQueryException, RepositoryException {
        return new DescendantNodeJoinConditionImpl(this.resolver, this.checkSelectorName(descendantSelectorName), this.checkSelectorName(ancestorSelectorName));
    }

    @Override
    public And and(Constraint constraint1, Constraint constraint2) throws InvalidQueryException, RepositoryException {
        if (constraint1 == null || constraint2 == null) {
            throw new InvalidQueryException("Constraints must not be null");
        }
        if (constraint1 instanceof ConstraintImpl && constraint2 instanceof ConstraintImpl) {
            return new AndImpl(this.resolver, (ConstraintImpl)constraint1, (ConstraintImpl)constraint2);
        }
        throw new RepositoryException("Unknown constraint implementation");
    }

    @Override
    public Or or(Constraint constraint1, Constraint constraint2) throws InvalidQueryException, RepositoryException {
        if (constraint1 == null || constraint2 == null) {
            throw new InvalidQueryException("Constraints must not be null");
        }
        if (constraint1 instanceof ConstraintImpl && constraint2 instanceof ConstraintImpl) {
            return new OrImpl(this.resolver, (ConstraintImpl)constraint1, (ConstraintImpl)constraint2);
        }
        throw new RepositoryException("Unknown constraint implementation");
    }

    @Override
    public Not not(Constraint constraint) throws InvalidQueryException, RepositoryException {
        if (!(constraint instanceof ConstraintImpl)) {
            throw new RepositoryException("Unknown Constraint implementation");
        }
        return new NotImpl(this.resolver, (ConstraintImpl)constraint);
    }

    @Override
    public Comparison comparison(DynamicOperand left, String operatorName, StaticOperand right) throws InvalidQueryException, RepositoryException {
        if (!(left instanceof DynamicOperandImpl)) {
            throw new RepositoryException("Invalid left operand: " + left);
        }
        if (!(right instanceof StaticOperandImpl)) {
            throw new RepositoryException("Invalid right operand: " + right);
        }
        return new ComparisonImpl(this.resolver, (DynamicOperandImpl)left, Operator.getOperatorByName(operatorName), (StaticOperandImpl)right);
    }

    public PropertyExistence propertyExistence(String propertyName) throws InvalidQueryException, RepositoryException {
        return new PropertyExistenceImpl(this.resolver, null, this.checkPropertyName(propertyName));
    }

    @Override
    public PropertyExistence propertyExistence(String selectorName, String propertyName) throws InvalidQueryException, RepositoryException {
        return new PropertyExistenceImpl(this.resolver, this.checkSelectorName(selectorName), this.checkPropertyName(propertyName));
    }

    public FullTextSearch fullTextSearch(String propertyName, StaticOperand fullTextSearchExpression) throws InvalidQueryException, RepositoryException {
        Name propName = null;
        if (propertyName != null) {
            propName = this.checkPropertyName(propertyName);
        }
        return new FullTextSearchImpl(this.resolver, null, propName, this.checkFullTextSearchExpression(fullTextSearchExpression));
    }

    @Override
    public FullTextSearch fullTextSearch(String selectorName, String propertyName, StaticOperand fullTextSearchExpression) throws InvalidQueryException, RepositoryException {
        if (fullTextSearchExpression == null) {
            throw new IllegalArgumentException("Full text search expression is null");
        }
        Name propName = null;
        if (propertyName != null) {
            propName = this.checkPropertyName(propertyName);
        }
        return new FullTextSearchImpl(this.resolver, this.checkSelectorName(selectorName), propName, this.checkFullTextSearchExpression(fullTextSearchExpression));
    }

    public SameNode sameNode(String path) throws InvalidQueryException, RepositoryException {
        return new SameNodeImpl(this.resolver, null, this.checkPath(path));
    }

    @Override
    public SameNode sameNode(String selectorName, String path) throws InvalidQueryException, RepositoryException {
        return new SameNodeImpl(this.resolver, this.checkSelectorName(selectorName), this.checkPath(path));
    }

    public ChildNode childNode(String path) throws InvalidQueryException, RepositoryException {
        return new ChildNodeImpl(this.resolver, null, this.checkPath(path));
    }

    @Override
    public ChildNode childNode(String selectorName, String path) throws InvalidQueryException, RepositoryException {
        return new ChildNodeImpl(this.resolver, this.checkSelectorName(selectorName), this.checkPath(path));
    }

    public DescendantNode descendantNode(String path) throws InvalidQueryException, RepositoryException {
        return new DescendantNodeImpl(this.resolver, null, this.checkPath(path));
    }

    @Override
    public DescendantNode descendantNode(String selectorName, String path) throws InvalidQueryException, RepositoryException {
        return new DescendantNodeImpl(this.resolver, this.checkSelectorName(selectorName), this.checkPath(path));
    }

    public PropertyValue propertyValue(String propertyName) throws InvalidQueryException, RepositoryException {
        return new PropertyValueImpl(this.resolver, null, this.checkPropertyName(propertyName));
    }

    @Override
    public PropertyValue propertyValue(String selectorName, String propertyName) throws InvalidQueryException, RepositoryException {
        return new PropertyValueImpl(this.resolver, this.checkSelectorName(selectorName), this.checkPropertyName(propertyName));
    }

    @Override
    public Length length(PropertyValue propertyValue) throws InvalidQueryException, RepositoryException {
        if (!(propertyValue instanceof PropertyValueImpl)) {
            throw new RepositoryException("Unknown PropertyValue implementation");
        }
        return new LengthImpl(this.resolver, (PropertyValueImpl)propertyValue);
    }

    public NodeName nodeName() throws InvalidQueryException, RepositoryException {
        return new NodeNameImpl(this.resolver, null);
    }

    @Override
    public NodeName nodeName(String selectorName) throws InvalidQueryException, RepositoryException {
        return new NodeNameImpl(this.resolver, this.checkSelectorName(selectorName));
    }

    public NodeLocalName nodeLocalName() throws InvalidQueryException, RepositoryException {
        return new NodeLocalNameImpl(this.resolver, null);
    }

    @Override
    public NodeLocalName nodeLocalName(String selectorName) throws InvalidQueryException, RepositoryException {
        return new NodeLocalNameImpl(this.resolver, this.checkSelectorName(selectorName));
    }

    public FullTextSearchScore fullTextSearchScore() throws InvalidQueryException, RepositoryException {
        return new FullTextSearchScoreImpl(this.resolver, null);
    }

    @Override
    public FullTextSearchScore fullTextSearchScore(String selectorName) throws InvalidQueryException, RepositoryException {
        return new FullTextSearchScoreImpl(this.resolver, this.checkSelectorName(selectorName));
    }

    @Override
    public LowerCase lowerCase(DynamicOperand operand) throws InvalidQueryException, RepositoryException {
        if (!(operand instanceof DynamicOperandImpl)) {
            throw new RepositoryException("Unknown DynamicOperand implementation");
        }
        return new LowerCaseImpl(this.resolver, (DynamicOperandImpl)operand);
    }

    @Override
    public UpperCase upperCase(DynamicOperand operand) throws InvalidQueryException, RepositoryException {
        if (!(operand instanceof DynamicOperandImpl)) {
            throw new RepositoryException("Unknown DynamicOperand implementation");
        }
        return new UpperCaseImpl(this.resolver, (DynamicOperandImpl)operand);
    }

    @Override
    public BindVariableValue bindVariable(String bindVariableName) throws InvalidQueryException, RepositoryException {
        if (bindVariableName == null) {
            throw new InvalidQueryException("bindVariableName must not be null");
        }
        try {
            return new BindVariableValueImpl(this.resolver, this.resolver.getQName(bindVariableName));
        }
        catch (NameException e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    @Override
    public Literal literal(Value value) throws InvalidQueryException, RepositoryException {
        if (value == null) {
            throw new InvalidQueryException("value must not be null");
        }
        return new LiteralImpl(this.resolver, value);
    }

    @Override
    public Ordering ascending(DynamicOperand operand) throws InvalidQueryException, RepositoryException {
        if (!(operand instanceof DynamicOperandImpl)) {
            throw new RepositoryException("Unknown DynamicOperand implementation");
        }
        return new OrderingImpl(this.resolver, (DynamicOperandImpl)operand, "jcr.order.ascending");
    }

    @Override
    public Ordering descending(DynamicOperand operand) throws InvalidQueryException, RepositoryException {
        if (!(operand instanceof DynamicOperandImpl)) {
            throw new RepositoryException("Unknown DynamicOperand implementation");
        }
        return new OrderingImpl(this.resolver, (DynamicOperandImpl)operand, "jcr.order.descending");
    }

    public Column column(String propertyName) throws InvalidQueryException, RepositoryException {
        Name propName = null;
        if (propertyName != null) {
            try {
                propName = this.resolver.getQName(propertyName);
            }
            catch (NameException e) {
                throw new InvalidQueryException(e.getMessage());
            }
        }
        return new ColumnImpl(this.resolver, null, propName, propertyName);
    }

    public Column column(String propertyName, String columnName) throws InvalidQueryException, RepositoryException {
        if (propertyName == null && columnName != null) {
            throw new InvalidQueryException("columnName must be null if propertyName is null");
        }
        Name propName = null;
        if (propertyName != null) {
            try {
                propName = this.resolver.getQName(propertyName);
            }
            catch (NameException e) {
                throw new InvalidQueryException(e.getMessage());
            }
        }
        return new ColumnImpl(this.resolver, null, propName, columnName);
    }

    @Override
    public Column column(String selectorName, String propertyName, String columnName) throws InvalidQueryException, RepositoryException {
        if (propertyName == null && columnName != null) {
            throw new InvalidQueryException("columnName must be null if propertyName is null");
        }
        Name propName = null;
        if (propertyName != null) {
            try {
                propName = this.resolver.getQName(propertyName);
            }
            catch (NameException e) {
                throw new InvalidQueryException(e.getMessage());
            }
        }
        return new ColumnImpl(this.resolver, this.checkSelectorName(selectorName), propName, columnName);
    }

    private Name checkSelectorName(String selectorName) throws RepositoryException {
        if (selectorName == null) {
            throw new InvalidQueryException("selectorName must not be null");
        }
        try {
            return this.resolver.getQName(selectorName);
        }
        catch (NameException e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    private Name checkNodeTypeName(String nodeTypeName) throws RepositoryException {
        if (nodeTypeName == null) {
            throw new InvalidQueryException("nodeTypeName must not be null");
        }
        try {
            return this.resolver.getQName(nodeTypeName);
        }
        catch (NameException e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    private Path checkPath(String path) throws RepositoryException {
        if (path == null) {
            throw new InvalidQueryException("path must not be null");
        }
        try {
            return this.resolver.getQPath(path);
        }
        catch (NameException e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    private Name checkPropertyName(String propertyName) throws RepositoryException {
        if (propertyName == null) {
            throw new InvalidQueryException("propertyName must not be null");
        }
        try {
            return this.resolver.getQName(propertyName);
        }
        catch (NameException e) {
            throw new InvalidQueryException(e.getMessage());
        }
    }

    private StaticOperand checkFullTextSearchExpression(StaticOperand fullTextSearchExpression) throws RepositoryException {
        if (fullTextSearchExpression == null) {
            throw new InvalidQueryException("fullTextSearchExpression must not be null");
        }
        return fullTextSearchExpression;
    }
}

