/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CombinedMarkupOutputFormat;
import freemarker.core.CommonTemplateMarkupOutputModel;

public final class TemplateCombinedMarkupOutputModel
extends CommonTemplateMarkupOutputModel<TemplateCombinedMarkupOutputModel> {
    private final CombinedMarkupOutputFormat outputFormat;

    TemplateCombinedMarkupOutputModel(String plainTextContent, String markupContent, CombinedMarkupOutputFormat outputFormat) {
        super(plainTextContent, markupContent);
        this.outputFormat = outputFormat;
    }

    @Override
    public CombinedMarkupOutputFormat getOutputFormat() {
        return this.outputFormat;
    }
}

