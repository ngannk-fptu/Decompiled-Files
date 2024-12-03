/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AliasTargetTemplateValueFormatException;
import freemarker.core.Environment;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.core.TemplateValueFormatException;
import freemarker.core._CoreLocaleUtils;
import freemarker.template.utility.StringUtil;
import java.util.Locale;
import java.util.Map;

public final class AliasTemplateNumberFormatFactory
extends TemplateNumberFormatFactory {
    private final String defaultTargetFormatString;
    private final Map<Locale, String> localizedTargetFormatStrings;

    public AliasTemplateNumberFormatFactory(String targetFormatString) {
        this.defaultTargetFormatString = targetFormatString;
        this.localizedTargetFormatStrings = null;
    }

    public AliasTemplateNumberFormatFactory(String defaultTargetFormatString, Map<Locale, String> localizedTargetFormatStrings) {
        this.defaultTargetFormatString = defaultTargetFormatString;
        this.localizedTargetFormatStrings = localizedTargetFormatStrings;
    }

    @Override
    public TemplateNumberFormat get(String params, Locale locale, Environment env) throws TemplateValueFormatException {
        TemplateFormatUtil.checkHasNoParameters(params);
        try {
            String targetFormatString;
            if (this.localizedTargetFormatStrings != null) {
                Locale lookupLocale = locale;
                targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
                while (targetFormatString == null && (lookupLocale = _CoreLocaleUtils.getLessSpecificLocale(lookupLocale)) != null) {
                    targetFormatString = this.localizedTargetFormatStrings.get(lookupLocale);
                }
            } else {
                targetFormatString = null;
            }
            if (targetFormatString == null) {
                targetFormatString = this.defaultTargetFormatString;
            }
            return env.getTemplateNumberFormat(targetFormatString, locale);
        }
        catch (TemplateValueFormatException e) {
            throw new AliasTargetTemplateValueFormatException("Failed to create format based on target format string,  " + StringUtil.jQuote(params) + ". Reason given: " + e.getMessage(), e);
        }
    }
}

