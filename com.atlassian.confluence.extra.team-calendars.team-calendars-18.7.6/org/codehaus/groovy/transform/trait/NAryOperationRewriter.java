/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.trait;

import java.util.Collection;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.TokenUtil;

class NAryOperationRewriter
extends ClassCodeExpressionTransformer {
    private final SourceUnit sourceUnit;
    private final Collection<String> knownFields;

    public NAryOperationRewriter(SourceUnit sourceUnit, Collection<String> knownFields) {
        this.sourceUnit = sourceUnit;
        this.knownFields = knownFields;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp instanceof BinaryExpression) {
            return this.transformBinaryExpression((BinaryExpression)exp);
        }
        if (exp instanceof PrefixExpression) {
            return this.transformPrefixExpression((PrefixExpression)exp);
        }
        if (exp instanceof PostfixExpression) {
            return this.transformPostfixExpression((PostfixExpression)exp);
        }
        return super.transform(exp);
    }

    private boolean isInternalFieldAccess(Expression exp) {
        Variable accessedVariable;
        if (exp instanceof VariableExpression && (accessedVariable = ((VariableExpression)exp).getAccessedVariable()) instanceof FieldNode) {
            return this.knownFields.contains(accessedVariable.getName());
        }
        if (exp instanceof PropertyExpression && (((PropertyExpression)exp).isImplicitThis() || "this".equals(((PropertyExpression)exp).getObjectExpression().getText()))) {
            return this.knownFields.contains(((PropertyExpression)exp).getProperty().getText());
        }
        return false;
    }

    private Expression transformPrefixExpression(PrefixExpression exp) {
        if (this.isInternalFieldAccess(exp.getExpression())) {
            Token operation = exp.getOperation();
            this.sourceUnit.addError(new SyntaxException("Prefix expressions on trait fields/properties are not supported in traits.", operation.getStartLine(), operation.getStartColumn()));
            return exp;
        }
        return super.transform(exp);
    }

    private Expression transformPostfixExpression(PostfixExpression exp) {
        if (this.isInternalFieldAccess(exp.getExpression())) {
            Token operation = exp.getOperation();
            this.sourceUnit.addError(new SyntaxException("Postfix expressions on trait fields/properties  are not supported in traits.", operation.getStartLine(), operation.getStartColumn()));
            return exp;
        }
        return super.transform(exp);
    }

    private Expression transformBinaryExpression(BinaryExpression exp) {
        int op = exp.getOperation().getType();
        int token = TokenUtil.removeAssignment(op);
        if (token == op) {
            return super.transform(exp);
        }
        BinaryExpression operation = new BinaryExpression(exp.getLeftExpression(), Token.newSymbol(token, -1, -1), exp.getRightExpression());
        operation.setSourcePosition(exp);
        BinaryExpression result = new BinaryExpression(exp.getLeftExpression(), Token.newSymbol(100, -1, -1), operation);
        result.setSourcePosition(exp);
        return result;
    }
}

