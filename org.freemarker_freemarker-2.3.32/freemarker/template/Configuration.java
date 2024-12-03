/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.cache.CacheStorage;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.cache.TemplateCache;
import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.TemplateNameFormat;
import freemarker.core.BugException;
import freemarker.core.CFormat;
import freemarker.core.CSSOutputFormat;
import freemarker.core.CombinedMarkupOutputFormat;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.JSONOutputFormat;
import freemarker.core.JavaScriptOrJSONCFormat;
import freemarker.core.JavaScriptOutputFormat;
import freemarker.core.LegacyCFormat;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.PlainTextOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.UndefinedOutputFormat;
import freemarker.core.UnregisteredOutputFormatException;
import freemarker.core.XHTMLOutputFormat;
import freemarker.core.XMLOutputFormat;
import freemarker.core._CoreAPI;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.core._SortedArraySet;
import freemarker.core._UnmodifiableCompositeSet;
import freemarker.log.Logger;
import freemarker.template.AttemptExceptionReporter;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.CaptureOutput;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.NormalizeNewlines;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StandardCompress;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.XmlEscape;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Configuration
extends Configurable
implements Cloneable,
ParserConfiguration {
    private static final Logger CACHE_LOG;
    private static final String VERSION_PROPERTIES_PATH = "/freemarker/version.properties";
    public static final String DEFAULT_ENCODING_KEY_SNAKE_CASE = "default_encoding";
    public static final String DEFAULT_ENCODING_KEY_CAMEL_CASE = "defaultEncoding";
    public static final String DEFAULT_ENCODING_KEY = "default_encoding";
    public static final String LOCALIZED_LOOKUP_KEY_SNAKE_CASE = "localized_lookup";
    public static final String LOCALIZED_LOOKUP_KEY_CAMEL_CASE = "localizedLookup";
    public static final String LOCALIZED_LOOKUP_KEY = "localized_lookup";
    public static final String STRICT_SYNTAX_KEY_SNAKE_CASE = "strict_syntax";
    public static final String STRICT_SYNTAX_KEY_CAMEL_CASE = "strictSyntax";
    public static final String STRICT_SYNTAX_KEY = "strict_syntax";
    public static final String WHITESPACE_STRIPPING_KEY_SNAKE_CASE = "whitespace_stripping";
    public static final String WHITESPACE_STRIPPING_KEY_CAMEL_CASE = "whitespaceStripping";
    public static final String WHITESPACE_STRIPPING_KEY = "whitespace_stripping";
    public static final String OUTPUT_FORMAT_KEY_SNAKE_CASE = "output_format";
    public static final String OUTPUT_FORMAT_KEY_CAMEL_CASE = "outputFormat";
    public static final String OUTPUT_FORMAT_KEY = "output_format";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_SNAKE_CASE = "recognize_standard_file_extensions";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE = "recognizeStandardFileExtensions";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY = "recognize_standard_file_extensions";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_SNAKE_CASE = "registered_custom_output_formats";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE = "registeredCustomOutputFormats";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY = "registered_custom_output_formats";
    public static final String AUTO_ESCAPING_POLICY_KEY_SNAKE_CASE = "auto_escaping_policy";
    public static final String AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE = "autoEscapingPolicy";
    public static final String AUTO_ESCAPING_POLICY_KEY = "auto_escaping_policy";
    public static final String CACHE_STORAGE_KEY_SNAKE_CASE = "cache_storage";
    public static final String CACHE_STORAGE_KEY_CAMEL_CASE = "cacheStorage";
    public static final String CACHE_STORAGE_KEY = "cache_storage";
    public static final String TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE = "template_update_delay";
    public static final String TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE = "templateUpdateDelay";
    public static final String TEMPLATE_UPDATE_DELAY_KEY = "template_update_delay";
    public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
    public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
    public static final String AUTO_IMPORT_KEY = "auto_import";
    public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
    public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
    public static final String AUTO_INCLUDE_KEY = "auto_include";
    public static final String TAG_SYNTAX_KEY_SNAKE_CASE = "tag_syntax";
    public static final String TAG_SYNTAX_KEY_CAMEL_CASE = "tagSyntax";
    public static final String TAG_SYNTAX_KEY = "tag_syntax";
    public static final String INTERPOLATION_SYNTAX_KEY_SNAKE_CASE = "interpolation_syntax";
    public static final String INTERPOLATION_SYNTAX_KEY_CAMEL_CASE = "interpolationSyntax";
    public static final String INTERPOLATION_SYNTAX_KEY = "interpolation_syntax";
    public static final String NAMING_CONVENTION_KEY_SNAKE_CASE = "naming_convention";
    public static final String NAMING_CONVENTION_KEY_CAMEL_CASE = "namingConvention";
    public static final String NAMING_CONVENTION_KEY = "naming_convention";
    public static final String TAB_SIZE_KEY_SNAKE_CASE = "tab_size";
    public static final String TAB_SIZE_KEY_CAMEL_CASE = "tabSize";
    public static final String TAB_SIZE_KEY = "tab_size";
    public static final String TEMPLATE_LOADER_KEY_SNAKE_CASE = "template_loader";
    public static final String TEMPLATE_LOADER_KEY_CAMEL_CASE = "templateLoader";
    public static final String TEMPLATE_LOADER_KEY = "template_loader";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_SNAKE_CASE = "template_lookup_strategy";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE = "templateLookupStrategy";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY = "template_lookup_strategy";
    public static final String TEMPLATE_NAME_FORMAT_KEY_SNAKE_CASE = "template_name_format";
    public static final String TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE = "templateNameFormat";
    public static final String TEMPLATE_NAME_FORMAT_KEY = "template_name_format";
    public static final String TEMPLATE_CONFIGURATIONS_KEY_SNAKE_CASE = "template_configurations";
    public static final String TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE = "templateConfigurations";
    public static final String TEMPLATE_CONFIGURATIONS_KEY = "template_configurations";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_SNAKE_CASE = "incompatible_improvements";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE = "incompatibleImprovements";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY = "incompatible_improvements";
    @Deprecated
    public static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
    @Deprecated
    public static final String INCOMPATIBLE_ENHANCEMENTS = "incompatible_enhancements";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_SNAKE_CASE = "fallback_on_null_loop_variable";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE = "fallbackOnNullLoopVariable";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY = "fallback_on_null_loop_variable";
    private static final String[] SETTING_NAMES_SNAKE_CASE;
    private static final String[] SETTING_NAMES_CAMEL_CASE;
    private static final Map<String, OutputFormat> STANDARD_OUTPUT_FORMATS;
    public static final int AUTO_DETECT_TAG_SYNTAX = 0;
    public static final int ANGLE_BRACKET_TAG_SYNTAX = 1;
    public static final int SQUARE_BRACKET_TAG_SYNTAX = 2;
    public static final int LEGACY_INTERPOLATION_SYNTAX = 20;
    public static final int DOLLAR_INTERPOLATION_SYNTAX = 21;
    public static final int SQUARE_BRACKET_INTERPOLATION_SYNTAX = 22;
    public static final int AUTO_DETECT_NAMING_CONVENTION = 10;
    public static final int LEGACY_NAMING_CONVENTION = 11;
    public static final int CAMEL_CASE_NAMING_CONVENTION = 12;
    public static final int DISABLE_AUTO_ESCAPING_POLICY = 20;
    public static final int ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY = 21;
    public static final int ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY = 22;
    public static final Version VERSION_2_3_0;
    public static final Version VERSION_2_3_19;
    public static final Version VERSION_2_3_20;
    public static final Version VERSION_2_3_21;
    public static final Version VERSION_2_3_22;
    public static final Version VERSION_2_3_23;
    public static final Version VERSION_2_3_24;
    public static final Version VERSION_2_3_25;
    public static final Version VERSION_2_3_26;
    public static final Version VERSION_2_3_27;
    public static final Version VERSION_2_3_28;
    public static final Version VERSION_2_3_29;
    public static final Version VERSION_2_3_30;
    public static final Version VERSION_2_3_31;
    public static final Version VERSION_2_3_32;
    public static final Version DEFAULT_INCOMPATIBLE_IMPROVEMENTS;
    @Deprecated
    public static final String DEFAULT_INCOMPATIBLE_ENHANCEMENTS;
    @Deprecated
    public static final int PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS;
    private static final String NULL = "null";
    private static final String DEFAULT = "default";
    private static final String JVM_DEFAULT = "JVM default";
    private static final Version VERSION;
    private static final String FM_24_DETECTION_CLASS_NAME = "freemarker.core._2_4_OrLaterMarker";
    private static final boolean FM_24_DETECTED;
    private static final Object defaultConfigLock;
    private static volatile Configuration defaultConfig;
    private boolean strictSyntax = true;
    private volatile boolean localizedLookup = true;
    private boolean whitespaceStripping = true;
    private int autoEscapingPolicy = 21;
    private OutputFormat outputFormat = UndefinedOutputFormat.INSTANCE;
    private boolean outputFormatExplicitlySet;
    private Boolean recognizeStandardFileExtensions;
    private Map<String, ? extends OutputFormat> registeredCustomOutputFormats = Collections.emptyMap();
    private Version incompatibleImprovements;
    private int tagSyntax = 1;
    private int interpolationSyntax = 20;
    private int namingConvention = 10;
    private int tabSize = 8;
    private boolean fallbackOnNullLoopVariable = true;
    private boolean preventStrippings;
    private TemplateCache cache;
    private boolean templateLoaderExplicitlySet;
    private boolean templateLookupStrategyExplicitlySet;
    private boolean templateNameFormatExplicitlySet;
    private boolean cacheStorageExplicitlySet;
    private boolean objectWrapperExplicitlySet;
    private boolean templateExceptionHandlerExplicitlySet;
    private boolean attemptExceptionReporterExplicitlySet;
    private boolean logTemplateExceptionsExplicitlySet;
    private boolean wrapUncheckedExceptionsExplicitlySet;
    private boolean localeExplicitlySet;
    private boolean defaultEncodingExplicitlySet;
    private boolean timeZoneExplicitlySet;
    private boolean cFormatExplicitlySet;
    private HashMap sharedVariables = new HashMap();
    private HashMap rewrappableSharedVariables = null;
    private String defaultEncoding = Configuration.getDefaultDefaultEncoding();
    private ConcurrentMap localeToCharsetMap = new ConcurrentHashMap();

    @Deprecated
    public Configuration() {
        this(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public Configuration(Version incompatibleImprovements) {
        super(incompatibleImprovements);
        Configuration.checkFreeMarkerVersionClash();
        NullArgumentException.check(INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE, incompatibleImprovements);
        Configuration.checkCurrentVersionNotRecycled(incompatibleImprovements);
        this.incompatibleImprovements = incompatibleImprovements;
        this.createTemplateCache();
        this.loadBuiltInSharedVariables();
    }

    private static void checkFreeMarkerVersionClash() {
        if (FM_24_DETECTED) {
            throw new RuntimeException("Clashing FreeMarker versions (" + VERSION + " and some post-2.3.x) detected: found post-2.3.x class " + FM_24_DETECTION_CLASS_NAME + ". You probably have two different freemarker.jar-s in the classpath.");
        }
    }

    private void createTemplateCache() {
        this.cache = new TemplateCache(this.getDefaultTemplateLoader(), this.getDefaultCacheStorage(), this.getDefaultTemplateLookupStrategy(), this.getDefaultTemplateNameFormat(), null, this);
        this.cache.clear();
        this.cache.setDelay(5000L);
    }

    private void recreateTemplateCacheWith(TemplateLoader loader, CacheStorage storage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations) {
        TemplateCache oldCache = this.cache;
        this.cache = new TemplateCache(loader, storage, templateLookupStrategy, templateNameFormat, templateConfigurations, this);
        this.cache.clear();
        this.cache.setDelay(oldCache.getDelay());
        this.cache.setLocalizedLookup(this.localizedLookup);
    }

    private void recreateTemplateCache() {
        this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.getTemplateConfigurations());
    }

    private TemplateLoader getDefaultTemplateLoader() {
        return Configuration.createDefaultTemplateLoader(this.getIncompatibleImprovements(), this.getTemplateLoader());
    }

    static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
        return Configuration.createDefaultTemplateLoader(incompatibleImprovements, null);
    }

    private static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements, TemplateLoader existingTemplateLoader) {
        if (incompatibleImprovements.intValue() < _VersionInts.V_2_3_21) {
            if (existingTemplateLoader instanceof LegacyDefaultFileTemplateLoader) {
                return existingTemplateLoader;
            }
            try {
                return new LegacyDefaultFileTemplateLoader();
            }
            catch (Exception e) {
                CACHE_LOG.warn("Couldn't create legacy default TemplateLoader which accesses the current directory. (Use new Configuration(Configuration.VERSION_2_3_21) or higher to avoid this.)", e);
                return null;
            }
        }
        return null;
    }

    private TemplateLookupStrategy getDefaultTemplateLookupStrategy() {
        return Configuration.getDefaultTemplateLookupStrategy(this.getIncompatibleImprovements());
    }

    static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
        return TemplateLookupStrategy.DEFAULT_2_3_0;
    }

    private TemplateNameFormat getDefaultTemplateNameFormat() {
        return Configuration.getDefaultTemplateNameFormat(this.getIncompatibleImprovements());
    }

    static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
        return TemplateNameFormat.DEFAULT_2_3_0;
    }

    private CacheStorage getDefaultCacheStorage() {
        return Configuration.createDefaultCacheStorage(this.getIncompatibleImprovements(), this.getCacheStorage());
    }

    static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements, CacheStorage existingCacheStorage) {
        if (existingCacheStorage instanceof DefaultSoftCacheStorage) {
            return existingCacheStorage;
        }
        return new DefaultSoftCacheStorage();
    }

    static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
        return Configuration.createDefaultCacheStorage(incompatibleImprovements, null);
    }

    private TemplateExceptionHandler getDefaultTemplateExceptionHandler() {
        return Configuration.getDefaultTemplateExceptionHandler(this.getIncompatibleImprovements());
    }

    private AttemptExceptionReporter getDefaultAttemptExceptionReporter() {
        return Configuration.getDefaultAttemptExceptionReporter(this.getIncompatibleImprovements());
    }

    private boolean getDefaultLogTemplateExceptions() {
        return Configuration.getDefaultLogTemplateExceptions(this.getIncompatibleImprovements());
    }

    private boolean getDefaultWrapUncheckedExceptions() {
        return Configuration.getDefaultWrapUncheckedExceptions(this.getIncompatibleImprovements());
    }

    private ObjectWrapper getDefaultObjectWrapper() {
        return Configuration.getDefaultObjectWrapper(this.getIncompatibleImprovements());
    }

    static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
        return TemplateExceptionHandler.DEBUG_HANDLER;
    }

    static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
        return AttemptExceptionReporter.LOG_ERROR_REPORTER;
    }

    static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
        return true;
    }

    static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
        return false;
    }

    @Override
    public Object clone() {
        try {
            Configuration copy = (Configuration)super.clone();
            copy.sharedVariables = new HashMap(this.sharedVariables);
            copy.localeToCharsetMap = new ConcurrentHashMap(this.localeToCharsetMap);
            copy.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            return copy;
        }
        catch (CloneNotSupportedException e) {
            throw new BugException("Cloning failed", e);
        }
    }

    private void loadBuiltInSharedVariables() {
        this.sharedVariables.put("capture_output", new CaptureOutput());
        this.sharedVariables.put("compress", StandardCompress.INSTANCE);
        this.sharedVariables.put("html_escape", new HtmlEscape());
        this.sharedVariables.put("normalize_newlines", new NormalizeNewlines());
        this.sharedVariables.put("xml_escape", new XmlEscape());
    }

    public void loadBuiltInEncodingMap() {
        this.localeToCharsetMap.clear();
        this.localeToCharsetMap.put("ar", "ISO-8859-6");
        this.localeToCharsetMap.put("be", "ISO-8859-5");
        this.localeToCharsetMap.put("bg", "ISO-8859-5");
        this.localeToCharsetMap.put("ca", "ISO-8859-1");
        this.localeToCharsetMap.put("cs", "ISO-8859-2");
        this.localeToCharsetMap.put("da", "ISO-8859-1");
        this.localeToCharsetMap.put("de", "ISO-8859-1");
        this.localeToCharsetMap.put("el", "ISO-8859-7");
        this.localeToCharsetMap.put("en", "ISO-8859-1");
        this.localeToCharsetMap.put("es", "ISO-8859-1");
        this.localeToCharsetMap.put("et", "ISO-8859-1");
        this.localeToCharsetMap.put("fi", "ISO-8859-1");
        this.localeToCharsetMap.put("fr", "ISO-8859-1");
        this.localeToCharsetMap.put("hr", "ISO-8859-2");
        this.localeToCharsetMap.put("hu", "ISO-8859-2");
        this.localeToCharsetMap.put("is", "ISO-8859-1");
        this.localeToCharsetMap.put("it", "ISO-8859-1");
        this.localeToCharsetMap.put("iw", "ISO-8859-8");
        this.localeToCharsetMap.put("ja", "Shift_JIS");
        this.localeToCharsetMap.put("ko", "EUC-KR");
        this.localeToCharsetMap.put("lt", "ISO-8859-2");
        this.localeToCharsetMap.put("lv", "ISO-8859-2");
        this.localeToCharsetMap.put("mk", "ISO-8859-5");
        this.localeToCharsetMap.put("nl", "ISO-8859-1");
        this.localeToCharsetMap.put("no", "ISO-8859-1");
        this.localeToCharsetMap.put("pl", "ISO-8859-2");
        this.localeToCharsetMap.put("pt", "ISO-8859-1");
        this.localeToCharsetMap.put("ro", "ISO-8859-2");
        this.localeToCharsetMap.put("ru", "ISO-8859-5");
        this.localeToCharsetMap.put("sh", "ISO-8859-5");
        this.localeToCharsetMap.put("sk", "ISO-8859-2");
        this.localeToCharsetMap.put("sl", "ISO-8859-2");
        this.localeToCharsetMap.put("sq", "ISO-8859-2");
        this.localeToCharsetMap.put("sr", "ISO-8859-5");
        this.localeToCharsetMap.put("sv", "ISO-8859-1");
        this.localeToCharsetMap.put("tr", "ISO-8859-9");
        this.localeToCharsetMap.put("uk", "ISO-8859-5");
        this.localeToCharsetMap.put("zh", "GB2312");
        this.localeToCharsetMap.put("zh_TW", "Big5");
    }

    public void clearEncodingMap() {
        this.localeToCharsetMap.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static Configuration getDefaultConfiguration() {
        Configuration defaultConfig = Configuration.defaultConfig;
        if (defaultConfig == null) {
            Object object = defaultConfigLock;
            synchronized (object) {
                defaultConfig = Configuration.defaultConfig;
                if (defaultConfig == null) {
                    Configuration.defaultConfig = defaultConfig = new Configuration();
                }
            }
        }
        return defaultConfig;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public static void setDefaultConfiguration(Configuration config) {
        Object object = defaultConfigLock;
        synchronized (object) {
            defaultConfig = config;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setTemplateLoader(TemplateLoader templateLoader) {
        Configuration configuration = this;
        synchronized (configuration) {
            if (this.cache.getTemplateLoader() != templateLoader) {
                this.recreateTemplateCacheWith(templateLoader, this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            }
            this.templateLoaderExplicitlySet = true;
        }
    }

    public void unsetTemplateLoader() {
        if (this.templateLoaderExplicitlySet) {
            this.setTemplateLoader(this.getDefaultTemplateLoader());
            this.templateLoaderExplicitlySet = false;
        }
    }

    public boolean isTemplateLoaderExplicitlySet() {
        return this.templateLoaderExplicitlySet;
    }

    public TemplateLoader getTemplateLoader() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateLoader();
    }

    public void setTemplateLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
        if (this.cache.getTemplateLookupStrategy() != templateLookupStrategy) {
            this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), templateLookupStrategy, this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
        }
        this.templateLookupStrategyExplicitlySet = true;
    }

    public void unsetTemplateLookupStrategy() {
        if (this.templateLookupStrategyExplicitlySet) {
            this.setTemplateLookupStrategy(this.getDefaultTemplateLookupStrategy());
            this.templateLookupStrategyExplicitlySet = false;
        }
    }

    public boolean isTemplateLookupStrategyExplicitlySet() {
        return this.templateLookupStrategyExplicitlySet;
    }

    public TemplateLookupStrategy getTemplateLookupStrategy() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateLookupStrategy();
    }

    public void setTemplateNameFormat(TemplateNameFormat templateNameFormat) {
        if (this.cache.getTemplateNameFormat() != templateNameFormat) {
            this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), templateNameFormat, this.cache.getTemplateConfigurations());
        }
        this.templateNameFormatExplicitlySet = true;
    }

    public void unsetTemplateNameFormat() {
        if (this.templateNameFormatExplicitlySet) {
            this.setTemplateNameFormat(this.getDefaultTemplateNameFormat());
            this.templateNameFormatExplicitlySet = false;
        }
    }

    public boolean isTemplateNameFormatExplicitlySet() {
        return this.templateNameFormatExplicitlySet;
    }

    public TemplateNameFormat getTemplateNameFormat() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateNameFormat();
    }

    public void setTemplateConfigurations(TemplateConfigurationFactory templateConfigurations) {
        if (this.cache.getTemplateConfigurations() != templateConfigurations) {
            if (templateConfigurations != null) {
                templateConfigurations.setConfiguration(this);
            }
            this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), templateConfigurations);
        }
    }

    public TemplateConfigurationFactory getTemplateConfigurations() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateConfigurations();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCacheStorage(CacheStorage cacheStorage) {
        Configuration configuration = this;
        synchronized (configuration) {
            if (this.getCacheStorage() != cacheStorage) {
                this.recreateTemplateCacheWith(this.cache.getTemplateLoader(), cacheStorage, this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            }
            this.cacheStorageExplicitlySet = true;
        }
    }

    public void unsetCacheStorage() {
        if (this.cacheStorageExplicitlySet) {
            this.setCacheStorage(this.getDefaultCacheStorage());
            this.cacheStorageExplicitlySet = false;
        }
    }

    public boolean isCacheStorageExplicitlySet() {
        return this.cacheStorageExplicitlySet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CacheStorage getCacheStorage() {
        Configuration configuration = this;
        synchronized (configuration) {
            if (this.cache == null) {
                return null;
            }
            return this.cache.getCacheStorage();
        }
    }

    public void setDirectoryForTemplateLoading(File dir) throws IOException {
        String path;
        TemplateLoader tl = this.getTemplateLoader();
        if (tl instanceof FileTemplateLoader && (path = ((FileTemplateLoader)tl).baseDir.getCanonicalPath()).equals(dir.getCanonicalPath())) {
            return;
        }
        this.setTemplateLoader(new FileTemplateLoader(dir));
    }

    public void setServletContextForTemplateLoading(Object servletContext, String path) {
        try {
            Object[] constructorParams;
            Class[] constructorParamTypes;
            Class webappTemplateLoaderClass = ClassUtil.forName("freemarker.cache.WebappTemplateLoader");
            Class servletContextClass = ClassUtil.forName("javax.servlet.ServletContext");
            if (path == null) {
                constructorParamTypes = new Class[]{servletContextClass};
                constructorParams = new Object[]{servletContext};
            } else {
                constructorParamTypes = new Class[]{servletContextClass, String.class};
                constructorParams = new Object[]{servletContext, path};
            }
            this.setTemplateLoader((TemplateLoader)webappTemplateLoaderClass.getConstructor(constructorParamTypes).newInstance(constructorParams));
        }
        catch (Exception e) {
            throw new BugException(e);
        }
    }

    public void setClassForTemplateLoading(Class resourceLoaderClass, String basePackagePath) {
        this.setTemplateLoader(new ClassTemplateLoader(resourceLoaderClass, basePackagePath));
    }

    public void setClassLoaderForTemplateLoading(ClassLoader classLoader, String basePackagePath) {
        this.setTemplateLoader(new ClassTemplateLoader(classLoader, basePackagePath));
    }

    @Deprecated
    public void setTemplateUpdateDelay(int seconds) {
        this.cache.setDelay(1000L * (long)seconds);
    }

    public void setTemplateUpdateDelayMilliseconds(long millis) {
        this.cache.setDelay(millis);
    }

    public long getTemplateUpdateDelayMilliseconds() {
        return this.cache.getDelay();
    }

    @Deprecated
    public void setStrictSyntaxMode(boolean b) {
        this.strictSyntax = b;
    }

    @Override
    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        ObjectWrapper prevObjectWrapper = this.getObjectWrapper();
        super.setObjectWrapper(objectWrapper);
        this.objectWrapperExplicitlySet = true;
        if (objectWrapper != prevObjectWrapper) {
            try {
                this.setSharedVariablesFromRewrappableSharedVariables();
            }
            catch (TemplateModelException e) {
                throw new RuntimeException("Failed to re-wrap earliearly set shared variables with the newly set object wrapper", e);
            }
        }
    }

    public void unsetObjectWrapper() {
        if (this.objectWrapperExplicitlySet) {
            this.setObjectWrapper(this.getDefaultObjectWrapper());
            this.objectWrapperExplicitlySet = false;
        }
    }

    public boolean isObjectWrapperExplicitlySet() {
        return this.objectWrapperExplicitlySet;
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        this.localeExplicitlySet = true;
    }

    public void unsetLocale() {
        if (this.localeExplicitlySet) {
            this.setLocale(Configuration.getDefaultLocale());
            this.localeExplicitlySet = false;
        }
    }

    public boolean isLocaleExplicitlySet() {
        return this.localeExplicitlySet;
    }

    static Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        super.setTimeZone(timeZone);
        this.timeZoneExplicitlySet = true;
    }

    public void unsetTimeZone() {
        if (this.timeZoneExplicitlySet) {
            this.setTimeZone(Configuration.getDefaultTimeZone());
            this.timeZoneExplicitlySet = false;
        }
    }

    public boolean isTimeZoneExplicitlySet() {
        return this.timeZoneExplicitlySet;
    }

    static TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }

    @Override
    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        super.setTemplateExceptionHandler(templateExceptionHandler);
        this.templateExceptionHandlerExplicitlySet = true;
    }

    public void unsetTemplateExceptionHandler() {
        if (this.templateExceptionHandlerExplicitlySet) {
            this.setTemplateExceptionHandler(this.getDefaultTemplateExceptionHandler());
            this.templateExceptionHandlerExplicitlySet = false;
        }
    }

    public boolean isTemplateExceptionHandlerExplicitlySet() {
        return this.templateExceptionHandlerExplicitlySet;
    }

    @Override
    public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
        super.setAttemptExceptionReporter(attemptExceptionReporter);
        this.attemptExceptionReporterExplicitlySet = true;
    }

    public void unsetAttemptExceptionReporter() {
        if (this.attemptExceptionReporterExplicitlySet) {
            this.setAttemptExceptionReporter(this.getDefaultAttemptExceptionReporter());
            this.attemptExceptionReporterExplicitlySet = false;
        }
    }

    public boolean isAttemptExceptionReporterExplicitlySet() {
        return this.attemptExceptionReporterExplicitlySet;
    }

    @Override
    public void setLogTemplateExceptions(boolean value) {
        super.setLogTemplateExceptions(value);
        this.logTemplateExceptionsExplicitlySet = true;
    }

    public void unsetLogTemplateExceptions() {
        if (this.logTemplateExceptionsExplicitlySet) {
            this.setLogTemplateExceptions(this.getDefaultLogTemplateExceptions());
            this.logTemplateExceptionsExplicitlySet = false;
        }
    }

    public boolean isLogTemplateExceptionsExplicitlySet() {
        return this.logTemplateExceptionsExplicitlySet;
    }

    @Override
    public void setWrapUncheckedExceptions(boolean value) {
        super.setWrapUncheckedExceptions(value);
        this.wrapUncheckedExceptionsExplicitlySet = true;
    }

    public void unsetWrapUncheckedExceptions() {
        if (this.wrapUncheckedExceptionsExplicitlySet) {
            this.setWrapUncheckedExceptions(this.getDefaultWrapUncheckedExceptions());
            this.wrapUncheckedExceptionsExplicitlySet = false;
        }
    }

    public boolean isWrapUncheckedExceptionsExplicitlySet() {
        return this.wrapUncheckedExceptionsExplicitlySet;
    }

    @Override
    public boolean getStrictSyntaxMode() {
        return this.strictSyntax;
    }

    public void setIncompatibleImprovements(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        if (!this.incompatibleImprovements.equals(incompatibleImprovements)) {
            Configuration.checkCurrentVersionNotRecycled(incompatibleImprovements);
            this.incompatibleImprovements = incompatibleImprovements;
            if (!this.templateLoaderExplicitlySet) {
                this.templateLoaderExplicitlySet = true;
                this.unsetTemplateLoader();
            }
            if (!this.templateLookupStrategyExplicitlySet) {
                this.templateLookupStrategyExplicitlySet = true;
                this.unsetTemplateLookupStrategy();
            }
            if (!this.templateNameFormatExplicitlySet) {
                this.templateNameFormatExplicitlySet = true;
                this.unsetTemplateNameFormat();
            }
            if (!this.cacheStorageExplicitlySet) {
                this.cacheStorageExplicitlySet = true;
                this.unsetCacheStorage();
            }
            if (!this.templateExceptionHandlerExplicitlySet) {
                this.templateExceptionHandlerExplicitlySet = true;
                this.unsetTemplateExceptionHandler();
            }
            if (!this.attemptExceptionReporterExplicitlySet) {
                this.attemptExceptionReporterExplicitlySet = true;
                this.unsetAttemptExceptionReporter();
            }
            if (!this.logTemplateExceptionsExplicitlySet) {
                this.logTemplateExceptionsExplicitlySet = true;
                this.unsetLogTemplateExceptions();
            }
            if (!this.cFormatExplicitlySet) {
                this.cFormatExplicitlySet = true;
                this.unsetCFormat();
            }
            if (!this.wrapUncheckedExceptionsExplicitlySet) {
                this.wrapUncheckedExceptionsExplicitlySet = true;
                this.unsetWrapUncheckedExceptions();
            }
            if (!this.objectWrapperExplicitlySet) {
                this.objectWrapperExplicitlySet = true;
                this.unsetObjectWrapper();
            }
            this.recreateTemplateCache();
        }
    }

    private static void checkCurrentVersionNotRecycled(Version incompatibleImprovements) {
        _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "Configuration");
    }

    @Override
    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    @Deprecated
    public void setIncompatibleEnhancements(String version) {
        this.setIncompatibleImprovements(new Version(version));
    }

    @Deprecated
    public String getIncompatibleEnhancements() {
        return this.incompatibleImprovements.toString();
    }

    @Deprecated
    public int getParsedIncompatibleEnhancements() {
        return this.getIncompatibleImprovements().intValue();
    }

    public void setWhitespaceStripping(boolean b) {
        this.whitespaceStripping = b;
    }

    @Override
    public boolean getWhitespaceStripping() {
        return this.whitespaceStripping;
    }

    public void setAutoEscapingPolicy(int autoEscapingPolicy) {
        _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
        int prevAutoEscaping = this.getAutoEscapingPolicy();
        this.autoEscapingPolicy = autoEscapingPolicy;
        if (prevAutoEscaping != autoEscapingPolicy) {
            this.clearTemplateCache();
        }
    }

    @Override
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        if (outputFormat == null) {
            throw new NullArgumentException(OUTPUT_FORMAT_KEY_CAMEL_CASE, "You may meant: " + UndefinedOutputFormat.class.getSimpleName() + ".INSTANCE");
        }
        OutputFormat prevOutputFormat = this.getOutputFormat();
        this.outputFormat = outputFormat;
        this.outputFormatExplicitlySet = true;
        if (prevOutputFormat != outputFormat) {
            this.clearTemplateCache();
        }
    }

    @Override
    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }

    public boolean isOutputFormatExplicitlySet() {
        return this.outputFormatExplicitlySet;
    }

    public void unsetOutputFormat() {
        this.outputFormat = UndefinedOutputFormat.INSTANCE;
        this.outputFormatExplicitlySet = false;
    }

    public OutputFormat getOutputFormat(String name) throws UnregisteredOutputFormatException {
        if (name.length() == 0) {
            throw new IllegalArgumentException("0-length format name");
        }
        if (name.charAt(name.length() - 1) == '}') {
            int openBrcIdx = name.indexOf(123);
            if (openBrcIdx == -1) {
                throw new IllegalArgumentException("Missing opening '{' in: " + name);
            }
            MarkupOutputFormat outerOF = this.getMarkupOutputFormatForCombined(name.substring(0, openBrcIdx));
            MarkupOutputFormat innerOF = this.getMarkupOutputFormatForCombined(name.substring(openBrcIdx + 1, name.length() - 1));
            return new CombinedMarkupOutputFormat(name, outerOF, innerOF);
        }
        OutputFormat custOF = this.registeredCustomOutputFormats.get(name);
        if (custOF != null) {
            return custOF;
        }
        OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(name);
        if (stdOF == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unregistered output format name, ");
            sb.append(StringUtil.jQuote(name));
            sb.append(". The output formats registered in the Configuration are: ");
            TreeSet<String> registeredNames = new TreeSet<String>();
            registeredNames.addAll(STANDARD_OUTPUT_FORMATS.keySet());
            registeredNames.addAll(this.registeredCustomOutputFormats.keySet());
            boolean first = true;
            for (String registeredName : registeredNames) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(StringUtil.jQuote(registeredName));
            }
            throw new UnregisteredOutputFormatException(sb.toString());
        }
        return stdOF;
    }

    private MarkupOutputFormat getMarkupOutputFormatForCombined(String outerName) throws UnregisteredOutputFormatException {
        OutputFormat of = this.getOutputFormat(outerName);
        if (!(of instanceof MarkupOutputFormat)) {
            throw new IllegalArgumentException("The \"" + outerName + "\" output format can't be used in ...{...} expression, because it's not a markup format.");
        }
        MarkupOutputFormat outerOF = (MarkupOutputFormat)of;
        return outerOF;
    }

    public void setRegisteredCustomOutputFormats(Collection<? extends OutputFormat> registeredCustomOutputFormats) {
        NullArgumentException.check(registeredCustomOutputFormats);
        LinkedHashMap<String, OutputFormat> m = new LinkedHashMap<String, OutputFormat>(registeredCustomOutputFormats.size() * 4 / 3, 1.0f);
        for (OutputFormat outputFormat : registeredCustomOutputFormats) {
            String name = outputFormat.getName();
            if (name.equals(UndefinedOutputFormat.INSTANCE.getName())) {
                throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
            }
            if (name.equals(PlainTextOutputFormat.INSTANCE.getName())) {
                throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
            }
            if (name.length() == 0) {
                throw new IllegalArgumentException("The output format name can't be 0 long");
            }
            if (!Character.isLetterOrDigit(name.charAt(0))) {
                throw new IllegalArgumentException("The output format name must start with letter or digit: " + name);
            }
            if (name.indexOf(43) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"+\" character: " + name);
            }
            if (name.indexOf(123) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"{\" character: " + name);
            }
            if (name.indexOf(125) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"}\" character: " + name);
            }
            OutputFormat replaced = m.put(outputFormat.getName(), outputFormat);
            if (replaced == null) continue;
            if (replaced == outputFormat) {
                throw new IllegalArgumentException("Duplicate output format in the collection: " + outputFormat);
            }
            throw new IllegalArgumentException("Clashing output format names between " + replaced + " and " + outputFormat + ".");
        }
        this.registeredCustomOutputFormats = Collections.unmodifiableMap(m);
        this.clearTemplateCache();
    }

    public Collection<? extends OutputFormat> getRegisteredCustomOutputFormats() {
        return this.registeredCustomOutputFormats.values();
    }

    public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
        boolean prevEffectiveValue = this.getRecognizeStandardFileExtensions();
        this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
        if (prevEffectiveValue != recognizeStandardFileExtensions) {
            this.clearTemplateCache();
        }
    }

    public void unsetRecognizeStandardFileExtensions() {
        if (this.recognizeStandardFileExtensions != null) {
            this.recognizeStandardFileExtensions = null;
        }
    }

    public boolean isRecognizeStandardFileExtensionsExplicitlySet() {
        return this.recognizeStandardFileExtensions != null;
    }

    @Override
    public boolean getRecognizeStandardFileExtensions() {
        return this.recognizeStandardFileExtensions == null ? this.incompatibleImprovements.intValue() >= _VersionInts.V_2_3_24 : this.recognizeStandardFileExtensions;
    }

    @Override
    public void setCFormat(CFormat cFormat) {
        super.setCFormat(cFormat);
        this.cFormatExplicitlySet = true;
    }

    public void unsetCFormat() {
        if (this.cFormatExplicitlySet) {
            this.setCFormat(Configuration.getDefaultCFormat(this.incompatibleImprovements));
            this.cFormatExplicitlySet = false;
        }
    }

    static CFormat getDefaultCFormat(Version incompatibleImprovements) {
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_32 ? JavaScriptOrJSONCFormat.INSTANCE : LegacyCFormat.INSTANCE;
    }

    public boolean isCFormatExplicitlySet() {
        return this.cFormatExplicitlySet;
    }

    public void setTagSyntax(int tagSyntax) {
        _TemplateAPI.valideTagSyntaxValue(tagSyntax);
        this.tagSyntax = tagSyntax;
    }

    @Override
    public int getTagSyntax() {
        return this.tagSyntax;
    }

    public void setInterpolationSyntax(int interpolationSyntax) {
        _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
        this.interpolationSyntax = interpolationSyntax;
    }

    @Override
    public int getInterpolationSyntax() {
        return this.interpolationSyntax;
    }

    public void setNamingConvention(int namingConvention) {
        _TemplateAPI.validateNamingConventionValue(namingConvention);
        this.namingConvention = namingConvention;
    }

    @Override
    public int getNamingConvention() {
        return this.namingConvention;
    }

    public void setTabSize(int tabSize) {
        if (tabSize < 1) {
            throw new IllegalArgumentException("\"tabSize\" must be at least 1, but was " + tabSize);
        }
        if (tabSize > 256) {
            throw new IllegalArgumentException("\"tabSize\" can't be more than 256, but was " + tabSize);
        }
        this.tabSize = tabSize;
    }

    @Override
    public int getTabSize() {
        return this.tabSize;
    }

    public boolean getFallbackOnNullLoopVariable() {
        return this.fallbackOnNullLoopVariable;
    }

    public void setFallbackOnNullLoopVariable(boolean fallbackOnNullLoopVariable) {
        this.fallbackOnNullLoopVariable = fallbackOnNullLoopVariable;
    }

    boolean getPreventStrippings() {
        return this.preventStrippings;
    }

    void setPreventStrippings(boolean preventStrippings) {
        this.preventStrippings = preventStrippings;
    }

    public Template getTemplate(String name) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, null, null, null, true, false);
    }

    public Template getTemplate(String name, Locale locale) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, locale, null, null, true, false);
    }

    public Template getTemplate(String name, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, null, null, encoding, true, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, locale, null, encoding, true, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, locale, null, encoding, parseAsFTL, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        return this.getTemplate(name, locale, null, encoding, parseAsFTL, ignoreMissing);
    }

    public Template getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
        TemplateCache.MaybeMissingTemplate maybeTemp;
        Template temp;
        if (locale == null) {
            locale = this.getLocale();
        }
        if (encoding == null) {
            encoding = this.getEncoding(locale);
        }
        if ((temp = (maybeTemp = this.cache.getTemplate(name, locale, customLookupCondition, encoding, parseAsFTL)).getTemplate()) == null) {
            String msg;
            if (ignoreMissing) {
                return null;
            }
            TemplateLoader tl = this.getTemplateLoader();
            if (tl == null) {
                msg = "Don't know where to load template " + StringUtil.jQuote(name) + " from because the \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), so it's null.";
            } else {
                String missingTempNormName = maybeTemp.getMissingTemplateNormalizedName();
                String missingTempReason = maybeTemp.getMissingTemplateReason();
                TemplateLookupStrategy templateLookupStrategy = this.getTemplateLookupStrategy();
                msg = "Template not found for name " + StringUtil.jQuote(name) + (missingTempNormName != null && name != null && !this.removeInitialSlash(name).equals(missingTempNormName) ? " (normalized: " + StringUtil.jQuote(missingTempNormName) + ")" : "") + (customLookupCondition != null ? " and custom lookup condition " + StringUtil.jQuote(customLookupCondition) : "") + "." + (missingTempReason != null ? "\nReason given: " + this.ensureSentenceIsClosed(missingTempReason) : "") + "\nThe name was interpreted by this TemplateLoader: " + StringUtil.tryToString(tl) + "." + (!this.isKnownNonConfusingLookupStrategy(templateLookupStrategy) ? "\n(Before that, the name was possibly changed by this lookup strategy: " + StringUtil.tryToString(templateLookupStrategy) + ".)" : "") + (!this.templateLoaderExplicitlySet ? "\nWarning: The \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), and using the default value is most certainly not intended and dangerous, and can be the cause of this error." : "") + (missingTempReason == null && name.indexOf(92) != -1 ? "\nWarning: The name contains backslash (\"\\\") instead of slash (\"/\"); template names should use slash only." : "");
            }
            String normName = maybeTemp.getMissingTemplateNormalizedName();
            throw new TemplateNotFoundException(normName != null ? normName : name, customLookupCondition, msg);
        }
        return temp;
    }

    private boolean isKnownNonConfusingLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
        return templateLookupStrategy == TemplateLookupStrategy.DEFAULT_2_3_0;
    }

    private String removeInitialSlash(String name) {
        return name.startsWith("/") ? name.substring(1) : name;
    }

    private String ensureSentenceIsClosed(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        char lastChar = s.charAt(s.length() - 1);
        return lastChar == '.' || lastChar == '!' || lastChar == '?' ? s : s + ".";
    }

    public void setDefaultEncoding(String encoding) {
        this.defaultEncoding = encoding;
        this.defaultEncodingExplicitlySet = true;
    }

    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void unsetDefaultEncoding() {
        if (this.defaultEncodingExplicitlySet) {
            this.setDefaultEncoding(Configuration.getDefaultDefaultEncoding());
            this.defaultEncodingExplicitlySet = false;
        }
    }

    public boolean isDefaultEncodingExplicitlySet() {
        return this.defaultEncodingExplicitlySet;
    }

    private static String getDefaultDefaultEncoding() {
        return Configuration.getJVMDefaultEncoding();
    }

    private static String getJVMDefaultEncoding() {
        return SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
    }

    public String getEncoding(Locale locale) {
        if (this.localeToCharsetMap.isEmpty()) {
            return this.defaultEncoding;
        }
        NullArgumentException.check("locale", locale);
        String charset = (String)this.localeToCharsetMap.get(locale.toString());
        if (charset == null) {
            Locale l;
            if (locale.getVariant().length() > 0 && (charset = (String)this.localeToCharsetMap.get((l = new Locale(locale.getLanguage(), locale.getCountry())).toString())) != null) {
                this.localeToCharsetMap.put(locale.toString(), charset);
            }
            if ((charset = (String)this.localeToCharsetMap.get(locale.getLanguage())) != null) {
                this.localeToCharsetMap.put(locale.toString(), charset);
            }
        }
        return charset != null ? charset : this.defaultEncoding;
    }

    public void setEncoding(Locale locale, String encoding) {
        this.localeToCharsetMap.put(locale.toString(), encoding);
    }

    public void setSharedVariable(String name, TemplateModel tm) {
        TemplateModel replaced = this.sharedVariables.put(name, tm);
        if (replaced != null && this.rewrappableSharedVariables != null) {
            this.rewrappableSharedVariables.remove(name);
        }
    }

    public Set getSharedVariableNames() {
        return new HashSet(this.sharedVariables.keySet());
    }

    public void setSharedVariable(String name, Object value) throws TemplateModelException {
        this.setSharedVariable(name, this.getObjectWrapper().wrap(value));
    }

    public void setSharedVariables(Map<String, ?> map) throws TemplateModelException {
        this.rewrappableSharedVariables = new HashMap(map);
        this.sharedVariables.clear();
        this.setSharedVariablesFromRewrappableSharedVariables();
    }

    public void setSharedVaribles(Map map) throws TemplateModelException {
        this.setSharedVariables(map);
    }

    private void setSharedVariablesFromRewrappableSharedVariables() throws TemplateModelException {
        if (this.rewrappableSharedVariables == null) {
            return;
        }
        for (Map.Entry ent : this.rewrappableSharedVariables.entrySet()) {
            String name = (String)ent.getKey();
            Object value = ent.getValue();
            TemplateModel valueAsTM = value instanceof TemplateModel ? (TemplateModel)value : this.getObjectWrapper().wrap(value);
            this.sharedVariables.put(name, valueAsTM);
        }
    }

    public void setAllSharedVariables(TemplateHashModelEx hash) throws TemplateModelException {
        TemplateModelIterator keys = hash.keys().iterator();
        TemplateModelIterator values = hash.values().iterator();
        while (keys.hasNext()) {
            this.setSharedVariable(((TemplateScalarModel)keys.next()).getAsString(), values.next());
        }
    }

    public TemplateModel getSharedVariable(String name) {
        return (TemplateModel)this.sharedVariables.get(name);
    }

    public void clearSharedVariables() {
        this.sharedVariables.clear();
        this.loadBuiltInSharedVariables();
    }

    public void clearTemplateCache() {
        this.cache.clear();
    }

    public void removeTemplateFromCache(String name) throws IOException {
        Locale loc = this.getLocale();
        this.removeTemplateFromCache(name, loc, null, this.getEncoding(loc), true);
    }

    public void removeTemplateFromCache(String name, Locale locale) throws IOException {
        this.removeTemplateFromCache(name, locale, null, this.getEncoding(locale), true);
    }

    public void removeTemplateFromCache(String name, String encoding) throws IOException {
        this.removeTemplateFromCache(name, this.getLocale(), null, encoding, true);
    }

    public void removeTemplateFromCache(String name, Locale locale, String encoding) throws IOException {
        this.removeTemplateFromCache(name, locale, null, encoding, true);
    }

    public void removeTemplateFromCache(String name, Locale locale, String encoding, boolean parse) throws IOException {
        this.removeTemplateFromCache(name, locale, null, encoding, parse);
    }

    public void removeTemplateFromCache(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
        this.cache.removeTemplate(name, locale, customLookupCondition, encoding, parse);
    }

    public boolean getLocalizedLookup() {
        return this.cache.getLocalizedLookup();
    }

    public void setLocalizedLookup(boolean localizedLookup) {
        this.localizedLookup = localizedLookup;
        this.cache.setLocalizedLookup(localizedLookup);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setSetting(String name, String value) throws TemplateException {
        boolean unknown;
        block86: {
            unknown = false;
            try {
                if ("TemplateUpdateInterval".equalsIgnoreCase(name)) {
                    name = "template_update_delay";
                } else if ("DefaultEncoding".equalsIgnoreCase(name)) {
                    name = "default_encoding";
                }
                if ("default_encoding".equals(name) || DEFAULT_ENCODING_KEY_CAMEL_CASE.equals(name)) {
                    if (JVM_DEFAULT.equalsIgnoreCase(value)) {
                        this.setDefaultEncoding(Configuration.getJVMDefaultEncoding());
                    } else {
                        this.setDefaultEncoding(value);
                    }
                    break block86;
                }
                if ("localized_lookup".equals(name) || LOCALIZED_LOOKUP_KEY_CAMEL_CASE.equals(name)) {
                    this.setLocalizedLookup(StringUtil.getYesNo(value));
                    break block86;
                }
                if ("strict_syntax".equals(name) || STRICT_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                    this.setStrictSyntaxMode(StringUtil.getYesNo(value));
                    break block86;
                }
                if ("whitespace_stripping".equals(name) || WHITESPACE_STRIPPING_KEY_CAMEL_CASE.equals(name)) {
                    this.setWhitespaceStripping(StringUtil.getYesNo(value));
                    break block86;
                }
                if ("auto_escaping_policy".equals(name) || AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE.equals(name)) {
                    if ("enable_if_default".equals(value) || "enableIfDefault".equals(value)) {
                        this.setAutoEscapingPolicy(21);
                    } else if ("enable_if_supported".equals(value) || "enableIfSupported".equals(value)) {
                        this.setAutoEscapingPolicy(22);
                    } else {
                        if (!"disable".equals(value)) throw this.invalidSettingValueException(name, value);
                        this.setAutoEscapingPolicy(20);
                    }
                    break block86;
                }
                if ("output_format".equals(name) || OUTPUT_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetOutputFormat();
                    } else {
                        OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(value);
                        this.setOutputFormat(stdOF != null ? stdOF : (OutputFormat)_ObjectBuilderSettingEvaluator.eval(value, OutputFormat.class, true, _SettingEvaluationEnvironment.getCurrent()));
                    }
                    break block86;
                }
                if ("registered_custom_output_formats".equals(name) || REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE.equals(name)) {
                    List list = (List)_ObjectBuilderSettingEvaluator.eval(value, List.class, true, _SettingEvaluationEnvironment.getCurrent());
                    for (Object item : list) {
                        if (item instanceof OutputFormat) continue;
                        throw new _MiscTemplateException(this.getEnvironment(), "Invalid value for setting ", new _DelayedJQuote(name), ": List items must be " + OutputFormat.class.getName() + " instances, in: ", value);
                    }
                    this.setRegisteredCustomOutputFormats(list);
                    break block86;
                }
                if ("recognize_standard_file_extensions".equals(name) || RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetRecognizeStandardFileExtensions();
                    } else {
                        this.setRecognizeStandardFileExtensions(StringUtil.getYesNo(value));
                    }
                    break block86;
                }
                if ("cache_storage".equals(name) || CACHE_STORAGE_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetCacheStorage();
                    }
                    if (value.indexOf(46) == -1) {
                        int strongSize = 0;
                        int softSize = 0;
                        Map map = StringUtil.parseNameValuePairList(value, String.valueOf(Integer.MAX_VALUE));
                        for (Map.Entry ent : map.entrySet()) {
                            int pvalue;
                            String pname = (String)ent.getKey();
                            try {
                                pvalue = Integer.parseInt((String)ent.getValue());
                            }
                            catch (NumberFormatException e) {
                                throw this.invalidSettingValueException(name, value);
                            }
                            if ("soft".equalsIgnoreCase(pname)) {
                                softSize = pvalue;
                                continue;
                            }
                            if (!"strong".equalsIgnoreCase(pname)) throw this.invalidSettingValueException(name, value);
                            strongSize = pvalue;
                        }
                        if (softSize == 0 && strongSize == 0) {
                            throw this.invalidSettingValueException(name, value);
                        }
                        this.setCacheStorage(new MruCacheStorage(strongSize, softSize));
                        break block86;
                    }
                    this.setCacheStorage((CacheStorage)_ObjectBuilderSettingEvaluator.eval(value, CacheStorage.class, false, _SettingEvaluationEnvironment.getCurrent()));
                    break block86;
                }
                if ("template_update_delay".equals(name) || TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE.equals(name)) {
                    String valueWithoutUnit;
                    long multiplier;
                    if (value.endsWith("ms")) {
                        multiplier = 1L;
                        valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 2));
                    } else if (value.endsWith("s")) {
                        multiplier = 1000L;
                        valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                    } else if (value.endsWith("m")) {
                        multiplier = 60000L;
                        valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                    } else if (value.endsWith("h")) {
                        multiplier = 3600000L;
                        valueWithoutUnit = this.rightTrim(value.substring(0, value.length() - 1));
                    } else {
                        multiplier = 1000L;
                        valueWithoutUnit = value;
                    }
                    this.setTemplateUpdateDelayMilliseconds((long)Integer.parseInt(valueWithoutUnit) * multiplier);
                } else if ("tag_syntax".equals(name) || TAG_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                    if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
                        this.setTagSyntax(0);
                    } else if ("angle_bracket".equals(value) || "angleBracket".equals(value)) {
                        this.setTagSyntax(1);
                    } else {
                        if (!"square_bracket".equals(value) && !"squareBracket".equals(value)) throw this.invalidSettingValueException(name, value);
                        this.setTagSyntax(2);
                    }
                } else if ("interpolation_syntax".equals(name) || INTERPOLATION_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                    if ("legacy".equals(value)) {
                        this.setInterpolationSyntax(20);
                    } else if ("dollar".equals(value)) {
                        this.setInterpolationSyntax(21);
                    } else {
                        if (!"square_bracket".equals(value) && !"squareBracket".equals(value)) throw this.invalidSettingValueException(name, value);
                        this.setInterpolationSyntax(22);
                    }
                } else if ("naming_convention".equals(name) || NAMING_CONVENTION_KEY_CAMEL_CASE.equals(name)) {
                    if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
                        this.setNamingConvention(10);
                    } else if ("legacy".equals(value)) {
                        this.setNamingConvention(11);
                    } else {
                        if (!"camel_case".equals(value) && !"camelCase".equals(value)) throw this.invalidSettingValueException(name, value);
                        this.setNamingConvention(12);
                    }
                } else if ("tab_size".equals(name) || TAB_SIZE_KEY_CAMEL_CASE.equals(name)) {
                    this.setTabSize(Integer.parseInt(value));
                } else if ("incompatible_improvements".equals(name) || INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE.equals(name)) {
                    this.setIncompatibleImprovements(new Version(value));
                } else if (INCOMPATIBLE_ENHANCEMENTS.equals(name)) {
                    this.setIncompatibleEnhancements(value);
                } else if ("template_loader".equals(name) || TEMPLATE_LOADER_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetTemplateLoader();
                    } else {
                        this.setTemplateLoader((TemplateLoader)_ObjectBuilderSettingEvaluator.eval(value, TemplateLoader.class, true, _SettingEvaluationEnvironment.getCurrent()));
                    }
                } else if ("template_lookup_strategy".equals(name) || TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetTemplateLookupStrategy();
                    } else {
                        this.setTemplateLookupStrategy((TemplateLookupStrategy)_ObjectBuilderSettingEvaluator.eval(value, TemplateLookupStrategy.class, false, _SettingEvaluationEnvironment.getCurrent()));
                    }
                } else if ("template_name_format".equals(name) || TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equalsIgnoreCase(DEFAULT)) {
                        this.unsetTemplateNameFormat();
                    } else if (value.equalsIgnoreCase("default_2_3_0")) {
                        this.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_3_0);
                    } else {
                        if (!value.equalsIgnoreCase("default_2_4_0")) throw this.invalidSettingValueException(name, value);
                        this.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
                    }
                } else if ("template_configurations".equals(name) || TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE.equals(name)) {
                    if (value.equals(NULL)) {
                        this.setTemplateConfigurations(null);
                    } else {
                        this.setTemplateConfigurations((TemplateConfigurationFactory)_ObjectBuilderSettingEvaluator.eval(value, TemplateConfigurationFactory.class, false, _SettingEvaluationEnvironment.getCurrent()));
                    }
                } else if ("fallback_on_null_loop_variable".equals(name) || FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE.equals(name)) {
                    this.setFallbackOnNullLoopVariable(StringUtil.getYesNo(value));
                } else {
                    unknown = true;
                }
            }
            catch (Exception e) {
                throw this.settingValueAssignmentException(name, value, e);
            }
        }
        if (!unknown) return;
        super.setSetting(name, value);
    }

    private String rightTrim(String s) {
        int ln;
        for (ln = s.length(); ln > 0 && Character.isWhitespace(s.charAt(ln - 1)); --ln) {
        }
        return s.substring(0, ln);
    }

    @Override
    public Set<String> getSettingNames(boolean camelCase) {
        return new _UnmodifiableCompositeSet<String>(super.getSettingNames(camelCase), new _SortedArraySet<String>(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE));
    }

    @Override
    protected String getCorrectedNameForUnknownSetting(String name) {
        if ("encoding".equals(name) || "charset".equals(name) || "default_charset".equals(name)) {
            return "default_encoding";
        }
        if ("defaultCharset".equals(name)) {
            return DEFAULT_ENCODING_KEY_CAMEL_CASE;
        }
        return super.getCorrectedNameForUnknownSetting(name);
    }

    @Override
    protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
        Template t = env.getMainTemplate();
        this.doAutoImports(env, t);
        this.doAutoIncludes(env, t);
    }

    private void doAutoImports(Environment env, Template t) throws IOException, TemplateException {
        String nsVarName;
        Map<String, String> envAutoImports = env.getAutoImportsWithoutFallback();
        Map<String, String> tAutoImports = t.getAutoImportsWithoutFallback();
        boolean lazyAutoImports = env.getLazyAutoImports() != null ? env.getLazyAutoImports().booleanValue() : env.getLazyImports();
        for (Map.Entry<String, String> autoImport : this.getAutoImportsWithoutFallback().entrySet()) {
            nsVarName = autoImport.getKey();
            if (tAutoImports != null && tAutoImports.containsKey(nsVarName) || envAutoImports != null && envAutoImports.containsKey(nsVarName)) continue;
            env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
        }
        if (tAutoImports != null) {
            for (Map.Entry<String, String> autoImport : tAutoImports.entrySet()) {
                nsVarName = autoImport.getKey();
                if (envAutoImports != null && envAutoImports.containsKey(nsVarName)) continue;
                env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
            }
        }
        if (envAutoImports != null) {
            for (Map.Entry<String, String> autoImport : envAutoImports.entrySet()) {
                nsVarName = autoImport.getKey();
                env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
            }
        }
    }

    private void doAutoIncludes(Environment env, Template t) throws TemplateException, IOException, TemplateNotFoundException, MalformedTemplateNameException, ParseException {
        List<String> tAutoIncludes = t.getAutoIncludesWithoutFallback();
        List<String> envAutoIncludes = env.getAutoIncludesWithoutFallback();
        for (String templateName : this.getAutoIncludesWithoutFallback()) {
            if (tAutoIncludes != null && tAutoIncludes.contains(templateName) || envAutoIncludes != null && envAutoIncludes.contains(templateName)) continue;
            env.include(this.getTemplate(templateName, env.getLocale()));
        }
        if (tAutoIncludes != null) {
            for (String templateName : tAutoIncludes) {
                if (envAutoIncludes != null && envAutoIncludes.contains(templateName)) continue;
                env.include(this.getTemplate(templateName, env.getLocale()));
            }
        }
        if (envAutoIncludes != null) {
            for (String templateName : envAutoIncludes) {
                env.include(this.getTemplate(templateName, env.getLocale()));
            }
        }
    }

    @Deprecated
    public static String getVersionNumber() {
        return VERSION.toString();
    }

    public static Version getVersion() {
        return VERSION;
    }

    public static ObjectWrapper getDefaultObjectWrapper(Version incompatibleImprovements) {
        if (incompatibleImprovements.intValue() < _VersionInts.V_2_3_21) {
            return ObjectWrapper.DEFAULT_WRAPPER;
        }
        return new DefaultObjectWrapperBuilder(incompatibleImprovements).build();
    }

    public Set getSupportedBuiltInNames() {
        return this.getSupportedBuiltInNames(this.getNamingConvention());
    }

    public Set<String> getSupportedBuiltInNames(int namingConvention) {
        return _CoreAPI.getSupportedBuiltInNames(namingConvention);
    }

    public Set getSupportedBuiltInDirectiveNames() {
        return this.getSupportedBuiltInDirectiveNames(this.getNamingConvention());
    }

    public Set<String> getSupportedBuiltInDirectiveNames(int namingConvention) {
        if (namingConvention == 10) {
            return _CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES;
        }
        if (namingConvention == 11) {
            return _CoreAPI.LEGACY_BUILT_IN_DIRECTIVE_NAMES;
        }
        if (namingConvention == 12) {
            return _CoreAPI.CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;
        }
        throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
    }

    private static String getRequiredVersionProperty(Properties vp, String properyName) {
        String s = vp.getProperty(properyName);
        if (s == null) {
            throw new RuntimeException("Version file is corrupt: \"" + properyName + "\" property is missing.");
        }
        return s;
    }

    static {
        boolean fm24detected;
        CACHE_LOG = Logger.getLogger("freemarker.cache");
        SETTING_NAMES_SNAKE_CASE = new String[]{"auto_escaping_policy", "cache_storage", "default_encoding", "fallback_on_null_loop_variable", "incompatible_improvements", "interpolation_syntax", "localized_lookup", "naming_convention", "output_format", "recognize_standard_file_extensions", "registered_custom_output_formats", "strict_syntax", "tab_size", "tag_syntax", "template_configurations", "template_loader", "template_lookup_strategy", "template_name_format", "template_update_delay", "whitespace_stripping"};
        SETTING_NAMES_CAMEL_CASE = new String[]{AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE, CACHE_STORAGE_KEY_CAMEL_CASE, DEFAULT_ENCODING_KEY_CAMEL_CASE, FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE, INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE, INTERPOLATION_SYNTAX_KEY_CAMEL_CASE, LOCALIZED_LOOKUP_KEY_CAMEL_CASE, NAMING_CONVENTION_KEY_CAMEL_CASE, OUTPUT_FORMAT_KEY_CAMEL_CASE, RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE, REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE, STRICT_SYNTAX_KEY_CAMEL_CASE, TAB_SIZE_KEY_CAMEL_CASE, TAG_SYNTAX_KEY_CAMEL_CASE, TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE, TEMPLATE_LOADER_KEY_CAMEL_CASE, TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE, TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE, TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE, WHITESPACE_STRIPPING_KEY_CAMEL_CASE};
        STANDARD_OUTPUT_FORMATS = new HashMap<String, OutputFormat>();
        STANDARD_OUTPUT_FORMATS.put(UndefinedOutputFormat.INSTANCE.getName(), UndefinedOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(HTMLOutputFormat.INSTANCE.getName(), HTMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(XHTMLOutputFormat.INSTANCE.getName(), XHTMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(XMLOutputFormat.INSTANCE.getName(), XMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(RTFOutputFormat.INSTANCE.getName(), RTFOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(PlainTextOutputFormat.INSTANCE.getName(), PlainTextOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(CSSOutputFormat.INSTANCE.getName(), CSSOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(JavaScriptOutputFormat.INSTANCE.getName(), JavaScriptOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(JSONOutputFormat.INSTANCE.getName(), JSONOutputFormat.INSTANCE);
        VERSION_2_3_0 = new Version(2, 3, 0);
        VERSION_2_3_19 = new Version(2, 3, 19);
        VERSION_2_3_20 = new Version(2, 3, 20);
        VERSION_2_3_21 = new Version(2, 3, 21);
        VERSION_2_3_22 = new Version(2, 3, 22);
        VERSION_2_3_23 = new Version(2, 3, 23);
        VERSION_2_3_24 = new Version(2, 3, 24);
        VERSION_2_3_25 = new Version(2, 3, 25);
        VERSION_2_3_26 = new Version(2, 3, 26);
        VERSION_2_3_27 = new Version(2, 3, 27);
        VERSION_2_3_28 = new Version(2, 3, 28);
        VERSION_2_3_29 = new Version(2, 3, 29);
        VERSION_2_3_30 = new Version(2, 3, 30);
        VERSION_2_3_31 = new Version(2, 3, 31);
        VERSION_2_3_32 = new Version(2, 3, 32);
        DEFAULT_INCOMPATIBLE_IMPROVEMENTS = VERSION_2_3_0;
        DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.toString();
        PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.intValue();
        try {
            Date buildDate;
            Properties props = ClassUtil.loadProperties(Configuration.class, VERSION_PROPERTIES_PATH);
            String versionString = Configuration.getRequiredVersionProperty(props, "version");
            String buildDateStr = Configuration.getRequiredVersionProperty(props, "buildTimestamp");
            if (buildDateStr.endsWith("Z")) {
                buildDateStr = buildDateStr.substring(0, buildDateStr.length() - 1) + "+0000";
            }
            try {
                buildDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(buildDateStr);
            }
            catch (java.text.ParseException e) {
                buildDate = null;
            }
            Boolean gaeCompliant = Boolean.valueOf(Configuration.getRequiredVersionProperty(props, "isGAECompliant"));
            VERSION = new Version(versionString, gaeCompliant, buildDate);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load and parse /freemarker/version.properties", e);
        }
        try {
            Class.forName(FM_24_DETECTION_CLASS_NAME);
            fm24detected = true;
        }
        catch (ClassNotFoundException e) {
            fm24detected = false;
        }
        catch (LinkageError e) {
            fm24detected = true;
        }
        catch (Throwable e) {
            fm24detected = false;
        }
        FM_24_DETECTED = fm24detected;
        defaultConfigLock = new Object();
    }

    private static class DefaultSoftCacheStorage
    extends SoftCacheStorage {
        private DefaultSoftCacheStorage() {
        }
    }

    private static class LegacyDefaultFileTemplateLoader
    extends FileTemplateLoader {
    }
}

