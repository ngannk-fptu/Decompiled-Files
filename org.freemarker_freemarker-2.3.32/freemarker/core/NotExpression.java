/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BooleanExpression;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;

final class NotExpression
extends BooleanExpression {
    private final Expression target;

    NotExpression(Expression target) {
        this.target = target;
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return !this.target.evalToBoolean(env);
    }

    @Override
    public String getCanonicalForm() {
        return "!" + this.target.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return "!";
    }

    @Override
    boolean isLiteral() {
        return this.target.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new NotExpression(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.target;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.RIGHT_HAND_OPERAND;
    }
}

