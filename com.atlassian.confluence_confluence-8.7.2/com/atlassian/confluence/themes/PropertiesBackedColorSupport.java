/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.themes;

import com.atlassian.core.util.ClassLoaderUtils;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesBackedColorSupport {
    private static final Logger log = LoggerFactory.getLogger(PropertiesBackedColorSupport.class);
    private final Properties backingProperties = new Properties();

    public PropertiesBackedColorSupport(String name) {
        try {
            this.backingProperties.load(ClassLoaderUtils.getResourceAsStream((String)name, PropertiesBackedColorSupport.class));
        }
        catch (IOException e) {
            log.error("Could not load default colours - couldn't find " + name + ":" + e, (Throwable)e);
        }
    }

    public String get(String colourName) {
        return (String)this.backingProperties.get(colourName);
    }
}

