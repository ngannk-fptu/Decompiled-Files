/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.Arrays;
import java.util.Locale;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.ast.TypeDiscriminatorMetadata;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.FunctionNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class MethodNode
extends AbstractSelectExpression
implements FunctionNode {
    private static final Logger LOG = CoreLogging.logger(MethodNode.class);
    private String methodName;
    private FromElement fromElement;
    private String[] selectColumns;
    private SQLFunction function;
    private boolean inSelect;

    @Override
    public boolean isScalar() throws SemanticException {
        return true;
    }

    @Override
    public SQLFunction getSQLFunction() {
        return this.function;
    }

    @Override
    public Type getFirstArgumentType() {
        AST argument = this.getFirstChild();
        while (argument != null) {
            if (!(argument instanceof SqlNode)) continue;
            Type type = ((SqlNode)argument).getDataType();
            if (type != null) {
                return type;
            }
            argument = argument.getNextSibling();
        }
        return null;
    }

    public void resolve(boolean inSelect) throws SemanticException {
        AST nameNode = this.getFirstChild();
        AST exprListNode = nameNode.getNextSibling();
        this.initializeMethodNode(nameNode, inSelect);
        if (ASTUtil.hasExactlyOneChild(exprListNode)) {
            if ("type".equals(this.methodName)) {
                this.typeDiscriminator(exprListNode.getFirstChild());
                return;
            }
            if (this.isCollectionPropertyMethod()) {
                this.collectionProperty(exprListNode.getFirstChild(), nameNode);
                return;
            }
        }
        this.dialectFunction(exprListNode);
    }

    public void initializeMethodNode(AST name, boolean inSelect) {
        name.setType(155);
        String text = name.getText();
        this.methodName = text.toLowerCase(Locale.ROOT);
        this.inSelect = inSelect;
    }

    private void typeDiscriminator(AST path) throws SemanticException {
        if (path == null) {
            throw new SemanticException("type() discriminator reference has no path!");
        }
        FromReferenceNode pathAsFromReferenceNode = (FromReferenceNode)path;
        FromElement fromElement = pathAsFromReferenceNode.getFromElement();
        TypeDiscriminatorMetadata typeDiscriminatorMetadata = fromElement.getTypeDiscriminatorMetadata();
        this.setDataType(typeDiscriminatorMetadata.getResolutionType());
        this.setText(typeDiscriminatorMetadata.getSqlFragment());
        this.setType(150);
    }

    private void dialectFunction(AST exprList) {
        this.function = this.getSessionFactoryHelper().findSQLFunction(this.methodName);
        if (this.function != null) {
            AST firstChild = exprList != null ? exprList.getFirstChild() : null;
            Type functionReturnType = this.getSessionFactoryHelper().findFunctionReturnType(this.methodName, this.function, firstChild);
            this.setDataType(functionReturnType);
        }
    }

    public boolean isCollectionPropertyMethod() {
        return CollectionProperties.isAnyCollectionProperty(this.methodName);
    }

    private void collectionProperty(AST path, AST name) throws SemanticException {
        if (path == null) {
            throw new SemanticException("Collection function " + name.getText() + " has no path!");
        }
        SqlNode expr = (SqlNode)path;
        Type type = expr.getDataType();
        LOG.debugf("collectionProperty() :  name=%s type=%s", (Object)name, (Object)type);
        this.resolveCollectionProperty((AST)expr);
    }

    protected void resolveCollectionProperty(AST expr) throws SemanticException {
        String propertyName = CollectionProperties.getNormalizedPropertyName(this.methodName);
        if (expr instanceof FromReferenceNode) {
            FromReferenceNode collectionNode = (FromReferenceNode)expr;
            this.fromElement = collectionNode.getFromElement();
            if ("elements".equals(propertyName)) {
                QueryableCollection queryableCollection = this.fromElement.getQueryableCollection();
                String path = collectionNode.getPath() + "[]." + propertyName;
                LOG.debugf("Creating elements for %s", (Object)path);
                if (!this.fromElement.isCollectionOfValuesOrComponents()) {
                    this.getWalker().addQuerySpaces(queryableCollection.getElementPersister().getQuerySpaces());
                }
                this.setDataType(queryableCollection.getElementType());
                this.selectColumns = this.fromElement.toColumns(this.fromElement.getTableAlias(), propertyName, this.inSelect);
            } else {
                this.setDataType(this.fromElement.getPropertyType(propertyName, propertyName));
                this.selectColumns = this.fromElement.toColumns(this.fromElement.getTableAlias(), propertyName, this.inSelect);
            }
            if (collectionNode instanceof DotNode) {
                this.prepareAnyImplicitJoins((DotNode)collectionNode);
            }
            if (!this.inSelect) {
                this.fromElement.setText("");
                this.fromElement.setUseWhereFragment(false);
            }
        } else {
            throw new SemanticException("Unexpected expression " + expr + " found for collection function " + propertyName);
        }
        this.prepareSelectColumns(this.selectColumns);
        this.setText(this.selectColumns[0]);
        this.setType(150);
    }

    private void prepareAnyImplicitJoins(DotNode dotNode) throws SemanticException {
        if (dotNode.getLhs() instanceof DotNode) {
            DotNode lhs = (DotNode)dotNode.getLhs();
            FromElement lhsOrigin = lhs.getFromElement();
            if (lhsOrigin != null && lhsOrigin.getText() != null && lhsOrigin.getText().isEmpty()) {
                String lhsOriginText = lhsOrigin.getQueryable().getTableName() + " " + lhsOrigin.getTableAlias();
                lhsOrigin.setText(lhsOriginText);
            }
            this.prepareAnyImplicitJoins(lhs);
        }
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        if (this.selectColumns == null) {
            ColumnHelper.generateSingleScalarColumn(this, i);
        } else {
            ColumnHelper.generateScalarColumns(this, this.selectColumns, i);
        }
    }

    protected void prepareSelectColumns(String[] columns) {
    }

    @Override
    public FromElement getFromElement() {
        return this.fromElement;
    }

    public String getDisplayText() {
        return "{method=" + this.methodName + ",selectColumns=" + (this.selectColumns == null ? null : Arrays.asList(this.selectColumns)) + ",fromElement=" + this.fromElement.getTableAlias() + "}";
    }
}

