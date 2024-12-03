/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import java.io.IOException;

final class MixedContent
extends TemplateElement {
    MixedContent() {
    }

    @Deprecated
    void addElement(TemplateElement element) {
        this.addChild(element);
    }

    @Deprecated
    void addElement(int index, TemplateElement element) {
        this.addChild(index, element);
    }

    @Override
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        super.postParseCleanup(stripWhitespace);
        return this.getChildCount() == 1 ? this.getChild(0) : this;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            return this.getChildrenCanonicalForm();
        }
        if (this.getParentElement() == null) {
            return "root";
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    protected boolean isOutputCacheable() {
        int ln = this.getChildCount();
        for (int i = 0; i < ln; ++i) {
            if (this.getChild(i).isOutputCacheable()) continue;
            return false;
        }
        return true;
    }

    @Override
    String getNodeTypeSymbol() {
        return "#mixed_content";
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

