/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AbstractJSONLikeFormat;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

public final class JavaScriptOrJSONCFormat
extends AbstractJSONLikeFormat {
    public static final String NAME = "JavaScript or JSON";
    public static final JavaScriptOrJSONCFormat INSTANCE = new JavaScriptOrJSONCFormat();

    private JavaScriptOrJSONCFormat() {
    }

    @Override
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JAVA_SCRIPT_OR_JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

