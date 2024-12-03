/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2;

import java.util.Iterator;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLPropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemConfiguration
extends MapConfiguration {
    private static final Log LOG = LogFactory.getLog(SystemConfiguration.class);

    public SystemConfiguration() {
        super(System.getProperties());
    }

    public static void setSystemProperties(String fileName) throws ConfigurationException {
        SystemConfiguration.setSystemProperties(null, fileName);
    }

    public static void setSystemProperties(String basePath, String fileName) throws ConfigurationException {
        BaseConfiguration config = fileName.endsWith(".xml") ? new XMLPropertiesConfiguration() : new PropertiesConfiguration();
        FileHandler handler = new FileHandler((FileBased)((Object)config));
        handler.setBasePath(basePath);
        handler.setFileName(fileName);
        handler.load();
        SystemConfiguration.setSystemProperties(config);
    }

    public static void setSystemProperties(Configuration systemConfig) {
        Iterator<String> iter = systemConfig.getKeys();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = (String)systemConfig.getProperty(key);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("Setting system property " + key + " to " + value));
            }
            System.setProperty(key, value);
        }
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return System.getProperties().stringPropertyNames().iterator();
    }
}

