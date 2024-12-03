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

final class AutoEscBlock
extends TemplateElement {
    AutoEscBlock(TemplateElements children) {
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<" + this.getNodeTypeSymbol() + "\">" + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + ">";
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#autoesc";
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
    boolean isIgnorable(boolean stripWhitespace) {
        return this.getChildCount() == 0;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

