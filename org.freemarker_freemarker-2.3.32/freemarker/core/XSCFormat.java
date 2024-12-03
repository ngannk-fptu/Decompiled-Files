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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public final class XSCFormat
extends CFormat {
    public static final String NAME = "XS";
    public static final XSCFormat INSTANCE = new XSCFormat();
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("INF", "-INF", "NaN", "INF", "-INF", "NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat)LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    @Override
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat)LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }

    private XSCFormat() {
    }

    @Override
    TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override
    String formatString(String s, Environment env) throws TemplateException {
        return s;
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
        return "";
    }

    @Override
    public String getName() {
        return NAME;
    }

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("INF");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }
}

