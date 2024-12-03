/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.util;

import java.util.Optional;
import java.util.jar.JarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.JavaSystemSetting;
import software.amazon.awssdk.utils.StringUtils;

@ThreadSafe
@SdkProtectedApi
public final class SdkUserAgent {
    private static final String UA_STRING = "aws-sdk-{platform}/{version} {os.name}/{os.version} {java.vm.name}/{java.vm.version} Java/{java.version}{language.and.region}{additional.languages} vendor/{java.vendor}";
    private static final String UA_DENYLIST_REGEX = "[() ,/:;<=>?@\\[\\]{}\\\\]";
    private static final Logger log = LoggerFactory.getLogger(SdkUserAgent.class);
    private static final String UNKNOWN = "unknown";
    private static volatile SdkUserAgent instance;
    private static final String[] USER_AGENT_SEARCH;
    private String userAgent;

    private SdkUserAgent() {
        this.initializeUserAgent();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static SdkUserAgent create() {
        if (instance != null) return instance;
        Class<SdkUserAgent> clazz = SdkUserAgent.class;
        synchronized (SdkUserAgent.class) {
            if (instance != null) return instance;
            instance = new SdkUserAgent();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    public String userAgent() {
        return this.userAgent;
    }

    private void initializeUserAgent() {
        this.userAgent = this.getUserAgent();
    }

    @SdkTestInternalApi
    String getUserAgent() {
        Optional<String> language = JavaSystemSetting.USER_LANGUAGE.getStringValue();
        Optional<String> region = JavaSystemSetting.USER_REGION.getStringValue();
        String languageAndRegion = "";
        if (language.isPresent() && region.isPresent()) {
            languageAndRegion = " (" + SdkUserAgent.sanitizeInput(language.get()) + "_" + SdkUserAgent.sanitizeInput(region.get()) + ")";
        }
        return StringUtils.replaceEach(UA_STRING, USER_AGENT_SEARCH, new String[]{"java", "2.21.15", SdkUserAgent.sanitizeInput(JavaSystemSetting.OS_NAME.getStringValue().orElse(null)), SdkUserAgent.sanitizeInput(JavaSystemSetting.OS_VERSION.getStringValue().orElse(null)), SdkUserAgent.sanitizeInput(JavaSystemSetting.JAVA_VM_NAME.getStringValue().orElse(null)), SdkUserAgent.sanitizeInput(JavaSystemSetting.JAVA_VM_VERSION.getStringValue().orElse(null)), SdkUserAgent.sanitizeInput(JavaSystemSetting.JAVA_VERSION.getStringValue().orElse(null)), SdkUserAgent.sanitizeInput(JavaSystemSetting.JAVA_VENDOR.getStringValue().orElse(null)), SdkUserAgent.getAdditionalJvmLanguages(), languageAndRegion});
    }

    private static String sanitizeInput(String input) {
        return input == null ? UNKNOWN : input.replaceAll(UA_DENYLIST_REGEX, "_");
    }

    private static String getAdditionalJvmLanguages() {
        return SdkUserAgent.concat(SdkUserAgent.concat("", SdkUserAgent.scalaVersion(), " "), SdkUserAgent.kotlinVersion(), " ");
    }

    private static String scalaVersion() {
        String scalaVersion;
        block3: {
            scalaVersion = "";
            try {
                Class<?> scalaProperties = Class.forName("scala.util.Properties");
                scalaVersion = "scala";
                String version = (String)scalaProperties.getMethod("versionNumberString", new Class[0]).invoke(null, new Object[0]);
                scalaVersion = SdkUserAgent.concat(scalaVersion, version, "/");
            }
            catch (ClassNotFoundException scalaProperties) {
            }
            catch (Exception e) {
                if (!log.isTraceEnabled()) break block3;
                log.trace("Exception attempting to get Scala version.", e);
            }
        }
        return scalaVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String kotlinVersion() {
        String kotlinVersion = "";
        JarInputStream kotlinJar = null;
        try {
            Class<?> kotlinUnit = Class.forName("kotlin.Unit");
            kotlinVersion = "kotlin";
            kotlinJar = new JarInputStream(kotlinUnit.getProtectionDomain().getCodeSource().getLocation().openStream());
            String version = kotlinJar.getManifest().getMainAttributes().getValue("Implementation-Version");
            kotlinVersion = SdkUserAgent.concat(kotlinVersion, version, "/");
            IoUtils.closeQuietly(kotlinJar, log);
        }
        catch (ClassNotFoundException kotlinUnit) {
            IoUtils.closeQuietly(kotlinJar, log);
        }
        catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("Exception attempting to get Kotlin version.", e);
            }
        }
        finally {
            IoUtils.closeQuietly(kotlinJar, log);
        }
        return kotlinVersion;
    }

    private static String concat(String prefix, String suffix, String separator) {
        return suffix != null && !suffix.isEmpty() ? prefix + separator + suffix : prefix;
    }

    static {
        USER_AGENT_SEARCH = new String[]{"{platform}", "{version}", "{os.name}", "{os.version}", "{java.vm.name}", "{java.vm.version}", "{java.version}", "{java.vendor}", "{additional.languages}", "{language.and.region}"};
    }
}

