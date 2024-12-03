/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class ExpressionWithFixedResult
extends Expression {
    private final TemplateModel fixedResult;
    private final Expression sourceExpression;

    ExpressionWithFixedResult(TemplateModel fixedResult, Expression sourceExpression) {
        this.fixedResult = fixedResult;
        this.sourceExpression = sourceExpression;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.fixedResult;
    }

    @Override
    boolean isLiteral() {
        return this.sourceExpression.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ExpressionWithFixedResult(this.fixedResult, this.sourceExpression.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    public String getCanonicalForm() {
        return this.sourceExpression.getCanonicalForm();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.sourceExpression.getNodeTypeSymbol();
    }

    @Override
    int getParameterCount() {
        return this.sourceExpression.getParameterCount();
    }

    @Override
    Object getParameterValue(int idx) {
        return this.sourceExpression.getParameterValue(idx);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return this.sourceExpression.getParameterRole(idx);
    }
}

