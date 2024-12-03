/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CommonMarkupOutputFormat;
import freemarker.core.TemplateRTFOutputModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

public class RTFOutputFormat
extends CommonMarkupOutputFormat<TemplateRTFOutputModel> {
    public static final RTFOutputFormat INSTANCE = new RTFOutputFormat();

    protected RTFOutputFormat() {
    }

    @Override
    public String getName() {
        return "RTF";
    }

    @Override
    public String getMimeType() {
        return "application/rtf";
    }

    @Override
    public void output(String textToEsc, Writer out) throws IOException, TemplateModelException {
        StringUtil.RTFEnc(textToEsc, out);
    }

    @Override
    public String escapePlainText(String plainTextContent) {
        return StringUtil.RTFEnc(plainTextContent);
    }

    @Override
    public boolean isLegacyBuiltInBypassed(String builtInName) {
        return builtInName.equals("rtf");
    }

    @Override
    protected TemplateRTFOutputModel newTemplateMarkupOutputModel(String plainTextContent, String markupContent) {
        return new TemplateRTFOutputModel(plainTextContent, markupContent);
    }
}

