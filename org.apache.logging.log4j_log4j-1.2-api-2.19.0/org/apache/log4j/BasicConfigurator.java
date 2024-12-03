/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.StackLocatorUtil
 */
package org.apache.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class BasicConfigurator {
    public static void configure() {
        LogManager.reconfigure(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static void configure(Appender appender) {
        LogManager.getRootLogger(StackLocatorUtil.getCallerClassLoader((int)2)).addAppender(appender);
    }

    public static void resetConfiguration() {
        LogManager.resetConfiguration(StackLocatorUtil.getCallerClassLoader((int)2));
    }

    protected BasicConfigurator() {
    }
}

