/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOError;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.classpath.SystemJarFinder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClasspathOrder {
    private final ScanSpec scanSpec;
    public ReflectionUtils reflectionUtils;
    private final Set<String> classpathEntryUniqueResolvedPaths = new HashSet<String>();
    private final List<ClasspathEntry> order = new ArrayList<ClasspathEntry>();
    private static final List<String> AUTOMATIC_PACKAGE_ROOT_SUFFIXES = new ArrayList<String>();
    private static final Pattern schemeMatcher = Pattern.compile("^[a-zA-Z][a-zA-Z+\\-.]+:");

    ClasspathOrder(ScanSpec scanSpec, ReflectionUtils reflectionUtils) {
        this.scanSpec = scanSpec;
        this.reflectionUtils = reflectionUtils;
    }

    public List<ClasspathEntry> getOrder() {
        return this.order;
    }

    public Set<String> getClasspathEntryUniqueResolvedPaths() {
        return this.classpathEntryUniqueResolvedPaths;
    }

    private boolean filter(URL classpathElementURL, String classpathElementPath) {
        if (this.scanSpec.classpathElementFilters != null) {
            for (Object filterObj : this.scanSpec.classpathElementFilters) {
                if ((classpathElementURL == null || !(filterObj instanceof ClassGraph.ClasspathElementURLFilter) || ((ClassGraph.ClasspathElementURLFilter)filterObj).includeClasspathElement(classpathElementURL)) && (classpathElementPath == null || !(filterObj instanceof ClassGraph.ClasspathElementFilter) || ((ClassGraph.ClasspathElementFilter)filterObj).includeClasspathElement(classpathElementPath))) continue;
                return false;
            }
        }
        return true;
    }

    boolean addSystemClasspathEntry(String pathEntry, ClassLoader classLoader) {
        if (this.classpathEntryUniqueResolvedPaths.add(pathEntry)) {
            this.order.add(new ClasspathEntry(pathEntry, classLoader));
            return true;
        }
        return false;
    }

    private boolean addClasspathEntry(Object pathElement, String pathElementStr, ClassLoader classLoader, ScanSpec scanSpec) {
        String pathElementStrWithoutSuffix = pathElementStr;
        boolean hasSuffix = false;
        for (String suffix : AUTOMATIC_PACKAGE_ROOT_SUFFIXES) {
            if (!pathElementStr.endsWith(suffix)) continue;
            pathElementStrWithoutSuffix = pathElementStr.substring(0, pathElementStr.length() - suffix.length());
            hasSuffix = true;
            break;
        }
        if (pathElement instanceof URL || pathElement instanceof URI || pathElement instanceof Path || pathElement instanceof File) {
            Object pathElementWithoutSuffix = pathElement;
            if (hasSuffix) {
                try {
                    pathElementWithoutSuffix = pathElement instanceof URL ? new URL(pathElementStrWithoutSuffix) : (pathElement instanceof URI ? new URI(pathElementStrWithoutSuffix) : (pathElement instanceof Path ? Paths.get(pathElementStrWithoutSuffix, new String[0]) : pathElementStrWithoutSuffix));
                }
                catch (MalformedURLException | URISyntaxException | InvalidPathException e) {
                    try {
                        pathElementWithoutSuffix = pathElement instanceof URL ? new URL("file:" + pathElementStrWithoutSuffix) : (pathElement instanceof URI ? new URI("file:" + pathElementStrWithoutSuffix) : pathElementStrWithoutSuffix);
                    }
                    catch (MalformedURLException | URISyntaxException | InvalidPathException e2) {
                        return false;
                    }
                }
            }
            if (this.classpathEntryUniqueResolvedPaths.add(pathElementStrWithoutSuffix)) {
                this.order.add(new ClasspathEntry(pathElementWithoutSuffix, classLoader));
                return true;
            }
        } else {
            String pathElementStrResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStrWithoutSuffix);
            if (scanSpec.overrideClasspath == null && (SystemJarFinder.getJreLibOrExtJars().contains(pathElementStrResolved) || pathElementStrResolved.equals(SystemJarFinder.getJreRtJarPath()))) {
                return false;
            }
            if (this.classpathEntryUniqueResolvedPaths.add(pathElementStrResolved)) {
                this.order.add(new ClasspathEntry(pathElementStrResolved, classLoader));
                return true;
            }
        }
        return false;
    }

    public boolean addClasspathEntry(Object pathElement, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
        String pathElementStr;
        if (pathElement == null) {
            return false;
        }
        if (pathElement instanceof Path) {
            try {
                pathElementStr = ((Path)pathElement).toUri().toString();
                if (pathElementStr.startsWith("file:///")) {
                    pathElementStr = ((Path)pathElement).toFile().toString();
                }
            }
            catch (IOError | SecurityException e) {
                pathElementStr = pathElement.toString();
            }
        } else {
            pathElementStr = pathElement.toString();
        }
        if ((pathElementStr = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStr)).isEmpty()) {
            return false;
        }
        URL pathElementURL = null;
        boolean hasWildcardSuffix = false;
        if (pathElementStr.endsWith("/*") || pathElementStr.endsWith("\\*")) {
            hasWildcardSuffix = true;
            pathElementStr = pathElementStr.substring(0, pathElementStr.length() - 2);
        } else if (pathElementStr.equals("*")) {
            hasWildcardSuffix = true;
            pathElementStr = "";
        } else {
            Matcher m1 = schemeMatcher.matcher(pathElementStr);
            if (m1.find()) {
                try {
                    pathElementURL = pathElement instanceof URL ? (URL)pathElement : (pathElement instanceof URI ? ((URI)pathElement).toURL() : (pathElement instanceof Path ? ((Path)pathElement).toUri().toURL() : (pathElement instanceof File ? ((File)pathElement).toURI().toURL() : null)));
                }
                catch (IOError | IllegalArgumentException | SecurityException | MalformedURLException throwable) {
                    // empty catch block
                }
                if (pathElementURL == null) {
                    String urlStr = pathElementStr.replace("%", "%25");
                    try {
                        pathElementURL = new URL(urlStr);
                    }
                    catch (MalformedURLException e) {
                        try {
                            pathElementURL = new File(urlStr).toURI().toURL();
                        }
                        catch (IOError | IllegalArgumentException | SecurityException | MalformedURLException e1) {
                            try {
                                pathElementURL = new URL(pathElementStr);
                            }
                            catch (MalformedURLException malformedURLException) {
                                // empty catch block
                            }
                        }
                    }
                }
                if (pathElementURL == null && log != null) {
                    log.log("Failed to convert classpath element to URL: " + pathElement);
                }
            }
        }
        if (pathElementURL != null || pathElement instanceof URI || pathElement instanceof File || pathElement instanceof Path) {
            String classpathElementObj;
            if (!this.filter(pathElementURL, pathElementStr)) {
                if (log != null) {
                    log.log("Classpath element did not match filter criterion, skipping: " + pathElementStr);
                }
                return false;
            }
            String string = pathElement instanceof File ? pathElementStr : (classpathElementObj = pathElementURL != null ? pathElementURL : pathElement);
            if (this.addClasspathEntry((Object)classpathElementObj, pathElementStr, classLoader, scanSpec)) {
                if (log != null) {
                    log.log("Found classpath element: " + pathElementStr);
                }
                return true;
            }
            if (log != null) {
                log.log("Ignoring duplicate classpath element: " + pathElementStr);
            }
            return false;
        }
        if (hasWildcardSuffix) {
            String baseDirPath = pathElementStr;
            String baseDirPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), baseDirPath);
            if (!this.filter(pathElementURL, baseDirPath) || !baseDirPathResolved.equals(baseDirPath) && !this.filter(pathElementURL, baseDirPathResolved)) {
                if (log != null) {
                    log.log("Classpath element did not match filter criterion, skipping: " + pathElementStr);
                }
                return false;
            }
            File baseDir = new File(baseDirPathResolved);
            if (!baseDir.exists()) {
                if (log != null) {
                    log.log("Directory does not exist for wildcard classpath element: " + pathElementStr);
                }
                return false;
            }
            if (!FileUtils.canRead(baseDir)) {
                if (log != null) {
                    log.log("Cannot read directory for wildcard classpath element: " + pathElementStr);
                }
                return false;
            }
            if (!baseDir.isDirectory()) {
                if (log != null) {
                    log.log("Wildcard is appended to something other than a directory: " + pathElementStr);
                }
                return false;
            }
            LogNode dirLog = log == null ? null : log.log("Adding classpath elements from wildcarded directory: " + pathElementStr);
            File[] baseDirFiles = baseDir.listFiles();
            if (baseDirFiles != null) {
                for (File fileInDir : baseDirFiles) {
                    String name = fileInDir.getName();
                    if (name.equals(".") || name.equals("..")) continue;
                    String fileInDirPath = fileInDir.getPath();
                    String fileInDirPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), fileInDirPath);
                    if (this.addClasspathEntry((Object)fileInDirPathResolved, fileInDirPathResolved, classLoader, scanSpec)) {
                        if (dirLog == null) continue;
                        dirLog.log("Found classpath element: " + fileInDirPath + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved));
                        continue;
                    }
                    if (dirLog == null) continue;
                    dirLog.log("Ignoring duplicate classpath element: " + fileInDirPath + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved));
                }
                return true;
            }
            return false;
        }
        if (pathElementStr.indexOf(42) >= 0) {
            if (log != null) {
                log.log("Wildcard classpath elements can only end with a suffix of \"/*\", can't use globs elsewhere in the path: " + pathElementStr);
            }
            return false;
        }
        String pathElementResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStr);
        if (!this.filter(pathElementURL, pathElementStr) || !pathElementResolved.equals(pathElementStr) && !this.filter(pathElementURL, pathElementResolved)) {
            if (log != null) {
                log.log("Classpath element did not match filter criterion, skipping: " + pathElementStr + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
            }
            return false;
        }
        if (pathElementResolved.startsWith("//")) {
            try {
                File file = new File(pathElementResolved);
                if (this.addClasspathEntry((Object)file, pathElementResolved, classLoader, scanSpec)) {
                    if (log != null) {
                        log.log("Found classpath element: " + file + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
                    }
                    return true;
                }
                if (log != null) {
                    log.log("Ignoring duplicate classpath element: " + pathElementStr + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
                }
                return false;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.addClasspathEntry((Object)pathElementResolved, pathElementResolved, classLoader, scanSpec)) {
            if (log != null) {
                log.log("Found classpath element: " + pathElementStr + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
            }
            return true;
        }
        if (log != null) {
            log.log("Ignoring duplicate classpath element: " + pathElementStr + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
        }
        return false;
    }

    public boolean addClasspathEntries(List<Object> overrideClasspath, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
        if (overrideClasspath == null || overrideClasspath.isEmpty()) {
            return false;
        }
        for (Object pathElement : overrideClasspath) {
            this.addClasspathEntry(pathElement, classLoader, scanSpec, log);
        }
        return true;
    }

    public boolean addClasspathPathStr(String pathStr, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
        if (pathStr == null || pathStr.isEmpty()) {
            return false;
        }
        String[] parts = JarUtils.smartPathSplit(pathStr, scanSpec);
        if (parts.length == 0) {
            return false;
        }
        for (String pathElement : parts) {
            this.addClasspathEntry((Object)pathElement, classLoader, scanSpec, log);
        }
        return true;
    }

    public boolean addClasspathEntryObject(Object pathObject, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
        boolean valid = false;
        if (pathObject != null) {
            if (pathObject instanceof URL || pathObject instanceof URI || pathObject instanceof Path || pathObject instanceof File) {
                valid |= this.addClasspathEntry(pathObject, classLoader, scanSpec, log);
            } else if (pathObject instanceof Iterable) {
                for (Object elt : (Iterable)pathObject) {
                    valid |= this.addClasspathEntryObject(elt, classLoader, scanSpec, log);
                }
            } else {
                Class<?> valClass = pathObject.getClass();
                if (valClass.isArray()) {
                    int n = Array.getLength(pathObject);
                    for (int j = 0; j < n; ++j) {
                        Object elt = Array.get(pathObject, j);
                        valid |= this.addClasspathEntryObject(elt, classLoader, scanSpec, log);
                    }
                } else {
                    valid |= this.addClasspathPathStr(pathObject.toString(), classLoader, scanSpec, log);
                }
            }
        }
        return valid;
    }

    static {
        for (String prefix : ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES) {
            AUTOMATIC_PACKAGE_ROOT_SUFFIXES.add("!/" + prefix.substring(0, prefix.length() - 1));
        }
    }

    public static class ClasspathEntry {
        public final Object classpathEntryObj;
        public final ClassLoader classLoader;

        public ClasspathEntry(Object classpathEntryObj, ClassLoader classLoader) {
            this.classpathEntryObj = classpathEntryObj;
            this.classLoader = classLoader;
        }

        public int hashCode() {
            return Objects.hash(this.classpathEntryObj);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ClasspathEntry)) {
                return false;
            }
            ClasspathEntry other = (ClasspathEntry)obj;
            return Objects.equals(this.classpathEntryObj, other.classpathEntryObj);
        }

        public String toString() {
            return this.classpathEntryObj + " [" + this.classLoader + "]";
        }
    }
}

