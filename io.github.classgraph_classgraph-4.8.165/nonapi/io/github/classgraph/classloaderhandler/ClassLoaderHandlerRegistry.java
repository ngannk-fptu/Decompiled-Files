/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nonapi.io.github.classgraph.classloaderhandler.AntClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.ClassGraphClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.CxfContainerClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.EquinoxClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.EquinoxContextFinderClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.FallbackClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.FelixClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.JBossClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.JPMSClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.OSGiDefaultClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.ParentLastDelegationOrderTestClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.PlexusClassWorldsClassRealmClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.QuarkusClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.SpringBootRestartClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.TomcatWebappClassLoaderBaseHandler;
import nonapi.io.github.classgraph.classloaderhandler.URLClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.UnoOneJarClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WeblogicClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WebsphereLibertyClassLoaderHandler;
import nonapi.io.github.classgraph.classloaderhandler.WebsphereTraditionalClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassLoaderHandlerRegistry {
    public static final List<ClassLoaderHandlerRegistryEntry> CLASS_LOADER_HANDLERS = Collections.unmodifiableList(Arrays.asList(new ClassLoaderHandlerRegistryEntry(AntClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(EquinoxClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(EquinoxContextFinderClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(FelixClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(JBossClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(WeblogicClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(WebsphereLibertyClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(WebsphereTraditionalClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(OSGiDefaultClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(SpringBootRestartClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(TomcatWebappClassLoaderBaseHandler.class), new ClassLoaderHandlerRegistryEntry(CxfContainerClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(PlexusClassWorldsClassRealmClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(QuarkusClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(UnoOneJarClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(ParentLastDelegationOrderTestClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(JPMSClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(URLClassLoaderHandler.class), new ClassLoaderHandlerRegistryEntry(ClassGraphClassLoaderHandler.class)));
    public static final ClassLoaderHandlerRegistryEntry FALLBACK_HANDLER = new ClassLoaderHandlerRegistryEntry(FallbackClassLoaderHandler.class);
    public static final String[] AUTOMATIC_LIB_DIR_PREFIXES = new String[]{"BOOT-INF/lib/", "WEB-INF/lib/", "WEB-INF/lib-provided/", "META-INF/lib/", "lib/", "lib/ext/", "main/"};
    public static final String[] AUTOMATIC_PACKAGE_ROOT_PREFIXES = new String[]{"classes/", "test-classes/", "BOOT-INF/classes/", "WEB-INF/classes/"};

    private ClassLoaderHandlerRegistry() {
    }

    public static class ClassLoaderHandlerRegistryEntry {
        private final Method canHandleMethod;
        private final Method findClassLoaderOrderMethod;
        private final Method findClasspathOrderMethod;
        public final Class<? extends ClassLoaderHandler> classLoaderHandlerClass;

        private ClassLoaderHandlerRegistryEntry(Class<? extends ClassLoaderHandler> classLoaderHandlerClass) {
            this.classLoaderHandlerClass = classLoaderHandlerClass;
            try {
                this.canHandleMethod = classLoaderHandlerClass.getDeclaredMethod("canHandle", Class.class, LogNode.class);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not find canHandle method for " + classLoaderHandlerClass.getName(), e);
            }
            try {
                this.findClassLoaderOrderMethod = classLoaderHandlerClass.getDeclaredMethod("findClassLoaderOrder", ClassLoader.class, ClassLoaderOrder.class, LogNode.class);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not find findClassLoaderOrder method for " + classLoaderHandlerClass.getName(), e);
            }
            try {
                this.findClasspathOrderMethod = classLoaderHandlerClass.getDeclaredMethod("findClasspathOrder", ClassLoader.class, ClasspathOrder.class, ScanSpec.class, LogNode.class);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not find findClasspathOrder method for " + classLoaderHandlerClass.getName(), e);
            }
        }

        public boolean canHandle(Class<?> classLoader, LogNode log) {
            try {
                return (Boolean)this.canHandleMethod.invoke(null, classLoader, log);
            }
            catch (Throwable e) {
                throw new RuntimeException("Exception while calling canHandle for " + this.classLoaderHandlerClass.getName(), e);
            }
        }

        public void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
            try {
                this.findClassLoaderOrderMethod.invoke(null, classLoader, classLoaderOrder, log);
            }
            catch (Throwable e) {
                throw new RuntimeException("Exception while calling findClassLoaderOrder for " + this.classLoaderHandlerClass.getName(), e);
            }
        }

        public void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
            try {
                this.findClasspathOrderMethod.invoke(null, classLoader, classpathOrder, scanSpec, log);
            }
            catch (Throwable e) {
                throw new RuntimeException("Exception while calling findClassLoaderOrder for " + this.classLoaderHandlerClass.getName(), e);
            }
        }
    }
}

