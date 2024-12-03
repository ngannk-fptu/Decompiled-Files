/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.FMParserTokenManager;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.StandardCFormats;
import freemarker.core.TemplateElement;
import freemarker.core.Token;
import freemarker.core._CoreStringUtils;
import freemarker.template.Configuration;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.util.Arrays;

final class PropertySetting
extends TemplateElement {
    private final String key;
    private final Expression value;
    private final ValueSafetyChecker valueSafetyChecker;
    static final String[] SETTING_NAMES = new String[]{"booleanFormat", "boolean_format", "cFormat", "c_format", "classicCompatible", "classic_compatible", "dateFormat", "date_format", "datetimeFormat", "datetime_format", "locale", "numberFormat", "number_format", "outputEncoding", "output_encoding", "sqlDateAndTimeTimeZone", "sql_date_and_time_time_zone", "timeFormat", "timeZone", "time_format", "time_zone", "urlEscapingCharset", "url_escaping_charset"};

    PropertySetting(Token keyTk, FMParserTokenManager tokenManager, Expression value, Configuration cfg) throws ParseException {
        final String key = keyTk.image;
        if (Arrays.binarySearch(SETTING_NAMES, key) < 0) {
            StringBuilder sb = new StringBuilder();
            if (_TemplateAPI.getConfigurationSettingNames(cfg, true).contains(key) || _TemplateAPI.getConfigurationSettingNames(cfg, false).contains(key)) {
                sb.append("The setting name is recognized, but changing this setting from inside a template isn't supported.");
            } else {
                sb.append("Unknown setting name: ");
                sb.append(StringUtil.jQuote(key)).append(".");
                sb.append(" The allowed setting names are: ");
                int namingConvention = tokenManager.namingConvention;
                int shownNamingConvention = namingConvention != 10 ? namingConvention : 11;
                boolean first = true;
                for (int i = 0; i < SETTING_NAMES.length; ++i) {
                    String correctName = SETTING_NAMES[i];
                    int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(correctName);
                    if (!(shownNamingConvention == 12 ? correctNameNamingConvetion != 11 : correctNameNamingConvetion != 12)) continue;
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(SETTING_NAMES[i]);
                }
            }
            throw new ParseException(sb.toString(), null, keyTk);
        }
        this.key = key;
        this.value = value;
        this.valueSafetyChecker = key.equals("c_format") || key.equals("cFormat") ? new ValueSafetyChecker(){

            @Override
            public void check(Environment env, String actualValue) throws TemplateException {
                if (actualValue.startsWith("@") || StandardCFormats.STANDARD_C_FORMATS.containsKey(actualValue) || actualValue.equals("default")) {
                    return;
                }
                throw new TemplateException("It's not allowed to set \"" + key + "\" to " + StringUtil.jQuote(actualValue) + " in a template. Use a standard c format name (" + _CoreStringUtils.commaSeparatedJQuotedItems(StandardCFormats.STANDARD_C_FORMATS.keySet()) + "), or registered custom  c format name after a \"@\".", env);
            }
        } : null;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException {
        TemplateModel mval = this.value.eval(env);
        String strval = mval instanceof TemplateScalarModel ? ((TemplateScalarModel)mval).getAsString() : (mval instanceof TemplateBooleanModel ? (((TemplateBooleanModel)mval).getAsBoolean() ? "true" : "false") : (mval instanceof TemplateNumberModel ? ((TemplateNumberModel)mval).getAsNumber().toString() : this.value.evalAndCoerceToStringOrUnsupportedMarkup(env)));
        if (this.valueSafetyChecker != null) {
            this.valueSafetyChecker.check(env, strval);
        }
        env.setSetting(this.key, strval);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        sb.append(' ');
        sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.key));
        sb.append('=');
        sb.append(this.value.getCanonicalForm());
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#setting";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.key;
            }
            case 1: {
                return this.value;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.ITEM_KEY;
            }
            case 1: {
                return ParameterRole.ITEM_VALUE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    private static interface ValueSafetyChecker {
        public void check(Environment var1, String var2) throws TemplateException;
    }
}

