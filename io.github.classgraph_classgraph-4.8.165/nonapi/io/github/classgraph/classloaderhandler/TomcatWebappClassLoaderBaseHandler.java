/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.List;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class TomcatWebappClassLoaderBaseHandler
implements ClassLoaderHandler {
    private TomcatWebappClassLoaderBaseHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return "org.apache.catalina.loader.WebappClassLoaderBase".equals(classLoaderClass.getName());
    }

    private static boolean isParentFirst(ClassLoader classLoader, ReflectionUtils reflectionUtils) {
        Object delegateObject = reflectionUtils.getFieldVal(false, (Object)classLoader, "delegate");
        if (delegateObject != null) {
            return (Boolean)delegateObject;
        }
        return true;
    }

    public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
        boolean isParentFirst = TomcatWebappClassLoaderBaseHandler.isParentFirst(classLoader, classLoaderOrder.reflectionUtils);
        if (isParentFirst) {
            classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        }
        if ("org.apache.tomee.catalina.TomEEWebappClassLoader".equals(classLoader.getClass().getName())) {
            try {
                classLoaderOrder.delegateTo(Class.forName("org.apache.openejb.OpenEJB").getClassLoader(), true, log);
            }
            catch (ClassNotFoundException | LinkageError throwable) {
                // empty catch block
            }
        }
        classLoaderOrder.add(classLoader, log);
        if (!isParentFirst) {
            classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        }
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        Object resources = classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getResources");
        Object baseURLs = classpathOrder.reflectionUtils.invokeMethod(false, resources, "getBaseUrls");
        classpathOrder.addClasspathEntryObject(baseURLs, classLoader, scanSpec, log);
        List allResources = (List)classpathOrder.reflectionUtils.getFieldVal(false, resources, "allResources");
        if (allResources != null) {
            for (List webResourceSetList : allResources) {
                for (Object webResourceSet : webResourceSetList) {
                    String className;
                    String base;
                    if (webResourceSet == null) continue;
                    File file = (File)classpathOrder.reflectionUtils.invokeMethod(false, webResourceSet, "getFileBase");
                    String string = base = file == null ? null : file.getPath();
                    if (base == null) {
                        base = (String)classpathOrder.reflectionUtils.invokeMethod(false, webResourceSet, "getBase");
                    }
                    if (base == null) {
                        base = (String)classpathOrder.reflectionUtils.invokeMethod(false, webResourceSet, "getBaseUrlString");
                    }
                    if (base == null) continue;
                    String archivePath = (String)classpathOrder.reflectionUtils.getFieldVal(false, webResourceSet, "archivePath");
                    if (archivePath != null && !archivePath.isEmpty()) {
                        base = base + "!" + (archivePath.startsWith("/") ? archivePath : "/" + archivePath);
                    }
                    boolean isJar = (className = webResourceSet.getClass().getName()).equals("java.org.apache.catalina.webresources.JarResourceSet") || className.equals("java.org.apache.catalina.webresources.JarWarResourceSet");
                    String internalPath = (String)classpathOrder.reflectionUtils.invokeMethod(false, webResourceSet, "getInternalPath");
                    if (internalPath != null && !internalPath.isEmpty() && !internalPath.equals("/")) {
                        classpathOrder.addClasspathEntryObject(base + (isJar ? "!" : "") + (internalPath.startsWith("/") ? internalPath : "/" + internalPath), classLoader, scanSpec, log);
                        continue;
                    }
                    classpathOrder.addClasspathEntryObject(base, classLoader, scanSpec, log);
                }
            }
        }
        Object urls = classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getURLs");
        classpathOrder.addClasspathEntryObject(urls, classLoader, scanSpec, log);
    }
}

