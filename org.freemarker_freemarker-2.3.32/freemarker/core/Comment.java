/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.utility.StringUtil;

@Deprecated
public final class Comment
extends TemplateElement {
    private final String text;

    Comment(String text) {
        this.text = text;
    }

    @Override
    TemplateElement[] accept(Environment env) {
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            return "<#--" + this.text + "-->";
        }
        return "comment " + StringUtil.jQuote(this.text.trim());
    }

    @Override
    String getNodeTypeSymbol() {
        return "#--...--";
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
        return this.text;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    public String getText() {
        return this.text;
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

