/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.core.RTFOutputFormat;

public class TemplateRTFOutputModel
extends CommonTemplateMarkupOutputModel<TemplateRTFOutputModel> {
    protected TemplateRTFOutputModel(String plainTextContent, String markupContent) {
        super(plainTextContent, markupContent);
    }

    @Override
    public RTFOutputFormat getOutputFormat() {
        return RTFOutputFormat.INSTANCE;
    }
}

