/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.i18ndirectives;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.i18ndirectives.I18nUtils;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyLibraryAssistedJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.ibm.icu.text.CompactDecimalFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import java.util.List;
import java.util.Set;

class FormatNumDirective
implements SoyJavaPrintDirective,
SoyLibraryAssistedJsSrcPrintDirective {
    private static final ImmutableMap<String, String> JS_ARGS_TO_ENUM = ImmutableMap.builder().put((Object)"'decimal'", (Object)"goog.i18n.NumberFormat.Format.DECIMAL").put((Object)"'currency'", (Object)"goog.i18n.NumberFormat.Format.CURRENCY").put((Object)"'percent'", (Object)"goog.i18n.NumberFormat.Format.PERCENT").put((Object)"'scientific'", (Object)"goog.i18n.NumberFormat.Format.SCIENTIFIC").put((Object)"'compact_short'", (Object)"goog.i18n.NumberFormat.Format.COMPACT_SHORT").put((Object)"'compact_long'", (Object)"goog.i18n.NumberFormat.Format.COMPACT_LONG").build();
    private static final ImmutableSet<Integer> VALID_ARGS_SIZES = ImmutableSet.of((Object)0, (Object)1, (Object)2);
    private static final ImmutableSet<String> REQUIRED_JS_LIBS = ImmutableSet.of((Object)"goog.i18n.NumberFormat");
    private final Provider<String> localeStringProvider;

    @Inject
    FormatNumDirective(@ApiCallScopeBindingAnnotations.LocaleString Provider<String> localeStringProvider) {
        this.localeStringProvider = localeStringProvider;
    }

    @Override
    public String getName() {
        return "|formatNum";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return VALID_ARGS_SIZES;
    }

    @Override
    public boolean shouldCancelAutoescape() {
        return false;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        NumberFormat numberFormat;
        String formatType;
        ULocale uLocale = I18nUtils.parseULocale(this.localeStringProvider.get());
        if (args.size() > 1) {
            uLocale = uLocale.setKeywordValue("numbers", args.get(1).stringValue());
        }
        String string = formatType = args.isEmpty() ? "decimal" : args.get(0).stringValue();
        if ("decimal".equals(formatType)) {
            numberFormat = NumberFormat.getInstance(uLocale);
        } else if ("percent".equals(formatType)) {
            numberFormat = NumberFormat.getPercentInstance(uLocale);
        } else if ("currency".equals(formatType)) {
            numberFormat = NumberFormat.getCurrencyInstance(uLocale);
        } else if ("scientific".equals(formatType)) {
            numberFormat = NumberFormat.getScientificInstance(uLocale);
        } else if ("compact_short".equals(formatType)) {
            CompactDecimalFormat compactNumberFormat = CompactDecimalFormat.getInstance(uLocale, CompactDecimalFormat.CompactStyle.SHORT);
            compactNumberFormat.setMaximumSignificantDigits(3);
            numberFormat = compactNumberFormat;
        } else if ("compact_long".equals(formatType)) {
            CompactDecimalFormat compactNumberFormat = CompactDecimalFormat.getInstance(uLocale, CompactDecimalFormat.CompactStyle.LONG);
            compactNumberFormat.setMaximumSignificantDigits(3);
            numberFormat = compactNumberFormat;
        } else {
            throw SoySyntaxException.createWithoutMetaInfo("First argument to formatNum must be constant, and one of: 'decimal', 'currency', 'percent', 'scientific', 'compact_short', or 'compact_long'.");
        }
        return StringData.forValue(numberFormat.format(((NumberData)value).toFloat()));
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        String numberFormatDecl;
        String numberFormatType;
        String string = numberFormatType = !args.isEmpty() ? args.get(0).getText() : null;
        if (numberFormatType == null) {
            numberFormatDecl = "goog.i18n.NumberFormat.Format.DECIMAL";
        } else if (JS_ARGS_TO_ENUM.containsKey((Object)numberFormatType)) {
            numberFormatDecl = (String)JS_ARGS_TO_ENUM.get((Object)numberFormatType);
        } else {
            throw SoySyntaxException.createWithoutMetaInfo("First argument to formatNum must be constant, and one of: 'decimal', 'currency', 'percent', 'scientific', 'compact_short', or 'compact_long'.");
        }
        StringBuilder expr = new StringBuilder();
        expr.append("(new goog.i18n.NumberFormat(" + numberFormatDecl + "))");
        if ("'compact_short'".equals(numberFormatType) || "'compact_long'".equals(numberFormatType)) {
            expr.append(".setSignificantDigits(3)");
        }
        expr.append(".format(" + value.getText() + ")");
        return new JsExpr(expr.toString(), Integer.MAX_VALUE);
    }

    @Override
    public ImmutableSet<String> getRequiredJsLibNames() {
        return REQUIRED_JS_LIBS;
    }
}

