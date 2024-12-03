/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CFormat;
import freemarker.core.CTemplateNumberFormat;
import freemarker.core.Environment;
import freemarker.core.LegacyCFormat;
import freemarker.core.TemplateNumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public abstract class AbstractJSONLikeFormat
extends CFormat {
    private static final TemplateNumberFormat TEMPLATE_NUMBER_FORMAT = new CTemplateNumberFormat("Infinity", "-Infinity", "NaN", "Infinity", "-Infinity", "NaN");
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE = (DecimalFormat)LegacyCFormat.LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();

    AbstractJSONLikeFormat() {
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
    final String getNullString() {
        return "null";
    }

    @Override
    final TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return TEMPLATE_NUMBER_FORMAT;
    }

    @Override
    NumberFormat getLegacyNumberFormat(Environment env) {
        return (NumberFormat)LEGACY_NUMBER_FORMAT_PROTOTYPE.clone();
    }

    static {
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE.getDecimalFormatSymbols();
        symbols.setInfinity("Infinity");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE.setDecimalFormatSymbols(symbols);
    }
}

