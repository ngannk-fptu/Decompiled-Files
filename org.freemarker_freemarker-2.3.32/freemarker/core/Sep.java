/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.IteratorBlock;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import java.io.IOException;

class Sep
extends TemplateElement {
    public Sep(TemplateElements children) {
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
        if (iterCtx == null) {
            throw new _MiscTemplateException(env, this.getNodeTypeSymbol(), " without iteration in context");
        }
        if (iterCtx.hasNext()) {
            return this.getChildBuffer();
        }
        return null;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</");
            sb.append(this.getNodeTypeSymbol());
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#sep";
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
}

