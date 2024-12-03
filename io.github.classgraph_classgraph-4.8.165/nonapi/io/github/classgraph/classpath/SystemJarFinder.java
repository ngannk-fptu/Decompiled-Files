/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.classpath;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public final class SystemJarFinder {
    private static final Set<String> RT_JARS;
    private static final String RT_JAR;
    private static final Set<String> JRE_LIB_OR_EXT_JARS;

    private SystemJarFinder() {
    }

    private static boolean addJREPath(File dir) {
        File[] dirFiles;
        if (dir != null && !dir.getPath().isEmpty() && FileUtils.canReadAndIsDir(dir) && (dirFiles = dir.listFiles()) != null) {
            for (File file : dirFiles) {
                String filePath = file.getPath();
                if (!filePath.endsWith(".jar")) continue;
                String jarPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), filePath);
                if (jarPathResolved.endsWith("/rt.jar")) {
                    RT_JARS.add(jarPathResolved);
                } else {
                    JRE_LIB_OR_EXT_JARS.add(jarPathResolved);
                }
                try {
                    File canonicalFile = file.getCanonicalFile();
                    String canonicalFilePath = canonicalFile.getPath();
                    if (canonicalFilePath.equals(filePath)) continue;
                    String canonicalJarPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), filePath);
                    JRE_LIB_OR_EXT_JARS.add(canonicalJarPathResolved);
                }
                catch (IOException | SecurityException exception) {
                    // empty catch block
                }
            }
            return true;
        }
        return false;
    }

    public static String getJreRtJarPath() {
        return RT_JAR;
    }

    public static Set<String> getJreLibOrExtJars() {
        return JRE_LIB_OR_EXT_JARS;
    }

    static {
        String javaExtDirs;
        RT_JARS = new LinkedHashSet<String>();
        JRE_LIB_OR_EXT_JARS = new LinkedHashSet<String>();
        String javaHome = VersionFinder.getProperty("java.home");
        if (javaHome == null || javaHome.isEmpty()) {
            javaHome = System.getenv("JAVA_HOME");
        }
        if (javaHome != null && !javaHome.isEmpty()) {
            File javaHomeFile = new File(javaHome);
            SystemJarFinder.addJREPath(javaHomeFile);
            if (javaHomeFile.getName().equals("jre")) {
                File file = javaHomeFile.getParentFile();
                SystemJarFinder.addJREPath(file);
                SystemJarFinder.addJREPath(new File(file, "lib"));
                SystemJarFinder.addJREPath(new File(file, "lib/ext"));
            } else {
                SystemJarFinder.addJREPath(new File(javaHomeFile, "jre"));
            }
            SystemJarFinder.addJREPath(new File(javaHomeFile, "lib"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "lib/ext"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "jre/lib"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "jre/lib/ext"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "packages"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "packages/lib"));
            SystemJarFinder.addJREPath(new File(javaHomeFile, "packages/lib/ext"));
        }
        if ((javaExtDirs = VersionFinder.getProperty("java.ext.dirs")) != null && !javaExtDirs.isEmpty()) {
            for (String javaExtDir : JarUtils.smartPathSplit(javaExtDirs, null)) {
                if (javaExtDir.isEmpty()) continue;
                SystemJarFinder.addJREPath(new File(javaExtDir));
            }
        }
        switch (VersionFinder.OS) {
            case Linux: 
            case Unix: 
            case BSD: 
            case Unknown: {
                SystemJarFinder.addJREPath(new File("/usr/java/packages"));
                SystemJarFinder.addJREPath(new File("/usr/java/packages/lib"));
                SystemJarFinder.addJREPath(new File("/usr/java/packages/lib/ext"));
                break;
            }
            case MacOSX: {
                SystemJarFinder.addJREPath(new File("/System/Library/Java"));
                SystemJarFinder.addJREPath(new File("/System/Library/Java/Libraries"));
                SystemJarFinder.addJREPath(new File("/System/Library/Java/Extensions"));
                break;
            }
            case Windows: {
                String string;
                String string2 = string = File.separatorChar == '\\' ? System.getenv("SystemRoot") : null;
                if (string == null) break;
                SystemJarFinder.addJREPath(new File(string, "Sun\\Java"));
                SystemJarFinder.addJREPath(new File(string, "Sun\\Java\\lib"));
                SystemJarFinder.addJREPath(new File(string, "Sun\\Java\\lib\\ext"));
                SystemJarFinder.addJREPath(new File(string, "Oracle\\Java"));
                SystemJarFinder.addJREPath(new File(string, "Oracle\\Java\\lib"));
                SystemJarFinder.addJREPath(new File(string, "Oracle\\Java\\lib\\ext"));
                break;
            }
            case Solaris: {
                SystemJarFinder.addJREPath(new File("/usr/jdk/packages"));
                SystemJarFinder.addJREPath(new File("/usr/jdk/packages/lib"));
                SystemJarFinder.addJREPath(new File("/usr/jdk/packages/lib/ext"));
                break;
            }
        }
        RT_JAR = RT_JARS.isEmpty() ? null : FastPathResolver.resolve(RT_JARS.iterator().next());
    }
}

