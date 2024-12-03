/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonTemplateMarkupOutputModel;
import freemarker.core.MarkupOutputFormat;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;

public abstract class CommonMarkupOutputFormat<MO extends CommonTemplateMarkupOutputModel>
extends MarkupOutputFormat<MO> {
    protected CommonMarkupOutputFormat() {
    }

    @Override
    public final MO fromPlainTextByEscaping(String textToEsc) throws TemplateModelException {
        return this.newTemplateMarkupOutputModel(textToEsc, null);
    }

    @Override
    public final MO fromMarkup(String markupText) throws TemplateModelException {
        return this.newTemplateMarkupOutputModel(null, markupText);
    }

    @Override
    public final void output(MO mo, Writer out) throws IOException, TemplateModelException {
        String mc = ((CommonTemplateMarkupOutputModel)mo).getMarkupContent();
        if (mc != null) {
            out.write(mc);
        } else {
            this.output(((CommonTemplateMarkupOutputModel)mo).getPlainTextContent(), out);
        }
    }

    @Override
    public abstract void output(String var1, Writer var2) throws IOException, TemplateModelException;

    @Override
    public final String getSourcePlainText(MO mo) throws TemplateModelException {
        return ((CommonTemplateMarkupOutputModel)mo).getPlainTextContent();
    }

    @Override
    public final String getMarkupString(MO mo) throws TemplateModelException {
        String mc = ((CommonTemplateMarkupOutputModel)mo).getMarkupContent();
        if (mc != null) {
            return mc;
        }
        mc = this.escapePlainText(((CommonTemplateMarkupOutputModel)mo).getPlainTextContent());
        ((CommonTemplateMarkupOutputModel)mo).setMarkupContent(mc);
        return mc;
    }

    @Override
    public final MO concat(MO mo1, MO mo2) throws TemplateModelException {
        String mc3;
        String pc1 = ((CommonTemplateMarkupOutputModel)mo1).getPlainTextContent();
        String mc1 = ((CommonTemplateMarkupOutputModel)mo1).getMarkupContent();
        String pc2 = ((CommonTemplateMarkupOutputModel)mo2).getPlainTextContent();
        String mc2 = ((CommonTemplateMarkupOutputModel)mo2).getMarkupContent();
        String pc3 = pc1 != null && pc2 != null ? pc1 + pc2 : null;
        String string = mc3 = mc1 != null && mc2 != null ? mc1 + mc2 : null;
        if (pc3 != null || mc3 != null) {
            return this.newTemplateMarkupOutputModel(pc3, mc3);
        }
        if (pc1 != null) {
            return this.newTemplateMarkupOutputModel(null, this.getMarkupString(mo1) + mc2);
        }
        return this.newTemplateMarkupOutputModel(null, mc1 + this.getMarkupString(mo2));
    }

    @Override
    public boolean isEmpty(MO mo) throws TemplateModelException {
        String s = ((CommonTemplateMarkupOutputModel)mo).getPlainTextContent();
        if (s != null) {
            return s.length() == 0;
        }
        return ((CommonTemplateMarkupOutputModel)mo).getMarkupContent().length() == 0;
    }

    @Override
    public boolean isOutputFormatMixingAllowed() {
        return false;
    }

    @Override
    public boolean isAutoEscapedByDefault() {
        return true;
    }

    protected abstract MO newTemplateMarkupOutputModel(String var1, String var2) throws TemplateModelException;
}

