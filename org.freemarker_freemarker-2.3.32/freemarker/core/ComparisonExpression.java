/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BooleanExpression;
import freemarker.core.BugException;
import freemarker.core.BuiltInsForMultipleTypes;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.MiscUtil;
import freemarker.core.NumberLiteral;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;

final class ComparisonExpression
extends BooleanExpression {
    private final Expression left;
    private final Expression right;
    private final int operation;
    private final String opString;

    ComparisonExpression(Expression left, Expression right, String opString) {
        this.left = left;
        this.right = right;
        this.opString = opString = opString.intern();
        if (opString == "==" || opString == "=") {
            this.operation = 1;
        } else if (opString == "!=") {
            this.operation = 2;
        } else if (opString == "gt" || opString == "\\gt" || opString == ">" || opString == "&gt;") {
            this.operation = 4;
        } else if (opString == "gte" || opString == "\\gte" || opString == ">=" || opString == "&gt;=") {
            this.operation = 6;
        } else if (opString == "lt" || opString == "\\lt" || opString == "<" || opString == "&lt;") {
            this.operation = 3;
        } else if (opString == "lte" || opString == "\\lte" || opString == "<=" || opString == "&lt;=") {
            this.operation = 5;
        } else {
            throw new BugException("Unknown comparison operator " + opString);
        }
        Expression cleanedLeft = MiscUtil.peelParentheses(left);
        Expression cleanedRight = MiscUtil.peelParentheses(right);
        if (cleanedLeft instanceof BuiltInsForMultipleTypes.sizeBI) {
            if (cleanedRight instanceof NumberLiteral) {
                ((BuiltInsForMultipleTypes.sizeBI)cleanedLeft).setCountingLimit(this.operation, (NumberLiteral)cleanedRight);
            }
        } else if (cleanedRight instanceof BuiltInsForMultipleTypes.sizeBI && cleanedLeft instanceof NumberLiteral) {
            ((BuiltInsForMultipleTypes.sizeBI)cleanedRight).setCountingLimit(EvalUtil.mirrorCmpOperator(this.operation), (NumberLiteral)cleanedLeft);
        }
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return EvalUtil.compare(this.left, this.operation, this.opString, this.right, this, env);
    }

    @Override
    public String getCanonicalForm() {
        return this.left.getCanonicalForm() + ' ' + this.opString + ' ' + this.right.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.opString;
    }

    @Override
    boolean isLiteral() {
        return this.constantValue != null || this.left.isLiteral() && this.right.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ComparisonExpression(this.left.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.opString);
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        return idx == 0 ? this.left : this.right;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }
}

