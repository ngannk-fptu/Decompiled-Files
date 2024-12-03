/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class StackTraceUtils {
    public static final String STACK_LOG_NAME = "StackTrace";
    private static final Logger STACK_LOG;
    private static final String[] GROOVY_PACKAGES;
    private static List<Closure> tests;

    public static void addClassTest(Closure test) {
        tests.add(test);
    }

    public static Throwable sanitize(Throwable t) {
        if (!Boolean.getBoolean("groovy.full.stacktrace")) {
            StackTraceElement[] trace = t.getStackTrace();
            ArrayList<StackTraceElement> newTrace = new ArrayList<StackTraceElement>();
            for (StackTraceElement stackTraceElement : trace) {
                if (!StackTraceUtils.isApplicationClass(stackTraceElement.getClassName())) continue;
                newTrace.add(stackTraceElement);
            }
            STACK_LOG.log(Level.WARNING, "Sanitizing stacktrace:", t);
            StackTraceElement[] clean = new StackTraceElement[newTrace.size()];
            newTrace.toArray(clean);
            t.setStackTrace(clean);
        }
        return t;
    }

    public static void printSanitizedStackTrace(Throwable t, PrintWriter p) {
        StackTraceElement[] trace;
        t = StackTraceUtils.sanitize(t);
        for (StackTraceElement stackTraceElement : trace = t.getStackTrace()) {
            p.println("at " + stackTraceElement.getClassName() + "(" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + ")");
        }
    }

    public static void printSanitizedStackTrace(Throwable t) {
        StackTraceUtils.printSanitizedStackTrace(t, new PrintWriter(System.err));
    }

    public static boolean isApplicationClass(String className) {
        for (Closure test : tests) {
            Object result = test.call((Object)className);
            if (result == null) continue;
            return DefaultTypeTransformation.castToBoolean(result);
        }
        for (String groovyPackage : GROOVY_PACKAGES) {
            if (!className.startsWith(groovyPackage)) continue;
            return false;
        }
        return true;
    }

    public static Throwable extractRootCause(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

    public static Throwable sanitizeRootCause(Throwable t) {
        return StackTraceUtils.sanitize(StackTraceUtils.extractRootCause(t));
    }

    public static Throwable deepSanitize(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null) {
            current = StackTraceUtils.sanitize(current.getCause());
        }
        return StackTraceUtils.sanitize(t);
    }

    static {
        block1: {
            Enumeration<String> existingLogs = LogManager.getLogManager().getLoggerNames();
            while (existingLogs.hasMoreElements()) {
                if (!STACK_LOG_NAME.equals(existingLogs.nextElement())) continue;
                STACK_LOG = Logger.getLogger(STACK_LOG_NAME);
                break block1;
            }
            STACK_LOG = Logger.getLogger(STACK_LOG_NAME);
            STACK_LOG.setUseParentHandlers(false);
        }
        GROOVY_PACKAGES = System.getProperty("groovy.sanitized.stacktraces", "groovy.,org.codehaus.groovy.,java.,javax.,sun.,gjdk.groovy.,").split("(\\s|,)+");
        tests = new ArrayList<Closure>();
    }
}

