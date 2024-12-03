/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.IfBlock;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.template.TemplateException;
import java.io.IOException;

final class ConditionalBlock
extends TemplateElement {
    static final int TYPE_IF = 0;
    static final int TYPE_ELSE = 1;
    static final int TYPE_ELSE_IF = 2;
    final Expression condition;
    private final int type;

    ConditionalBlock(Expression condition, TemplateElements children, int type) {
        this.condition = condition;
        this.setChildren(children);
        this.type = type;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        if (this.condition == null || this.condition.evalToBoolean(env)) {
            return this.getChildBuffer();
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(this.getNodeTypeSymbol());
        if (this.condition != null) {
            buf.append(' ');
            buf.append(this.condition.getCanonicalForm());
        }
        if (canonical) {
            buf.append(">");
            buf.append(this.getChildrenCanonicalForm());
            if (!(this.getParentElement() instanceof IfBlock)) {
                buf.append("</#if>");
            }
        }
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        if (this.type == 1) {
            return "#else";
        }
        if (this.type == 0) {
            return "#if";
        }
        if (this.type == 2) {
            return "#elseif";
        }
        throw new BugException("Unknown type");
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.condition;
            }
            case 1: {
                return this.type;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.CONDITION;
            }
            case 1: {
                return ParameterRole.AST_NODE_SUBTYPE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

