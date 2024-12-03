/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.FlowControlException;
import freemarker.core.Macro;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;

public final class ReturnInstruction
extends TemplateElement {
    private Expression exp;

    ReturnInstruction(Expression exp) {
        this.exp = exp;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException {
        if (this.exp != null) {
            env.setLastReturnValue(this.exp.eval(env));
        }
        if (this.nextSibling() == null && this.getParentElement() instanceof Macro) {
            return null;
        }
        throw Return.INSTANCE;
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
        return "#return";
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
        return ParameterRole.VALUE;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    public static class Return
    extends FlowControlException {
        static final Return INSTANCE = new Return();

        private Return() {
        }
    }
}

