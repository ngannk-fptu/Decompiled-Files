/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Properties getProperties(String resourceName, Class<?> callingClass) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (is == null && (is = PropertyUtils.class.getClassLoader().getResourceAsStream(resourceName)) == null && (is = callingClass.getClassLoader().getResourceAsStream(resourceName)) == null) {
            return null;
        }
        Properties props = new Properties();
        try {
            props.load(is);
        }
        catch (IOException e) {
            log.error("Error loading properties from stream.", (Throwable)e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)is);
        }
        return props;
    }
}

