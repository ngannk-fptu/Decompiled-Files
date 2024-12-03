/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class JBossClassLoaderHandler
implements ClassLoaderHandler {
    private JBossClassLoaderHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return "org.jboss.modules.ModuleClassLoader".equals(classLoaderClass.getName());
    }

    public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
        classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        classLoaderOrder.add(classLoader, log);
    }

    private static void handleResourceLoader(Object resourceLoader, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
        File file;
        if (resourceLoader == null) {
            return;
        }
        Object root = classpathOrderOut.reflectionUtils.getFieldVal(false, resourceLoader, "root");
        File physicalFile = (File)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getPhysicalFile");
        String path = null;
        if (physicalFile != null) {
            File file2;
            String name = (String)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getName");
            path = name != null ? (FileUtils.canRead(file2 = new File(physicalFile.getParentFile(), name)) ? file2.getAbsolutePath() : physicalFile.getAbsolutePath()) : physicalFile.getAbsolutePath();
        } else {
            path = (String)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getPathName");
            if (path == null) {
                File file3 = root instanceof Path ? ((Path)root).toFile() : (file = root instanceof File ? (File)root : null);
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            }
        }
        if (path == null && (file = (File)classpathOrderOut.reflectionUtils.getFieldVal(false, resourceLoader, "fileOfJar")) != null) {
            path = file.getAbsolutePath();
        }
        if (path != null) {
            classpathOrderOut.addClasspathEntry((Object)path, classLoader, scanSpec, log);
        } else if (log != null) {
            log.log("Could not determine classpath for ResourceLoader: " + resourceLoader);
        }
    }

    private static void handleRealModule(Object module, Set<Object> visitedModules, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
        Object vfsResourceLoaders;
        if (!visitedModules.add(module)) {
            return;
        }
        ClassLoader moduleLoader = (ClassLoader)classpathOrderOut.reflectionUtils.invokeMethod(false, module, "getClassLoader");
        if (moduleLoader == null) {
            moduleLoader = classLoader;
        }
        if ((vfsResourceLoaders = classpathOrderOut.reflectionUtils.invokeMethod(false, moduleLoader, "getResourceLoaders")) != null) {
            int n = Array.getLength(vfsResourceLoaders);
            for (int i = 0; i < n; ++i) {
                Object resourceLoader = Array.get(vfsResourceLoaders, i);
                JBossClassLoaderHandler.handleResourceLoader(resourceLoader, moduleLoader, classpathOrderOut, scanSpec, log);
            }
        }
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        Object module = classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getModule");
        Object callerModuleLoader = classpathOrder.reflectionUtils.invokeMethod(false, module, "getCallerModuleLoader");
        HashSet<Object> visitedModules = new HashSet<Object>();
        Map moduleMap = (Map)classpathOrder.reflectionUtils.getFieldVal(false, callerModuleLoader, "moduleMap");
        Set<Object> moduleMapEntries = moduleMap != null ? moduleMap.entrySet() : Collections.emptySet();
        for (Map.Entry entry : moduleMapEntries) {
            Object val = entry.getValue();
            Object realModule = classpathOrder.reflectionUtils.invokeMethod(false, val, "getModule");
            JBossClassLoaderHandler.handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
        }
        Map pathsMap = (Map)classpathOrder.reflectionUtils.invokeMethod(false, module, "getPaths");
        for (Map.Entry ent : pathsMap.entrySet()) {
            for (Object localLoader : (List)ent.getValue()) {
                Object moduleClassLoader = classpathOrder.reflectionUtils.getFieldVal(false, localLoader, "this$0");
                Object realModule = classpathOrder.reflectionUtils.getFieldVal(false, moduleClassLoader, "module");
                JBossClassLoaderHandler.handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
            }
        }
    }
}

