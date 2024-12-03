/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class ArrayExpression
extends Expression {
    private List<Expression> expressions;
    private List<Expression> sizeExpression;
    private ClassNode elementType;

    private static ClassNode makeArray(ClassNode base, List<Expression> sizeExpression) {
        ClassNode ret = base.makeArray();
        if (sizeExpression == null) {
            return ret;
        }
        int size = sizeExpression.size();
        for (int i = 1; i < size; ++i) {
            ret = ret.makeArray();
        }
        return ret;
    }

    public ArrayExpression(ClassNode elementType, List<Expression> expressions, List<Expression> sizeExpression) {
        super.setType(ArrayExpression.makeArray(elementType, sizeExpression));
        if (expressions == null) {
            expressions = Collections.emptyList();
        }
        this.elementType = elementType;
        this.expressions = expressions;
        this.sizeExpression = sizeExpression;
        for (Expression item : expressions) {
            if (item == null || item instanceof Expression) continue;
            throw new ClassCastException("Item: " + item + " is not an Expression");
        }
        if (sizeExpression != null) {
            for (Expression item : sizeExpression) {
                if (item instanceof Expression) continue;
                throw new ClassCastException("Item: " + item + " is not an Expression");
            }
        }
    }

    public ArrayExpression(ClassNode elementType, List<Expression> expressions) {
        this(elementType, expressions, null);
    }

    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitArrayExpression(this);
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        List<Expression> exprList = this.transformExpressions(this.expressions, transformer);
        List<Expression> sizes = null;
        if (this.sizeExpression != null) {
            sizes = this.transformExpressions(this.sizeExpression, transformer);
        }
        ArrayExpression ret = new ArrayExpression(this.elementType, exprList, sizes);
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public Expression getExpression(int i) {
        return this.expressions.get(i);
    }

    public ClassNode getElementType() {
        return this.elementType;
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
        for (Expression expression : this.expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(expression.getText());
        }
        buffer.append("]");
        return buffer.toString();
    }

    public List<Expression> getSizeExpression() {
        return this.sizeExpression;
    }

    public String toString() {
        return super.toString() + this.expressions;
    }
}

