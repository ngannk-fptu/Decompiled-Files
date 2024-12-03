/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.util.CtSym;
import org.eclipse.jdt.internal.compiler.util.JrtFileSystem;
import org.eclipse.jdt.internal.compiler.util.RuntimeIOException;

public class JRTUtil {
    public static final boolean DISABLE_CACHE = Boolean.getBoolean("org.eclipse.jdt.disable_JRT_cache");
    public static final String JAVA_BASE = "java.base".intern();
    public static final char[] JAVA_BASE_CHAR = JAVA_BASE.toCharArray();
    static final String MODULES_SUBDIR = "/modules";
    static final String[] DEFAULT_MODULE = new String[]{JAVA_BASE};
    static final String[] NO_MODULE = new String[0];
    static final String MULTIPLE = "MU";
    static final String DEFAULT_PACKAGE = "";
    static String MODULE_TO_LOAD;
    public static final String JRT_FS_JAR = "jrt-fs.jar";
    static URI JRT_URI;
    public static final int NOTIFY_FILES = 1;
    public static final int NOTIFY_PACKAGES = 2;
    public static final int NOTIFY_MODULES = 4;
    public static final int NOTIFY_ALL = 7;
    private static Map<String, Optional<JrtFileSystem>> images;
    private static final Map<Path, CtSym> ctSymFiles;

    static {
        JRT_URI = URI.create("jrt:/");
        images = new ConcurrentHashMap<String, Optional<JrtFileSystem>>();
        ctSymFiles = new ConcurrentHashMap<Path, CtSym>();
    }

    public static JrtFileSystem getJrtSystem(File image) {
        return JRTUtil.getJrtSystem(image, null);
    }

    public static JrtFileSystem getJrtSystem(File image, String release) {
        String key = image.toString();
        if (release != null) {
            key = String.valueOf(key) + "|" + release;
        }
        Optional system = images.computeIfAbsent(key, x -> {
            try {
                return Optional.ofNullable(JrtFileSystem.getNewJrtFileSystem(image, release));
            }
            catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
        return system.orElse(null);
    }

    public static CtSym getCtSym(Path jdkHome) throws IOException {
        CtSym ctSym;
        try {
            ctSym = ctSymFiles.compute(jdkHome, (x, current) -> {
                if (current == null || !current.getFs().isOpen()) {
                    try {
                        return new CtSym((Path)x);
                    }
                    catch (IOException e) {
                        throw new RuntimeIOException(e);
                    }
                }
                return current;
            });
        }
        catch (RuntimeIOException rio) {
            throw rio.getCause();
        }
        return ctSym;
    }

    public static void reset() {
        images.clear();
        MODULE_TO_LOAD = System.getProperty("modules.to.load");
    }

    public static void walkModuleImage(File image, JrtFileVisitor<Path> visitor, int notify) throws IOException {
        JRTUtil.getJrtSystem(image, null).walkModuleImage(visitor, notify);
    }

    public static void walkModuleImage(File image, String release, JrtFileVisitor<Path> visitor, int notify) throws IOException {
        JRTUtil.getJrtSystem(image, release).walkModuleImage(visitor, notify);
    }

    public static InputStream getContentFromJrt(File jrt, String fileName, String module) throws IOException {
        return JRTUtil.getJrtSystem(jrt).getContentFromJrt(fileName, module);
    }

    public static byte[] getClassfileContent(File jrt, String fileName, String module) throws IOException {
        return JRTUtil.getJrtSystem(jrt).getClassfileContent(fileName, module);
    }

    public static ClassFileReader getClassfile(File jrt, String fileName, IModule module) throws IOException, ClassFormatException {
        return JRTUtil.getJrtSystem(jrt).getClassfile(fileName, module);
    }

    public static ClassFileReader getClassfile(File jrt, String fileName, String module, Predicate<String> moduleNameFilter) throws IOException, ClassFormatException {
        return JRTUtil.getJrtSystem(jrt).getClassfile(fileName, module, moduleNameFilter);
    }

    public static List<String> getModulesDeclaringPackage(File jrt, String qName, String moduleName) {
        return JRTUtil.getJrtSystem(jrt).getModulesDeclaringPackage(qName, moduleName);
    }

    public static boolean hasCompilationUnit(File jrt, String qualifiedPackageName, String moduleName) {
        return JRTUtil.getJrtSystem(jrt).hasClassFile(qualifiedPackageName, moduleName);
    }

    public static String sanitizedFileName(Path path) {
        String p = path.getFileName().toString();
        if (p.length() > 1 && p.charAt(p.length() - 1) == '/') {
            return p.substring(0, p.length() - 1);
        }
        return p;
    }

    public static byte[] safeReadBytes(Path path) throws IOException {
        try {
            return Files.readAllBytes(path);
        }
        catch (ClosedByInterruptException | NoSuchFileException iOException) {
            return null;
        }
    }

    static abstract class AbstractFileVisitor<T>
    implements FileVisitor<T> {
        AbstractFileVisitor() {
        }

        @Override
        public FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(T file, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(T file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(T dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }

    public static interface JrtFileVisitor<T> {
        public FileVisitResult visitPackage(T var1, T var2, BasicFileAttributes var3) throws IOException;

        public FileVisitResult visitFile(T var1, T var2, BasicFileAttributes var3) throws IOException;

        public FileVisitResult visitModule(T var1, String var2) throws IOException;
    }
}

