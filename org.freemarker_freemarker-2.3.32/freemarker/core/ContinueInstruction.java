/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BreakOrContinueException;
import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;

final class ContinueInstruction
extends TemplateElement {
    ContinueInstruction() {
    }

    @Override
    TemplateElement[] accept(Environment env) {
        throw BreakOrContinueException.CONTINUE_INSTANCE;
    }

    @Override
    protected String dump(boolean canonical) {
        return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#continue";
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

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

