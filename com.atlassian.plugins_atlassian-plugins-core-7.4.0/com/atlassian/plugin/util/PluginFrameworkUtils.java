/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.util.ClassLoaderUtils;
import io.atlassian.util.concurrent.LazyReference;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginFrameworkUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PluginFrameworkUtils.class);
    private static final String BUILD_PROPERTY_PATH = "META-INF/maven/com.atlassian.plugins/atlassian-plugins-core/pom.properties";
    private static final LazyReference<String> pluginFrameworkVersionRef = new LazyReference<String>(){

        protected String create() {
            return PluginFrameworkUtils.getPluginFrameworkVersionInternal();
        }
    };

    private PluginFrameworkUtils() {
    }

    public static String getPluginFrameworkVersion() {
        return (String)pluginFrameworkVersionRef.get();
    }

    private static String getPluginFrameworkVersionInternal() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = ClassLoaderUtils.getResourceAsStream(BUILD_PROPERTY_PATH, PluginFrameworkUtils.class);
            if (in != null) {
                props.load(in);
                String string = props.getProperty("version");
                return string;
            }
            String string = "2.7.0";
            return string;
        }
        catch (IOException e) {
            LOG.error("cannot determine the plugin framework version", (Throwable)e);
            throw new IllegalStateException("cannot determine the plugin framework version", e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
    }
}

