/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.util.SortedSet;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.URLClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class PlexusClassWorldsClassRealmClassLoaderHandler
implements ClassLoaderHandler {
    private PlexusClassWorldsClassRealmClassLoaderHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return "org.codehaus.plexus.classworlds.realm.ClassRealm".equals(classLoaderClass.getName());
    }

    private static boolean isParentFirstStrategy(ClassLoader classRealmInstance, ReflectionUtils reflectionUtils) {
        String strategyClassName;
        Object strategy = reflectionUtils.getFieldVal(false, (Object)classRealmInstance, "strategy");
        return strategy == null || !(strategyClassName = strategy.getClass().getName()).equals("org.codehaus.plexus.classworlds.strategy.SelfFirstStrategy") && !strategyClassName.equals("org.codehaus.plexus.classworlds.strategy.OsgiBundleStrategy");
    }

    public static void findClassLoaderOrder(ClassLoader classRealm, ClassLoaderOrder classLoaderOrder, LogNode log) {
        boolean isParentFirst;
        Object foreignImports = classLoaderOrder.reflectionUtils.getFieldVal(false, (Object)classRealm, "foreignImports");
        if (foreignImports != null) {
            SortedSet foreignImportEntries = (SortedSet)foreignImports;
            for (Object entry : foreignImportEntries) {
                ClassLoader foreignImportClassLoader = (ClassLoader)classLoaderOrder.reflectionUtils.invokeMethod(false, entry, "getClassLoader");
                classLoaderOrder.delegateTo(foreignImportClassLoader, true, log);
            }
        }
        if (!(isParentFirst = PlexusClassWorldsClassRealmClassLoaderHandler.isParentFirstStrategy(classRealm, classLoaderOrder.reflectionUtils))) {
            classLoaderOrder.add(classRealm, log);
        }
        ClassLoader parentClassLoader = (ClassLoader)classLoaderOrder.reflectionUtils.invokeMethod(false, classRealm, "getParentClassLoader");
        classLoaderOrder.delegateTo(parentClassLoader, true, log);
        classLoaderOrder.delegateTo(classRealm.getParent(), true, log);
        if (isParentFirst) {
            classLoaderOrder.add(classRealm, log);
        }
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        URLClassLoaderHandler.findClasspathOrder(classLoader, classpathOrder, scanSpec, log);
    }
}

