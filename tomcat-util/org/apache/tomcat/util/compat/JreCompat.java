/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.compat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.jar.JarFile;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.tomcat.util.compat.Jre16Compat;
import org.apache.tomcat.util.compat.Jre19Compat;
import org.apache.tomcat.util.compat.Jre21Compat;
import org.apache.tomcat.util.compat.Jre22Compat;
import org.apache.tomcat.util.compat.Jre9Compat;
import org.apache.tomcat.util.res.StringManager;

public class JreCompat {
    private static final int RUNTIME_MAJOR_VERSION = 8;
    private static final JreCompat instance;
    private static final boolean graalAvailable;
    private static final boolean jre9Available;
    private static final boolean jre11Available;
    private static final boolean jre16Available;
    private static final boolean jre19Available;
    private static final boolean jre21Available;
    private static final boolean jre22Available;
    private static final StringManager sm;
    protected static final Method setApplicationProtocolsMethod;
    protected static final Method getApplicationProtocolMethod;

    public static JreCompat getInstance() {
        return instance;
    }

    public static boolean isGraalAvailable() {
        return graalAvailable;
    }

    public static boolean isAlpnSupported() {
        return setApplicationProtocolsMethod != null && getApplicationProtocolMethod != null;
    }

    public static boolean isJre9Available() {
        return jre9Available;
    }

    public static boolean isJre11Available() {
        return jre11Available;
    }

    public static boolean isJre16Available() {
        return jre16Available;
    }

    public static boolean isJre19Available() {
        return jre19Available;
    }

    public static boolean isJre21Available() {
        return jre21Available;
    }

    public static boolean isJre22Available() {
        return jre22Available;
    }

    public boolean isInstanceOfInaccessibleObjectException(Throwable t) {
        return false;
    }

    public void setApplicationProtocols(SSLParameters sslParameters, String[] protocols) {
        if (setApplicationProtocolsMethod != null) {
            try {
                setApplicationProtocolsMethod.invoke((Object)sslParameters, new Object[]{protocols});
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        } else {
            throw new UnsupportedOperationException(sm.getString("jreCompat.noApplicationProtocols"));
        }
    }

    public String getApplicationProtocol(SSLEngine sslEngine) {
        if (getApplicationProtocolMethod != null) {
            try {
                return (String)getApplicationProtocolMethod.invoke((Object)sslEngine, new Object[0]);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnsupportedOperationException(e);
            }
        }
        throw new UnsupportedOperationException(sm.getString("jreCompat.noApplicationProtocol"));
    }

    public void disableCachingForJarUrlConnections() throws IOException {
        URL url = new URL("jar:file://dummy.jar!/");
        URLConnection uConn = url.openConnection();
        uConn.setDefaultUseCaches(false);
    }

    public void addBootModulePath(Deque<URL> classPathUrlsToProcess) {
    }

    public final JarFile jarFileNewInstance(String s) throws IOException {
        return this.jarFileNewInstance(new File(s));
    }

    public JarFile jarFileNewInstance(File f) throws IOException {
        return new JarFile(f);
    }

    public boolean jarFileIsMultiRelease(JarFile jarFile) {
        return false;
    }

    public int jarFileRuntimeMajorVersion() {
        return 8;
    }

    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        return true;
    }

    public boolean isExported(Class<?> type) {
        return true;
    }

    public String getModuleName(Class<?> type) {
        return "NO_MODULE_JAVA_8";
    }

    public SocketAddress getUnixDomainSocketAddress(String path) {
        return null;
    }

    public ServerSocketChannel openUnixDomainServerSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public SocketChannel openUnixDomainSocketChannel() {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noUnixDomainSocket"));
    }

    public Object getExecutor(Thread thread) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = null;
        Object target = null;
        for (String fieldName : new String[]{"target", "runnable", "action"}) {
            try {
                Field targetField = thread.getClass().getDeclaredField(fieldName);
                targetField.setAccessible(true);
                target = targetField.get(thread);
                break;
            }
            catch (NoSuchFieldException nfe) {
            }
        }
        if (target != null && target.getClass().getCanonicalName() != null && (target.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || target.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
            Field executorField = target.getClass().getDeclaredField("this$0");
            executorField.setAccessible(true);
            result = executorField.get(target);
        }
        return result;
    }

    public Object createVirtualThreadBuilder(String name) {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noVirtualThreads"));
    }

    public void threadBuilderStart(Object threadBuilder, Runnable command) {
        throw new UnsupportedOperationException(sm.getString("jreCompat.noVirtualThreads"));
    }

    static {
        sm = StringManager.getManager(JreCompat.class);
        boolean result = false;
        try {
            Class<?> nativeImageClazz = Class.forName("org.graalvm.nativeimage.ImageInfo");
            result = Boolean.TRUE.equals(nativeImageClazz.getMethod("inImageCode", new Class[0]).invoke(null, new Object[0]));
        }
        catch (ClassNotFoundException nativeImageClazz) {
        }
        catch (IllegalArgumentException | ReflectiveOperationException nativeImageClazz) {
            // empty catch block
        }
        boolean bl = graalAvailable = result || System.getProperty("org.graalvm.nativeimage.imagecode") != null;
        if (Jre22Compat.isSupported()) {
            instance = new Jre22Compat();
            jre22Available = true;
            jre21Available = true;
            jre19Available = true;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre21Compat.isSupported()) {
            instance = new Jre21Compat();
            jre22Available = false;
            jre21Available = true;
            jre19Available = true;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre19Compat.isSupported()) {
            instance = new Jre19Compat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = true;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre16Compat.isSupported()) {
            instance = new Jre16Compat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = false;
            jre16Available = true;
            jre9Available = true;
        } else if (Jre9Compat.isSupported()) {
            instance = new Jre9Compat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = false;
            jre16Available = false;
            jre9Available = true;
        } else {
            instance = new JreCompat();
            jre22Available = false;
            jre21Available = false;
            jre19Available = false;
            jre16Available = false;
            jre9Available = false;
        }
        jre11Available = instance.jarFileRuntimeMajorVersion() >= 11;
        Method m1 = null;
        Method m2 = null;
        try {
            m1 = SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            m2 = SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
        }
        catch (IllegalArgumentException | ReflectiveOperationException exception) {
            // empty catch block
        }
        setApplicationProtocolsMethod = m1;
        getApplicationProtocolMethod = m2;
    }
}

