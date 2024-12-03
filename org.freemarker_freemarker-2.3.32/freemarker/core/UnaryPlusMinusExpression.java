/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.NonNumericalException;
import freemarker.core.ParameterRole;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

final class UnaryPlusMinusExpression
extends Expression {
    private static final int TYPE_MINUS = 0;
    private static final int TYPE_PLUS = 1;
    private final Expression target;
    private final boolean isMinus;
    private static final Integer MINUS_ONE = -1;

    UnaryPlusMinusExpression(Expression target, boolean isMinus) {
        this.target = target;
        this.isMinus = isMinus;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateNumberModel targetModel = null;
        TemplateModel tm = this.target.eval(env);
        try {
            targetModel = (TemplateNumberModel)tm;
        }
        catch (ClassCastException cce) {
            throw new NonNumericalException(this.target, tm, env);
        }
        if (!this.isMinus) {
            return targetModel;
        }
        this.target.assertNonNull(targetModel, env);
        Number n = targetModel.getAsNumber();
        n = ArithmeticEngine.CONSERVATIVE_ENGINE.multiply(MINUS_ONE, n);
        return new SimpleNumber(n);
    }

    @Override
    public String getCanonicalForm() {
        String op = this.isMinus ? "-" : "+";
        return op + this.target.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.isMinus ? "-..." : "+...";
    }

    @Override
    boolean isLiteral() {
        return this.target.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new UnaryPlusMinusExpression(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.isMinus);
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.target;
            }
            case 1: {
                return this.isMinus ? 0 : 1;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.RIGHT_HAND_OPERAND;
            }
            case 1: {
                return ParameterRole.AST_NODE_SUBTYPE;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}

