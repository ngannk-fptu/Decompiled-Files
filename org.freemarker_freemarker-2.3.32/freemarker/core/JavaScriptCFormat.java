/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AbstractJSONLikeFormat;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

public final class JavaScriptCFormat
extends AbstractJSONLikeFormat {
    public static final String NAME = "JavaScript";
    public static final JavaScriptCFormat INSTANCE = new JavaScriptCFormat();

    private JavaScriptCFormat() {
    }

    @Override
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JAVA_SCRIPT, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

