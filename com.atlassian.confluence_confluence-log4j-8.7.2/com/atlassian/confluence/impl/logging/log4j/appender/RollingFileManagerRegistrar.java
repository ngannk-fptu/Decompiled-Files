/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.logging.LogAppenderController
 *  org.apache.log4j.Appender
 *  org.apache.log4j.bridge.AppenderWrapper
 *  org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender
 *  org.apache.logging.log4j.core.appender.rolling.RollingFileManager
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import com.atlassian.confluence.impl.logging.LogAppenderController;
import org.apache.log4j.Appender;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;

final class RollingFileManagerRegistrar {
    static void register(Appender appender) {
        LogAppenderController.registerRolloverRunner((Runnable)RollingFileManagerRegistrar.extractRolloverRunner(appender));
    }

    static Runnable extractRolloverRunner(Appender appender) {
        RollingFileManager rollingFileManager = RollingFileManagerRegistrar.extractRollingFileManager(appender);
        return () -> ((RollingFileManager)rollingFileManager).rollover();
    }

    private static RollingFileManager extractRollingFileManager(Appender fileAppender) {
        return (RollingFileManager)((AbstractOutputStreamAppender)((AppenderWrapper)fileAppender).getAppender()).getManager();
    }

    private RollingFileManagerRegistrar() {
    }
}

