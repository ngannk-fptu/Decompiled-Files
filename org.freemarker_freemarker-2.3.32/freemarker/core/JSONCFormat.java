/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AbstractJSONLikeFormat;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

public final class JSONCFormat
extends AbstractJSONLikeFormat {
    public static final String NAME = "JSON";
    public static final JSONCFormat INSTANCE = new JSONCFormat();

    private JSONCFormat() {
    }

    @Override
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

