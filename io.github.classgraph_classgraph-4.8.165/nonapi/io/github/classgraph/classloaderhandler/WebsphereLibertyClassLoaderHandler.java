/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class WebsphereLibertyClassLoaderHandler
implements ClassLoaderHandler {
    private static final String PKG_PREFIX = "com.ibm.ws.classloading.internal.";
    private static final String IBM_APP_CLASS_LOADER = "com.ibm.ws.classloading.internal.AppClassLoader";
    private static final String IBM_THREAD_CONTEXT_CLASS_LOADER = "com.ibm.ws.classloading.internal.ThreadContextClassLoader";

    private WebsphereLibertyClassLoaderHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return IBM_APP_CLASS_LOADER.equals(classLoaderClass.getName()) || IBM_THREAD_CONTEXT_CLASS_LOADER.equals(classLoaderClass.getName());
    }

    public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
        classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        classLoaderOrder.add(classLoader, log);
    }

    private static Collection<Object> getPaths(Object containerClassLoader, ReflectionUtils reflectionUtils) {
        if (containerClassLoader == null) {
            return Collections.emptyList();
        }
        Collection<Object> urls = WebsphereLibertyClassLoaderHandler.callGetUrls(containerClassLoader, "getContainerURLs", reflectionUtils);
        if (urls != null && !urls.isEmpty()) {
            return urls;
        }
        Object container = reflectionUtils.getFieldVal(false, containerClassLoader, "container");
        if (container == null) {
            return Collections.emptyList();
        }
        urls = WebsphereLibertyClassLoaderHandler.callGetUrls(container, "getURLs", reflectionUtils);
        if (urls != null && !urls.isEmpty()) {
            return urls;
        }
        Object delegate = reflectionUtils.getFieldVal(false, container, "delegate");
        if (delegate == null) {
            return Collections.emptyList();
        }
        String path = (String)reflectionUtils.getFieldVal(false, delegate, "path");
        if (path != null && path.length() > 0) {
            return Collections.singletonList(path);
        }
        Object base = reflectionUtils.getFieldVal(false, delegate, "base");
        if (base == null) {
            return Collections.emptyList();
        }
        Object archiveFile = reflectionUtils.getFieldVal(false, base, "archiveFile");
        if (archiveFile != null) {
            File file = (File)archiveFile;
            return Collections.singletonList(file.getAbsolutePath());
        }
        return Collections.emptyList();
    }

    private static Collection<Object> callGetUrls(Object container, String methodName, ReflectionUtils reflectionUtils) {
        block6: {
            if (container != null) {
                try {
                    Collection results = (Collection)reflectionUtils.invokeMethod(false, container, methodName);
                    if (results == null || results.isEmpty()) break block6;
                    HashSet<Object> allUrls = new HashSet<Object>();
                    for (Object result : results) {
                        if (result instanceof Collection) {
                            for (Object url : (Collection)result) {
                                if (url == null) continue;
                                allUrls.add(url);
                            }
                            continue;
                        }
                        if (result == null) continue;
                        allUrls.add(result);
                    }
                    return allUrls;
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                    // empty catch block
                }
            }
        }
        return Collections.emptyList();
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        block3: {
            Object smartClassPath;
            block4: {
                Object appLoader = classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "appLoader");
                smartClassPath = appLoader != null ? classpathOrder.reflectionUtils.getFieldVal(false, appLoader, "smartClassPath") : classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "smartClassPath");
                if (smartClassPath == null) break block3;
                Collection<Object> paths = WebsphereLibertyClassLoaderHandler.callGetUrls(smartClassPath, "getClassPath", classpathOrder.reflectionUtils);
                if (paths.isEmpty()) break block4;
                for (Object path : paths) {
                    classpathOrder.addClasspathEntry(path, classLoader, scanSpec, log);
                }
                break block3;
            }
            List classPathElements = (List)classpathOrder.reflectionUtils.getFieldVal(false, smartClassPath, "classPath");
            if (classPathElements == null || classPathElements.isEmpty()) break block3;
            for (Object classPathElement : classPathElements) {
                Collection<Object> subPaths = WebsphereLibertyClassLoaderHandler.getPaths(classPathElement, classpathOrder.reflectionUtils);
                for (Object path : subPaths) {
                    classpathOrder.addClasspathEntry(path, classLoader, scanSpec, log);
                }
            }
        }
    }
}

