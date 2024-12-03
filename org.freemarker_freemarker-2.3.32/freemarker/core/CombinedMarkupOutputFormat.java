/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.TemplateCombinedMarkupOutputModel;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public final class CombinedMarkupOutputFormat
extends CommonMarkupOutputFormat<TemplateCombinedMarkupOutputModel> {
    private final String name;
    private final MarkupOutputFormat outer;
    private final MarkupOutputFormat inner;

    public CombinedMarkupOutputFormat(MarkupOutputFormat outer, MarkupOutputFormat inner) {
        this(null, outer, inner);
    }

    public CombinedMarkupOutputFormat(String name, MarkupOutputFormat outer, MarkupOutputFormat inner) {
        this.name = name != null ? null : outer.getName() + "{" + inner.getName() + "}";
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMimeType() {
        return this.outer.getMimeType();
    }

    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        this.outer.output(this.inner.escapePlainText(textToEsc), out);
    }

    @Override
    public <MO2 extends TemplateMarkupOutputModel<MO2>> void outputForeign(MO2 mo, Writer out) throws IOException, TemplateModelException {
        this.outer.outputForeign(mo, out);
    }

    @Override
    public String escapePlainText(String plainTextContent) throws TemplateModelException {
        return this.outer.escapePlainText(this.inner.escapePlainText(plainTextContent));
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) throws TemplateModelException {
        return this.outer.isLegacyBuiltInBypassed(builtInName);
    }

    @Override
    public boolean isAutoEscapedByDefault() {
        return this.outer.isAutoEscapedByDefault();
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return this.outer.isOutputFormatMixingAllowed();
    }

    public MarkupOutputFormat getOuterOutputFormat() {
        return this.outer;
    }

    public MarkupOutputFormat getInnerOutputFormat() {
        return this.inner;
    }

    @Override
    protected TemplateCombinedMarkupOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateCombinedMarkupOutputModel(plainTextContent, markupContent, this);
    }
}

