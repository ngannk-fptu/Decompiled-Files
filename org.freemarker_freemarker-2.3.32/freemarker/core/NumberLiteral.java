/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

final class NumberLiteral
extends Expression
implements TemplateNumberModel {
    private final Number value;

    public NumberLiteral(Number value) {
        this.value = value;
    }

    @Override
    TemplateModel _eval(Environment env) {
        return new SimpleNumber(this.value);
    }

    @Override
    public String evalAndCoerceToPlainText(Environment env) throws TemplateException {
        return env.formatNumberToPlainText(this, this, false);
    }

    @Override
    public Number getAsNumber() {
        return this.value;
    }

    String getName() {
        return "the number: '" + this.value + "'";
    }

    @Override
    public String getCanonicalForm() {
        return this.value.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.getCanonicalForm();
    }

    @Override
    boolean isLiteral() {
        return true;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new NumberLiteral(this.value);
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

