/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.JavaVersion
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceConversionSandboxConfigurationFactory {
    private static final Logger log = LoggerFactory.getLogger((String)"com.atlassian.confluence.impl.util.sandbox.ConversionSandbox");
    private static final String JAVA_OPTIONS_SEPARATOR = System.getProperty("conversion.sandbox.java.options.separator", ",");
    static final List<String> JPMS_ARGS = ImmutableList.of((Object)"--illegal-access=warn", (Object)"--add-opens", (Object)"java.desktop/com.sun.imageio.plugins.jpeg=ALL-UNNAMED");
    private static final List<String> JAVA_NETWORK_PROPERTIES = ImmutableList.of((Object)"java.net.preferIPv4Stack", (Object)"java.net.preferIPv6Addresses", (Object)"http.proxyHost", (Object)"http.proxyPort", (Object)"http.nonProxyHosts", (Object)"https.proxyHost", (Object)"https.proxyPort", (Object)"socksProxyHost", (Object)"socksProxyPort", (Object)"socksProxyVersion", (Object)"java.net.socks.username", (Object)"java.net.socks.password", (Object[])new String[]{"java.net.useSystemProxies", "http.agent", "http.keepalive", "http.maxConnections", "http.maxRedirects", "http.auth.digest.validateServer", "http.auth.digest.validateProxy", "http.auth.digest.cnonceRepeat", "http.auth.ntlm.domain", "networkaddress.cache.ttl", "networkaddress.cache.negative.ttl"});
    static final String JAVA_AWT_HEADLESS_PROPERTY = "-Djava.awt.headless=true";
    private static final SandboxPoolConfiguration configuration = ConfluenceConversionSandboxConfigurationFactory.buildConfig(SystemUtils.isJavaVersionAtLeast((JavaVersion)JavaVersion.JAVA_9));

    static SandboxPoolConfiguration buildConfig(boolean includeJpmsArgs) {
        return SandboxPoolConfiguration.builder().withConcurrencyLevel(ConfluenceConversionSandboxConfigurationFactory.poolSize()).withMemoryInMegabytes(ConfluenceConversionSandboxConfigurationFactory.memoryLimit()).withStackInMegabytes(ConfluenceConversionSandboxConfigurationFactory.stackLimit()).withStartupTimeLimit(Duration.ofSeconds(ConfluenceConversionSandboxConfigurationFactory.startupTimeLimit())).withLogLevel(Level.parse(ConfluenceConversionSandboxConfigurationFactory.logLevel())).withErrorConsumer((name, message) -> log.info(name + ": " + ConfluenceConversionSandboxConfigurationFactory.trim(message))).withDebugPortOffset(ConfluenceConversionSandboxConfigurationFactory.debugPort()).withJavaOptions(ConfluenceConversionSandboxConfigurationFactory.javaOptions(includeJpmsArgs)).build();
    }

    private static String trim(String message) {
        if (message.endsWith(System.lineSeparator())) {
            return message.substring(0, message.lastIndexOf(System.lineSeparator()));
        }
        return message;
    }

    public SandboxPoolConfiguration get() {
        return configuration;
    }

    private static int poolSize() {
        return Integer.getInteger("conversion.sandbox.pool.size", 2);
    }

    private static int startupTimeLimit() {
        return Integer.getInteger("conversion.sandbox.startup.time.limit.secs", 30);
    }

    private static int memoryLimit() {
        return Integer.getInteger("conversion.sandbox.memory.limit.megabytes", 512);
    }

    private static int stackLimit() {
        return Integer.getInteger("conversion.sandbox.stack.limit.megabytes", 2);
    }

    private static String logLevel() {
        return System.getProperty("conversion.sandbox.log.level", "INFO");
    }

    private static Integer debugPort() {
        return Integer.getInteger("conversion.sandbox.debug.port", null);
    }

    private static String[] javaOptions(boolean includeJpmsArgs) {
        ArrayList<String> options = new ArrayList<String>();
        options.add(JAVA_AWT_HEADLESS_PROPERTY);
        String explicitOptions = System.getProperty("conversion.sandbox.java.options", null);
        if (explicitOptions != null) {
            options.addAll(Arrays.asList(explicitOptions.split(JAVA_OPTIONS_SEPARATOR)));
        } else {
            options.addAll(ConfluenceConversionSandboxConfigurationFactory.getNetworkProperties());
            if (includeJpmsArgs) {
                options.addAll(JPMS_ARGS);
            }
        }
        return options.toArray(new String[0]);
    }

    private static List<String> getNetworkProperties() {
        ArrayList<String> networkProperties = new ArrayList<String>();
        for (String key : JAVA_NETWORK_PROPERTIES) {
            String value = System.getProperty(key);
            if (value == null) continue;
            networkProperties.add(String.format("-D%s=%s", key, value));
        }
        return networkProperties;
    }
}

