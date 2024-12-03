/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ArithmeticEngine;
import freemarker.core.CFormat;
import freemarker.core.CustomAttribute;
import freemarker.core.DefaultTruncateBuiltinAlgorithm;
import freemarker.core.Environment;
import freemarker.core.OptInTemplateClassResolver;
import freemarker.core.ParseException;
import freemarker.core.StandardCFormats;
import freemarker.core.TemplateClassResolver;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.core.TruncateBuiltinAlgorithm;
import freemarker.core._CoreAPI;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.core._SortedArraySet;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.AttemptExceptionReporter;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

public class Configurable {
    static final String BOOLEAN_FORMAT_LEGACY_DEFAULT = "true,false";
    static final String C_FORMAT_STRING = "c";
    private static final String NULL = "null";
    private static final String DEFAULT = "default";
    private static final String DEFAULT_2_3_0 = "default_2_3_0";
    private static final String JVM_DEFAULT = "JVM default";
    public static final String LOCALE_KEY_SNAKE_CASE = "locale";
    public static final String LOCALE_KEY_CAMEL_CASE = "locale";
    public static final String LOCALE_KEY = "locale";
    public static final String C_FORMAT_KEY_SNAKE_CASE = "c_format";
    public static final String C_FORMAT_KEY_CAMEL_CASE = "cFormat";
    public static final String C_FORMAT_KEY = "c_format";
    public static final String NUMBER_FORMAT_KEY_SNAKE_CASE = "number_format";
    public static final String NUMBER_FORMAT_KEY_CAMEL_CASE = "numberFormat";
    public static final String NUMBER_FORMAT_KEY = "number_format";
    public static final String CUSTOM_NUMBER_FORMATS_KEY_SNAKE_CASE = "custom_number_formats";
    public static final String CUSTOM_NUMBER_FORMATS_KEY_CAMEL_CASE = "customNumberFormats";
    public static final String CUSTOM_NUMBER_FORMATS_KEY = "custom_number_formats";
    public static final String TIME_FORMAT_KEY_SNAKE_CASE = "time_format";
    public static final String TIME_FORMAT_KEY_CAMEL_CASE = "timeFormat";
    public static final String TIME_FORMAT_KEY = "time_format";
    public static final String DATE_FORMAT_KEY_SNAKE_CASE = "date_format";
    public static final String DATE_FORMAT_KEY_CAMEL_CASE = "dateFormat";
    public static final String DATE_FORMAT_KEY = "date_format";
    public static final String CUSTOM_DATE_FORMATS_KEY_SNAKE_CASE = "custom_date_formats";
    public static final String CUSTOM_DATE_FORMATS_KEY_CAMEL_CASE = "customDateFormats";
    public static final String CUSTOM_DATE_FORMATS_KEY = "custom_date_formats";
    public static final String DATETIME_FORMAT_KEY_SNAKE_CASE = "datetime_format";
    public static final String DATETIME_FORMAT_KEY_CAMEL_CASE = "datetimeFormat";
    public static final String DATETIME_FORMAT_KEY = "datetime_format";
    public static final String TIME_ZONE_KEY_SNAKE_CASE = "time_zone";
    public static final String TIME_ZONE_KEY_CAMEL_CASE = "timeZone";
    public static final String TIME_ZONE_KEY = "time_zone";
    public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_SNAKE_CASE = "sql_date_and_time_time_zone";
    public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY_CAMEL_CASE = "sqlDateAndTimeTimeZone";
    public static final String SQL_DATE_AND_TIME_TIME_ZONE_KEY = "sql_date_and_time_time_zone";
    public static final String CLASSIC_COMPATIBLE_KEY_SNAKE_CASE = "classic_compatible";
    public static final String CLASSIC_COMPATIBLE_KEY_CAMEL_CASE = "classicCompatible";
    public static final String CLASSIC_COMPATIBLE_KEY = "classic_compatible";
    public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_SNAKE_CASE = "template_exception_handler";
    public static final String TEMPLATE_EXCEPTION_HANDLER_KEY_CAMEL_CASE = "templateExceptionHandler";
    public static final String TEMPLATE_EXCEPTION_HANDLER_KEY = "template_exception_handler";
    public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_SNAKE_CASE = "attempt_exception_reporter";
    public static final String ATTEMPT_EXCEPTION_REPORTER_KEY_CAMEL_CASE = "attemptExceptionReporter";
    public static final String ATTEMPT_EXCEPTION_REPORTER_KEY = "attempt_exception_reporter";
    public static final String ARITHMETIC_ENGINE_KEY_SNAKE_CASE = "arithmetic_engine";
    public static final String ARITHMETIC_ENGINE_KEY_CAMEL_CASE = "arithmeticEngine";
    public static final String ARITHMETIC_ENGINE_KEY = "arithmetic_engine";
    public static final String OBJECT_WRAPPER_KEY_SNAKE_CASE = "object_wrapper";
    public static final String OBJECT_WRAPPER_KEY_CAMEL_CASE = "objectWrapper";
    public static final String OBJECT_WRAPPER_KEY = "object_wrapper";
    public static final String BOOLEAN_FORMAT_KEY_SNAKE_CASE = "boolean_format";
    public static final String BOOLEAN_FORMAT_KEY_CAMEL_CASE = "booleanFormat";
    public static final String BOOLEAN_FORMAT_KEY = "boolean_format";
    public static final String OUTPUT_ENCODING_KEY_SNAKE_CASE = "output_encoding";
    public static final String OUTPUT_ENCODING_KEY_CAMEL_CASE = "outputEncoding";
    public static final String OUTPUT_ENCODING_KEY = "output_encoding";
    public static final String URL_ESCAPING_CHARSET_KEY_SNAKE_CASE = "url_escaping_charset";
    public static final String URL_ESCAPING_CHARSET_KEY_CAMEL_CASE = "urlEscapingCharset";
    public static final String URL_ESCAPING_CHARSET_KEY = "url_escaping_charset";
    public static final String STRICT_BEAN_MODELS_KEY_SNAKE_CASE = "strict_bean_models";
    public static final String STRICT_BEAN_MODELS_KEY_CAMEL_CASE = "strictBeanModels";
    public static final String STRICT_BEAN_MODELS_KEY = "strict_bean_models";
    public static final String AUTO_FLUSH_KEY_SNAKE_CASE = "auto_flush";
    public static final String AUTO_FLUSH_KEY_CAMEL_CASE = "autoFlush";
    public static final String AUTO_FLUSH_KEY = "auto_flush";
    public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_SNAKE_CASE = "new_builtin_class_resolver";
    public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY_CAMEL_CASE = "newBuiltinClassResolver";
    public static final String NEW_BUILTIN_CLASS_RESOLVER_KEY = "new_builtin_class_resolver";
    public static final String SHOW_ERROR_TIPS_KEY_SNAKE_CASE = "show_error_tips";
    public static final String SHOW_ERROR_TIPS_KEY_CAMEL_CASE = "showErrorTips";
    public static final String SHOW_ERROR_TIPS_KEY = "show_error_tips";
    public static final String API_BUILTIN_ENABLED_KEY_SNAKE_CASE = "api_builtin_enabled";
    public static final String API_BUILTIN_ENABLED_KEY_CAMEL_CASE = "apiBuiltinEnabled";
    public static final String API_BUILTIN_ENABLED_KEY = "api_builtin_enabled";
    public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_SNAKE_CASE = "truncate_builtin_algorithm";
    public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY_CAMEL_CASE = "truncateBuiltinAlgorithm";
    public static final String TRUNCATE_BUILTIN_ALGORITHM_KEY = "truncate_builtin_algorithm";
    public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_SNAKE_CASE = "log_template_exceptions";
    public static final String LOG_TEMPLATE_EXCEPTIONS_KEY_CAMEL_CASE = "logTemplateExceptions";
    public static final String LOG_TEMPLATE_EXCEPTIONS_KEY = "log_template_exceptions";
    public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_SNAKE_CASE = "wrap_unchecked_exceptions";
    public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY_CAMEL_CASE = "wrapUncheckedExceptions";
    public static final String WRAP_UNCHECKED_EXCEPTIONS_KEY = "wrap_unchecked_exceptions";
    public static final String LAZY_IMPORTS_KEY_SNAKE_CASE = "lazy_imports";
    public static final String LAZY_IMPORTS_KEY_CAMEL_CASE = "lazyImports";
    public static final String LAZY_IMPORTS_KEY = "lazy_imports";
    public static final String LAZY_AUTO_IMPORTS_KEY_SNAKE_CASE = "lazy_auto_imports";
    public static final String LAZY_AUTO_IMPORTS_KEY_CAMEL_CASE = "lazyAutoImports";
    public static final String LAZY_AUTO_IMPORTS_KEY = "lazy_auto_imports";
    public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
    public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
    public static final String AUTO_IMPORT_KEY = "auto_import";
    public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
    public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
    public static final String AUTO_INCLUDE_KEY = "auto_include";
    @Deprecated
    public static final String STRICT_BEAN_MODELS = "strict_bean_models";
    private static final String[] SETTING_NAMES_SNAKE_CASE = new String[]{"api_builtin_enabled", "arithmetic_engine", "attempt_exception_reporter", "auto_flush", "auto_import", "auto_include", "boolean_format", "c_format", "classic_compatible", "custom_date_formats", "custom_number_formats", "date_format", "datetime_format", "lazy_auto_imports", "lazy_imports", "locale", "log_template_exceptions", "new_builtin_class_resolver", "number_format", "object_wrapper", "output_encoding", "show_error_tips", "sql_date_and_time_time_zone", "strict_bean_models", "template_exception_handler", "time_format", "time_zone", "truncate_builtin_algorithm", "url_escaping_charset", "wrap_unchecked_exceptions"};
    private static final String[] SETTING_NAMES_CAMEL_CASE = new String[]{"apiBuiltinEnabled", "arithmeticEngine", "attemptExceptionReporter", "autoFlush", "autoImport", "autoInclude", "booleanFormat", "cFormat", "classicCompatible", "customDateFormats", "customNumberFormats", "dateFormat", "datetimeFormat", "lazyAutoImports", "lazyImports", "locale", "logTemplateExceptions", "newBuiltinClassResolver", "numberFormat", "objectWrapper", "outputEncoding", "showErrorTips", "sqlDateAndTimeTimeZone", "strictBeanModels", "templateExceptionHandler", "timeFormat", "timeZone", "truncateBuiltinAlgorithm", "urlEscapingCharset", "wrapUncheckedExceptions"};
    private Configurable parent;
    private Properties properties;
    private HashMap<Object, Object> customAttributes;
    private Locale locale;
    private CFormat cFormat;
    private String numberFormat;
    private String timeFormat;
    private String dateFormat;
    private String dateTimeFormat;
    private TimeZone timeZone;
    private TimeZone sqlDataAndTimeTimeZone;
    private boolean sqlDataAndTimeTimeZoneSet;
    private String booleanFormat;
    private Integer classicCompatible;
    private TemplateExceptionHandler templateExceptionHandler;
    private AttemptExceptionReporter attemptExceptionReporter;
    private ArithmeticEngine arithmeticEngine;
    private ObjectWrapper objectWrapper;
    private String outputEncoding;
    private boolean outputEncodingSet;
    private String urlEscapingCharset;
    private boolean urlEscapingCharsetSet;
    private Boolean autoFlush;
    private Boolean showErrorTips;
    private TemplateClassResolver newBuiltinClassResolver;
    private Boolean apiBuiltinEnabled;
    private TruncateBuiltinAlgorithm truncateBuiltinAlgorithm;
    private Boolean logTemplateExceptions;
    private Boolean wrapUncheckedExceptions;
    private Map<String, ? extends TemplateDateFormatFactory> customDateFormats;
    private Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats;
    private LinkedHashMap<String, String> autoImports;
    private ArrayList<String> autoIncludes;
    private Boolean lazyImports;
    private Boolean lazyAutoImports;
    private boolean lazyAutoImportsSet;
    private static final String ALLOWED_CLASSES_SNAKE_CASE = "allowed_classes";
    private static final String TRUSTED_TEMPLATES_SNAKE_CASE = "trusted_templates";
    private static final String ALLOWED_CLASSES_CAMEL_CASE = "allowedClasses";
    private static final String TRUSTED_TEMPLATES_CAMEL_CASE = "trustedTemplates";

    @Deprecated
    public Configurable() {
        this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    protected Configurable(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        this.parent = null;
        this.properties = new Properties();
        this.locale = _TemplateAPI.getDefaultLocale();
        this.properties.setProperty("locale", this.locale.toString());
        this.timeZone = _TemplateAPI.getDefaultTimeZone();
        this.properties.setProperty("time_zone", this.timeZone.getID());
        this.sqlDataAndTimeTimeZone = null;
        this.properties.setProperty("sql_date_and_time_time_zone", String.valueOf(this.sqlDataAndTimeTimeZone));
        this.numberFormat = "number";
        this.properties.setProperty("number_format", this.numberFormat);
        this.timeFormat = "";
        this.properties.setProperty("time_format", this.timeFormat);
        this.dateFormat = "";
        this.properties.setProperty("date_format", this.dateFormat);
        this.dateTimeFormat = "";
        this.properties.setProperty("datetime_format", this.dateTimeFormat);
        this.cFormat = _TemplateAPI.getDefaultCFormat(incompatibleImprovements);
        this.classicCompatible = 0;
        this.properties.setProperty("classic_compatible", this.classicCompatible.toString());
        this.templateExceptionHandler = _TemplateAPI.getDefaultTemplateExceptionHandler(incompatibleImprovements);
        this.properties.setProperty("template_exception_handler", this.templateExceptionHandler.getClass().getName());
        this.wrapUncheckedExceptions = _TemplateAPI.getDefaultWrapUncheckedExceptions(incompatibleImprovements);
        this.attemptExceptionReporter = _TemplateAPI.getDefaultAttemptExceptionReporter(incompatibleImprovements);
        this.arithmeticEngine = ArithmeticEngine.BIGDECIMAL_ENGINE;
        this.properties.setProperty("arithmetic_engine", this.arithmeticEngine.getClass().getName());
        this.objectWrapper = Configuration.getDefaultObjectWrapper(incompatibleImprovements);
        this.autoFlush = Boolean.TRUE;
        this.properties.setProperty("auto_flush", this.autoFlush.toString());
        this.newBuiltinClassResolver = TemplateClassResolver.UNRESTRICTED_RESOLVER;
        this.properties.setProperty("new_builtin_class_resolver", this.newBuiltinClassResolver.getClass().getName());
        this.truncateBuiltinAlgorithm = DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE;
        this.showErrorTips = Boolean.TRUE;
        this.properties.setProperty("show_error_tips", this.showErrorTips.toString());
        this.apiBuiltinEnabled = Boolean.FALSE;
        this.properties.setProperty("api_builtin_enabled", this.apiBuiltinEnabled.toString());
        this.logTemplateExceptions = _TemplateAPI.getDefaultLogTemplateExceptions(incompatibleImprovements);
        this.properties.setProperty("log_template_exceptions", this.logTemplateExceptions.toString());
        this.setBooleanFormat(BOOLEAN_FORMAT_LEGACY_DEFAULT);
        this.customAttributes = new HashMap();
        this.customDateFormats = Collections.emptyMap();
        this.customNumberFormats = Collections.emptyMap();
        this.lazyImports = false;
        this.lazyAutoImportsSet = true;
        this.initAutoImportsMap();
        this.initAutoIncludesList();
    }

    public Configurable(Configurable parent) {
        this.parent = parent;
        this.properties = new Properties(parent.properties);
        this.customAttributes = new HashMap(0);
    }

    protected Object clone() throws CloneNotSupportedException {
        Configurable copy = (Configurable)super.clone();
        if (this.properties != null) {
            copy.properties = new Properties(this.properties);
        }
        if (this.customAttributes != null) {
            copy.customAttributes = (HashMap)this.customAttributes.clone();
        }
        if (this.autoImports != null) {
            copy.autoImports = (LinkedHashMap)this.autoImports.clone();
        }
        if (this.autoIncludes != null) {
            copy.autoIncludes = (ArrayList)this.autoIncludes.clone();
        }
        return copy;
    }

    public final Configurable getParent() {
        return this.parent;
    }

    void setParent(Configurable parent) {
        this.parent = parent;
    }

    public void setClassicCompatible(boolean classicCompatibility) {
        this.classicCompatible = classicCompatibility ? 1 : 0;
        this.properties.setProperty("classic_compatible", this.classicCompatibilityIntToString(this.classicCompatible));
    }

    public void setClassicCompatibleAsInt(int classicCompatibility) {
        if (classicCompatibility < 0 || classicCompatibility > 2) {
            throw new IllegalArgumentException("Unsupported \"classicCompatibility\": " + classicCompatibility);
        }
        this.classicCompatible = classicCompatibility;
    }

    private String classicCompatibilityIntToString(Integer i) {
        if (i == null) {
            return null;
        }
        if (i == 0) {
            return "false";
        }
        if (i == 1) {
            return "true";
        }
        return i.toString();
    }

    public boolean isClassicCompatible() {
        return this.classicCompatible != null ? this.classicCompatible != 0 : this.parent.isClassicCompatible();
    }

    public int getClassicCompatibleAsInt() {
        return this.classicCompatible != null ? this.classicCompatible.intValue() : this.parent.getClassicCompatibleAsInt();
    }

    public boolean isClassicCompatibleSet() {
        return this.classicCompatible != null;
    }

    public void setLocale(Locale locale) {
        NullArgumentException.check("locale", locale);
        this.locale = locale;
        this.properties.setProperty("locale", locale.toString());
    }

    public Locale getLocale() {
        return this.locale != null ? this.locale : this.parent.getLocale();
    }

    public boolean isCFormatSet() {
        return this.cFormat != null;
    }

    public void setCFormat(CFormat cFormat) {
        NullArgumentException.check(C_FORMAT_KEY_CAMEL_CASE, cFormat);
        this.cFormat = cFormat;
    }

    public CFormat getCFormat() {
        return this.cFormat != null ? this.cFormat : this.parent.getCFormat();
    }

    public boolean isLocaleSet() {
        return this.locale != null;
    }

    public void setTimeZone(TimeZone timeZone) {
        NullArgumentException.check(TIME_ZONE_KEY_CAMEL_CASE, timeZone);
        this.timeZone = timeZone;
        this.properties.setProperty("time_zone", timeZone.getID());
    }

    public TimeZone getTimeZone() {
        return this.timeZone != null ? this.timeZone : this.parent.getTimeZone();
    }

    public boolean isTimeZoneSet() {
        return this.timeZone != null;
    }

    public void setSQLDateAndTimeTimeZone(TimeZone tz) {
        this.sqlDataAndTimeTimeZone = tz;
        this.sqlDataAndTimeTimeZoneSet = true;
        this.properties.setProperty("sql_date_and_time_time_zone", tz != null ? tz.getID() : NULL);
    }

    public TimeZone getSQLDateAndTimeTimeZone() {
        return this.sqlDataAndTimeTimeZoneSet ? this.sqlDataAndTimeTimeZone : (this.parent != null ? this.parent.getSQLDateAndTimeTimeZone() : null);
    }

    public boolean isSQLDateAndTimeTimeZoneSet() {
        return this.sqlDataAndTimeTimeZoneSet;
    }

    public void setNumberFormat(String numberFormat) {
        NullArgumentException.check(NUMBER_FORMAT_KEY_CAMEL_CASE, numberFormat);
        this.numberFormat = numberFormat;
        this.properties.setProperty("number_format", numberFormat);
    }

    public String getNumberFormat() {
        return this.numberFormat != null ? this.numberFormat : this.parent.getNumberFormat();
    }

    public boolean isNumberFormatSet() {
        return this.numberFormat != null;
    }

    public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormats() {
        return this.customNumberFormats == null ? this.parent.getCustomNumberFormats() : this.customNumberFormats;
    }

    public Map<String, ? extends TemplateNumberFormatFactory> getCustomNumberFormatsWithoutFallback() {
        return this.customNumberFormats;
    }

    public void setCustomNumberFormats(Map<String, ? extends TemplateNumberFormatFactory> customNumberFormats) {
        NullArgumentException.check(CUSTOM_NUMBER_FORMATS_KEY_CAMEL_CASE, customNumberFormats);
        this.validateFormatNames(customNumberFormats.keySet());
        this.customNumberFormats = customNumberFormats;
    }

    private void validateFormatNames(Set<String> keySet) {
        for (String name : keySet) {
            if (name.length() == 0) {
                throw new IllegalArgumentException("Format names can't be 0 length");
            }
            char firstChar = name.charAt(0);
            if (firstChar == '@') {
                throw new IllegalArgumentException("Format names can't start with '@'. '@' is only used when referring to them from format strings. In: " + name);
            }
            if (!Character.isLetter(firstChar)) {
                throw new IllegalArgumentException("Format name must start with letter: " + name);
            }
            for (int i = 1; i < name.length(); ++i) {
                if (Character.isLetterOrDigit(name.charAt(i))) continue;
                throw new IllegalArgumentException("Format name can only contain letters and digits: " + name);
            }
        }
    }

    public boolean isCustomNumberFormatsSet() {
        return this.customNumberFormats != null;
    }

    public TemplateNumberFormatFactory getCustomNumberFormat(String name) {
        TemplateNumberFormatFactory r;
        if (this.customNumberFormats != null && (r = this.customNumberFormats.get(name)) != null) {
            return r;
        }
        return this.parent != null ? this.parent.getCustomNumberFormat(name) : null;
    }

    public boolean hasCustomFormats() {
        return this.customNumberFormats != null && !this.customNumberFormats.isEmpty() || this.customDateFormats != null && !this.customDateFormats.isEmpty() || this.getParent() != null && this.getParent().hasCustomFormats();
    }

    public void setBooleanFormat(String booleanFormat) {
        Configurable.validateBooleanFormat(booleanFormat);
        this.booleanFormat = booleanFormat;
        this.properties.setProperty("boolean_format", booleanFormat);
    }

    private static void validateBooleanFormat(String booleanFormat) {
        Configurable.parseOrValidateBooleanFormat(booleanFormat, true);
    }

    static String[] parseBooleanFormat(String booleanFormat) {
        return Configurable.parseOrValidateBooleanFormat(booleanFormat, false);
    }

    private static String[] parseOrValidateBooleanFormat(String booleanFormat, boolean validateOnly) {
        NullArgumentException.check(BOOLEAN_FORMAT_KEY_CAMEL_CASE, booleanFormat);
        if (booleanFormat.equals(C_FORMAT_STRING)) {
            if (validateOnly) {
                return null;
            }
            return CollectionUtils.EMPTY_STRING_ARRAY;
        }
        if (booleanFormat.equals(BOOLEAN_FORMAT_LEGACY_DEFAULT)) {
            return null;
        }
        int commaIdx = booleanFormat.indexOf(44);
        if (commaIdx == -1) {
            throw new IllegalArgumentException("Setting value must be a string that contains two comma-separated values for true and false, or it must be \"c\", but it was " + StringUtil.jQuote(booleanFormat) + ".");
        }
        if (validateOnly) {
            return null;
        }
        return new String[]{booleanFormat.substring(0, commaIdx), booleanFormat.substring(commaIdx + 1)};
    }

    public String getBooleanFormat() {
        return this.booleanFormat != null ? this.booleanFormat : this.parent.getBooleanFormat();
    }

    public boolean isBooleanFormatSet() {
        return this.booleanFormat != null;
    }

    public void setTimeFormat(String timeFormat) {
        NullArgumentException.check(TIME_FORMAT_KEY_CAMEL_CASE, timeFormat);
        this.timeFormat = timeFormat;
        this.properties.setProperty("time_format", timeFormat);
    }

    public String getTimeFormat() {
        return this.timeFormat != null ? this.timeFormat : this.parent.getTimeFormat();
    }

    public boolean isTimeFormatSet() {
        return this.timeFormat != null;
    }

    public void setDateFormat(String dateFormat) {
        NullArgumentException.check(DATE_FORMAT_KEY_CAMEL_CASE, dateFormat);
        this.dateFormat = dateFormat;
        this.properties.setProperty("date_format", dateFormat);
    }

    public String getDateFormat() {
        return this.dateFormat != null ? this.dateFormat : this.parent.getDateFormat();
    }

    public boolean isDateFormatSet() {
        return this.dateFormat != null;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        NullArgumentException.check("dateTimeFormat", dateTimeFormat);
        this.dateTimeFormat = dateTimeFormat;
        this.properties.setProperty("datetime_format", dateTimeFormat);
    }

    public String getDateTimeFormat() {
        return this.dateTimeFormat != null ? this.dateTimeFormat : this.parent.getDateTimeFormat();
    }

    public boolean isDateTimeFormatSet() {
        return this.dateTimeFormat != null;
    }

    public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormats() {
        return this.customDateFormats == null ? this.parent.getCustomDateFormats() : this.customDateFormats;
    }

    public Map<String, ? extends TemplateDateFormatFactory> getCustomDateFormatsWithoutFallback() {
        return this.customDateFormats;
    }

    public void setCustomDateFormats(Map<String, ? extends TemplateDateFormatFactory> customDateFormats) {
        NullArgumentException.check(CUSTOM_DATE_FORMATS_KEY_CAMEL_CASE, customDateFormats);
        this.validateFormatNames(customDateFormats.keySet());
        this.customDateFormats = customDateFormats;
    }

    public boolean isCustomDateFormatsSet() {
        return this.customDateFormats != null;
    }

    public TemplateDateFormatFactory getCustomDateFormat(String name) {
        TemplateDateFormatFactory r;
        if (this.customDateFormats != null && (r = this.customDateFormats.get(name)) != null) {
            return r;
        }
        return this.parent != null ? this.parent.getCustomDateFormat(name) : null;
    }

    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        NullArgumentException.check(TEMPLATE_EXCEPTION_HANDLER_KEY_CAMEL_CASE, templateExceptionHandler);
        this.templateExceptionHandler = templateExceptionHandler;
        this.properties.setProperty("template_exception_handler", templateExceptionHandler.getClass().getName());
    }

    public TemplateExceptionHandler getTemplateExceptionHandler() {
        return this.templateExceptionHandler != null ? this.templateExceptionHandler : this.parent.getTemplateExceptionHandler();
    }

    public boolean isTemplateExceptionHandlerSet() {
        return this.templateExceptionHandler != null;
    }

    public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
        NullArgumentException.check(ATTEMPT_EXCEPTION_REPORTER_KEY_CAMEL_CASE, attemptExceptionReporter);
        this.attemptExceptionReporter = attemptExceptionReporter;
    }

    public AttemptExceptionReporter getAttemptExceptionReporter() {
        return this.attemptExceptionReporter != null ? this.attemptExceptionReporter : this.parent.getAttemptExceptionReporter();
    }

    public boolean isAttemptExceptionReporterSet() {
        return this.attemptExceptionReporter != null;
    }

    public void setArithmeticEngine(ArithmeticEngine arithmeticEngine) {
        NullArgumentException.check(ARITHMETIC_ENGINE_KEY_CAMEL_CASE, arithmeticEngine);
        this.arithmeticEngine = arithmeticEngine;
        this.properties.setProperty("arithmetic_engine", arithmeticEngine.getClass().getName());
    }

    public ArithmeticEngine getArithmeticEngine() {
        return this.arithmeticEngine != null ? this.arithmeticEngine : this.parent.getArithmeticEngine();
    }

    public boolean isArithmeticEngineSet() {
        return this.arithmeticEngine != null;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        NullArgumentException.check(OBJECT_WRAPPER_KEY_CAMEL_CASE, objectWrapper);
        this.objectWrapper = objectWrapper;
        this.properties.setProperty("object_wrapper", objectWrapper.getClass().getName());
    }

    public ObjectWrapper getObjectWrapper() {
        return this.objectWrapper != null ? this.objectWrapper : this.parent.getObjectWrapper();
    }

    public boolean isObjectWrapperSet() {
        return this.objectWrapper != null;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
        if (outputEncoding != null) {
            this.properties.setProperty("output_encoding", outputEncoding);
        } else {
            this.properties.remove("output_encoding");
        }
        this.outputEncodingSet = true;
    }

    public String getOutputEncoding() {
        return this.outputEncodingSet ? this.outputEncoding : (this.parent != null ? this.parent.getOutputEncoding() : null);
    }

    public boolean isOutputEncodingSet() {
        return this.outputEncodingSet;
    }

    public void setURLEscapingCharset(String urlEscapingCharset) {
        this.urlEscapingCharset = urlEscapingCharset;
        if (urlEscapingCharset != null) {
            this.properties.setProperty("url_escaping_charset", urlEscapingCharset);
        } else {
            this.properties.remove("url_escaping_charset");
        }
        this.urlEscapingCharsetSet = true;
    }

    public String getURLEscapingCharset() {
        return this.urlEscapingCharsetSet ? this.urlEscapingCharset : (this.parent != null ? this.parent.getURLEscapingCharset() : null);
    }

    public boolean isURLEscapingCharsetSet() {
        return this.urlEscapingCharsetSet;
    }

    public void setNewBuiltinClassResolver(TemplateClassResolver newBuiltinClassResolver) {
        NullArgumentException.check(NEW_BUILTIN_CLASS_RESOLVER_KEY_CAMEL_CASE, newBuiltinClassResolver);
        this.newBuiltinClassResolver = newBuiltinClassResolver;
        this.properties.setProperty("new_builtin_class_resolver", newBuiltinClassResolver.getClass().getName());
    }

    public TemplateClassResolver getNewBuiltinClassResolver() {
        return this.newBuiltinClassResolver != null ? this.newBuiltinClassResolver : this.parent.getNewBuiltinClassResolver();
    }

    public boolean isNewBuiltinClassResolverSet() {
        return this.newBuiltinClassResolver != null;
    }

    public void setAutoFlush(boolean autoFlush) {
        this.autoFlush = autoFlush;
        this.properties.setProperty("auto_flush", String.valueOf(autoFlush));
    }

    public boolean getAutoFlush() {
        return this.autoFlush != null ? this.autoFlush : (this.parent != null ? this.parent.getAutoFlush() : true);
    }

    public boolean isAutoFlushSet() {
        return this.autoFlush != null;
    }

    public void setShowErrorTips(boolean showTips) {
        this.showErrorTips = showTips;
        this.properties.setProperty("show_error_tips", String.valueOf(showTips));
    }

    public boolean getShowErrorTips() {
        return this.showErrorTips != null ? this.showErrorTips : (this.parent != null ? this.parent.getShowErrorTips() : true);
    }

    public boolean isShowErrorTipsSet() {
        return this.showErrorTips != null;
    }

    public void setAPIBuiltinEnabled(boolean value) {
        this.apiBuiltinEnabled = value;
        this.properties.setProperty("api_builtin_enabled", String.valueOf(value));
    }

    public boolean isAPIBuiltinEnabled() {
        return this.apiBuiltinEnabled != null ? this.apiBuiltinEnabled : (this.parent != null ? this.parent.isAPIBuiltinEnabled() : false);
    }

    public boolean isAPIBuiltinEnabledSet() {
        return this.apiBuiltinEnabled != null;
    }

    public void setTruncateBuiltinAlgorithm(TruncateBuiltinAlgorithm truncateBuiltinAlgorithm) {
        NullArgumentException.check(TRUNCATE_BUILTIN_ALGORITHM_KEY_CAMEL_CASE, truncateBuiltinAlgorithm);
        this.truncateBuiltinAlgorithm = truncateBuiltinAlgorithm;
    }

    public TruncateBuiltinAlgorithm getTruncateBuiltinAlgorithm() {
        return this.truncateBuiltinAlgorithm != null ? this.truncateBuiltinAlgorithm : this.parent.getTruncateBuiltinAlgorithm();
    }

    public boolean isTruncateBuiltinAlgorithmSet() {
        return this.truncateBuiltinAlgorithm != null;
    }

    public void setLogTemplateExceptions(boolean value) {
        this.logTemplateExceptions = value;
        this.properties.setProperty("log_template_exceptions", String.valueOf(value));
    }

    public boolean getLogTemplateExceptions() {
        return this.logTemplateExceptions != null ? this.logTemplateExceptions : (this.parent != null ? this.parent.getLogTemplateExceptions() : true);
    }

    public boolean isLogTemplateExceptionsSet() {
        return this.logTemplateExceptions != null;
    }

    public void setWrapUncheckedExceptions(boolean wrapUncheckedExceptions) {
        this.wrapUncheckedExceptions = wrapUncheckedExceptions;
    }

    public boolean getWrapUncheckedExceptions() {
        return this.wrapUncheckedExceptions != null ? this.wrapUncheckedExceptions : (this.parent != null ? this.parent.getWrapUncheckedExceptions() : false);
    }

    public boolean isWrapUncheckedExceptionsSet() {
        return this.wrapUncheckedExceptions != null;
    }

    public boolean getLazyImports() {
        return this.lazyImports != null ? this.lazyImports.booleanValue() : this.parent.getLazyImports();
    }

    public void setLazyImports(boolean lazyImports) {
        this.lazyImports = lazyImports;
    }

    public boolean isLazyImportsSet() {
        return this.lazyImports != null;
    }

    public Boolean getLazyAutoImports() {
        return this.lazyAutoImportsSet ? this.lazyAutoImports : this.parent.getLazyAutoImports();
    }

    public void setLazyAutoImports(Boolean lazyAutoImports) {
        this.lazyAutoImports = lazyAutoImports;
        this.lazyAutoImportsSet = true;
    }

    public boolean isLazyAutoImportsSet() {
        return this.lazyAutoImportsSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addAutoImport(String namespaceVarName, String templateName) {
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoImports == null) {
                this.initAutoImportsMap();
            } else {
                this.autoImports.remove(namespaceVarName);
            }
            this.autoImports.put(namespaceVarName, templateName);
        }
    }

    private void initAutoImportsMap() {
        this.autoImports = new LinkedHashMap(4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAutoImport(String namespaceVarName) {
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoImports != null) {
                this.autoImports.remove(namespaceVarName);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAutoImports(Map map) {
        NullArgumentException.check("map", map);
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoImports != null) {
                this.autoImports.clear();
            }
            for (Map.Entry entry : map.entrySet()) {
                Object key = entry.getKey();
                if (!(key instanceof String)) {
                    throw new IllegalArgumentException("Key in Map wasn't a String, but a(n) " + key.getClass().getName() + ".");
                }
                Object value = entry.getValue();
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException("Value in Map wasn't a String, but a(n) " + value.getClass().getName() + ".");
                }
                this.addAutoImport((String)key, (String)value);
            }
        }
    }

    public Map<String, String> getAutoImports() {
        return this.autoImports != null ? this.autoImports : this.parent.getAutoImports();
    }

    public boolean isAutoImportsSet() {
        return this.autoImports != null;
    }

    public Map<String, String> getAutoImportsWithoutFallback() {
        return this.autoImports;
    }

    public void addAutoInclude(String templateName) {
        this.addAutoInclude(templateName, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addAutoInclude(String templateName, boolean keepDuplicate) {
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoIncludes == null) {
                this.initAutoIncludesList();
            } else if (!keepDuplicate) {
                this.autoIncludes.remove(templateName);
            }
            this.autoIncludes.add(templateName);
        }
    }

    private void initAutoIncludesList() {
        this.autoIncludes = new ArrayList(4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAutoIncludes(List templateNames) {
        NullArgumentException.check("templateNames", templateNames);
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoIncludes != null) {
                this.autoIncludes.clear();
            }
            for (Object templateName : templateNames) {
                if (!(templateName instanceof String)) {
                    throw new IllegalArgumentException("List items must be String-s.");
                }
                this.addAutoInclude((String)templateName, this instanceof Configuration && ((Configuration)this).getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_25);
            }
        }
    }

    public List<String> getAutoIncludes() {
        return this.autoIncludes != null ? this.autoIncludes : this.parent.getAutoIncludes();
    }

    public boolean isAutoIncludesSet() {
        return this.autoIncludes != null;
    }

    public List<String> getAutoIncludesWithoutFallback() {
        return this.autoIncludes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAutoInclude(String templateName) {
        Configurable configurable = this;
        synchronized (configurable) {
            if (this.autoIncludes != null) {
                this.autoIncludes.remove(templateName);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void setSetting(String name, String value) throws TemplateException {
        boolean unknown = false;
        try {
            if ("locale".equals(name)) {
                if (JVM_DEFAULT.equalsIgnoreCase(value)) {
                    this.setLocale(Locale.getDefault());
                } else {
                    this.setLocale(StringUtil.deduceLocale(value));
                }
            } else if ("number_format".equals(name) || NUMBER_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                this.setNumberFormat(value);
            } else if ("custom_number_formats".equals(name) || CUSTOM_NUMBER_FORMATS_KEY_CAMEL_CASE.equals(name)) {
                Map map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, _SettingEvaluationEnvironment.getCurrent());
                _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
                _CoreAPI.checkSettingValueItemsType("Map values", TemplateNumberFormatFactory.class, map.values());
                this.setCustomNumberFormats(map);
            } else if ("time_format".equals(name) || TIME_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                this.setTimeFormat(value);
            } else if ("date_format".equals(name) || DATE_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                this.setDateFormat(value);
            } else if ("datetime_format".equals(name) || DATETIME_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                this.setDateTimeFormat(value);
            } else if ("custom_date_formats".equals(name) || CUSTOM_DATE_FORMATS_KEY_CAMEL_CASE.equals(name)) {
                Map map = (Map)_ObjectBuilderSettingEvaluator.eval(value, Map.class, false, _SettingEvaluationEnvironment.getCurrent());
                _CoreAPI.checkSettingValueItemsType("Map keys", String.class, map.keySet());
                _CoreAPI.checkSettingValueItemsType("Map values", TemplateDateFormatFactory.class, map.values());
                this.setCustomDateFormats(map);
            } else if ("time_zone".equals(name) || TIME_ZONE_KEY_CAMEL_CASE.equals(name)) {
                this.setTimeZone(this.parseTimeZoneSettingValue(value));
            } else if ("sql_date_and_time_time_zone".equals(name) || SQL_DATE_AND_TIME_TIME_ZONE_KEY_CAMEL_CASE.equals(name)) {
                this.setSQLDateAndTimeTimeZone(value.equals(NULL) ? null : this.parseTimeZoneSettingValue(value));
            } else if ("classic_compatible".equals(name) || CLASSIC_COMPATIBLE_KEY_CAMEL_CASE.equals(name)) {
                char firstChar = value != null && value.length() > 0 ? value.charAt(0) : (char)'\u0000';
                if (Character.isDigit(firstChar) || firstChar == '+' || firstChar == '-') {
                    this.setClassicCompatibleAsInt(Integer.parseInt(value));
                } else {
                    this.setClassicCompatible(value != null ? StringUtil.getYesNo(value) : false);
                }
            } else if ("template_exception_handler".equals(name) || TEMPLATE_EXCEPTION_HANDLER_KEY_CAMEL_CASE.equals(name)) {
                if (value.indexOf(46) == -1) {
                    if ("debug".equalsIgnoreCase(value)) {
                        this.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                    } else if ("html_debug".equalsIgnoreCase(value) || "htmlDebug".equals(value)) {
                        this.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                    } else if ("ignore".equalsIgnoreCase(value)) {
                        this.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                    } else if ("rethrow".equalsIgnoreCase(value)) {
                        this.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    } else {
                        if (!DEFAULT.equalsIgnoreCase(value) || !(this instanceof Configuration)) throw this.invalidSettingValueException(name, value);
                        ((Configuration)this).unsetTemplateExceptionHandler();
                    }
                } else {
                    this.setTemplateExceptionHandler((TemplateExceptionHandler)_ObjectBuilderSettingEvaluator.eval(value, TemplateExceptionHandler.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("attempt_exception_reporter".equals(name) || ATTEMPT_EXCEPTION_REPORTER_KEY_CAMEL_CASE.equals(name)) {
                if (value.indexOf(46) == -1) {
                    if ("log_error".equalsIgnoreCase(value) || "logError".equals(value)) {
                        this.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_ERROR_REPORTER);
                    } else if ("log_warn".equalsIgnoreCase(value) || "logWarn".equals(value)) {
                        this.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_WARN_REPORTER);
                    } else {
                        if (!DEFAULT.equalsIgnoreCase(value) || !(this instanceof Configuration)) throw this.invalidSettingValueException(name, value);
                        ((Configuration)this).unsetAttemptExceptionReporter();
                    }
                } else {
                    this.setAttemptExceptionReporter((AttemptExceptionReporter)_ObjectBuilderSettingEvaluator.eval(value, AttemptExceptionReporter.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("arithmetic_engine".equals(name) || ARITHMETIC_ENGINE_KEY_CAMEL_CASE.equals(name)) {
                if (value.indexOf(46) == -1) {
                    if ("bigdecimal".equalsIgnoreCase(value)) {
                        this.setArithmeticEngine(ArithmeticEngine.BIGDECIMAL_ENGINE);
                    } else {
                        if (!"conservative".equalsIgnoreCase(value)) throw this.invalidSettingValueException(name, value);
                        this.setArithmeticEngine(ArithmeticEngine.CONSERVATIVE_ENGINE);
                    }
                } else {
                    this.setArithmeticEngine((ArithmeticEngine)_ObjectBuilderSettingEvaluator.eval(value, ArithmeticEngine.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("object_wrapper".equals(name) || OBJECT_WRAPPER_KEY_CAMEL_CASE.equals(name)) {
                if (DEFAULT.equalsIgnoreCase(value)) {
                    if (this instanceof Configuration) {
                        ((Configuration)this).unsetObjectWrapper();
                    } else {
                        this.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
                    }
                } else if (DEFAULT_2_3_0.equalsIgnoreCase(value)) {
                    this.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_0));
                } else if ("simple".equalsIgnoreCase(value)) {
                    this.setObjectWrapper(ObjectWrapper.SIMPLE_WRAPPER);
                } else if ("beans".equalsIgnoreCase(value)) {
                    this.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
                } else if ("jython".equalsIgnoreCase(value)) {
                    Class<?> clazz = Class.forName("freemarker.ext.jython.JythonWrapper");
                    this.setObjectWrapper((ObjectWrapper)clazz.getField("INSTANCE").get(null));
                } else {
                    this.setObjectWrapper((ObjectWrapper)_ObjectBuilderSettingEvaluator.eval(value, ObjectWrapper.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("boolean_format".equals(name) || BOOLEAN_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                this.setBooleanFormat(value);
            } else if ("c_format".equals(name) || C_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase(DEFAULT)) {
                    if (!(this instanceof Configuration)) throw this.invalidSettingValueException(name, value);
                    ((Configuration)this).unsetCFormat();
                } else {
                    CFormat cFormat = StandardCFormats.STANDARD_C_FORMATS.get(value);
                    this.setCFormat(cFormat != null ? cFormat : (CFormat)_ObjectBuilderSettingEvaluator.eval(value, CFormat.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("output_encoding".equals(name) || OUTPUT_ENCODING_KEY_CAMEL_CASE.equals(name)) {
                this.setOutputEncoding(value);
            } else if ("url_escaping_charset".equals(name) || URL_ESCAPING_CHARSET_KEY_CAMEL_CASE.equals(name)) {
                this.setURLEscapingCharset(value);
            } else if ("strict_bean_models".equals(name) || STRICT_BEAN_MODELS_KEY_CAMEL_CASE.equals(name)) {
                this.setStrictBeanModels(StringUtil.getYesNo(value));
            } else if ("auto_flush".equals(name) || AUTO_FLUSH_KEY_CAMEL_CASE.equals(name)) {
                this.setAutoFlush(StringUtil.getYesNo(value));
            } else if ("show_error_tips".equals(name) || SHOW_ERROR_TIPS_KEY_CAMEL_CASE.equals(name)) {
                this.setShowErrorTips(StringUtil.getYesNo(value));
            } else if ("api_builtin_enabled".equals(name) || API_BUILTIN_ENABLED_KEY_CAMEL_CASE.equals(name)) {
                this.setAPIBuiltinEnabled(StringUtil.getYesNo(value));
            } else if ("truncate_builtin_algorithm".equals(name) || TRUNCATE_BUILTIN_ALGORITHM_KEY_CAMEL_CASE.equals(name)) {
                if ("ascii".equalsIgnoreCase(value)) {
                    this.setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.ASCII_INSTANCE);
                } else if ("unicode".equalsIgnoreCase(value)) {
                    this.setTruncateBuiltinAlgorithm(DefaultTruncateBuiltinAlgorithm.UNICODE_INSTANCE);
                } else {
                    this.setTruncateBuiltinAlgorithm((TruncateBuiltinAlgorithm)_ObjectBuilderSettingEvaluator.eval(value, TruncateBuiltinAlgorithm.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("new_builtin_class_resolver".equals(name) || NEW_BUILTIN_CLASS_RESOLVER_KEY_CAMEL_CASE.equals(name)) {
                if ("unrestricted".equals(value)) {
                    this.setNewBuiltinClassResolver(TemplateClassResolver.UNRESTRICTED_RESOLVER);
                } else if ("safer".equals(value)) {
                    this.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
                } else if ("allows_nothing".equals(value) || "allowsNothing".equals(value)) {
                    this.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
                } else if (value.indexOf(":") != -1) {
                    ArrayList segments = this.parseAsSegmentedList(value);
                    HashSet allowedClasses = null;
                    List trustedTemplates = null;
                    for (int i = 0; i < segments.size(); ++i) {
                        KeyValuePair kv = (KeyValuePair)segments.get(i);
                        String segmentKey = (String)kv.getKey();
                        List segmentValue = (List)kv.getValue();
                        if (segmentKey.equals(ALLOWED_CLASSES_SNAKE_CASE) || segmentKey.equals(ALLOWED_CLASSES_CAMEL_CASE)) {
                            allowedClasses = new HashSet(segmentValue);
                            continue;
                        }
                        if (!segmentKey.equals(TRUSTED_TEMPLATES_SNAKE_CASE) && !segmentKey.equals(TRUSTED_TEMPLATES_CAMEL_CASE)) throw new ParseException("Unrecognized list segment key: " + StringUtil.jQuote(segmentKey) + ". Supported keys are: \"" + ALLOWED_CLASSES_SNAKE_CASE + "\", \"" + ALLOWED_CLASSES_CAMEL_CASE + "\", \"" + TRUSTED_TEMPLATES_SNAKE_CASE + "\", \"" + TRUSTED_TEMPLATES_CAMEL_CASE + "\". ", 0, 0);
                        trustedTemplates = segmentValue;
                    }
                    this.setNewBuiltinClassResolver(new OptInTemplateClassResolver(allowedClasses, trustedTemplates));
                } else {
                    if ("allow_nothing".equals(value)) {
                        throw new IllegalArgumentException("The correct value would be: allows_nothing");
                    }
                    if ("allowNothing".equals(value)) {
                        throw new IllegalArgumentException("The correct value would be: allowsNothing");
                    }
                    if (value.indexOf(46) == -1) throw this.invalidSettingValueException(name, value);
                    this.setNewBuiltinClassResolver((TemplateClassResolver)_ObjectBuilderSettingEvaluator.eval(value, TemplateClassResolver.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("log_template_exceptions".equals(name) || LOG_TEMPLATE_EXCEPTIONS_KEY_CAMEL_CASE.equals(name)) {
                this.setLogTemplateExceptions(StringUtil.getYesNo(value));
            } else if ("wrap_unchecked_exceptions".equals(name) || WRAP_UNCHECKED_EXCEPTIONS_KEY_CAMEL_CASE.equals(name)) {
                this.setWrapUncheckedExceptions(StringUtil.getYesNo(value));
            } else if ("lazy_auto_imports".equals(name) || LAZY_AUTO_IMPORTS_KEY_CAMEL_CASE.equals(name)) {
                this.setLazyAutoImports(value.equals(NULL) ? null : Boolean.valueOf(StringUtil.getYesNo(value)));
            } else if ("lazy_imports".equals(name) || LAZY_IMPORTS_KEY_CAMEL_CASE.equals(name)) {
                this.setLazyImports(StringUtil.getYesNo(value));
            } else if ("auto_include".equals(name) || AUTO_INCLUDE_KEY_CAMEL_CASE.equals(name)) {
                this.setAutoIncludes(this.parseAsList(value));
            } else if ("auto_import".equals(name) || AUTO_IMPORT_KEY_CAMEL_CASE.equals(name)) {
                this.setAutoImports(this.parseAsImportList(value));
            } else {
                unknown = true;
            }
        }
        catch (Exception e) {
            throw this.settingValueAssignmentException(name, value, e);
        }
        if (!unknown) return;
        throw this.unknownSettingException(name);
    }

    public Set<String> getSettingNames(boolean camelCase) {
        return new _SortedArraySet<String>(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE);
    }

    private TimeZone parseTimeZoneSettingValue(String value) {
        TimeZone tz = JVM_DEFAULT.equalsIgnoreCase(value) ? TimeZone.getDefault() : TimeZone.getTimeZone(value);
        return tz;
    }

    @Deprecated
    public void setStrictBeanModels(boolean strict) {
        if (!(this.objectWrapper instanceof BeansWrapper)) {
            throw new IllegalStateException("The value of the object_wrapper setting isn't a " + BeansWrapper.class.getName() + ".");
        }
        ((BeansWrapper)this.objectWrapper).setStrict(strict);
    }

    @Deprecated
    public String getSetting(String key) {
        return this.properties.getProperty(key);
    }

    @Deprecated
    public Map getSettings() {
        return Collections.unmodifiableMap(this.properties);
    }

    protected Environment getEnvironment() {
        return this instanceof Environment ? (Environment)this : Environment.getCurrentEnvironment();
    }

    protected TemplateException unknownSettingException(String name) {
        return new UnknownSettingException(this.getEnvironment(), name, this.getCorrectedNameForUnknownSetting(name));
    }

    protected String getCorrectedNameForUnknownSetting(String name) {
        return null;
    }

    protected TemplateException settingValueAssignmentException(String name, String value, Throwable cause) {
        return new SettingValueAssignmentException(this.getEnvironment(), name, value, cause);
    }

    protected TemplateException invalidSettingValueException(String name, String value) {
        return new _MiscTemplateException(this.getEnvironment(), "Invalid value for setting ", new _DelayedJQuote(name), ": ", new _DelayedJQuote(value));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setSettings(Properties props) throws TemplateException {
        _SettingEvaluationEnvironment prevEnv = _SettingEvaluationEnvironment.startScope();
        try {
            for (String string : props.keySet()) {
                this.setSetting(string, props.getProperty(string).trim());
            }
        }
        finally {
            _SettingEvaluationEnvironment.endScope(prevEnv);
        }
    }

    public void setSettings(InputStream propsIn) throws TemplateException, IOException {
        Properties p = new Properties();
        p.load(propsIn);
        this.setSettings(p);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void setCustomAttribute(Object key, Object value) {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            this.customAttributes.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Object getCustomAttribute(Object key, CustomAttribute attr) {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            Object o = this.customAttributes.get(key);
            if (o == null && !this.customAttributes.containsKey(key)) {
                o = attr.create();
                this.customAttributes.put(key, o);
            }
            return o;
        }
    }

    boolean isCustomAttributeSet(Object key) {
        return this.customAttributes.containsKey(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void copyDirectCustomAttributes(Configurable target, boolean overwriteExisting) {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            for (Map.Entry<Object, Object> custAttrEnt : this.customAttributes.entrySet()) {
                Object custAttrKey = custAttrEnt.getKey();
                if (!overwriteExisting && target.isCustomAttributeSet(custAttrKey)) continue;
                if (custAttrKey instanceof String) {
                    target.setCustomAttribute((String)custAttrKey, custAttrEnt.getValue());
                    continue;
                }
                target.setCustomAttribute(custAttrKey, custAttrEnt.getValue());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCustomAttribute(String name, Object value) {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            this.customAttributes.put(name, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getCustomAttributeNames() {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            LinkedList<Object> names = new LinkedList<Object>(this.customAttributes.keySet());
            Iterator iter = names.iterator();
            while (iter.hasNext()) {
                if (iter.next() instanceof String) continue;
                iter.remove();
            }
            return names.toArray(new String[names.size()]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeCustomAttribute(String name) {
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            this.customAttributes.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getCustomAttribute(String name) {
        Object retval;
        HashMap<Object, Object> hashMap = this.customAttributes;
        synchronized (hashMap) {
            retval = this.customAttributes.get(name);
            if (retval == null && this.customAttributes.containsKey(name)) {
                return null;
            }
        }
        if (retval == null && this.parent != null) {
            return this.parent.getCustomAttribute(name);
        }
        return retval;
    }

    protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
        if (this.parent != null) {
            this.parent.doAutoImportsAndIncludes(env);
        }
    }

    protected ArrayList parseAsList(String text) throws ParseException {
        return new SettingStringParser(text).parseAsList();
    }

    protected ArrayList parseAsSegmentedList(String text) throws ParseException {
        return new SettingStringParser(text).parseAsSegmentedList();
    }

    protected HashMap parseAsImportList(String text) throws ParseException {
        return new SettingStringParser(text).parseAsImportList();
    }

    private static class SettingStringParser {
        private String text;
        private int p;
        private int ln;

        private SettingStringParser(String text) {
            this.text = text;
            this.p = 0;
            this.ln = text.length();
        }

        ArrayList parseAsSegmentedList() throws ParseException {
            char c;
            ArrayList<KeyValuePair> segments = new ArrayList<KeyValuePair>();
            ArrayList<String> currentSegment = null;
            while ((c = this.skipWS()) != ' ') {
                String item = this.fetchStringValue();
                c = this.skipWS();
                if (c == ':') {
                    currentSegment = new ArrayList<String>();
                    segments.add(new KeyValuePair(item, currentSegment));
                } else {
                    if (currentSegment == null) {
                        throw new ParseException("The very first list item must be followed by \":\" so it will be the key for the following sub-list.", 0, 0);
                    }
                    currentSegment.add(item);
                }
                if (c == ' ') break;
                if (c != ',' && c != ':') {
                    throw new ParseException("Expected \",\" or \":\" or the end of text but found \"" + c + "\"", 0, 0);
                }
                ++this.p;
            }
            return segments;
        }

        ArrayList parseAsList() throws ParseException {
            char c;
            ArrayList<String> seq = new ArrayList<String>();
            while ((c = this.skipWS()) != ' ') {
                seq.add(this.fetchStringValue());
                c = this.skipWS();
                if (c == ' ') break;
                if (c != ',') {
                    throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
                }
                ++this.p;
            }
            return seq;
        }

        HashMap parseAsImportList() throws ParseException {
            char c;
            HashMap<String, String> map = new HashMap<String, String>();
            while ((c = this.skipWS()) != ' ') {
                String lib = this.fetchStringValue();
                c = this.skipWS();
                if (c == ' ') {
                    throw new ParseException("Unexpected end of text: expected \"as\"", 0, 0);
                }
                String s = this.fetchKeyword();
                if (!s.equalsIgnoreCase("as")) {
                    throw new ParseException("Expected \"as\", but found " + StringUtil.jQuote(s), 0, 0);
                }
                c = this.skipWS();
                if (c == ' ') {
                    throw new ParseException("Unexpected end of text: expected gate hash name", 0, 0);
                }
                String ns = this.fetchStringValue();
                map.put(ns, lib);
                c = this.skipWS();
                if (c == ' ') break;
                if (c != ',') {
                    throw new ParseException("Expected \",\" or the end of text but found \"" + c + "\"", 0, 0);
                }
                ++this.p;
            }
            return map;
        }

        String fetchStringValue() throws ParseException {
            String w = this.fetchWord();
            if (w.startsWith("'") || w.startsWith("\"")) {
                w = w.substring(1, w.length() - 1);
            }
            return StringUtil.FTLStringLiteralDec(w);
        }

        String fetchKeyword() throws ParseException {
            String w = this.fetchWord();
            if (w.startsWith("'") || w.startsWith("\"")) {
                throw new ParseException("Keyword expected, but a string value found: " + w, 0, 0);
            }
            return w;
        }

        char skipWS() {
            while (this.p < this.ln) {
                char c = this.text.charAt(this.p);
                if (!Character.isWhitespace(c)) {
                    return c;
                }
                ++this.p;
            }
            return ' ';
        }

        private String fetchWord() throws ParseException {
            if (this.p == this.ln) {
                throw new ParseException("Unexpeced end of text", 0, 0);
            }
            char c = this.text.charAt(this.p);
            int b = this.p;
            if (c == '\'' || c == '\"') {
                boolean escaped = false;
                char q = c;
                ++this.p;
                while (this.p < this.ln) {
                    c = this.text.charAt(this.p);
                    if (!escaped) {
                        if (c == '\\') {
                            escaped = true;
                        } else if (c == q) {
                            break;
                        }
                    } else {
                        escaped = false;
                    }
                    ++this.p;
                }
                if (this.p == this.ln) {
                    throw new ParseException("Missing " + q, 0, 0);
                }
                ++this.p;
                return this.text.substring(b, this.p);
            }
            while (Character.isLetterOrDigit(c = this.text.charAt(this.p)) || c == '/' || c == '\\' || c == '_' || c == '.' || c == '-' || c == '!' || c == '*' || c == '?') {
                ++this.p;
                if (this.p < this.ln) continue;
            }
            if (b == this.p) {
                throw new ParseException("Unexpected character: " + c, 0, 0);
            }
            return this.text.substring(b, this.p);
        }
    }

    private static class KeyValuePair {
        private final Object key;
        private final Object value;

        KeyValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        Object getKey() {
            return this.key;
        }

        Object getValue() {
            return this.value;
        }
    }

    public static class SettingValueAssignmentException
    extends _MiscTemplateException {
        private SettingValueAssignmentException(Environment env, String name, String value, Throwable cause) {
            super(cause, env, "Failed to set FreeMarker configuration setting ", new _DelayedJQuote(name), " to value ", new _DelayedJQuote(value), "; see cause exception.");
        }
    }

    public static class UnknownSettingException
    extends _MiscTemplateException {
        private UnknownSettingException(Environment env, String name, String correctedName) {
            Object[] objectArray;
            Object[] objectArray2 = new Object[3];
            objectArray2[0] = "Unknown FreeMarker configuration setting: ";
            objectArray2[1] = new _DelayedJQuote(name);
            if (correctedName == null) {
                objectArray = "";
            } else {
                Object[] objectArray3 = new Object[2];
                objectArray3[0] = ". You may meant: ";
                objectArray = objectArray3;
                objectArray3[1] = new _DelayedJQuote(correctedName);
            }
            objectArray2[2] = objectArray;
            super(env, objectArray2);
        }
    }
}

