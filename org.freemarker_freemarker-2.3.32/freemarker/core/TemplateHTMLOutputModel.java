/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.core.HTMLOutputFormat;

public class TemplateHTMLOutputModel
extends CommonTemplateMarkupOutputModel<TemplateHTMLOutputModel> {
    protected TemplateHTMLOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public HTMLOutputFormat getOutputFormat() {
        return HTMLOutputFormat.INSTANCE;
    }
}

