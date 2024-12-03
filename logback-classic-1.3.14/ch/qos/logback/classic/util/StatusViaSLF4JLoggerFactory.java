/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.spi.ContextAwareBase
 *  ch.qos.logback.core.status.ErrorStatus
 *  ch.qos.logback.core.status.InfoStatus
 *  ch.qos.logback.core.status.Status
 *  org.slf4j.ILoggerFactory
 *  org.slf4j.LoggerFactory
 */
package ch.qos.logback.classic.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

public class StatusViaSLF4JLoggerFactory {
    public static void addInfo(String msg, Object o) {
        StatusViaSLF4JLoggerFactory.addStatus((Status)new InfoStatus(msg, o));
    }

    public static void addError(String msg, Object o) {
        StatusViaSLF4JLoggerFactory.addStatus((Status)new ErrorStatus(msg, o));
    }

    public static void addError(String msg, Object o, Throwable t) {
        StatusViaSLF4JLoggerFactory.addStatus((Status)new ErrorStatus(msg, o, t));
    }

    public static void addStatus(Status status) {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        if (iLoggerFactory instanceof LoggerContext) {
            ContextAwareBase contextAwareBase = new ContextAwareBase();
            LoggerContext loggerContext = (LoggerContext)iLoggerFactory;
            contextAwareBase.setContext((Context)loggerContext);
            contextAwareBase.addStatus(status);
        }
    }
}

