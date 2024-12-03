/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Identifier;
import freemarker.core.NonHashException;
import freemarker.core.ParameterRole;
import freemarker.core._CoreStringUtils;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

final class Dot
extends Expression {
    private final Expression target;
    private final String key;

    Dot(Expression target, String key) {
        this.target = target;
        this.key = key;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel leftModel = this.target.eval(env);
        if (leftModel instanceof TemplateHashModel) {
            return ((TemplateHashModel)leftModel).get(this.key);
        }
        if (leftModel == null && env.isClassicCompatible()) {
            return null;
        }
        throw new NonHashException(this.target, leftModel, env);
    }

    @Override
    public String getCanonicalForm() {
        return this.target.getCanonicalForm() + this.getNodeTypeSymbol() + _CoreStringUtils.toFTLIdentifierReferenceAfterDot(this.key);
    }

    @Override
    String getNodeTypeSymbol() {
        return ".";
    }

    @Override
    boolean isLiteral() {
        return this.target.isLiteral();
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new Dot(this.target.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.key);
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        return idx == 0 ? this.target : this.key;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }

    String getRHO() {
        return this.key;
    }

    boolean onlyHasIdentifiers() {
        return this.target instanceof Identifier || this.target instanceof Dot && ((Dot)this.target).onlyHasIdentifiers();
    }
}

