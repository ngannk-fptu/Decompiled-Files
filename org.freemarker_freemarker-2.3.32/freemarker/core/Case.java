/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;

final class Case
extends TemplateElement {
    static final int TYPE_CASE = 0;
    static final int TYPE_DEFAULT = 1;
    Expression condition;

    Case(Expression matchingValue, TemplateElements children) {
        this.condition = matchingValue;
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (this.condition != null) {
            sb.append(' ');
            sb.append(this.condition.getCanonicalForm());
        }
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.condition != null ? "#case" : "#default";
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
                return this.condition != null ? 0 : 1;
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

