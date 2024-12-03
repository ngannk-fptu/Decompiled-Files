/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j;

import java.net.URL;
import java.util.Properties;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

public class PropertyConfigurator
implements Configurator {
    public static void configure(Properties properties) {
    }

    public static void configure(String configFilename) {
    }

    public static void configure(URL configURL) {
    }

    public static void configureAndWatch(String configFilename) {
    }

    public static void configureAndWatch(String configFilename, long delay) {
    }

    public void doConfigure(Properties properties, LoggerRepository hierarchy) {
    }

    public void doConfigure(String configFileName, LoggerRepository hierarchy) {
    }

    public void doConfigure(URL configURL, LoggerRepository hierarchy) {
    }
}

