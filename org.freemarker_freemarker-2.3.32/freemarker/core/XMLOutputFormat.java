/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.TemplateXMLOutputModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class XMLOutputFormat
extends CommonMarkupOutputFormat<TemplateXMLOutputModel> {
    public static final XMLOutputFormat INSTANCE = new XMLOutputFormat();

    protected XMLOutputFormat() {
    }

    @Override
    public String getName() {
        return "XML";
    }

    @Override
    public String getMimeType() {
        return "application/xml";
    }

    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        StringUtil.XMLEnc(textToEsc, out);
    }

    @Override
    public String escapePlainText(String plainTextContent) {
        return StringUtil.XMLEnc(plainTextContent);
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("xml");
    }

    @Override
    protected TemplateXMLOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateXMLOutputModel(plainTextContent, markupContent);
    }
}

