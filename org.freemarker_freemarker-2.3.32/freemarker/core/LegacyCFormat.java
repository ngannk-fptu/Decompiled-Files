/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CFormat;
import freemarker.core.Environment;
import freemarker.core.JavaTemplateNumberFormat;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.UnformattableValueException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public final class LegacyCFormat
extends CFormat {
    public static final LegacyCFormat INSTANCE = new LegacyCFormat();
    public static final String NAME = "legacy";
    static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0 = new DecimalFormat("0.################", new DecimalFormatSymbols(Locale.US));
    private static final DecimalFormat LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21;

    private LegacyCFormat() {
    }

    @Override
    final String formatString(String s, Environment env) throws TemplateException {
        return StringUtil.jsStringEnc(s, StringUtil.JsStringEncCompatibility.JAVA_SCRIPT_OR_JSON, StringUtil.JsStringEncQuotation.QUOTATION_MARK);
    }

    @Override
    final TemplateNumberFormat getTemplateNumberFormat(Environment env) {
        return this.getTemplateNumberFormat(env.getConfiguration().getIncompatibleImprovements().intValue());
    }

    TemplateNumberFormat getTemplateNumberFormat(int iciVersion) {
        return new LegacyCTemplateNumberFormat(this.getLegacyNumberFormat(iciVersion));
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
    NumberFormat getLegacyNumberFormat(Environment env) {
        return this.getLegacyNumberFormat(env.getConfiguration().getIncompatibleImprovements().intValue());
    }

    NumberFormat getLegacyNumberFormat(int iciVersion) {
        DecimalFormat numberFormatPrototype = iciVersion < _VersionInts.V_2_3_21 ? LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0 : LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21;
        return (NumberFormat)((NumberFormat)numberFormatPrototype).clone();
    }

    @Override
    public String getName() {
        return NAME;
    }

    static {
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.setGroupingUsed(false);
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.setDecimalSeparatorAlwaysShown(false);
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21 = (DecimalFormat)LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_0.clone();
        DecimalFormatSymbols symbols = LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21.getDecimalFormatSymbols();
        symbols.setInfinity("INF");
        symbols.setNaN("NaN");
        LEGACY_NUMBER_FORMAT_PROTOTYPE_2_3_21.setDecimalFormatSymbols(symbols);
    }

    static final class LegacyCTemplateNumberFormat
    extends JavaTemplateNumberFormat {
        public LegacyCTemplateNumberFormat(NumberFormat numberFormat) {
            super(numberFormat, "computer");
        }

        @Override
        public String formatToPlainText(TemplateNumberModel numberModel) throws UnformattableValueException, TemplateModelException {
            Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
            return this.format(number);
        }

        @Override
        public boolean isLocaleBound() {
            return false;
        }

        @Override
        String format(Number number) throws UnformattableValueException {
            if (number instanceof Integer || number instanceof Long) {
                return number.toString();
            }
            return super.format(number);
        }

        @Override
        public String getDescription() {
            return "LegacyC(" + super.getDescription() + ")";
        }
    }
}

