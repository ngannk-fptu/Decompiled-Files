/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.Configurator
 *  org.apache.logging.log4j.core.net.UrlConnectionFactory
 *  org.apache.logging.log4j.util.StackLocatorUtil
 */
package org.apache.log4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.bridge.FilterAdapter;
import org.apache.log4j.config.Log4j1Configuration;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableRenderer;
import org.apache.log4j.spi.ThrowableRendererSupport;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class PropertyConfigurator
implements Configurator {
    static final String CATEGORY_PREFIX = "log4j.category.";
    static final String LOGGER_PREFIX = "log4j.logger.";
    static final String FACTORY_PREFIX = "log4j.factory";
    static final String ADDITIVITY_PREFIX = "log4j.additivity.";
    static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
    static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
    static final String APPENDER_PREFIX = "log4j.appender.";
    static final String RENDERER_PREFIX = "log4j.renderer.";
    static final String THRESHOLD_PREFIX = "log4j.threshold";
    private static final String THROWABLE_RENDERER_PREFIX = "log4j.throwableRenderer";
    private static final String LOGGER_REF = "logger-ref";
    private static final String ROOT_REF = "root-ref";
    private static final String APPENDER_REF_TAG = "appender-ref";
    public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
    private static final String RESET_KEY = "log4j.reset";
    private static final String INTERNAL_ROOT_NAME = "root";
    protected Hashtable registry = new Hashtable(11);
    private LoggerRepository repository;
    protected LoggerFactory loggerFactory = new DefaultCategoryFactory();

    public static void configure(InputStream inputStream) {
        new PropertyConfigurator().doConfigure(inputStream, LogManager.getLoggerRepository(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configure(Properties properties) {
        new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configure(String fileName) {
        new PropertyConfigurator().doConfigure(fileName, LogManager.getLoggerRepository(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configure(URL configURL) {
        new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configureAndWatch(String configFilename) {
        PropertyConfigurator.configureAndWatch(configFilename, 60000L, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configureAndWatch(String configFilename, long delayMillis) {
        PropertyConfigurator.configureAndWatch(configFilename, delayMillis, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    static void configureAndWatch(String configFilename, long delay, ClassLoader classLoader) {
        PropertyWatchdog watchdog = new PropertyWatchdog(configFilename, classLoader);
        watchdog.setDelay(delay);
        watchdog.start();
    }

    private static Configuration reconfigure(Configuration configuration) {
        org.apache.logging.log4j.core.config.Configurator.reconfigure((Configuration)configuration);
        return configuration;
    }

    protected void configureLoggerFactory(Properties properties) {
        String factoryClassName = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY, properties);
        if (factoryClassName != null) {
            LogLog.debug("Setting category factory to [" + factoryClassName + "].");
            this.loggerFactory = (LoggerFactory)OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, this.loggerFactory);
            PropertySetter.setProperties(this.loggerFactory, properties, "log4j.factory.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void configureRootCategory(Properties properties, LoggerRepository loggerRepository) {
        String effectiveFrefix = ROOT_LOGGER_PREFIX;
        String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, properties);
        if (value == null) {
            value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, properties);
            effectiveFrefix = ROOT_CATEGORY_PREFIX;
        }
        if (value == null) {
            LogLog.debug("Could not find root logger information. Is this OK?");
        } else {
            Logger root;
            Logger logger = root = loggerRepository.getRootLogger();
            synchronized (logger) {
                this.parseCategory(properties, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
            }
        }
    }

    @Override
    public void doConfigure(InputStream inputStream, LoggerRepository loggerRepository) {
        this.doConfigure(inputStream, loggerRepository, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    Configuration doConfigure(InputStream inputStream, LoggerRepository loggerRepository, ClassLoader classLoader) {
        return this.doConfigure(this.loadProperties(inputStream), loggerRepository, classLoader);
    }

    public void doConfigure(Properties properties, LoggerRepository loggerRepository) {
        this.doConfigure(properties, loggerRepository, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    Configuration doConfigure(Properties properties, LoggerRepository loggerRepository, ClassLoader classLoader) {
        PropertiesConfiguration configuration = new PropertiesConfiguration(LogManager.getContext(classLoader), properties);
        configuration.doConfigure();
        this.repository = loggerRepository;
        this.registry.clear();
        return PropertyConfigurator.reconfigure((Configuration)configuration);
    }

    public void doConfigure(String fileName, LoggerRepository loggerRepository) {
        this.doConfigure(fileName, loggerRepository, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Configuration doConfigure(String fileName, LoggerRepository loggerRepository, ClassLoader classLoader) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(fileName, new String[0]), new OpenOption[0]);){
            Configuration configuration = this.doConfigure(inputStream, loggerRepository, classLoader);
            return configuration;
        }
        catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not read configuration file [" + fileName + "].", e);
            LogLog.error("Ignoring configuration file [" + fileName + "].");
            return null;
        }
    }

    @Override
    public void doConfigure(URL url, LoggerRepository loggerRepository) {
        this.doConfigure(url, loggerRepository, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Configuration doConfigure(URL url, LoggerRepository loggerRepository, ClassLoader classLoader) {
        LogLog.debug("Reading configuration from URL " + url);
        try {
            URLConnection urlConnection = UrlConnectionFactory.createConnection((URL)url);
            try (InputStream inputStream = urlConnection.getInputStream();){
                Configuration configuration = this.doConfigure(inputStream, loggerRepository, classLoader);
                return configuration;
            }
        }
        catch (IOException e) {
            LogLog.error("Could not read configuration file from URL [" + url + "].", e);
            LogLog.error("Ignoring configuration file [" + url + "].");
            return null;
        }
    }

    private Properties loadProperties(InputStream inputStream) {
        Properties loaded = new Properties();
        try {
            loaded.load(inputStream);
        }
        catch (IOException | IllegalArgumentException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not read configuration file from InputStream [" + inputStream + "].", e);
            LogLog.error("Ignoring configuration InputStream [" + inputStream + "].");
            return null;
        }
        return loaded;
    }

    void parseAdditivityForLogger(Properties properties, Logger logger, String loggerName) {
        String value = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + loggerName, properties);
        LogLog.debug("Handling log4j.additivity." + loggerName + "=[" + value + "]");
        if (value != null && !value.equals("")) {
            boolean additivity = OptionConverter.toBoolean(value, true);
            LogLog.debug("Setting additivity for \"" + loggerName + "\" to " + additivity);
            logger.setAdditivity(additivity);
        }
    }

    Appender parseAppender(Properties properties, String appenderName) {
        Appender appender = this.registryGet(appenderName);
        if (appender != null) {
            LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
            return appender;
        }
        String prefix = APPENDER_PREFIX + appenderName;
        String layoutPrefix = prefix + ".layout";
        appender = (Appender)OptionConverter.instantiateByKey(properties, prefix, Appender.class, null);
        if (appender == null) {
            LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
            return null;
        }
        appender.setName(appenderName);
        if (appender instanceof OptionHandler) {
            ErrorHandler eh;
            String errorHandlerPrefix;
            String errorHandlerClass;
            Layout layout;
            if (appender.requiresLayout() && (layout = (Layout)OptionConverter.instantiateByKey(properties, layoutPrefix, Layout.class, null)) != null) {
                appender.setLayout(layout);
                LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
                PropertySetter.setProperties(layout, properties, layoutPrefix + ".");
                LogLog.debug("End of parsing for \"" + appenderName + "\".");
            }
            if ((errorHandlerClass = OptionConverter.findAndSubst(errorHandlerPrefix = prefix + ".errorhandler", properties)) != null && (eh = (ErrorHandler)OptionConverter.instantiateByKey(properties, errorHandlerPrefix, ErrorHandler.class, null)) != null) {
                appender.setErrorHandler(eh);
                LogLog.debug("Parsing errorhandler options for \"" + appenderName + "\".");
                this.parseErrorHandler(eh, errorHandlerPrefix, properties, this.repository);
                Properties edited = new Properties();
                String[] keys = new String[]{errorHandlerPrefix + "." + ROOT_REF, errorHandlerPrefix + "." + LOGGER_REF, errorHandlerPrefix + "." + APPENDER_REF_TAG};
                Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
                while (iterator.hasNext()) {
                    int i;
                    Map.Entry<Object, Object> element;
                    Map.Entry<Object, Object> entry = element = iterator.next();
                    for (i = 0; i < keys.length && !keys[i].equals(entry.getKey()); ++i) {
                    }
                    if (i != keys.length) continue;
                    edited.put(entry.getKey(), entry.getValue());
                }
                PropertySetter.setProperties(eh, edited, errorHandlerPrefix + ".");
                LogLog.debug("End of errorhandler parsing for \"" + appenderName + "\".");
            }
            PropertySetter.setProperties(appender, properties, prefix + ".");
            LogLog.debug("Parsed \"" + appenderName + "\" options.");
        }
        this.parseAppenderFilters(properties, appenderName, appender);
        this.registryPut(appender);
        return appender;
    }

    void parseAppenderFilters(Properties properties, String appenderName, Appender appender) {
        String filterPrefix = APPENDER_PREFIX + appenderName + ".filter.";
        int fIdx = filterPrefix.length();
        Hashtable filters = new Hashtable();
        Enumeration<Object> e = properties.keys();
        String name = "";
        while (e.hasMoreElements()) {
            Vector<NameValue> filterOpts;
            String key = (String)e.nextElement();
            if (!key.startsWith(filterPrefix)) continue;
            int dotIdx = key.indexOf(46, fIdx);
            String filterKey = key;
            if (dotIdx != -1) {
                filterKey = key.substring(0, dotIdx);
                name = key.substring(dotIdx + 1);
            }
            if ((filterOpts = (Vector<NameValue>)filters.get(filterKey)) == null) {
                filterOpts = new Vector<NameValue>();
                filters.put(filterKey, filterOpts);
            }
            if (dotIdx == -1) continue;
            String value = OptionConverter.findAndSubst(key, properties);
            filterOpts.add(new NameValue(name, value));
        }
        SortedKeyEnumeration g = new SortedKeyEnumeration(filters);
        Filter head = null;
        while (g.hasMoreElements()) {
            String key = (String)g.nextElement();
            String clazz = properties.getProperty(key);
            if (clazz != null) {
                LogLog.debug("Filter key: [" + key + "] class: [" + properties.getProperty(key) + "] props: " + filters.get(key));
                Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
                if (filter == null) continue;
                PropertySetter propSetter = new PropertySetter(filter);
                Vector v = (Vector)filters.get(key);
                Enumeration filterProps = v.elements();
                while (filterProps.hasMoreElements()) {
                    NameValue kv = (NameValue)filterProps.nextElement();
                    propSetter.setProperty(kv.key, kv.value);
                }
                propSetter.activate();
                LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
                head = FilterAdapter.addFilter(head, filter);
                continue;
            }
            LogLog.warn("Missing class definition for filter: [" + key + "]");
        }
        appender.addFilter(head);
    }

    void parseCategory(Properties properties, Logger logger, String optionKey, String loggerName, String value) {
        LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
        StringTokenizer st = new StringTokenizer(value, ",");
        if (!value.startsWith(",") && !value.equals("")) {
            if (!st.hasMoreTokens()) {
                return;
            }
            String levelStr = st.nextToken();
            LogLog.debug("Level token is [" + levelStr + "].");
            if ("inherited".equalsIgnoreCase(levelStr) || "null".equalsIgnoreCase(levelStr)) {
                if (loggerName.equals(INTERNAL_ROOT_NAME)) {
                    LogLog.warn("The root logger cannot be set to null.");
                } else {
                    logger.setLevel(null);
                }
            } else {
                logger.setLevel(OptionConverter.toLevel(levelStr, Log4j1Configuration.DEFAULT_LEVEL));
            }
            LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
        }
        logger.removeAllAppenders();
        while (st.hasMoreTokens()) {
            String appenderName = st.nextToken().trim();
            if (appenderName == null || appenderName.equals(",")) continue;
            LogLog.debug("Parsing appender named \"" + appenderName + "\".");
            Appender appender = this.parseAppender(properties, appenderName);
            if (appender == null) continue;
            logger.addAppender(appender);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parseCatsAndRenderers(Properties properties, LoggerRepository loggerRepository) {
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            if (key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
                Logger logger;
                String loggerName = null;
                if (key.startsWith(CATEGORY_PREFIX)) {
                    loggerName = key.substring(CATEGORY_PREFIX.length());
                } else if (key.startsWith(LOGGER_PREFIX)) {
                    loggerName = key.substring(LOGGER_PREFIX.length());
                }
                String value = OptionConverter.findAndSubst(key, properties);
                Logger logger2 = logger = loggerRepository.getLogger(loggerName, this.loggerFactory);
                synchronized (logger2) {
                    this.parseCategory(properties, logger, key, loggerName, value);
                    this.parseAdditivityForLogger(properties, logger, loggerName);
                    continue;
                }
            }
            if (key.startsWith(RENDERER_PREFIX)) {
                String renderedClass = key.substring(RENDERER_PREFIX.length());
                String renderingClass = OptionConverter.findAndSubst(key, properties);
                if (!(loggerRepository instanceof RendererSupport)) continue;
                RendererMap.addRenderer((RendererSupport)((Object)loggerRepository), renderedClass, renderingClass);
                continue;
            }
            if (!key.equals(THROWABLE_RENDERER_PREFIX) || !(loggerRepository instanceof ThrowableRendererSupport)) continue;
            ThrowableRenderer tr = (ThrowableRenderer)OptionConverter.instantiateByKey(properties, THROWABLE_RENDERER_PREFIX, ThrowableRenderer.class, null);
            if (tr == null) {
                LogLog.error("Could not instantiate throwableRenderer.");
                continue;
            }
            PropertySetter setter = new PropertySetter(tr);
            setter.setProperties(properties, "log4j.throwableRenderer.");
            ((ThrowableRendererSupport)((Object)loggerRepository)).setThrowableRenderer(tr);
        }
    }

    private void parseErrorHandler(ErrorHandler errorHandler, String errorHandlerPrefix, Properties props, LoggerRepository loggerRepository) {
        if (errorHandler != null && loggerRepository != null) {
            Appender backup;
            String appenderName;
            String loggerName;
            boolean rootRef = OptionConverter.toBoolean(OptionConverter.findAndSubst(errorHandlerPrefix + ROOT_REF, props), false);
            if (rootRef) {
                errorHandler.setLogger(loggerRepository.getRootLogger());
            }
            if ((loggerName = OptionConverter.findAndSubst(errorHandlerPrefix + LOGGER_REF, props)) != null) {
                Logger logger = this.loggerFactory == null ? loggerRepository.getLogger(loggerName) : loggerRepository.getLogger(loggerName, this.loggerFactory);
                errorHandler.setLogger(logger);
            }
            if ((appenderName = OptionConverter.findAndSubst(errorHandlerPrefix + APPENDER_REF_TAG, props)) != null && (backup = this.parseAppender(props, appenderName)) != null) {
                errorHandler.setBackupAppender(backup);
            }
        }
    }

    Appender registryGet(String name) {
        return (Appender)this.registry.get(name);
    }

    void registryPut(Appender appender) {
        this.registry.put(appender.getName(), appender);
    }

    class SortedKeyEnumeration
    implements Enumeration {
        private final Enumeration e;

        public SortedKeyEnumeration(Hashtable ht) {
            Enumeration f = ht.keys();
            Vector<String> keys = new Vector<String>(ht.size());
            int last = 0;
            while (f.hasMoreElements()) {
                String s;
                int i;
                String key = (String)f.nextElement();
                for (i = 0; i < last && key.compareTo(s = (String)keys.get(i)) > 0; ++i) {
                }
                keys.add(i, key);
                ++last;
            }
            this.e = keys.elements();
        }

        @Override
        public boolean hasMoreElements() {
            return this.e.hasMoreElements();
        }

        public Object nextElement() {
            return this.e.nextElement();
        }
    }

    static class PropertyWatchdog
    extends FileWatchdog {
        private final ClassLoader classLoader;

        PropertyWatchdog(String fileName, ClassLoader classLoader) {
            super(fileName);
            this.classLoader = classLoader;
        }

        @Override
        public void doOnChange() {
            new PropertyConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository(), this.classLoader);
        }
    }

    static class NameValue {
        String key;
        String value;

        public NameValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}

