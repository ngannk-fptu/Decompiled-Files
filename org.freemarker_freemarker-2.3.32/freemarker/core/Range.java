/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BoundedRangeModel;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ListableRightUnboundedRangeModel;
import freemarker.core.NonBooleanException;
import freemarker.core.NonListableRightUnboundedRangeModel;
import freemarker.core.ParameterRole;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;

final class Range
extends Expression {
    static final int END_INCLUSIVE = 0;
    static final int END_EXCLUSIVE = 1;
    static final int END_UNBOUND = 2;
    static final int END_SIZE_LIMITED = 3;
    final Expression lho;
    final Expression rho;
    final int endType;

    Range(Expression lho, Expression rho, int endType) {
        this.lho = lho;
        this.rho = rho;
        this.endType = endType;
    }

    int getEndType() {
        return this.endType;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        int begin = this.lho.evalToNumber(env).intValue();
        if (this.endType != 2) {
            int lhoValue = this.rho.evalToNumber(env).intValue();
            return new BoundedRangeModel(begin, this.endType != 3 ? lhoValue : begin + lhoValue, this.endType == 0, this.endType == 3);
        }
        return _TemplateAPI.getTemplateLanguageVersionAsInt(this) >= _VersionInts.V_2_3_21 ? new ListableRightUnboundedRangeModel(begin) : new NonListableRightUnboundedRangeModel(begin);
    }

    @Override
    boolean evalToBoolean(Environment env) throws TemplateException {
        throw new NonBooleanException(this, new BoundedRangeModel(0, 0, false, false), env);
    }

    @Override
    public String getCanonicalForm() {
        String rhs = this.rho != null ? this.rho.getCanonicalForm() : "";
        return this.lho.getCanonicalForm() + this.getNodeTypeSymbol() + rhs;
    }

    @Override
    String getNodeTypeSymbol() {
        switch (this.endType) {
            case 1: {
                return "..<";
            }
            case 0: {
                return "..";
            }
            case 2: {
                return "..";
            }
            case 3: {
                return "..*";
            }
        }
        throw new BugException(this.endType);
    }

    @Override
    boolean isLiteral() {
        boolean rightIsLiteral = this.rho == null || this.rho.isLiteral();
        return this.constantValue != null || this.lho.isLiteral() && rightIsLiteral;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new Range(this.lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.endType);
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

