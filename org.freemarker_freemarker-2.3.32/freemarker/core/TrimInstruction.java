/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;

final class TrimInstruction
extends TemplateElement {
    static final int TYPE_T = 0;
    static final int TYPE_LT = 1;
    static final int TYPE_RT = 2;
    static final int TYPE_NT = 3;
    final boolean left;
    final boolean right;

    TrimInstruction(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    @Override
    TemplateElement[] accept(Environment env) {
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        if (this.left && this.right) {
            return "#t";
        }
        if (this.left) {
            return "#lt";
        }
        if (this.right) {
            return "#rt";
        }
        return "#nt";
    }

    @Override
    boolean isIgnorable(boolean stripWhitespace) {
        return true;
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
        int type = this.left && this.right ? 0 : (this.left ? 1 : (this.right ? 2 : 3));
        return type;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.AST_NODE_SUBTYPE;
    }

    @Override
    boolean isOutputCacheable() {
        return true;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

