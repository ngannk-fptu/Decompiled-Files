/*
 * Decompiled with CFR 0.152.
 */
package groovy.sql;

import groovy.lang.GroovyRuntimeException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.syntax.Token;

public class SqlWhereVisitor
extends CodeVisitorSupport {
    private final StringBuffer buffer = new StringBuffer();
    private final List<Object> parameters = new ArrayList<Object>();

    public String getWhere() {
        return this.buffer.toString();
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().visit(this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        boolean leaf;
        Expression left = expression.getLeftExpression();
        Expression right = expression.getRightExpression();
        boolean bl = leaf = right instanceof ConstantExpression || left instanceof ConstantExpression;
        if (!leaf) {
            this.buffer.append("(");
        }
        left.visit(this);
        this.buffer.append(" ");
        Token token = expression.getOperation();
        this.buffer.append(this.tokenAsSql(token));
        this.buffer.append(" ");
        right.visit(this);
        if (!leaf) {
            this.buffer.append(")");
        }
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        expression.getExpression().visit(this);
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        this.getParameters().add(expression.getValue());
        this.buffer.append("?");
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        this.buffer.append(expression.getPropertyAsString());
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        throw new GroovyRuntimeException("DataSet currently doesn't support arbitrary variables, only literals: found attempted reference to variable '" + expression.getName() + "'");
    }

    public List<Object> getParameters() {
        return this.parameters;
    }

    protected String tokenAsSql(Token token) {
        switch (token.getType()) {
            case 123: {
                return "=";
            }
            case 164: {
                return "and";
            }
            case 162: {
                return "or";
            }
        }
        return token.getText();
    }
}

