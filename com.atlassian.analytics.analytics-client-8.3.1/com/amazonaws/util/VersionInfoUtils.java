/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.util;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.internal.config.InternalConfig;
import com.amazonaws.util.ClassLoaderHelper;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ThreadSafe
public class VersionInfoUtils {
    static final String VERSION_INFO_FILE = "/com/amazonaws/sdk/versionInfo.properties";
    private static volatile String version;
    private static volatile String platform;
    private static volatile String userAgent;
    private static final Log log;
    private static final String UNKNOWN = "unknown";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getVersion() {
        if (version != null) return version;
        Class<VersionInfoUtils> clazz = VersionInfoUtils.class;
        synchronized (VersionInfoUtils.class) {
            if (version != null) return version;
            VersionInfoUtils.initializeVersion();
            // ** MonitorExit[var0] (shouldn't be in output)
            return version;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getPlatform() {
        if (platform != null) return platform;
        Class<VersionInfoUtils> clazz = VersionInfoUtils.class;
        synchronized (VersionInfoUtils.class) {
            if (platform != null) return platform;
            VersionInfoUtils.initializeVersion();
            // ** MonitorExit[var0] (shouldn't be in output)
            return platform;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getUserAgent() {
        if (userAgent != null) return userAgent;
        Class<VersionInfoUtils> clazz = VersionInfoUtils.class;
        synchronized (VersionInfoUtils.class) {
            if (userAgent != null) return userAgent;
            VersionInfoUtils.initializeUserAgent();
            // ** MonitorExit[var0] (shouldn't be in output)
            return userAgent;
        }
    }

    private static void initializeVersion() {
        InputStream inputStream = ClassLoaderHelper.getResourceAsStream(VERSION_INFO_FILE, true, VersionInfoUtils.class);
        Properties versionInfoProperties = new Properties();
        try {
            if (inputStream == null) {
                throw new Exception("/com/amazonaws/sdk/versionInfo.properties not found on classpath");
            }
            versionInfoProperties.load(inputStream);
            version = versionInfoProperties.getProperty("version");
            platform = versionInfoProperties.getProperty("platform");
        }
        catch (Exception e) {
            log.info((Object)("Unable to load version information for the running SDK: " + e.getMessage()));
            version = "unknown-version";
            platform = "java";
        }
        finally {
            IOUtils.closeQuietly(inputStream, log);
        }
    }

    private static void initializeUserAgent() {
        userAgent = VersionInfoUtils.userAgent();
    }

    static String userAgent() {
        String ua = InternalConfig.Factory.getInternalConfig().getUserAgentTemplate();
        if (ua == null) {
            return "aws-sdk-java";
        }
        if ((ua = ua.replace("{platform}", StringUtils.lowerCase(VersionInfoUtils.getPlatform())).replace("{version}", VersionInfoUtils.getVersion()).replace("{os.name}", VersionInfoUtils.replaceSpaces(System.getProperty("os.name"))).replace("{os.version}", VersionInfoUtils.replaceSpaces(System.getProperty("os.version"))).replace("{java.vm.name}", VersionInfoUtils.replaceSpaces(System.getProperty("java.vm.name"))).replace("{java.vm.version}", VersionInfoUtils.replaceSpaces(System.getProperty("java.vm.version"))).replace("{java.version}", VersionInfoUtils.replaceSpaces(System.getProperty("java.version"))).replace("{java.vendor}", VersionInfoUtils.replaceSpaces(System.getProperty("java.vendor")))).contains("{additional.languages}")) {
            ua = ua.replace("{additional.languages}", VersionInfoUtils.getAdditionalJvmLanguages());
        }
        String language = System.getProperty("user.language");
        String region = System.getProperty("user.region");
        String languageAndRegion = "";
        if (language != null && region != null) {
            languageAndRegion = " " + VersionInfoUtils.replaceSpaces(language) + "_" + VersionInfoUtils.replaceSpaces(region);
        }
        ua = ua.replace("{language.and.region}", languageAndRegion);
        return ua;
    }

    private static String replaceSpaces(String input) {
        return input == null ? UNKNOWN : input.replace(' ', '_');
    }

    private static String getAdditionalJvmLanguages() {
        StringBuilder versions = new StringBuilder();
        VersionInfoUtils.concat(versions, VersionInfoUtils.scalaVersion(), " ");
        VersionInfoUtils.concat(versions, VersionInfoUtils.clojureVersion(), " ");
        VersionInfoUtils.concat(versions, VersionInfoUtils.groovyVersion(), " ");
        VersionInfoUtils.concat(versions, VersionInfoUtils.jythonVersion(), " ");
        VersionInfoUtils.concat(versions, VersionInfoUtils.jrubyVersion(), " ");
        VersionInfoUtils.concat(versions, VersionInfoUtils.kotlinVersion(), " ");
        return versions.toString();
    }

    private static String scalaVersion() {
        return VersionInfoUtils.languageVersion("scala", "scala.util.Properties", "versionNumberString", true);
    }

    private static String clojureVersion() {
        return VersionInfoUtils.languageVersion("clojure", "clojure.core$clojure_version", "invokeStatic", true);
    }

    private static String groovyVersion() {
        return VersionInfoUtils.languageVersion("groovy", "groovy.lang.GroovySystem", "getVersion", true);
    }

    private static String jythonVersion() {
        return VersionInfoUtils.languageVersion("jython", "org.python.Version", "PY_VERSION", false);
    }

    private static String jrubyVersion() {
        return VersionInfoUtils.languageVersion("jruby", "org.jruby.runtime.Constants", "VERSION", false);
    }

    private static String kotlinVersion() {
        String version = VersionInfoUtils.kotlinVersionByClass();
        return version.equals("") ? VersionInfoUtils.kotlinVersionByJar() : version;
    }

    private static String kotlinVersionByClass() {
        StringBuilder kotlinVersion;
        block3: {
            kotlinVersion = new StringBuilder("");
            try {
                Class<?> versionClass = Class.forName("kotlin.KotlinVersion");
                kotlinVersion.append("kotlin");
                String version = versionClass.getField("CURRENT").get(null).toString();
                VersionInfoUtils.concat(kotlinVersion, version, "/");
            }
            catch (ClassNotFoundException versionClass) {
            }
            catch (Exception e) {
                if (!log.isTraceEnabled()) break block3;
                log.trace((Object)"Exception attempting to get Kotlin version.", (Throwable)e);
            }
        }
        return kotlinVersion.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String kotlinVersionByJar() {
        StringBuilder kotlinVersion = new StringBuilder("");
        JarInputStream kotlinJar = null;
        try {
            Class<?> kotlinUnit = Class.forName("kotlin.Unit");
            kotlinVersion.append("kotlin");
            kotlinJar = new JarInputStream(kotlinUnit.getProtectionDomain().getCodeSource().getLocation().openStream());
            String version = kotlinJar.getManifest().getMainAttributes().getValue("Implementation-Version");
            VersionInfoUtils.concat(kotlinVersion, version, "/");
            IOUtils.closeQuietly(kotlinJar, log);
        }
        catch (ClassNotFoundException kotlinUnit) {
            IOUtils.closeQuietly(kotlinJar, log);
        }
        catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace((Object)"Exception attempting to get Kotlin version.", (Throwable)e);
            }
        }
        finally {
            IOUtils.closeQuietly(kotlinJar, log);
        }
        return kotlinVersion.toString();
    }

    private static String languageVersion(String language, String className, String methodOrFieldName, boolean isMethod) {
        StringBuilder sb;
        block3: {
            sb = new StringBuilder();
            try {
                Class<?> clz = Class.forName(className);
                sb.append(language);
                String version = isMethod ? (String)clz.getMethod(methodOrFieldName, new Class[0]).invoke(null, new Object[0]) : (String)clz.getField(methodOrFieldName).get(null);
                VersionInfoUtils.concat(sb, version, "/");
            }
            catch (ClassNotFoundException clz) {
            }
            catch (Exception e) {
                if (!log.isTraceEnabled()) break block3;
                log.trace((Object)("Exception attempting to get " + language + " version."), (Throwable)e);
            }
        }
        return sb.toString();
    }

    private static void concat(StringBuilder prefix, String suffix, String separator) {
        if (suffix != null && !suffix.isEmpty()) {
            prefix.append(separator).append(suffix);
        }
    }

    static {
        log = LogFactory.getLog(VersionInfoUtils.class);
    }
}

