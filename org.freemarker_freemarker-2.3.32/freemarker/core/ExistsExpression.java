/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.ParameterRole;
import freemarker.core.ParentheticalExpression;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class ExistsExpression
extends Expression {
    protected final Expression exp;

    ExistsExpression(Expression exp) {
        this.exp = exp;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel tm;
        if (this.exp instanceof ParentheticalExpression) {
            boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
            try {
                tm = this.exp.eval(env);
            }
            catch (InvalidReferenceException ire) {
                tm = null;
            }
            finally {
                env.setFastInvalidReferenceExceptions(lastFIRE);
            }
        } else {
            tm = this.exp.eval(env);
        }
        return tm == null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
    }

    @Override
    boolean isLiteral() {
        return false;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        return new ExistsExpression(this.exp.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }

    @Override
    public String getCanonicalForm() {
        return this.exp.getCanonicalForm() + this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "??";
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        return this.exp;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        return ParameterRole.LEFT_HAND_OPERAND;
    }
}

