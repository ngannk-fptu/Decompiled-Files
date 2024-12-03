/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.template.TemplateException;
import java.io.IOException;

final class OutputFormatBlock
extends TemplateElement {
    private final Expression paramExp;

    OutputFormatBlock(TemplateElements children, Expression paramExp) {
        this.paramExp = paramExp;
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<" + this.getNodeTypeSymbol() + " \"" + this.paramExp.getCanonicalForm() + "\">" + this.getChildrenCanonicalForm() + "</" + this.getNodeTypeSymbol() + ">";
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#outputformat";
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.paramExp;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.VALUE;
        }
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

