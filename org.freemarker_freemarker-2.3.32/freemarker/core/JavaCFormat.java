/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CFormat;
import freemarker.core.CTemplateNumberFormat;
import freemarker.core.Environment;
import freemarker.core.LegacyCFormat;
import freemarker.core.TemplateNumberFormat;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public final class JavaCFormat
extends CFormat {
    public static final String NAME = "Java";
    public static final JavaCFormat INSTANCE = new JavaCFormat();
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("Double.POSITIVE_INFINITY", "Double.NEGATIVE_INFINITY", "Double.NaN", "Float.POSITIVE_INFINITY", "Float.NEGATIVE_INFINITY", "Float.NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat)LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    private JavaCFormat() {
    }

    @Override
    TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override
    String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.javaStringEnc(s, true);
    }

    @Override
    String getTrueString() {
        return "true";
    }

    @Override
    String getFalseString() {
        return "false";
    }

    @Override
    String getNullString() {
        return "null";
    }

    @Override
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat)LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }

    @Override
    public String getName() {
        return NAME;
    }

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("Double.POSITIVE_INFINITY");
        symbols.setNaN("Double.NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }
}

