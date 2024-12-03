/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.io.IOError;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class QuarkusClassLoaderHandler
implements ClassLoaderHandler {
    private static final String RUNTIME_CLASSLOADER = "io.quarkus.runner.RuntimeClassLoader";
    private static final String QUARKUS_CLASSLOADER = "io.quarkus.bootstrap.classloading.QuarkusClassLoader";
    private static final String RUNNER_CLASSLOADER = "io.quarkus.bootstrap.runner.RunnerClassLoader";

    private QuarkusClassLoaderHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return RUNTIME_CLASSLOADER.equals(classLoaderClass.getName()) || QUARKUS_CLASSLOADER.equals(classLoaderClass.getName()) || RUNNER_CLASSLOADER.equals(classLoaderClass.getName());
    }

    public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
        classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        classLoaderOrder.add(classLoader, log);
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        String classLoaderName = classLoader.getClass().getName();
        if (RUNTIME_CLASSLOADER.equals(classLoaderName)) {
            QuarkusClassLoaderHandler.findClasspathOrderForRuntimeClassloader(classLoader, classpathOrder, scanSpec, log);
        } else if (QUARKUS_CLASSLOADER.equals(classLoaderName)) {
            QuarkusClassLoaderHandler.findClasspathOrderForQuarkusClassloader(classLoader, classpathOrder, scanSpec, log);
        } else if (RUNNER_CLASSLOADER.equals(classLoaderName)) {
            QuarkusClassLoaderHandler.findClasspathOrderForRunnerClassloader(classLoader, classpathOrder, scanSpec, log);
        }
    }

    private static void findClasspathOrderForQuarkusClassloader(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        for (Object element : (Collection)classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "elements")) {
            String elementClassName = element.getClass().getName();
            if ("io.quarkus.bootstrap.classloading.JarClassPathElement".equals(elementClassName)) {
                classpathOrder.addClasspathEntry(classpathOrder.reflectionUtils.getFieldVal(false, element, "file"), classLoader, scanSpec, log);
                continue;
            }
            if ("io.quarkus.bootstrap.classloading.DirectoryClassPathElement".equals(elementClassName)) {
                classpathOrder.addClasspathEntry(classpathOrder.reflectionUtils.getFieldVal(false, element, "root"), classLoader, scanSpec, log);
                continue;
            }
            Object rootPath = classpathOrder.reflectionUtils.invokeMethod(false, element, "getRoot");
            if (!(rootPath instanceof Path)) continue;
            classpathOrder.addClasspathEntry(rootPath, classLoader, scanSpec, log);
        }
    }

    private static void findClasspathOrderForRuntimeClassloader(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        Collection applicationClassDirectories = (Collection)classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "applicationClassDirectories");
        if (applicationClassDirectories != null) {
            for (Path path : applicationClassDirectories) {
                try {
                    URI uri = path.toUri();
                    classpathOrder.addClasspathEntryObject(uri, classLoader, scanSpec, log);
                }
                catch (IOError | SecurityException e) {
                    if (log == null) continue;
                    log.log("Could not convert path to URI: " + path);
                }
            }
        }
    }

    private static void findClasspathOrderForRunnerClassloader(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        Iterator iterator = ((Map)classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "resourceDirectoryMap")).values().iterator();
        while (iterator.hasNext()) {
            Object[] elementArray;
            for (Object element : elementArray = (Object[])iterator.next()) {
                String elementClassName = element.getClass().getName();
                if (!"io.quarkus.bootstrap.runner.JarResource".equals(elementClassName)) continue;
                classpathOrder.addClasspathEntry(classpathOrder.reflectionUtils.getFieldVal(false, element, "jarPath"), classLoader, scanSpec, log);
            }
        }
    }
}

