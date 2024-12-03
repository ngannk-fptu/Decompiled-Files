/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import java.io.IOException;

final class FallbackInstruction
extends TemplateElement {
    FallbackInstruction() {
    }

    @Override
    TemplateElement[] accept(Environment env) throws IOException, TemplateException {
        env.fallback();
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        return canonical ? "<" + this.getNodeTypeSymbol() + "/>" : this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#fallback";
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

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}

