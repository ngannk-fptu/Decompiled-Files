/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util;

import com.atlassian.core.util.ClassLoaderUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
    private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);

    public static Properties getProperties(String resource, Class callingClass) {
        return PropertyUtils.getPropertiesFromStream(ClassLoaderUtils.getResourceAsStream(resource, callingClass));
    }

    public static Properties getPropertiesFromFile(File file) {
        try {
            return PropertyUtils.getPropertiesFromStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            log.error("Error loading properties from file: " + file.getPath() + ". File does not exist.", (Throwable)e);
            return null;
        }
    }

    public static Properties getPropertiesFromStream(InputStream is) {
        if (is == null) {
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

