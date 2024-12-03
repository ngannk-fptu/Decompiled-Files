/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.template.TemplateException;
import java.io.IOException;

final class RecoveryBlock
extends TemplateElement {
    RecoveryBlock(TemplateElements children) {
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder buf = new StringBuilder();
            buf.append('<').append(this.getNodeTypeSymbol()).append('>');
            buf.append(this.getChildrenCanonicalForm());
            return buf.toString();
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#recover";
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

