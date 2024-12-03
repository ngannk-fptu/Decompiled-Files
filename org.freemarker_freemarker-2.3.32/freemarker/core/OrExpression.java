/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BooleanExpression;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;

final class OrExpression
extends BooleanExpression {
    private final Expression lho;
    private final Expression rho;

    OrExpression(Expression lho, Expression rho) {
        this.lho = lho;
        this.rho = rho;
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return this.lho.evalToBoolean(env) || this.rho.evalToBoolean(env);
    }

    @Override
    public String getCanonicalForm() {
        return this.lho.getCanonicalForm() + " || " + this.rho.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return "||";
    }

    @Override
    boolean isLiteral() {
        return this.constantValue != null || this.lho.isLiteral() && this.rho.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new OrExpression(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.lho;
            }
            case 1: {
                return this.rho;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }
}

