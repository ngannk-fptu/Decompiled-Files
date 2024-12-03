/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import org.codehaus.groovy.reflection.ReflectionUtils;
import org.codehaus.groovy.runtime.RegexSupport;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class DefaultGroovyStaticMethods {
    public static Thread start(Thread self, Closure closure) {
        return DefaultGroovyStaticMethods.createThread(null, false, closure);
    }

    public static Thread start(Thread self, String name, Closure closure) {
        return DefaultGroovyStaticMethods.createThread(name, false, closure);
    }

    public static Thread startDaemon(Thread self, Closure closure) {
        return DefaultGroovyStaticMethods.createThread(null, true, closure);
    }

    public static Thread startDaemon(Thread self, String name, Closure closure) {
        return DefaultGroovyStaticMethods.createThread(name, true, closure);
    }

    private static Thread createThread(String name, boolean daemon, Closure closure) {
        Thread thread;
        Thread thread2 = thread = name != null ? new Thread((Runnable)closure, name) : new Thread(closure);
        if (daemon) {
            thread.setDaemon(true);
        }
        thread.start();
        return thread;
    }

    public static Matcher getLastMatcher(Matcher self) {
        return RegexSupport.getLastMatcher();
    }

    private static void sleepImpl(long millis, Closure closure) {
        long start = System.currentTimeMillis();
        long rest = millis;
        while (rest > 0L) {
            try {
                Thread.sleep(rest);
                rest = 0L;
            }
            catch (InterruptedException e) {
                if (closure != null && DefaultTypeTransformation.castToBoolean(closure.call((Object)e))) {
                    return;
                }
                long current = System.currentTimeMillis();
                rest = millis + start - current;
            }
        }
    }

    public static void sleep(Object self, long milliseconds) {
        DefaultGroovyStaticMethods.sleepImpl(milliseconds, null);
    }

    public static void sleep(Object self, long milliseconds, Closure onInterrupt) {
        DefaultGroovyStaticMethods.sleepImpl(milliseconds, onInterrupt);
    }

    public static Date parse(Date self, String format, String input) throws ParseException {
        return new SimpleDateFormat(format).parse(input);
    }

    public static Date parse(Date self, String format, String input, TimeZone zone) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(zone);
        return sdf.parse(input);
    }

    public static Date parseToStringDate(Date self, String dateToString) throws ParseException {
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateToString);
    }

    public static ResourceBundle getBundle(ResourceBundle self, String bundleName) {
        return DefaultGroovyStaticMethods.getBundle(self, bundleName, Locale.getDefault());
    }

    public static ResourceBundle getBundle(ResourceBundle self, String bundleName, Locale locale) {
        ClassLoader targetCL;
        Class c = ReflectionUtils.getCallingClass();
        ClassLoader classLoader = targetCL = c != null ? c.getClassLoader() : null;
        if (targetCL == null) {
            targetCL = ClassLoader.getSystemClassLoader();
        }
        return ResourceBundle.getBundle(bundleName, locale, targetCL);
    }

    public static File createTempDir(File self) throws IOException {
        return DefaultGroovyStaticMethods.createTempDir(self, "groovy-generated-", "-tmpdir");
    }

    public static File createTempDir(File self, String prefix, String suffix) throws IOException {
        int MAXTRIES = 3;
        int accessDeniedCounter = 0;
        File tempFile = null;
        for (int i = 0; i < 3; ++i) {
            try {
                tempFile = File.createTempFile(prefix, suffix);
                tempFile.delete();
                tempFile.mkdirs();
                break;
            }
            catch (IOException ioe) {
                if (ioe.getMessage().startsWith("Access is denied")) {
                    ++accessDeniedCounter;
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
                if (i != 2) continue;
                if (accessDeniedCounter == 3) {
                    String msg = "Access is denied.\nWe tried " + accessDeniedCounter + " times to create a temporary directory and failed each time. If you are on Windows you are possibly victim to http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6325169.  this is no bug in Groovy.";
                    throw new IOException(msg);
                }
                throw ioe;
            }
        }
        return tempFile;
    }

    public static long currentTimeSeconds(System self) {
        return System.currentTimeMillis() / 1000L;
    }
}

