/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class HTMLOutputFormat
extends CommonMarkupOutputFormat<TemplateHTMLOutputModel> {
    public static final HTMLOutputFormat INSTANCE = new HTMLOutputFormat();

    protected HTMLOutputFormat() {
    }

    @Override
    public String getName() {
        return "HTML";
    }

    @Override
    public String getMimeType() {
        return "text/html";
    }

    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        StringUtil.XHTMLEnc(textToEsc, out);
    }

    @Override
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XHTMLEnc(plainTextContent);
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("html") || builtInName.equals("xml") || builtInName.equals("xhtml");
    }

    @Override
    protected TemplateHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateHTMLOutputModel(plainTextContent, markupContent);
    }
}

