/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.MarkupOutputFormat;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateModelException;

public class _MarkupBuilder<MO extends TemplateMarkupOutputModel> {
    private final String markupSource;
    private final MarkupOutputFormat<MO> markupOutputFormat;

    public _MarkupBuilder(MarkupOutputFormat<MO> markupOutputFormat, String markupSource) {
        this.markupOutputFormat = markupOutputFormat;
        this.markupSource = markupSource;
    }

    public MO build() throws TemplateModelException {
        return this.markupOutputFormat.fromMarkup(this.markupSource);
    }
}

