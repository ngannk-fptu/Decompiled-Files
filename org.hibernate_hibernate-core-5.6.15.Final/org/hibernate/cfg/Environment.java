/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.Version;
import org.hibernate.bytecode.internal.none.BytecodeProviderImpl;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.UnsupportedLogger;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.jboss.logging.Logger;

public final class Environment
implements AvailableSettings {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)Environment.class.getName());
    private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
    private static final boolean ENABLE_BINARY_STREAMS;
    private static final boolean ENABLE_REFLECTION_OPTIMIZER;
    private static final boolean ENABLE_LEGACY_PROXY_CLASSNAMES;
    private static final Properties GLOBAL_PROPERTIES;
    public static final String BYTECODE_PROVIDER_NAME_JAVASSIST = "javassist";
    public static final String BYTECODE_PROVIDER_NAME_BYTEBUDDY = "bytebuddy";
    public static final String BYTECODE_PROVIDER_NAME_NONE = "none";
    public static final String BYTECODE_PROVIDER_NAME_DEFAULT = "bytebuddy";

    @Deprecated
    public static void verifyProperties(Map<?, ?> configurationValues) {
    }

    @Deprecated
    public static boolean jvmHasTimestampBug() {
        return false;
    }

    @Deprecated
    public static boolean useStreamsForBinary() {
        return ENABLE_BINARY_STREAMS;
    }

    @Deprecated
    public static boolean useReflectionOptimizer() {
        return ENABLE_REFLECTION_OPTIMIZER;
    }

    @Deprecated
    public static BytecodeProvider getBytecodeProvider() {
        return BYTECODE_PROVIDER_INSTANCE;
    }

    @Deprecated
    public static boolean useLegacyProxyClassnames() {
        return ENABLE_LEGACY_PROXY_CLASSNAMES;
    }

    private Environment() {
        throw new UnsupportedOperationException();
    }

    public static Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll((Map<?, ?>)GLOBAL_PROPERTIES);
        return copy;
    }

    @Deprecated
    public static String isolationLevelToString(int isolation) {
        return ConnectionProviderInitiator.toIsolationNiceName(isolation);
    }

    public static BytecodeProvider buildBytecodeProvider(Properties properties) {
        String provider = ConfigurationHelper.getString("hibernate.bytecode.provider", properties, "bytebuddy");
        return Environment.buildBytecodeProvider(provider);
    }

    private static BytecodeProvider buildBytecodeProvider(String providerName) {
        if (BYTECODE_PROVIDER_NAME_NONE.equals(providerName)) {
            return new BytecodeProviderImpl();
        }
        if ("bytebuddy".equals(providerName)) {
            return new org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl();
        }
        if (BYTECODE_PROVIDER_NAME_JAVASSIST.equals(providerName)) {
            throw LOG.usingRemovedJavassistBytecodeProvider();
        }
        LOG.bytecodeProvider(providerName);
        LOG.unknownBytecodeProvider(providerName, "bytebuddy");
        return new org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Version.logVersion();
        GLOBAL_PROPERTIES = new Properties();
        GLOBAL_PROPERTIES.setProperty("hibernate.bytecode.use_reflection_optimizer", Boolean.FALSE.toString());
        try {
            InputStream stream = ConfigHelper.getResourceAsStream("/hibernate.properties");
            try {
                GLOBAL_PROPERTIES.load(stream);
                LOG.propertiesLoaded(ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, "hibernate.connection.password"));
            }
            catch (Exception e) {
                LOG.unableToLoadProperties();
            }
            finally {
                try {
                    stream.close();
                }
                catch (IOException ioe) {
                    LOG.unableToCloseStreamError(ioe);
                }
            }
        }
        catch (HibernateException he) {
            LOG.propertiesNotFound();
        }
        try {
            Properties systemProperties;
            Properties properties = systemProperties = System.getProperties();
            synchronized (properties) {
                GLOBAL_PROPERTIES.putAll((Map<?, ?>)systemProperties);
            }
        }
        catch (SecurityException se) {
            LOG.unableToCopySystemProperties();
        }
        ENABLE_BINARY_STREAMS = ConfigurationHelper.getBoolean("hibernate.jdbc.use_streams_for_binary", GLOBAL_PROPERTIES);
        if (ENABLE_BINARY_STREAMS) {
            LOG.usingStreams();
        }
        if (ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean("hibernate.bytecode.use_reflection_optimizer", GLOBAL_PROPERTIES)) {
            LOG.usingReflectionOptimizer();
        }
        if (ENABLE_LEGACY_PROXY_CLASSNAMES = ConfigurationHelper.getBoolean("hibernate.bytecode.enforce_legacy_proxy_classnames", GLOBAL_PROPERTIES)) {
            UnsupportedLogger unsupportedLogger = (UnsupportedLogger)Logger.getMessageLogger(UnsupportedLogger.class, (String)Environment.class.getName());
            unsupportedLogger.usingLegacyClassnamesForProxies();
        }
        BYTECODE_PROVIDER_INSTANCE = Environment.buildBytecodeProvider(GLOBAL_PROPERTIES);
    }
}

