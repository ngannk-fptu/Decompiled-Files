/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;

final class BooleanLiteral
extends Expression {
    private final boolean val;

    public BooleanLiteral(boolean val) {
        this.val = val;
    }

    static TemplateBooleanModel getTemplateModel(boolean b) {
        return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    @Override
    boolean evalToBoolean(Environment env) {
        return this.val;
    }

    @Override
    public String getCanonicalForm() {
        return this.val ? "true" : "false";
    }

    @Override
    String getNodeTypeSymbol() {
        return this.getCanonicalForm();
    }

    @Override
    public String toString() {
        return this.val ? "true" : "false";
    }

    @Override
    TemplateModel _eval(Environment env) {
        return this.val ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    @Override
    boolean isLiteral() {
        return true;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new BooleanLiteral(this.val);
    }

    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
}

