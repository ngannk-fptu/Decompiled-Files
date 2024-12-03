/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateXHTMLOutputModel;
import freemarker.core.XMLOutputFormat;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class XHTMLOutputFormat
extends XMLOutputFormat {
    public static final XHTMLOutputFormat INSTANCE = new XHTMLOutputFormat();

    protected XHTMLOutputFormat() {
    }

    @Override
    public String getName() {
        return "XHTML";
    }

    @Override
    public String getMimeType() {
        return "application/xhtml+xml";
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
    protected TemplateXHTMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateXHTMLOutputModel(plainTextContent, markupContent);
    }
}

