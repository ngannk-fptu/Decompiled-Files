/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

final class ParentheticalExpression
extends Expression {
    private final Expression nested;

    ParentheticalExpression(Expression nested) {
        this.nested = nested;
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        return this.nested.evalToBoolean(env);
    }

    @Override
    public String getCanonicalForm() {
        return "(" + this.nested.getCanonicalForm() + ")";
    }

    @Override
    String getNodeTypeSymbol() {
        return "(...)";
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.nested.eval(env);
    }

    @Override
    public boolean isLiteral() {
        return this.nested.isLiteral();
    }

    Expression getNestedExpression() {
        return this.nested;
    }

    @Override
    void enableLazilyGeneratedResult() {
        this.nested.enableLazilyGeneratedResult();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ParentheticalExpression(this.nested.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
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
        return this.nested;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.ENCLOSED_OPERAND;
    }
}

