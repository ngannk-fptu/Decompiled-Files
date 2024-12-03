/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.syntax.Token;

public class BinaryExpression
extends Expression {
    private Expression leftExpression;
    private Expression rightExpression;
    private final Token operation;

    public BinaryExpression(Expression leftExpression, Token operation, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.operation = operation;
        this.rightExpression = rightExpression;
    }

    public String toString() {
        return super.toString() + "[" + this.leftExpression + this.operation + this.rightExpression + "]";
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        BinaryExpression ret = new BinaryExpression(transformer.transform(this.leftExpression), this.operation, transformer.transform(this.rightExpression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public Expression getLeftExpression() {
        return this.leftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {
        this.leftExpression = leftExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        this.rightExpression = rightExpression;
    }

    public Token getOperation() {
        return this.operation;
    }

    public Expression getRightExpression() {
        return this.rightExpression;
    }

    @Override
    public String getText() {
        if (this.operation.getType() == 30) {
            return this.leftExpression.getText() + "[" + this.rightExpression.getText() + "]";
        }
        return "(" + this.leftExpression.getText() + " " + this.operation.getText() + " " + this.rightExpression.getText() + ")";
    }

    public static BinaryExpression newAssignmentExpression(Variable variable, Expression rhs) {
        VariableExpression lhs = new VariableExpression(variable);
        Token operator = Token.newPlaceholder(100);
        return new BinaryExpression(lhs, operator, rhs);
    }

    public static BinaryExpression newInitializationExpression(String variable, ClassNode type, Expression rhs) {
        VariableExpression lhs = new VariableExpression(variable);
        if (type != null) {
            lhs.setType(type);
        }
        Token operator = Token.newPlaceholder(100);
        return new BinaryExpression(lhs, operator, rhs);
    }
}

