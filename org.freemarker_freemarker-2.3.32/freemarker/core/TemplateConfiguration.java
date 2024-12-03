/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Configurable;
import freemarker.core.OutputFormat;
import freemarker.core.ParserConfiguration;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TemplateConfiguration
extends Configurable
implements ParserConfiguration {
    private boolean parentConfigurationSet;
    private Integer tagSyntax;
    private Integer interpolationSyntax;
    private Integer namingConvention;
    private Boolean whitespaceStripping;
    private Boolean strictSyntaxMode;
    private Integer autoEscapingPolicy;
    private Boolean recognizeStandardFileExtensions;
    private OutputFormat outputFormat;
    private String encoding;
    private Integer tabSize;

    public TemplateConfiguration() {
        super(Configuration.getDefaultConfiguration());
    }

    @Override
    void setParent(Configurable cfg) {
        NullArgumentException.check("cfg", cfg);
        if (!(cfg instanceof Configuration)) {
            throw new IllegalArgumentException("The parent of a TemplateConfiguration can only be a Configuration");
        }
        if (this.parentConfigurationSet) {
            if (this.getParent() != cfg) {
                throw new IllegalStateException("This TemplateConfiguration is already associated with a different Configuration instance.");
            }
            return;
        }
        if (((Configuration)cfg).getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_22 && this.hasAnyConfigurableSet()) {
            throw new IllegalStateException("This TemplateConfiguration can't be associated to a Configuration that has incompatibleImprovements less than 2.3.22, because it changes non-parser settings.");
        }
        super.setParent(cfg);
        this.parentConfigurationSet = true;
    }

    public void setParentConfiguration(Configuration cfg) {
        this.setParent(cfg);
    }

    public Configuration getParentConfiguration() {
        return this.parentConfigurationSet ? (Configuration)this.getParent() : null;
    }

    private Configuration getNonNullParentConfiguration() {
        this.checkParentConfigurationSet();
        return (Configuration)this.getParent();
    }

    public void merge(TemplateConfiguration tc) {
        if (tc.isAPIBuiltinEnabledSet()) {
            this.setAPIBuiltinEnabled(tc.isAPIBuiltinEnabled());
        }
        if (tc.isArithmeticEngineSet()) {
            this.setArithmeticEngine(tc.getArithmeticEngine());
        }
        if (tc.isAutoEscapingPolicySet()) {
            this.setAutoEscapingPolicy(tc.getAutoEscapingPolicy());
        }
        if (tc.isAutoFlushSet()) {
            this.setAutoFlush(tc.getAutoFlush());
        }
        if (tc.isBooleanFormatSet()) {
            this.setBooleanFormat(tc.getBooleanFormat());
        }
        if (tc.isClassicCompatibleSet()) {
            this.setClassicCompatibleAsInt(tc.getClassicCompatibleAsInt());
        }
        if (tc.isCustomDateFormatsSet()) {
            this.setCustomDateFormats(this.mergeMaps(this.getCustomDateFormats(), tc.getCustomDateFormats(), false));
        }
        if (tc.isCustomNumberFormatsSet()) {
            this.setCustomNumberFormats(this.mergeMaps(this.getCustomNumberFormats(), tc.getCustomNumberFormats(), false));
        }
        if (tc.isDateFormatSet()) {
            this.setDateFormat(tc.getDateFormat());
        }
        if (tc.isDateTimeFormatSet()) {
            this.setDateTimeFormat(tc.getDateTimeFormat());
        }
        if (tc.isCFormatSet()) {
            this.setCFormat(tc.getCFormat());
        }
        if (tc.isEncodingSet()) {
            this.setEncoding(tc.getEncoding());
        }
        if (tc.isLocaleSet()) {
            this.setLocale(tc.getLocale());
        }
        if (tc.isLogTemplateExceptionsSet()) {
            this.setLogTemplateExceptions(tc.getLogTemplateExceptions());
        }
        if (tc.isWrapUncheckedExceptionsSet()) {
            this.setWrapUncheckedExceptions(tc.getWrapUncheckedExceptions());
        }
        if (tc.isNamingConventionSet()) {
            this.setNamingConvention(tc.getNamingConvention());
        }
        if (tc.isNewBuiltinClassResolverSet()) {
            this.setNewBuiltinClassResolver(tc.getNewBuiltinClassResolver());
        }
        if (tc.isTruncateBuiltinAlgorithmSet()) {
            this.setTruncateBuiltinAlgorithm(tc.getTruncateBuiltinAlgorithm());
        }
        if (tc.isNumberFormatSet()) {
            this.setNumberFormat(tc.getNumberFormat());
        }
        if (tc.isObjectWrapperSet()) {
            this.setObjectWrapper(tc.getObjectWrapper());
        }
        if (tc.isOutputEncodingSet()) {
            this.setOutputEncoding(tc.getOutputEncoding());
        }
        if (tc.isOutputFormatSet()) {
            this.setOutputFormat(tc.getOutputFormat());
        }
        if (tc.isRecognizeStandardFileExtensionsSet()) {
            this.setRecognizeStandardFileExtensions(tc.getRecognizeStandardFileExtensions());
        }
        if (tc.isShowErrorTipsSet()) {
            this.setShowErrorTips(tc.getShowErrorTips());
        }
        if (tc.isSQLDateAndTimeTimeZoneSet()) {
            this.setSQLDateAndTimeTimeZone(tc.getSQLDateAndTimeTimeZone());
        }
        if (tc.isStrictSyntaxModeSet()) {
            this.setStrictSyntaxMode(tc.getStrictSyntaxMode());
        }
        if (tc.isTagSyntaxSet()) {
            this.setTagSyntax(tc.getTagSyntax());
        }
        if (tc.isInterpolationSyntaxSet()) {
            this.setInterpolationSyntax(tc.getInterpolationSyntax());
        }
        if (tc.isTemplateExceptionHandlerSet()) {
            this.setTemplateExceptionHandler(tc.getTemplateExceptionHandler());
        }
        if (tc.isAttemptExceptionReporterSet()) {
            this.setAttemptExceptionReporter(tc.getAttemptExceptionReporter());
        }
        if (tc.isTimeFormatSet()) {
            this.setTimeFormat(tc.getTimeFormat());
        }
        if (tc.isTimeZoneSet()) {
            this.setTimeZone(tc.getTimeZone());
        }
        if (tc.isURLEscapingCharsetSet()) {
            this.setURLEscapingCharset(tc.getURLEscapingCharset());
        }
        if (tc.isWhitespaceStrippingSet()) {
            this.setWhitespaceStripping(tc.getWhitespaceStripping());
        }
        if (tc.isTabSizeSet()) {
            this.setTabSize(tc.getTabSize());
        }
        if (tc.isLazyImportsSet()) {
            this.setLazyImports(tc.getLazyImports());
        }
        if (tc.isLazyAutoImportsSet()) {
            this.setLazyAutoImports(tc.getLazyAutoImports());
        }
        if (tc.isAutoImportsSet()) {
            this.setAutoImports(this.mergeMaps(this.getAutoImportsWithoutFallback(), tc.getAutoImportsWithoutFallback(), true));
        }
        if (tc.isAutoIncludesSet()) {
            this.setAutoIncludes(this.mergeLists(this.getAutoIncludesWithoutFallback(), tc.getAutoIncludesWithoutFallback()));
        }
        tc.copyDirectCustomAttributes(this, true);
    }

    public void apply(Template template) {
        Configuration cfg = this.getNonNullParentConfiguration();
        if (template.getConfiguration() != cfg) {
            throw new IllegalArgumentException("The argument Template doesn't belong to the same Configuration as the TemplateConfiguration");
        }
        if (this.isAPIBuiltinEnabledSet() && !template.isAPIBuiltinEnabledSet()) {
            template.setAPIBuiltinEnabled(this.isAPIBuiltinEnabled());
        }
        if (this.isArithmeticEngineSet() && !template.isArithmeticEngineSet()) {
            template.setArithmeticEngine(this.getArithmeticEngine());
        }
        if (this.isAutoFlushSet() && !template.isAutoFlushSet()) {
            template.setAutoFlush(this.getAutoFlush());
        }
        if (this.isBooleanFormatSet() && !template.isBooleanFormatSet()) {
            template.setBooleanFormat(this.getBooleanFormat());
        }
        if (this.isClassicCompatibleSet() && !template.isClassicCompatibleSet()) {
            template.setClassicCompatibleAsInt(this.getClassicCompatibleAsInt());
        }
        if (this.isCustomDateFormatsSet()) {
            template.setCustomDateFormats(this.mergeMaps(this.getCustomDateFormats(), template.getCustomDateFormatsWithoutFallback(), false));
        }
        if (this.isCustomNumberFormatsSet()) {
            template.setCustomNumberFormats(this.mergeMaps(this.getCustomNumberFormats(), template.getCustomNumberFormatsWithoutFallback(), false));
        }
        if (this.isDateFormatSet() && !template.isDateFormatSet()) {
            template.setDateFormat(this.getDateFormat());
        }
        if (this.isDateTimeFormatSet() && !template.isDateTimeFormatSet()) {
            template.setDateTimeFormat(this.getDateTimeFormat());
        }
        if (this.isCFormatSet() && !template.isCFormatSet()) {
            template.setCFormat(this.getCFormat());
        }
        if (this.isEncodingSet() && template.getEncoding() == null) {
            template.setEncoding(this.getEncoding());
        }
        if (this.isLocaleSet() && !template.isLocaleSet()) {
            template.setLocale(this.getLocale());
        }
        if (this.isLogTemplateExceptionsSet() && !template.isLogTemplateExceptionsSet()) {
            template.setLogTemplateExceptions(this.getLogTemplateExceptions());
        }
        if (this.isWrapUncheckedExceptionsSet() && !template.isWrapUncheckedExceptionsSet()) {
            template.setWrapUncheckedExceptions(this.getWrapUncheckedExceptions());
        }
        if (this.isNewBuiltinClassResolverSet() && !template.isNewBuiltinClassResolverSet()) {
            template.setNewBuiltinClassResolver(this.getNewBuiltinClassResolver());
        }
        if (this.isTruncateBuiltinAlgorithmSet() && !template.isTruncateBuiltinAlgorithmSet()) {
            template.setTruncateBuiltinAlgorithm(this.getTruncateBuiltinAlgorithm());
        }
        if (this.isNumberFormatSet() && !template.isNumberFormatSet()) {
            template.setNumberFormat(this.getNumberFormat());
        }
        if (this.isObjectWrapperSet() && !template.isObjectWrapperSet()) {
            template.setObjectWrapper(this.getObjectWrapper());
        }
        if (this.isOutputEncodingSet() && !template.isOutputEncodingSet()) {
            template.setOutputEncoding(this.getOutputEncoding());
        }
        if (this.isShowErrorTipsSet() && !template.isShowErrorTipsSet()) {
            template.setShowErrorTips(this.getShowErrorTips());
        }
        if (this.isSQLDateAndTimeTimeZoneSet() && !template.isSQLDateAndTimeTimeZoneSet()) {
            template.setSQLDateAndTimeTimeZone(this.getSQLDateAndTimeTimeZone());
        }
        if (this.isTemplateExceptionHandlerSet() && !template.isTemplateExceptionHandlerSet()) {
            template.setTemplateExceptionHandler(this.getTemplateExceptionHandler());
        }
        if (this.isAttemptExceptionReporterSet() && !template.isAttemptExceptionReporterSet()) {
            template.setAttemptExceptionReporter(this.getAttemptExceptionReporter());
        }
        if (this.isTimeFormatSet() && !template.isTimeFormatSet()) {
            template.setTimeFormat(this.getTimeFormat());
        }
        if (this.isTimeZoneSet() && !template.isTimeZoneSet()) {
            template.setTimeZone(this.getTimeZone());
        }
        if (this.isURLEscapingCharsetSet() && !template.isURLEscapingCharsetSet()) {
            template.setURLEscapingCharset(this.getURLEscapingCharset());
        }
        if (this.isLazyImportsSet() && !template.isLazyImportsSet()) {
            template.setLazyImports(this.getLazyImports());
        }
        if (this.isLazyAutoImportsSet() && !template.isLazyAutoImportsSet()) {
            template.setLazyAutoImports(this.getLazyAutoImports());
        }
        if (this.isAutoImportsSet()) {
            template.setAutoImports(this.mergeMaps(this.getAutoImports(), template.getAutoImportsWithoutFallback(), true));
        }
        if (this.isAutoIncludesSet()) {
            template.setAutoIncludes(this.mergeLists(this.getAutoIncludes(), template.getAutoIncludesWithoutFallback()));
        }
        this.copyDirectCustomAttributes(template, false);
    }

    public void setTagSyntax(int tagSyntax) {
        _TemplateAPI.valideTagSyntaxValue(tagSyntax);
        this.tagSyntax = tagSyntax;
    }

    @Override
    public int getTagSyntax() {
        return this.tagSyntax != null ? this.tagSyntax.intValue() : this.getNonNullParentConfiguration().getTagSyntax();
    }

    public boolean isTagSyntaxSet() {
        return this.tagSyntax != null;
    }

    public void setInterpolationSyntax(int interpolationSyntax) {
        _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
        this.interpolationSyntax = interpolationSyntax;
    }

    @Override
    public int getInterpolationSyntax() {
        return this.interpolationSyntax != null ? this.interpolationSyntax.intValue() : this.getNonNullParentConfiguration().getInterpolationSyntax();
    }

    public boolean isInterpolationSyntaxSet() {
        return this.interpolationSyntax != null;
    }

    public void setNamingConvention(int namingConvention) {
        _TemplateAPI.validateNamingConventionValue(namingConvention);
        this.namingConvention = namingConvention;
    }

    @Override
    public int getNamingConvention() {
        return this.namingConvention != null ? this.namingConvention.intValue() : this.getNonNullParentConfiguration().getNamingConvention();
    }

    public boolean isNamingConventionSet() {
        return this.namingConvention != null;
    }

    public void setWhitespaceStripping(boolean whitespaceStripping) {
        this.whitespaceStripping = whitespaceStripping;
    }

    @Override
    public boolean getWhitespaceStripping() {
        return this.whitespaceStripping != null ? this.whitespaceStripping.booleanValue() : this.getNonNullParentConfiguration().getWhitespaceStripping();
    }

    public boolean isWhitespaceStrippingSet() {
        return this.whitespaceStripping != null;
    }

    public void setAutoEscapingPolicy(int autoEscapingPolicy) {
        _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
        this.autoEscapingPolicy = autoEscapingPolicy;
    }

    @Override
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy != null ? this.autoEscapingPolicy.intValue() : this.getNonNullParentConfiguration().getAutoEscapingPolicy();
    }

    public boolean isAutoEscapingPolicySet() {
        return this.autoEscapingPolicy != null;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        NullArgumentException.check("outputFormat", outputFormat);
        this.outputFormat = outputFormat;
    }

    @Override
    public OutputFormat getOutputFormat() {
        return this.outputFormat != null ? this.outputFormat : this.getNonNullParentConfiguration().getOutputFormat();
    }

    public boolean isOutputFormatSet() {
        return this.outputFormat != null;
    }

    public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
        this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
    }

    @Override
    public boolean getRecognizeStandardFileExtensions() {
        return this.recognizeStandardFileExtensions != null ? this.recognizeStandardFileExtensions.booleanValue() : this.getNonNullParentConfiguration().getRecognizeStandardFileExtensions();
    }

    public boolean isRecognizeStandardFileExtensionsSet() {
        return this.recognizeStandardFileExtensions != null;
    }

    public void setStrictSyntaxMode(boolean strictSyntaxMode) {
        this.strictSyntaxMode = strictSyntaxMode;
    }

    @Override
    public boolean getStrictSyntaxMode() {
        return this.strictSyntaxMode != null ? this.strictSyntaxMode.booleanValue() : this.getNonNullParentConfiguration().getStrictSyntaxMode();
    }

    public boolean isStrictSyntaxModeSet() {
        return this.strictSyntaxMode != null;
    }

    @Override
    public void setStrictBeanModels(boolean strict) {
        throw new UnsupportedOperationException("Setting strictBeanModels on " + TemplateConfiguration.class.getSimpleName() + " level isn't supported.");
    }

    public String getEncoding() {
        return this.encoding != null ? this.encoding : this.getNonNullParentConfiguration().getDefaultEncoding();
    }

    public void setEncoding(String encoding) {
        NullArgumentException.check("encoding", encoding);
        this.encoding = encoding;
    }

    public boolean isEncodingSet() {
        return this.encoding != null;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    @Override
    public int getTabSize() {
        return this.tabSize != null ? this.tabSize.intValue() : this.getNonNullParentConfiguration().getTabSize();
    }

    public boolean isTabSizeSet() {
        return this.tabSize != null;
    }

    @Override
    public Version getIncompatibleImprovements() {
        return this.getNonNullParentConfiguration().getIncompatibleImprovements();
    }

    private void checkParentConfigurationSet() {
        if (!this.parentConfigurationSet) {
            throw new IllegalStateException("The TemplateConfiguration wasn't associated with a Configuration yet.");
        }
    }

    private boolean hasAnyConfigurableSet() {
        return this.isAPIBuiltinEnabledSet() || this.isArithmeticEngineSet() || this.isAutoFlushSet() || this.isAutoImportsSet() || this.isAutoIncludesSet() || this.isBooleanFormatSet() || this.isClassicCompatibleSet() || this.isCustomDateFormatsSet() || this.isCustomNumberFormatsSet() || this.isDateFormatSet() || this.isDateTimeFormatSet() || this.isCFormatSet() || this.isLazyImportsSet() || this.isLazyAutoImportsSet() || this.isLocaleSet() || this.isLogTemplateExceptionsSet() || this.isWrapUncheckedExceptionsSet() || this.isNewBuiltinClassResolverSet() || this.isTruncateBuiltinAlgorithmSet() || this.isNumberFormatSet() || this.isObjectWrapperSet() || this.isOutputEncodingSet() || this.isShowErrorTipsSet() || this.isSQLDateAndTimeTimeZoneSet() || this.isTemplateExceptionHandlerSet() || this.isAttemptExceptionReporterSet() || this.isTimeFormatSet() || this.isTimeZoneSet() || this.isURLEscapingCharsetSet();
    }

    private Map mergeMaps(Map m1, Map m2, boolean overwriteUpdatesOrder) {
        if (m1 == null) {
            return m2;
        }
        if (m2 == null) {
            return m1;
        }
        if (m1.isEmpty()) {
            return m2;
        }
        if (m2.isEmpty()) {
            return m1;
        }
        LinkedHashMap mergedM = new LinkedHashMap((m1.size() + m2.size()) * 4 / 3 + 1, 0.75f);
        mergedM.putAll(m1);
        for (Object m2Key : m2.keySet()) {
            mergedM.remove(m2Key);
        }
        mergedM.putAll(m2);
        return mergedM;
    }

    private List<String> mergeLists(List<String> list1, List<String> list2) {
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }
        if (list1.isEmpty()) {
            return list2;
        }
        if (list2.isEmpty()) {
            return list1;
        }
        ArrayList<String> mergedList = new ArrayList<String>(list1.size() + list2.size());
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }
}

