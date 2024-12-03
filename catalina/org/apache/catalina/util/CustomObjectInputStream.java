/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;

public final class CustomObjectInputStream
extends ObjectInputStream {
    private static final StringManager sm = StringManager.getManager(CustomObjectInputStream.class);
    private static final WeakHashMap<ClassLoader, Set<String>> reportedClassCache = new WeakHashMap();
    private final ClassLoader classLoader;
    private final Set<String> reportedClasses;
    private final Log log;
    private final Pattern allowedClassNamePattern;
    private final String allowedClassNameFilter;
    private final boolean warnOnFailure;

    public CustomObjectInputStream(InputStream stream, ClassLoader classLoader) throws IOException {
        this(stream, classLoader, null, null, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CustomObjectInputStream(InputStream stream, ClassLoader classLoader, Log log, Pattern allowedClassNamePattern, boolean warnOnFailure) throws IOException {
        super(stream);
        Set<String> reportedClasses;
        if (log == null && allowedClassNamePattern != null && warnOnFailure) {
            throw new IllegalArgumentException(sm.getString("customObjectInputStream.logRequired"));
        }
        this.classLoader = classLoader;
        this.log = log;
        this.allowedClassNamePattern = allowedClassNamePattern;
        this.allowedClassNameFilter = allowedClassNamePattern == null ? null : allowedClassNamePattern.toString();
        this.warnOnFailure = warnOnFailure;
        WeakHashMap<ClassLoader, Set<String>> weakHashMap = reportedClassCache;
        synchronized (weakHashMap) {
            reportedClasses = reportedClassCache.get(classLoader);
        }
        if (reportedClasses == null) {
            Set<String> original;
            reportedClasses = ConcurrentHashMap.newKeySet();
            WeakHashMap<ClassLoader, Set<String>> weakHashMap2 = reportedClassCache;
            synchronized (weakHashMap2) {
                original = reportedClassCache.putIfAbsent(classLoader, reportedClasses);
            }
            if (original != null) {
                reportedClasses = original;
            }
        }
        this.reportedClasses = reportedClasses;
    }

    @Override
    public Class<?> resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException, IOException {
        boolean allowed;
        String name = classDesc.getName();
        if (this.allowedClassNamePattern != null && !(allowed = this.allowedClassNamePattern.matcher(name).matches())) {
            boolean doLog = this.warnOnFailure && this.reportedClasses.add(name);
            String msg = sm.getString("customObjectInputStream.nomatch", new Object[]{name, this.allowedClassNameFilter});
            if (doLog) {
                this.log.warn((Object)msg);
            } else if (this.log.isDebugEnabled()) {
                this.log.debug((Object)msg);
            }
            throw new InvalidClassException(msg);
        }
        try {
            return Class.forName(name, false, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            try {
                return super.resolveClass(classDesc);
            }
            catch (ClassNotFoundException e2) {
                throw e;
            }
        }
    }

    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        Class[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            cinterfaces[i] = this.classLoader.loadClass(interfaces[i]);
        }
        try {
            Class<?> proxyClass = Proxy.getProxyClass(this.classLoader, cinterfaces);
            return proxyClass;
        }
        catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }
}

