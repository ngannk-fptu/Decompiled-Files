/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.TemplateMarkupOutputModel;

public abstract class CommonTemplateMarkupOutputModel<MO extends CommonTemplateMarkupOutputModel<MO>>
implements TemplateMarkupOutputModel<MO> {
    private final String plainTextContent;
    private String markupContent;

    protected CommonTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        this.plainTextContent = plainTextContent;
        this.markupContent = markupContent;
    }

    @Override
    public abstract CommonMarkupOutputFormat<MO> getOutputFormat();

    final String getPlainTextContent() {
        return this.plainTextContent;
    }

    final String getMarkupContent() {
        return this.markupContent;
    }

    final void setMarkupContent(String markupContent) {
        this.markupContent = markupContent;
    }

    public String toString() {
        return "markupOutput(format=" + this.getOutputFormat().getName() + ", " + (this.plainTextContent != null ? "plainText=" + this.plainTextContent : "markup=" + this.markupContent) + ")";
    }
}

