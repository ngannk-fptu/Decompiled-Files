/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.policy.privateutil;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.spi.LoggingProvider;
import java.util.Iterator;
import java.util.ServiceLoader;

public final class PolicyLogger
extends Logger {
    private static final String POLICY_PACKAGE_ROOT = "com.sun.xml.ws.policy";

    private PolicyLogger(String policyLoggerName, String className) {
        super(policyLoggerName, className);
    }

    public static PolicyLogger getLogger(Class<?> componentClass) {
        String componentClassName = componentClass.getName();
        if (componentClassName.startsWith(POLICY_PACKAGE_ROOT)) {
            return new PolicyLogger(PolicyLogger.getLoggingSubsystemName() + componentClassName.substring(POLICY_PACKAGE_ROOT.length()), componentClassName);
        }
        return new PolicyLogger(PolicyLogger.getLoggingSubsystemName() + "." + componentClassName, componentClassName);
    }

    private static String getLoggingSubsystemName() {
        Iterator<LoggingProvider> iterator = ServiceLoader.load(LoggingProvider.class).iterator();
        if (iterator.hasNext()) {
            LoggingProvider p = iterator.next();
            return p.getLoggingSubsystemName().concat(".wspolicy");
        }
        return "wspolicy";
    }
}

