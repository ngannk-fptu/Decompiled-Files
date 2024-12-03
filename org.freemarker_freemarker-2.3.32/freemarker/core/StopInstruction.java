/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.StopException;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;

final class StopInstruction
extends TemplateElement {
    private Expression exp;

    StopInstruction(Expression exp) {
        this.exp = exp;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException {
        if (this.exp == null) {
            throw new StopException(env);
        }
        throw new StopException(env, this.exp.evalAndCoerceToPlainText(env));
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (this.exp != null) {
            sb.append(' ');
            sb.append(this.exp.getCanonicalForm());
        }
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#stop";
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
        return this.exp;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.MESSAGE;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

