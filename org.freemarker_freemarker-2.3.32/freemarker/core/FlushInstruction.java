/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import java.io.IOException;

final class FlushInstruction
extends TemplateElement {
    FlushInstruction() {
    }

    @Override
    TemplateElement[] accept(Environment env) throws IOException {
        env.getOut().flush();
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#flush";
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

