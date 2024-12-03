/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

public abstract class SpringProperties {
    private static final String PROPERTIES_RESOURCE_LOCATION = "spring.properties";
    private static final Log logger;
    private static final Properties localProperties;

    public static void setProperty(String key, @Nullable String value) {
        if (value != null) {
            localProperties.setProperty(key, value);
        } else {
            localProperties.remove(key);
        }
    }

    @Nullable
    public static String getProperty(String key) {
        String value;
        block3: {
            value = localProperties.getProperty(key);
            if (value == null) {
                try {
                    value = System.getProperty(key);
                }
                catch (Throwable ex) {
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Could not retrieve system property '" + key + "': " + ex);
                }
            }
        }
        return value;
    }

    public static void setFlag(String key) {
        localProperties.put(key, Boolean.TRUE.toString());
    }

    public static boolean getFlag(String key) {
        return Boolean.parseBoolean(SpringProperties.getProperty(key));
    }

    static {
        block5: {
            logger = LogFactory.getLog(SpringProperties.class);
            localProperties = new Properties();
            try {
                URL url;
                ClassLoader cl = SpringProperties.class.getClassLoader();
                URL uRL = url = cl != null ? cl.getResource(PROPERTIES_RESOURCE_LOCATION) : ClassLoader.getSystemResource(PROPERTIES_RESOURCE_LOCATION);
                if (url == null) break block5;
                logger.info("Found 'spring.properties' file in local classpath");
                try (InputStream is = url.openStream();){
                    localProperties.load(is);
                }
            }
            catch (IOException ex) {
                if (!logger.isInfoEnabled()) break block5;
                logger.info("Could not load 'spring.properties' file from local classpath: " + ex);
            }
        }
    }
}

