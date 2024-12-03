/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.xhtmlrenderer.DefaultCSSMarker;
import org.xhtmlrenderer.util.GeneralUtil;
import org.xhtmlrenderer.util.LoggerUtil;
import org.xhtmlrenderer.util.XRLog;

public class Configuration {
    private Properties properties;
    private Level logLevel;
    private static Configuration sInstance;
    private List startupLogRecords = new ArrayList();
    private Logger configLogger;
    private static final String SF_FILE_NAME = "resources/conf/xhtmlrenderer.conf";

    private Configuration() {
        try {
            try {
                String val;
                try {
                    val = System.getProperty("show-config");
                }
                catch (SecurityException ex) {
                    val = null;
                }
                this.logLevel = Level.OFF;
                if (val != null) {
                    this.logLevel = LoggerUtil.parseLogLevel(val, Level.OFF);
                }
            }
            catch (SecurityException e) {
                System.err.println(e.getLocalizedMessage());
            }
            this.loadDefaultProperties();
            String sysOverrideFile = this.getSystemPropertyOverrideFileName();
            if (sysOverrideFile != null) {
                this.loadOverrideProperties(sysOverrideFile);
            } else {
                String userHomeOverrideFileName = this.getUserHomeOverrideFileName();
                if (userHomeOverrideFileName != null) {
                    this.loadOverrideProperties(userHomeOverrideFileName);
                }
            }
            this.loadSystemProperties();
            this.logAfterLoad();
        }
        catch (RuntimeException e) {
            this.handleUnexpectedExceptionOnInit(e);
            throw e;
        }
        catch (Exception e) {
            this.handleUnexpectedExceptionOnInit(e);
            throw new RuntimeException(e);
        }
    }

    private void handleUnexpectedExceptionOnInit(Exception e) {
        System.err.println("Could not initialize configuration for Flying Saucer library. Message is: " + e.getMessage());
        e.printStackTrace();
    }

    public static void setConfigLogger(Logger logger) {
        Configuration config = Configuration.instance();
        config.configLogger = logger;
        if (config.startupLogRecords != null) {
            for (LogRecord lr : config.startupLogRecords) {
                logger.log(lr.getLevel(), lr.getMessage());
            }
            config.startupLogRecords = null;
        }
    }

    private void println(Level level, String msg) {
        if (this.logLevel != Level.OFF) {
            if (this.configLogger == null) {
                this.startupLogRecords.add(new LogRecord(level, msg));
            } else {
                this.configLogger.log(level, msg);
            }
        }
    }

    private void info(String msg) {
        if (this.logLevel.intValue() <= Level.INFO.intValue()) {
            this.println(Level.INFO, msg);
        }
    }

    private void warning(String msg) {
        if (this.logLevel.intValue() <= Level.WARNING.intValue()) {
            this.println(Level.WARNING, msg);
        }
    }

    private void warning(String msg, Throwable th) {
        this.warning(msg);
        th.printStackTrace();
    }

    private void fine(String msg) {
        if (this.logLevel.intValue() <= Level.FINE.intValue()) {
            this.println(Level.FINE, msg);
        }
    }

    private void finer(String msg) {
        if (this.logLevel.intValue() <= Level.FINER.intValue()) {
            this.println(Level.FINER, msg);
        }
    }

    private void loadDefaultProperties() {
        block7: {
            try {
                InputStream readStream = GeneralUtil.openStreamFromClasspath(new DefaultCSSMarker(), SF_FILE_NAME);
                if (readStream == null) {
                    System.err.println("WARNING: Flying Saucer: No configuration files found in classpath using URL: resources/conf/xhtmlrenderer.conf, resorting to hard-coded fallback properties.");
                    this.properties = this.newFallbackProperties();
                    break block7;
                }
                try {
                    this.properties = new Properties();
                    this.properties.load(readStream);
                }
                finally {
                    readStream.close();
                }
            }
            catch (RuntimeException rex) {
                throw rex;
            }
            catch (Exception ex) {
                throw new RuntimeException("Could not load properties file for configuration.", ex);
            }
        }
        this.info("Configuration loaded from resources/conf/xhtmlrenderer.conf");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadOverrideProperties(String uri) {
        try {
            Properties temp;
            block26: {
                File f = new File(uri);
                temp = new Properties();
                if (f.exists()) {
                    this.info("Found config override file " + f.getAbsolutePath());
                    try {
                        BufferedInputStream readStream = new BufferedInputStream(new FileInputStream(f));
                        try {
                            temp.load(readStream);
                            break block26;
                        }
                        finally {
                            ((InputStream)readStream).close();
                        }
                    }
                    catch (IOException iex) {
                        this.warning("Error while loading override properties file; skipping.", iex);
                        return;
                    }
                }
                InputStream in = null;
                try {
                    URL url = new URL(uri);
                    in = new BufferedInputStream(url.openStream());
                    this.info("Found config override URI " + uri);
                    temp.load(in);
                }
                catch (MalformedURLException e) {
                    this.warning("URI for override properties is malformed, skipping: " + uri);
                    return;
                }
                catch (IOException e) {
                    this.warning("Overridden properties could not be loaded from URI: " + uri, e);
                    return;
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
            Enumeration<Object> elem = this.properties.keys();
            ArrayList<Object> lp = Collections.list(elem);
            Collections.sort(lp);
            Iterator iter = lp.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                String key = (String)iter.next();
                String val = temp.getProperty(key);
                if (val == null) continue;
                this.properties.setProperty(key, val);
                this.finer("  " + key + " -> " + val);
                ++cnt;
            }
            this.finer("Configuration: " + cnt + " properties overridden from secondary properties file.");
            Enumeration<Object> allRead = temp.keys();
            ArrayList<Object> ap = Collections.list(allRead);
            Collections.sort(ap);
            iter = ap.iterator();
            cnt = 0;
            while (iter.hasNext()) {
                String key = (String)iter.next();
                String val = temp.getProperty(key);
                if (val == null) continue;
                this.properties.setProperty(key, val);
                this.finer("  (+)" + key + " -> " + val);
                ++cnt;
            }
            this.finer("Configuration: " + cnt + " properties added from secondary properties file.");
        }
        catch (SecurityException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private String getSystemPropertyOverrideFileName() {
        try {
            return System.getProperty("xr.conf");
        }
        catch (SecurityException e) {
            return null;
        }
    }

    private String getUserHomeOverrideFileName() {
        try {
            return System.getProperty("user.home") + File.separator + ".flyingsaucer" + File.separator + "local.xhtmlrenderer.conf";
        }
        catch (SecurityException e) {
            return null;
        }
    }

    private void loadSystemProperties() {
        Enumeration<Object> elem = this.properties.keys();
        ArrayList<Object> lp = Collections.list(elem);
        Collections.sort(lp);
        Iterator iter = lp.iterator();
        this.fine("Overriding loaded configuration from System properties.");
        int cnt = 0;
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (!key.startsWith("xr.")) continue;
            try {
                String val = System.getProperty(key);
                if (val == null) continue;
                this.properties.setProperty(key, val);
                this.finer("  Overrode value for " + key);
                ++cnt;
            }
            catch (SecurityException val) {}
        }
        this.fine("Configuration: " + cnt + " properties overridden from System properties.");
        try {
            Properties sysProps = System.getProperties();
            Enumeration<Object> keys = sysProps.keys();
            cnt = 0;
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                if (!key.startsWith("xr.") || this.properties.containsKey(key)) continue;
                Object val = sysProps.get(key);
                this.properties.put(key, val);
                this.finer("  (+) " + key);
                ++cnt;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.fine("Configuration: " + cnt + " FS properties added from System properties.");
    }

    private void logAfterLoad() {
        Enumeration<Object> elem = this.properties.keys();
        ArrayList<Object> lp = Collections.list(elem);
        Collections.sort(lp);
        Iterator iter = lp.iterator();
        this.finer("Configuration contains " + this.properties.size() + " keys.");
        this.finer("List of configuration properties, after override:");
        while (iter.hasNext()) {
            String key = (String)iter.next();
            String val = this.properties.getProperty(key);
            this.finer("  " + key + " = " + val);
        }
        this.finer("Properties list complete.");
    }

    public static String valueFor(String key) {
        Configuration conf = Configuration.instance();
        String val = conf.properties.getProperty(key);
        if (val == null) {
            conf.warning("CONFIGURATION: no value found for key " + key);
        }
        return val;
    }

    public static boolean hasValue(String key) {
        Configuration conf = Configuration.instance();
        String val = conf.properties.getProperty(key);
        return val != null;
    }

    public static int valueAsByte(String key, byte defaultVal) {
        byte bval;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            bval = Byte.valueOf(val);
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as a byte, but value of '" + val + "' is not a byte. Check configuration.");
            bval = defaultVal;
        }
        return bval;
    }

    public static int valueAsShort(String key, short defaultVal) {
        short sval;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            sval = Short.valueOf(val);
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as a short, but value of '" + val + "' is not a short. Check configuration.");
            sval = defaultVal;
        }
        return sval;
    }

    public static int valueAsInt(String key, int defaultVal) {
        int ival;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            ival = Integer.valueOf(val);
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as an integer, but value of '" + val + "' is not an integer. Check configuration.");
            ival = defaultVal;
        }
        return ival;
    }

    public static char valueAsChar(String key, char defaultVal) {
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        if (val.length() > 1) {
            XRLog.exception("Property '" + key + "' was requested as a character. The value of '" + val + "' is too long to be a char. Returning only the first character.");
        }
        return val.charAt(0);
    }

    public static long valueAsLong(String key, long defaultVal) {
        long lval;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            lval = Long.valueOf(val);
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as a long, but value of '" + val + "' is not a long. Check configuration.");
            lval = defaultVal;
        }
        return lval;
    }

    public static float valueAsFloat(String key, float defaultVal) {
        float fval;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            fval = Float.valueOf(val).floatValue();
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as a float, but value of '" + val + "' is not a float. Check configuration.");
            fval = defaultVal;
        }
        return fval;
    }

    public static double valueAsDouble(String key, double defaultVal) {
        double dval;
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        try {
            dval = Double.valueOf(val);
        }
        catch (NumberFormatException nex) {
            XRLog.exception("Property '" + key + "' was requested as a double, but value of '" + val + "' is not a double. Check configuration.");
            dval = defaultVal;
        }
        return dval;
    }

    public static String valueFor(String key, String defaultVal) {
        Configuration conf = Configuration.instance();
        String val = conf.properties.getProperty(key);
        String string = val = val == null ? defaultVal : val;
        if (val == null) {
            conf.warning("CONFIGURATION: no value found for key " + key + " and no default given.");
        }
        return val;
    }

    public static Iterator keysByPrefix(String prefix) {
        Configuration conf = Configuration.instance();
        Iterator<Object> iter = conf.properties.keySet().iterator();
        ArrayList<String> l = new ArrayList<String>();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (!key.startsWith(prefix)) continue;
            l.add(key);
        }
        return l.iterator();
    }

    public static void main(String[] args) {
        try {
            System.out.println("byte: " + String.valueOf(Configuration.valueAsByte("xr.test-config-byte", (byte)15)));
            System.out.println("short: " + String.valueOf(Configuration.valueAsShort("xr.test-config-short", (short)20)));
            System.out.println("int: " + String.valueOf(Configuration.valueAsInt("xr.test-config-int", 25)));
            System.out.println("long: " + String.valueOf(Configuration.valueAsLong("xr.test-config-long", 30L)));
            System.out.println("float: " + String.valueOf(Configuration.valueAsFloat("xr.test-config-float", 45.5f)));
            System.out.println("double: " + String.valueOf(Configuration.valueAsDouble("xr.test-config-double", 50.75)));
            System.out.println("boolean: " + String.valueOf(Configuration.isTrue("xr.test-config-boolean", false)));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isTrue(String key, boolean defaultVal) {
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultVal;
        }
        if ("true|false".indexOf(val) == -1) {
            XRLog.exception("Property '" + key + "' was requested as a boolean, but value of '" + val + "' is not a boolean. Check configuration.");
            return defaultVal;
        }
        return Boolean.valueOf(val);
    }

    public static boolean isFalse(String key, boolean defaultVal) {
        return !Configuration.isTrue(key, defaultVal);
    }

    private static synchronized Configuration instance() {
        if (sInstance == null) {
            sInstance = new Configuration();
        }
        return sInstance;
    }

    public static Object valueFromClassConstant(String key, Object defaultValue) {
        Object cnstVal;
        Class<?> klass;
        String cnst;
        String klassname;
        Configuration conf = Configuration.instance();
        String val = Configuration.valueFor(key);
        if (val == null) {
            return defaultValue;
        }
        int idx = val.lastIndexOf(".");
        try {
            klassname = val.substring(0, idx);
            cnst = val.substring(idx + 1);
        }
        catch (IndexOutOfBoundsException e) {
            conf.warning("Property key " + key + " for object value constant is not properly formatted; should be FQN<dot>constant, is " + val);
            return defaultValue;
        }
        try {
            klass = Class.forName(klassname);
        }
        catch (ClassNotFoundException e) {
            conf.warning("Property for object value constant " + key + " is not a FQN: " + klassname);
            return defaultValue;
        }
        try {
            Field fld = klass.getDeclaredField(cnst);
            try {
                cnstVal = fld.get(klass);
            }
            catch (IllegalAccessException e) {
                conf.warning("Property for object value constant " + key + ", field is not public: " + klassname + "." + cnst);
                return defaultValue;
            }
        }
        catch (NoSuchFieldException e) {
            conf.warning("Property for object value constant " + key + " is not a FQN: " + klassname);
            return defaultValue;
        }
        return cnstVal;
    }

    private Properties newFallbackProperties() {
        Properties props = new Properties();
        props.setProperty("xr.css.user-agent-default-css", "/resources/css/");
        props.setProperty("xr.test.files.hamlet", "/demos/browser/xhtml/hamlet.xhtml");
        props.setProperty("xr.simple-log-format", "{1} {2}:: {5}");
        props.setProperty("xr.simple-log-format-throwable", "{1} {2}:: {5}");
        props.setProperty("xr.test-config-byte", "8");
        props.setProperty("xr.test-config-short", "16");
        props.setProperty("xr.test-config-int", "100");
        props.setProperty("xr.test-config-long", "2000");
        props.setProperty("xr.test-config-float", "3000.25F");
        props.setProperty("xr.test-config-double", "4000.50D");
        props.setProperty("xr.test-config-boolean", "true");
        props.setProperty("xr.util-logging.loggingEnabled", "false");
        props.setProperty("xr.util-logging.handlers", "java.util.logging.ConsoleHandler");
        props.setProperty("xr.util-logging.use-parent-handler", "false");
        props.setProperty("xr.util-logging.java.util.logging.ConsoleHandler.level", "INFO");
        props.setProperty("xr.util-logging.java.util.logging.ConsoleHandler.formatter", "org.xhtmlrenderer.util.XRSimpleLogFormatter");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.config.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.exception.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.general.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.init.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.load.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.load.xml-entities.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.match.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.cascade.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.css-parse.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.layout.level", "ALL");
        props.setProperty("xr.util-logging.org.xhtmlrenderer.render.level", "ALL");
        props.setProperty("xr.load.xml-reader", "default");
        props.setProperty("xr.load.configure-features", "false");
        props.setProperty("xr.load.validation", "false");
        props.setProperty("xr.load.string-interning", "false");
        props.setProperty("xr.load.namespaces", "false");
        props.setProperty("xr.load.namespace-prefixes", "false");
        props.setProperty("xr.layout.whitespace.experimental", "true");
        props.setProperty("xr.layout.bad-sizing-hack", "false");
        props.setProperty("xr.renderer.viewport-repaint", "true");
        props.setProperty("xr.renderer.draw.backgrounds", "true");
        props.setProperty("xr.renderer.draw.borders", "true");
        props.setProperty("xr.renderer.debug.box-outlines", "false");
        props.setProperty("xr.renderer.replace-missing-characters", "false");
        props.setProperty("xr.renderer.missing-character-replacement", "false");
        props.setProperty("xr.text.scale", "1.0");
        props.setProperty("xr.text.aa-smoothing-level", "1");
        props.setProperty("xr.text.aa-fontsize-threshhold", "25");
        props.setProperty("xr.text.aa-rendering-hint", "RenderingHints.VALUE_TEXT_ANTIALIAS_HGRB");
        props.setProperty("xr.cache.stylesheets", "false");
        props.setProperty("xr.incremental.enabled", "false");
        props.setProperty("xr.incremental.lazyimage", "false");
        props.setProperty("xr.incremental.debug.layoutdelay", "0");
        props.setProperty("xr.incremental.repaint.print-timing", "false");
        props.setProperty("xr.use.threads", "false");
        props.setProperty("xr.use.listeners", "true");
        props.setProperty("xr.image.buffered", "false");
        props.setProperty("xr.image.scale", "LOW");
        props.setProperty("xr.image.render-quality", "java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR");
        return props;
    }
}

