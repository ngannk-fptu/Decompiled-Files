/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ClassGraphClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.classpath.CallStackReader;
import nonapi.io.github.classgraph.classpath.ClassLoaderFinder;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.classpath.ModuleFinder;
import nonapi.io.github.classgraph.classpath.SystemJarFinder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClasspathFinder {
    private final ClasspathOrder classpathOrder;
    private final ModuleFinder moduleFinder;
    private ClassLoader[] classLoaderOrderRespectingParentDelegation;
    private ClassGraphClassLoader delegateClassGraphClassLoader;

    public ClasspathOrder getClasspathOrder() {
        return this.classpathOrder;
    }

    public ModuleFinder getModuleFinder() {
        return this.moduleFinder;
    }

    public ClassLoader[] getClassLoaderOrderRespectingParentDelegation() {
        return this.classLoaderOrderRespectingParentDelegation;
    }

    public ClassGraphClassLoader getDelegateClassGraphClassLoader() {
        return this.delegateClassGraphClassLoader;
    }

    public ClasspathFinder(ScanSpec scanSpec, ReflectionUtils reflectionUtils, LogNode log) {
        String[] pathElements;
        ClassLoader defaultClassLoader;
        boolean scanNonSystemModules;
        LogNode classpathFinderLog = log == null ? null : log.log("Finding classpath and modules");
        boolean forceScanJavaClassPath = false;
        if (scanSpec.overrideClasspath != null) {
            scanNonSystemModules = false;
        } else if (scanSpec.overrideClassLoaders != null) {
            scanNonSystemModules = false;
            for (ClassLoader classLoader : scanSpec.overrideClassLoaders) {
                String classLoaderClassName = classLoader.getClass().getName();
                if (!classLoaderClassName.equals("jdk.internal.loader.ClassLoaders$AppClassLoader") && !classLoaderClassName.equals("jdk.internal.loader.ClassLoaders$PlatformClassLoader")) continue;
                if (!scanSpec.enableSystemJarsAndModules) {
                    if (classpathFinderLog != null) {
                        classpathFinderLog.log("overrideClassLoaders() was called with an instance of " + classLoaderClassName + ", which is a system classloader, so enableSystemJarsAndModules() was called automatically");
                    }
                    scanSpec.enableSystemJarsAndModules = true;
                }
                forceScanJavaClassPath = true;
                if (classpathFinderLog == null) continue;
                classpathFinderLog.log("overrideClassLoaders() was called with an instance of " + classLoaderClassName + ", which is a system classloader, so the `java.lang.path` classpath will also be scanned");
            }
        } else {
            scanNonSystemModules = scanSpec.scanModules;
        }
        this.moduleFinder = scanNonSystemModules || scanSpec.enableSystemJarsAndModules ? new ModuleFinder(new CallStackReader(reflectionUtils).getClassContext(classpathFinderLog), scanSpec, scanNonSystemModules, scanSpec.enableSystemJarsAndModules, reflectionUtils, classpathFinderLog) : null;
        this.classpathOrder = new ClasspathOrder(scanSpec, reflectionUtils);
        ClassLoaderFinder classLoaderFinder = scanSpec.overrideClasspath == null && scanSpec.overrideClassLoaders == null ? new ClassLoaderFinder(scanSpec, reflectionUtils, classpathFinderLog) : null;
        ClassLoader[] contextClassLoaders = classLoaderFinder == null ? new ClassLoader[]{} : classLoaderFinder.getContextClassLoaders();
        ClassLoader classLoader = defaultClassLoader = contextClassLoaders.length > 0 ? contextClassLoaders[0] : null;
        if (scanSpec.overrideClasspath != null) {
            if (scanSpec.overrideClassLoaders != null && classpathFinderLog != null) {
                classpathFinderLog.log("It is not possible to override both the classpath and the ClassLoaders -- ignoring the ClassLoader override");
            }
            LogNode overrideLog = classpathFinderLog == null ? null : classpathFinderLog.log("Overriding classpath with: " + scanSpec.overrideClasspath);
            this.classpathOrder.addClasspathEntries(scanSpec.overrideClasspath, defaultClassLoader, scanSpec, overrideLog);
            if (overrideLog != null) {
                overrideLog.log("WARNING: when the classpath is overridden, there is no guarantee that the classes found by classpath scanning will be the same as the classes loaded by the context classloader");
            }
            this.classLoaderOrderRespectingParentDelegation = contextClassLoaders;
        }
        if (scanSpec.enableSystemJarsAndModules) {
            Object systemJarsLog;
            String jreRtJar = SystemJarFinder.getJreRtJarPath();
            Object object = systemJarsLog = classpathFinderLog == null ? null : classpathFinderLog.log("System jars:");
            if (jreRtJar != null) {
                if (scanSpec.enableSystemJarsAndModules) {
                    this.classpathOrder.addSystemClasspathEntry(jreRtJar, defaultClassLoader);
                    if (systemJarsLog != null) {
                        ((LogNode)systemJarsLog).log("Found rt.jar: " + jreRtJar);
                    }
                } else if (systemJarsLog != null) {
                    ((LogNode)systemJarsLog).log((scanSpec.enableSystemJarsAndModules ? "" : "Scanning disabled for rt.jar: ") + jreRtJar);
                }
            }
            boolean scanAllLibOrExtJars = !scanSpec.libOrExtJarAcceptReject.acceptAndRejectAreEmpty();
            for (String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
                if (scanAllLibOrExtJars || scanSpec.libOrExtJarAcceptReject.isSpecificallyAcceptedAndNotRejected(libOrExtJarPath)) {
                    this.classpathOrder.addSystemClasspathEntry(libOrExtJarPath, defaultClassLoader);
                    if (systemJarsLog == null) continue;
                    ((LogNode)systemJarsLog).log("Found lib or ext jar: " + libOrExtJarPath);
                    continue;
                }
                if (systemJarsLog == null) continue;
                ((LogNode)systemJarsLog).log("Scanning disabled for lib or ext jar: " + libOrExtJarPath);
            }
        }
        if (scanSpec.overrideClasspath == null) {
            ClassLoader[] origClassLoaderOrder;
            if (classpathFinderLog != null) {
                LogNode classLoaderHandlerLog = classpathFinderLog.log("ClassLoaderHandlers:");
                for (ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry classLoaderHandlerEntry : ClassLoaderHandlerRegistry.CLASS_LOADER_HANDLERS) {
                    classLoaderHandlerLog.log(classLoaderHandlerEntry.classLoaderHandlerClass.getName());
                }
            }
            LogNode classloaderOrderLog = classpathFinderLog == null ? null : classpathFinderLog.log("Finding unique classloaders in delegation order");
            ClassLoaderOrder classLoaderOrder = new ClassLoaderOrder(reflectionUtils);
            ClassLoader[] classLoaderArray = origClassLoaderOrder = scanSpec.overrideClassLoaders != null ? scanSpec.overrideClassLoaders.toArray(new ClassLoader[0]) : contextClassLoaders;
            if (origClassLoaderOrder != null) {
                for (ClassLoader classLoader2 : origClassLoaderOrder) {
                    classLoaderOrder.delegateTo(classLoader2, false, classloaderOrderLog);
                }
            }
            Set<ClassLoader> allParentClassLoaders = classLoaderOrder.getAllParentClassLoaders();
            LogNode classloaderURLLog = classpathFinderLog == null ? null : classpathFinderLog.log("Obtaining URLs from classloaders in delegation order");
            ArrayList<ClassLoader> finalClassLoaderOrder = new ArrayList<ClassLoader>();
            for (Map.Entry entry : classLoaderOrder.getClassLoaderOrder()) {
                ClassLoader classLoader3 = (ClassLoader)entry.getKey();
                ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry classLoaderHandlerRegistryEntry = (ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry)entry.getValue();
                if (!scanSpec.ignoreParentClassLoaders || !allParentClassLoaders.contains(classLoader3)) {
                    LogNode classloaderHandlerLog = classloaderURLLog == null ? null : classloaderURLLog.log("Classloader " + classLoader3.getClass().getName() + " is handled by " + classLoaderHandlerRegistryEntry.classLoaderHandlerClass.getName());
                    classLoaderHandlerRegistryEntry.findClasspathOrder(classLoader3, this.classpathOrder, scanSpec, classloaderHandlerLog);
                    finalClassLoaderOrder.add(classLoader3);
                } else if (classloaderURLLog != null) {
                    classloaderURLLog.log("Ignoring parent classloader " + classLoader3 + ", normally handled by " + classLoaderHandlerRegistryEntry.classLoaderHandlerClass.getName());
                }
                if (!(classLoader3 instanceof ClassGraphClassLoader)) continue;
                this.delegateClassGraphClassLoader = (ClassGraphClassLoader)classLoader3;
            }
            this.classLoaderOrderRespectingParentDelegation = finalClassLoaderOrder.toArray(new ClassLoader[0]);
        }
        if ((!scanSpec.ignoreParentClassLoaders && (scanSpec.overrideClassLoaders == null || forceScanJavaClassPath) && scanSpec.overrideClasspath == null || this.moduleFinder != null && this.moduleFinder.forceScanJavaClassPath()) && (pathElements = JarUtils.smartPathSplit(System.getProperty("java.class.path"), scanSpec)).length > 0) {
            LogNode sysPropLog = classpathFinderLog == null ? null : classpathFinderLog.log("Getting classpath entries from java.class.path");
            for (String pathElement : pathElements) {
                String pathElementResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElement);
                this.classpathOrder.addClasspathEntry((Object)pathElementResolved, defaultClassLoader, scanSpec, sysPropLog);
            }
        }
    }
}

