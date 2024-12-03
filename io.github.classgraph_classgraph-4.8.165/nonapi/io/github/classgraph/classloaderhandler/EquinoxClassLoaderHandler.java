/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classloaderhandler;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class EquinoxClassLoaderHandler
implements ClassLoaderHandler {
    private static boolean alreadyReadSystemBundles;
    private static final String[] FIELD_NAMES;

    private EquinoxClassLoaderHandler() {
    }

    public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
        return "org.eclipse.osgi.internal.loader.EquinoxClassLoader".equals(classLoaderClass.getName());
    }

    public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
        classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
        classLoaderOrder.add(classLoader, log);
    }

    private static void addBundleFile(Object bundlefile, Set<Object> path, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
        if (bundlefile != null && path.add(bundlefile)) {
            Object baseFile = classpathOrderOut.reflectionUtils.getFieldVal(false, bundlefile, "basefile");
            if (baseFile != null) {
                boolean foundClassPathElement = false;
                for (String fieldName : FIELD_NAMES) {
                    Object baseBundleFile;
                    Object fieldVal = classpathOrderOut.reflectionUtils.getFieldVal(false, bundlefile, fieldName);
                    if (fieldVal == null) continue;
                    foundClassPathElement = true;
                    Object base = baseFile;
                    String sep = "/";
                    if (bundlefile.getClass().getName().equals("org.eclipse.osgi.storage.bundlefile.NestedDirBundleFile") && (baseBundleFile = classpathOrderOut.reflectionUtils.getFieldVal(false, bundlefile, "baseBundleFile")) != null && baseBundleFile.getClass().getName().equals("org.eclipse.osgi.storage.bundlefile.ZipBundleFile")) {
                        base = baseBundleFile;
                        sep = "!/";
                    }
                    String pathElement = base + sep + fieldVal;
                    classpathOrderOut.addClasspathEntry((Object)pathElement, classLoader, scanSpec, log);
                    break;
                }
                if (!foundClassPathElement) {
                    classpathOrderOut.addClasspathEntry((Object)baseFile.toString(), classLoader, scanSpec, log);
                }
            }
            EquinoxClassLoaderHandler.addBundleFile(classpathOrderOut.reflectionUtils.getFieldVal(false, bundlefile, "wrapped"), path, classLoader, classpathOrderOut, scanSpec, log);
            EquinoxClassLoaderHandler.addBundleFile(classpathOrderOut.reflectionUtils.getFieldVal(false, bundlefile, "next"), path, classLoader, classpathOrderOut, scanSpec, log);
        }
    }

    private static void addClasspathEntries(Object owner, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
        Object entries = classpathOrderOut.reflectionUtils.getFieldVal(false, owner, "entries");
        if (entries != null) {
            int n = Array.getLength(entries);
            for (int i = 0; i < n; ++i) {
                Object entry = Array.get(entries, i);
                Object bundlefile = classpathOrderOut.reflectionUtils.getFieldVal(false, entry, "bundlefile");
                EquinoxClassLoaderHandler.addBundleFile(bundlefile, new HashSet<Object>(), classLoader, classpathOrderOut, scanSpec, log);
            }
        }
    }

    public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
        Object manager = classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "manager");
        EquinoxClassLoaderHandler.addClasspathEntries(manager, classLoader, classpathOrder, scanSpec, log);
        Object fragments = classpathOrder.reflectionUtils.getFieldVal(false, manager, "fragments");
        if (fragments != null) {
            int fragLength = Array.getLength(fragments);
            for (int f = 0; f < fragLength; ++f) {
                Object fragment = Array.get(fragments, f);
                EquinoxClassLoaderHandler.addClasspathEntries(fragment, classLoader, classpathOrder, scanSpec, log);
            }
        }
        if (!alreadyReadSystemBundles) {
            Object delegate = classpathOrder.reflectionUtils.getFieldVal(false, (Object)classLoader, "delegate");
            Object container = classpathOrder.reflectionUtils.getFieldVal(false, delegate, "container");
            Object storage = classpathOrder.reflectionUtils.getFieldVal(false, container, "storage");
            Object moduleContainer = classpathOrder.reflectionUtils.getFieldVal(false, storage, "moduleContainer");
            Object moduleDatabase = classpathOrder.reflectionUtils.getFieldVal(false, moduleContainer, "moduleDatabase");
            Object modulesById = classpathOrder.reflectionUtils.getFieldVal(false, moduleDatabase, "modulesById");
            Object module0 = classpathOrder.reflectionUtils.invokeMethod(false, modulesById, "get", Object.class, 0L);
            Object bundle = classpathOrder.reflectionUtils.invokeMethod(false, module0, "getBundle");
            Object bundleContext = classpathOrder.reflectionUtils.invokeMethod(false, bundle, "getBundleContext");
            Object bundles = classpathOrder.reflectionUtils.invokeMethod(false, bundleContext, "getBundles");
            if (bundles != null) {
                int n = Array.getLength(bundles);
                for (int i = 0; i < n; ++i) {
                    int fileIdx;
                    Object equinoxBundle = Array.get(bundles, i);
                    Object module = classpathOrder.reflectionUtils.getFieldVal(false, equinoxBundle, "module");
                    String location = (String)classpathOrder.reflectionUtils.getFieldVal(false, module, "location");
                    if (location == null || (fileIdx = location.indexOf("file:")) < 0) continue;
                    location = location.substring(fileIdx);
                    classpathOrder.addClasspathEntry((Object)location, classLoader, scanSpec, log);
                }
            }
            alreadyReadSystemBundles = true;
        }
    }

    static {
        FIELD_NAMES = new String[]{"cp", "nestedDirName"};
    }
}

