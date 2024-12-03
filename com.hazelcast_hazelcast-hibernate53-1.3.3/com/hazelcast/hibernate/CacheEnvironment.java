/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.logging.Logger
 *  org.hibernate.internal.util.StringHelper
 *  org.hibernate.internal.util.config.ConfigurationHelper
 */
package com.hazelcast.hibernate;

import com.hazelcast.logging.Logger;
import java.util.Map;
import java.util.Properties;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;

public final class CacheEnvironment {
    @Deprecated
    public static final String CONFIG_FILE_PATH_LEGACY = "hibernate.cache.provider_configuration_file_resource_path";
    public static final String CONFIG_FILE_PATH = "hibernate.cache.hazelcast.configuration_file_path";
    public static final String USE_NATIVE_CLIENT = "hibernate.cache.hazelcast.use_native_client";
    public static final String NATIVE_CLIENT_ADDRESS = "hibernate.cache.hazelcast.native_client_address";
    public static final String NATIVE_CLIENT_GROUP = "hibernate.cache.hazelcast.native_client_group";
    public static final String NATIVE_CLIENT_PASSWORD = "hibernate.cache.hazelcast.native_client_password";
    public static final String NATIVE_CLIENT_INSTANCE_NAME = "hibernate.cache.hazelcast.native_client_instance_name";
    public static final String SHUTDOWN_ON_STOP = "hibernate.cache.hazelcast.shutdown_on_session_factory_close";
    public static final String LOCK_TIMEOUT = "hibernate.cache.hazelcast.lock_timeout";
    public static final String HAZELCAST_INSTANCE_NAME = "hibernate.cache.hazelcast.instance_name";
    public static final String EXPLICIT_VERSION_CHECK = "hibernate.cache.hazelcast.explicit_version_check";
    public static final String HAZELCAST_OPERATION_TIMEOUT = "hazelcast.operation.call.timeout.millis";
    public static final String HAZELCAST_SHUTDOWN_HOOK_ENABLED = "hazelcast.shutdownhook.enabled";
    public static final String HAZELCAST_FACTORY = "hibernate.cache.hazelcast.factory";
    private static final int MAXIMUM_LOCK_TIMEOUT = 10000;
    private static final int DEFAULT_CACHE_TIMEOUT = 3600000;

    private CacheEnvironment() {
    }

    public static String getConfigFilePath(Properties props) {
        String configResourcePath = ConfigurationHelper.getString((String)CONFIG_FILE_PATH_LEGACY, (Map)props, null);
        if (StringHelper.isEmpty((String)configResourcePath)) {
            configResourcePath = ConfigurationHelper.getString((String)CONFIG_FILE_PATH, (Map)props, null);
        }
        return configResourcePath;
    }

    public static String getInstanceName(Properties props) {
        return ConfigurationHelper.getString((String)HAZELCAST_INSTANCE_NAME, (Map)props, null);
    }

    public static boolean isNativeClient(Properties props) {
        return ConfigurationHelper.getBoolean((String)USE_NATIVE_CLIENT, (Map)props, (boolean)false);
    }

    public static int getDefaultCacheTimeoutInMillis() {
        return 3600000;
    }

    public static int getLockTimeoutInMillis(Properties props) {
        int timeout = -1;
        try {
            timeout = ConfigurationHelper.getInt((String)LOCK_TIMEOUT, (Map)props, (int)-1);
        }
        catch (Exception e) {
            Logger.getLogger(CacheEnvironment.class).finest((Throwable)e);
        }
        if (timeout < 0) {
            timeout = 10000;
        }
        return timeout;
    }

    public static boolean shutdownOnStop(Properties props, boolean defaultValue) {
        return ConfigurationHelper.getBoolean((String)SHUTDOWN_ON_STOP, (Map)props, (boolean)defaultValue);
    }

    public static boolean isExplicitVersionCheckEnabled(Properties props) {
        return ConfigurationHelper.getBoolean((String)EXPLICIT_VERSION_CHECK, (Map)props, (boolean)false);
    }
}

