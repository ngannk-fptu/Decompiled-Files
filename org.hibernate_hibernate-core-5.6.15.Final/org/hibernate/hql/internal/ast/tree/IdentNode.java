/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.internal.ast.tree.AbstractMapComponentNode;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementFactory;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.IndexNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public class IdentNode
extends FromReferenceNode
implements SelectExpression {
    private boolean nakedPropertyRef;
    private boolean fromClauseAlias;
    private String[] columns;

    public String[] getColumns() {
        return this.columns;
    }

    @Override
    public void resolveIndex(AST parent) throws SemanticException {
        if (!this.isResolved() || !this.nakedPropertyRef) {
            throw new UnsupportedOperationException();
        }
        String propertyName = this.getOriginalText();
        if (!this.getDataType().isCollectionType()) {
            throw new SemanticException("Collection expected; [" + propertyName + "] does not refer to a collection property");
        }
        CollectionType type = (CollectionType)this.getDataType();
        String role = type.getRole();
        QueryableCollection queryableCollection = this.getSessionFactoryHelper().requireQueryableCollection(role);
        String alias = null;
        String columnTableAlias = this.getFromElement().getTableAlias();
        JoinType joinType = JoinType.INNER_JOIN;
        boolean fetch = false;
        FromElementFactory factory = new FromElementFactory(this.getWalker().getCurrentFromClause(), this.getFromElement(), propertyName, alias, this.getFromElement().toColumns(columnTableAlias, propertyName, false), true);
        FromElement elem = factory.createCollection(queryableCollection, role, joinType, fetch, true);
        this.setFromElement(elem);
        this.getWalker().addQuerySpaces(queryableCollection.getCollectionSpaces());
    }

    protected String[] resolveColumns(QueryableCollection collectionPersister) {
        FromElement fromElement = this.getFromElement();
        return fromElement.toColumns(fromElement.getCollectionTableAlias(), "elements", this.getWalker().isInSelect());
    }

    private void initText(String[] columns) {
        String text = String.join((CharSequence)", ", columns);
        if (columns.length > 1 && this.getWalker().isComparativeExpressionClause()) {
            text = "(" + text + ")";
        }
        this.setText(text);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) {
        if (this.isResolved()) {
            return;
        }
        if (this.getWalker().getCurrentFromClause().isFromElementAlias(this.getText())) {
            FromElement fromElement = this.getWalker().getCurrentFromClause().getFromElement(this.getText());
            if (fromElement.getQueryableCollection() != null && fromElement.getQueryableCollection().getElementType().isComponentType()) {
                if (this.getWalker().isInSelect()) {
                    this.setFromElement(fromElement);
                    super.setDataType(fromElement.getQueryableCollection().getElementType());
                    this.columns = this.resolveColumns(fromElement.getQueryableCollection());
                    this.initText(this.getColumns());
                    this.setFirstChild(null);
                } else {
                    this.resolveAsAlias();
                }
            } else if (this.resolveAsAlias()) {
                this.setResolved();
            }
        } else if (this.getColumns() != null && (this.getWalker().getAST() instanceof AbstractMapComponentNode || this.getWalker().getAST() instanceof IndexNode) && this.getWalker().getCurrentFromClause().isFromElementAlias(this.getOriginalText())) {
            this.setText(this.getOriginalText());
            if (this.resolveAsAlias()) {
                this.setResolved();
            }
        } else if (parent != null && parent.getType() == 15) {
            DotNode dot = (DotNode)parent;
            if (parent.getFirstChild() == this) {
                if (this.resolveAsNakedComponentPropertyRefLHS(dot)) {
                    this.setResolved();
                }
            } else if (this.resolveAsNakedComponentPropertyRefRHS(dot)) {
                this.setResolved();
            }
        } else {
            DereferenceType result = this.resolveAsNakedPropertyRef();
            if (result == DereferenceType.PROPERTY_REF) {
                this.setResolved();
            } else if (result == DereferenceType.COMPONENT_REF) {
                return;
            }
        }
        if (!this.isResolved()) {
            try {
                this.getWalker().getLiteralProcessor().processConstant((AST)this, false);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private boolean resolveAsAlias() {
        boolean isCompositeValue;
        String alias = this.getText();
        FromElement element = this.getWalker().getCurrentFromClause().getFromElement(alias);
        if (element == null) {
            return false;
        }
        element.applyTreatAsDeclarations(this.getWalker().getTreatAsDeclarationsByPath(alias));
        this.setType(148);
        this.setFromElement(element);
        CharSequence[] columnExpressions = element.getIdentityColumns();
        Dialect dialect = this.getWalker().getSessionFactoryHelper().getFactory().getDialect();
        boolean isInCount = this.getWalker().isInCount();
        boolean isInDistinctCount = isInCount && this.getWalker().isInCountDistinct();
        boolean isInNonDistinctCount = isInCount && !this.getWalker().isInCountDistinct();
        boolean bl = isCompositeValue = columnExpressions.length > 1;
        if (isCompositeValue) {
            if (isInNonDistinctCount && !dialect.supportsTupleCounts()) {
                this.setText((String)columnExpressions[0]);
            } else {
                boolean shouldSkipWrappingInParenthesis;
                String joinedFragment = String.join((CharSequence)", ", columnExpressions);
                boolean bl2 = shouldSkipWrappingInParenthesis = isInDistinctCount && !dialect.requiresParensForTupleDistinctCounts() || isInNonDistinctCount || this.getWalker().isInSelect() && !this.getWalker().isInCase() && !isInCount && dialect.supportsTuplesInSubqueries() || this.getWalker().getCurrentTopLevelClauseType() == 42 || this.getWalker().getCurrentTopLevelClauseType() == 25;
                if (!shouldSkipWrappingInParenthesis) {
                    joinedFragment = "(" + joinedFragment + ")";
                }
                this.setText(joinedFragment);
            }
            return true;
        }
        if (columnExpressions.length > 0) {
            this.setText((String)columnExpressions[0]);
            return true;
        }
        return false;
    }

    private Type getNakedPropertyType(FromElement fromElement) {
        if (fromElement == null) {
            return null;
        }
        String property = this.getOriginalText();
        Type propertyType = null;
        try {
            propertyType = fromElement.getPropertyType(property, property);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return propertyType;
    }

    private DereferenceType resolveAsNakedPropertyRef() {
        FromElement fromElement = this.locateSingleFromElement();
        if (fromElement == null) {
            return DereferenceType.UNKNOWN;
        }
        Queryable persister = fromElement.getQueryable();
        if (persister == null) {
            return DereferenceType.UNKNOWN;
        }
        Type propertyType = this.getNakedPropertyType(fromElement);
        if (propertyType == null) {
            return DereferenceType.UNKNOWN;
        }
        if (propertyType.isComponentType() || propertyType.isAssociationType()) {
            return DereferenceType.COMPONENT_REF;
        }
        this.setFromElement(fromElement);
        String property = this.getText();
        CharSequence[] columns = this.getWalker().isSelectStatement() ? persister.toColumns(fromElement.getTableAlias(), property) : persister.toColumns(property);
        String text = String.join((CharSequence)", ", columns);
        this.setText(columns.length == 1 ? text : "(" + text + ")");
        this.setType(150);
        super.setDataType(propertyType);
        this.nakedPropertyRef = true;
        return DereferenceType.PROPERTY_REF;
    }

    private boolean resolveAsNakedComponentPropertyRefLHS(DotNode parent) {
        Type propertyType;
        FromElement fromElement = this.locateSingleFromElement();
        if (fromElement == null) {
            return false;
        }
        Type componentType = this.getNakedPropertyType(fromElement);
        if (componentType == null) {
            throw new QueryException("Unable to resolve path [" + parent.getPath() + "], unexpected token [" + this.getOriginalText() + "]");
        }
        if (!componentType.isComponentType()) {
            throw new QueryException("Property '" + this.getOriginalText() + "' is not a component.  Use an alias to reference associations or collections.");
        }
        String propertyPath = this.getText() + "." + this.getNextSibling().getText();
        try {
            propertyType = fromElement.getPropertyType(this.getText(), propertyPath);
        }
        catch (Throwable t) {
            return false;
        }
        this.setFromElement(fromElement);
        parent.setPropertyPath(propertyPath);
        parent.setDataType(propertyType);
        return true;
    }

    private boolean resolveAsNakedComponentPropertyRefRHS(DotNode parent) {
        Type propertyType;
        FromElement fromElement = this.locateSingleFromElement();
        if (fromElement == null) {
            return false;
        }
        String propertyPath = parent.getLhs().getText() + "." + this.getText();
        try {
            propertyType = fromElement.getPropertyType(this.getText(), propertyPath);
        }
        catch (Throwable t) {
            return false;
        }
        this.setFromElement(fromElement);
        super.setDataType(propertyType);
        this.nakedPropertyRef = true;
        return true;
    }

    private FromElement locateSingleFromElement() {
        List fromElements = this.getWalker().getCurrentFromClause().getFromElements();
        if (fromElements == null || fromElements.size() != 1) {
            return null;
        }
        FromElement element = (FromElement)fromElements.get(0);
        if (element.getClassAlias() != null) {
            return null;
        }
        return element;
    }

    @Override
    public Type getDataType() {
        Type type = super.getDataType();
        if (type != null) {
            return type;
        }
        FromElement fe = this.getFromElement();
        if (fe != null) {
            return fe.getDataType();
        }
        SQLFunction sf = this.getWalker().getSessionFactoryHelper().findSQLFunction(this.getText());
        if (sf != null) {
            return sf.getReturnType(null, this.getWalker().getSessionFactoryHelper().getFactory());
        }
        return null;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        if (this.nakedPropertyRef) {
            ColumnHelper.generateSingleScalarColumn(this, i);
        } else {
            FromElement fe = this.getFromElement();
            if (fe != null) {
                if (fe.getQueryableCollection() != null && fe.getQueryableCollection().getElementType().isComponentType()) {
                    ColumnHelper.generateScalarColumns(this, this.getColumns(), i);
                } else {
                    this.setText(fe.renderScalarIdentifierSelect(i));
                }
            } else {
                ColumnHelper.generateSingleScalarColumn(this, i);
            }
        }
    }

    @Override
    public String getDisplayText() {
        StringBuilder buf = new StringBuilder();
        if (this.getType() == 148) {
            buf.append("{alias=").append(this.getOriginalText());
            if (this.getFromElement() == null) {
                buf.append(", no from element");
            } else {
                buf.append(", className=").append(this.getFromElement().getClassName());
                buf.append(", tableAlias=").append(this.getFromElement().getTableAlias());
            }
            buf.append("}");
        } else {
            buf.append("{originalText=").append(this.getOriginalText()).append("}");
        }
        return buf.toString();
    }

    private static enum DereferenceType {
        UNKNOWN,
        PROPERTY_REF,
        COMPONENT_REF;

    }
}

