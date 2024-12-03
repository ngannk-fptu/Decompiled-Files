/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util.compat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Deque;
import java.util.Set;
import java.util.jar.JarFile;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.res.StringManager;

class Jre9Compat
extends JreCompat {
    private static final Log log = LogFactory.getLog(Jre9Compat.class);
    private static final StringManager sm = StringManager.getManager(Jre9Compat.class);
    private static final Class<?> inaccessibleObjectExceptionClazz;
    private static final Method setDefaultUseCachesMethod;
    private static final Method bootMethod;
    private static final Method configurationMethod;
    private static final Method modulesMethod;
    private static final Method referenceMethod;
    private static final Method locationMethod;
    private static final Method isPresentMethod;
    private static final Method getMethod;
    private static final Constructor<JarFile> jarFileConstructor;
    private static final Method isMultiReleaseMethod;
    private static final Object RUNTIME_VERSION;
    private static final int RUNTIME_MAJOR_VERSION;
    private static final Method canAccessMethod;
    private static final Method getModuleMethod;
    private static final Method isExportedMethod;
    private static final Method getNameMethod;

    Jre9Compat() {
    }

    static boolean isSupported() {
        return inaccessibleObjectExceptionClazz != null;
    }

    @Override
    public boolean isInstanceOfInaccessibleObjectException(Throwable t) {
        if (t == null) {
            return false;
        }
        return inaccessibleObjectExceptionClazz.isAssignableFrom(t.getClass());
    }

    @Override
    public void disableCachingForJarUrlConnections() throws IOException {
        try {
            setDefaultUseCachesMethod.invoke(null, "JAR", Boolean.FALSE);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void addBootModulePath(Deque<URL> classPathUrlsToProcess) {
        try {
            Object bootLayer = bootMethod.invoke(null, new Object[0]);
            Object bootConfiguration = configurationMethod.invoke(bootLayer, new Object[0]);
            Set resolvedModules = (Set)modulesMethod.invoke(bootConfiguration, new Object[0]);
            for (Object resolvedModule : resolvedModules) {
                Object moduleReference = referenceMethod.invoke(resolvedModule, new Object[0]);
                Object optionalURI = locationMethod.invoke(moduleReference, new Object[0]);
                Boolean isPresent = (Boolean)isPresentMethod.invoke(optionalURI, new Object[0]);
                if (!isPresent.booleanValue()) continue;
                URI uri = (URI)getMethod.invoke(optionalURI, new Object[0]);
                try {
                    URL url = uri.toURL();
                    classPathUrlsToProcess.add(url);
                }
                catch (MalformedURLException e) {
                    log.warn((Object)sm.getString("jre9Compat.invalidModuleUri", uri), (Throwable)e);
                }
            }
        }
        catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public JarFile jarFileNewInstance(File f) throws IOException {
        try {
            return jarFileConstructor.newInstance(f, Boolean.TRUE, 1, RUNTIME_VERSION);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean jarFileIsMultiRelease(JarFile jarFile) {
        try {
            return (Boolean)isMultiReleaseMethod.invoke((Object)jarFile, new Object[0]);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    @Override
    public int jarFileRuntimeMajorVersion() {
        return RUNTIME_MAJOR_VERSION;
    }

    @Override
    public boolean canAccess(Object base, AccessibleObject accessibleObject) {
        try {
            return (Boolean)canAccessMethod.invoke((Object)accessibleObject, base);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            return false;
        }
    }

    @Override
    public boolean isExported(Class<?> type) {
        try {
            String packageName = type.getPackage().getName();
            Object module = getModuleMethod.invoke(type, new Object[0]);
            return (Boolean)isExportedMethod.invoke(module, packageName);
        }
        catch (ReflectiveOperationException e) {
            return false;
        }
    }

    @Override
    public String getModuleName(Class<?> type) {
        try {
            Object module = getModuleMethod.invoke(type, new Object[0]);
            String moduleName = (String)getNameMethod.invoke(module, new Object[0]);
            if (moduleName == null) {
                moduleName = "ALL-UNNAMED";
            }
            return moduleName;
        }
        catch (ReflectiveOperationException e) {
            return "ERROR";
        }
    }

    static {
        Class<?> c1 = null;
        Method m4 = null;
        Method m5 = null;
        Method m6 = null;
        Method m7 = null;
        Method m8 = null;
        Method m9 = null;
        Method m10 = null;
        Method m11 = null;
        Constructor c12 = null;
        Method m13 = null;
        Object o14 = null;
        Object o15 = null;
        Method m16 = null;
        Method m17 = null;
        Method m18 = null;
        Method m19 = null;
        try {
            c1 = Class.forName("java.lang.reflect.InaccessibleObjectException");
            Class<?> moduleLayerClazz = Class.forName("java.lang.ModuleLayer");
            Class<?> configurationClazz = Class.forName("java.lang.module.Configuration");
            Class<?> resolvedModuleClazz = Class.forName("java.lang.module.ResolvedModule");
            Class<?> moduleReferenceClazz = Class.forName("java.lang.module.ModuleReference");
            Class<?> optionalClazz = Class.forName("java.util.Optional");
            Class<?> versionClazz = Class.forName("java.lang.Runtime$Version");
            Method runtimeVersionMethod = JarFile.class.getMethod("runtimeVersion", new Class[0]);
            Method majorMethod = versionClazz.getMethod("major", new Class[0]);
            m4 = URLConnection.class.getMethod("setDefaultUseCaches", String.class, Boolean.TYPE);
            m5 = moduleLayerClazz.getMethod("boot", new Class[0]);
            m6 = moduleLayerClazz.getMethod("configuration", new Class[0]);
            m7 = configurationClazz.getMethod("modules", new Class[0]);
            m8 = resolvedModuleClazz.getMethod("reference", new Class[0]);
            m9 = moduleReferenceClazz.getMethod("location", new Class[0]);
            m10 = optionalClazz.getMethod("isPresent", new Class[0]);
            m11 = optionalClazz.getMethod("get", new Class[0]);
            c12 = JarFile.class.getConstructor(File.class, Boolean.TYPE, Integer.TYPE, versionClazz);
            m13 = JarFile.class.getMethod("isMultiRelease", new Class[0]);
            o14 = runtimeVersionMethod.invoke(null, new Object[0]);
            o15 = majorMethod.invoke(o14, new Object[0]);
            m16 = AccessibleObject.class.getMethod("canAccess", Object.class);
            m17 = Class.class.getMethod("getModule", new Class[0]);
            Class<?> moduleClass = Class.forName("java.lang.Module");
            m18 = moduleClass.getMethod("isExported", String.class);
            m19 = moduleClass.getMethod("getName", new Class[0]);
        }
        catch (ClassNotFoundException e) {
            if (c1 == null) {
                log.debug((Object)sm.getString("jre9Compat.javaPre9"), (Throwable)e);
            } else {
                log.error((Object)sm.getString("jre9Compat.unexpected"), (Throwable)e);
            }
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            log.error((Object)sm.getString("jre9Compat.unexpected"), (Throwable)e);
        }
        inaccessibleObjectExceptionClazz = c1;
        setDefaultUseCachesMethod = m4;
        bootMethod = m5;
        configurationMethod = m6;
        modulesMethod = m7;
        referenceMethod = m8;
        locationMethod = m9;
        isPresentMethod = m10;
        getMethod = m11;
        jarFileConstructor = c12;
        isMultiReleaseMethod = m13;
        RUNTIME_VERSION = o14;
        RUNTIME_MAJOR_VERSION = o15 != null ? (Integer)o15 : 8;
        canAccessMethod = m16;
        getModuleMethod = m17;
        isExportedMethod = m18;
        getNameMethod = m19;
    }
}

