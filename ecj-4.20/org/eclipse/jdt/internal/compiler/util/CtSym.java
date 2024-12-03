/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;

public class CtSym {
    private static final char JAVA_11 = 'B';
    public static final boolean DISABLE_CACHE = Boolean.getBoolean("org.eclipse.jdt.disable_CTSYM_cache");
    static boolean VERBOSE = false;
    private final Map<Path, Optional<byte[]>> fileCache = new ConcurrentHashMap<Path, Optional<byte[]>>(10007);
    private final Path jdkHome;
    private final Path ctSymFile;
    private FileSystem fs;
    Path root;
    private boolean isJRE12Plus;
    private final Map<String, List<Path>> releaseRootPaths = new ConcurrentHashMap<String, List<Path>>();
    private final Map<String, Map<String, Path>> allReleasesPaths = new ConcurrentHashMap<String, Map<String, Path>>();

    CtSym(Path jdkHome) throws IOException {
        this.jdkHome = jdkHome;
        this.ctSymFile = jdkHome.resolve("lib/ct.sym");
        this.init();
    }

    private void init() throws IOException {
        boolean exists = Files.exists(this.ctSymFile, new LinkOption[0]);
        if (!exists) {
            throw new FileNotFoundException("File " + this.ctSymFile + " does not exist");
        }
        FileSystem fst = null;
        URI uri = URI.create("jar:file:" + this.ctSymFile.toUri().getRawPath());
        try {
            fst = FileSystems.getFileSystem(uri);
        }
        catch (Exception exception) {}
        if (fst == null) {
            try {
                fst = FileSystems.newFileSystem(uri, new HashMap(), ClassLoader.getSystemClassLoader());
            }
            catch (FileSystemAlreadyExistsException fileSystemAlreadyExistsException) {
                fst = FileSystems.getFileSystem(uri);
            }
            catch (ProviderNotFoundException e) {
                throw new IOException("Failed to create ct.sym file system for " + this.ctSymFile, e);
            }
        }
        this.fs = fst;
        if (fst == null) {
            throw new IOException("Failed to create ct.sym file system for " + this.ctSymFile);
        }
        this.root = fst.getPath("/", new String[0]);
        this.isJRE12Plus = this.isCurrentRelease12plus();
    }

    public FileSystem getFs() {
        return this.fs;
    }

    public boolean isJRE12Plus() {
        return this.isJRE12Plus;
    }

    public Path getRoot() {
        return this.root;
    }

    public List<Path> releaseRoots(String releaseCode) {
        List list = this.releaseRootPaths.computeIfAbsent(releaseCode, x -> {
            ArrayList<Path> rootDirs = new ArrayList<Path>();
            try {
                Throwable throwable = null;
                Object var5_6 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.root);){
                    for (Path subdir : stream) {
                        String rel = subdir.getFileName().toString();
                        if (rel.contains("-") || !rel.contains(releaseCode)) continue;
                        rootDirs.add(subdir);
                    }
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (IOException iOException) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(rootDirs);
        });
        return list;
    }

    public Path getFullPath(String releaseCode, String qualifiedSignatureFileName, String moduleName) {
        Path path;
        String sep = this.fs.getSeparator();
        if (DISABLE_CACHE) {
            List<Path> releaseRoots = this.releaseRoots(releaseCode);
            for (Path rroot : releaseRoots) {
                Path p = null;
                if (this.isJRE12Plus()) {
                    if (moduleName == null) {
                        moduleName = this.getModuleInJre12plus(releaseCode, qualifiedSignatureFileName);
                    }
                    p = rroot.resolve(String.valueOf(moduleName) + sep + qualifiedSignatureFileName);
                } else {
                    p = rroot.resolve(qualifiedSignatureFileName);
                }
                if (!Files.exists(p, new LinkOption[0])) continue;
                if (VERBOSE) {
                    System.out.println("found: " + qualifiedSignatureFileName + " in " + p + " for module " + moduleName + "\n");
                }
                return p;
            }
            if (VERBOSE) {
                System.out.println("not found: " + qualifiedSignatureFileName + " for module " + moduleName);
            }
            return null;
        }
        Map<String, Path> releasePaths = this.getCachedReleasePaths(releaseCode);
        if (moduleName != null) {
            path = releasePaths.get(String.valueOf(moduleName) + sep + qualifiedSignatureFileName);
            if (path == null && !this.isJRE12Plus() && "A".equals(releaseCode)) {
                path = releasePaths.get(qualifiedSignatureFileName);
            }
        } else {
            path = releasePaths.get(qualifiedSignatureFileName);
        }
        if (VERBOSE) {
            if (path != null) {
                System.out.println("found: " + qualifiedSignatureFileName + " in " + path + " for module " + moduleName + "\n");
            } else {
                System.out.println("not found: " + qualifiedSignatureFileName + " for module " + moduleName);
            }
        }
        return path;
    }

    public String getModuleInJre12plus(String releaseCode, String qualifiedSignatureFileName) {
        if (DISABLE_CACHE) {
            return this.findModuleForFileInJre12plus(releaseCode, qualifiedSignatureFileName);
        }
        Map<String, Path> releasePaths = this.getCachedReleasePaths(releaseCode);
        Path path = releasePaths.get(qualifiedSignatureFileName);
        if (path != null && path.getNameCount() > 2) {
            return path.getName(1).toString();
        }
        return null;
    }

    private String findModuleForFileInJre12plus(String releaseCode, String qualifiedSignatureFileName) {
        for (Path rroot : this.releaseRoots(releaseCode)) {
            try {
                Throwable throwable = null;
                Object var6_7 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(rroot);){
                    for (Path subdir : stream) {
                        Path p = subdir.resolve(qualifiedSignatureFileName);
                        if (!Files.exists(p, new LinkOption[0]) || subdir.getNameCount() != 2) continue;
                        return subdir.getName(1).toString();
                    }
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                    } else if (throwable != throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
            }
            catch (IOException iOException) {}
        }
        return null;
    }

    private Map<String, Path> getCachedReleasePaths(String releaseCode) {
        Map result = this.allReleasesPaths.computeIfAbsent(releaseCode, x -> {
            List<Path> roots = this.releaseRoots(releaseCode);
            HashMap allReleaseFiles = new HashMap(4999);
            for (Path start : roots) {
                try {
                    Files.walk(start, new FileVisitOption[0]).filter(path -> Files.isRegularFile(path, new LinkOption[0])).forEach(p -> {
                        if (this.isJRE12Plus()) {
                            String binaryNameWithoutModule = p.subpath(2, p.getNameCount()).toString();
                            allReleaseFiles.put(binaryNameWithoutModule, p);
                            String binaryNameWithModule = p.subpath(1, p.getNameCount()).toString();
                            allReleaseFiles.put(binaryNameWithModule, p);
                        } else {
                            String binaryNameWithoutModule = p.subpath(1, p.getNameCount()).toString();
                            allReleaseFiles.put(binaryNameWithoutModule, p);
                        }
                    });
                }
                catch (IOException iOException) {}
            }
            return Collections.unmodifiableMap(allReleaseFiles);
        });
        return result;
    }

    public byte[] getFileBytes(Path path) throws IOException {
        if (DISABLE_CACHE) {
            return JRTUtil.safeReadBytes(path);
        }
        Optional bytes = this.fileCache.computeIfAbsent(path, key -> {
            try {
                return Optional.ofNullable(JRTUtil.safeReadBytes(key));
            }
            catch (IOException iOException) {
                return Optional.empty();
            }
        });
        if (VERBOSE) {
            System.out.println("got bytes: " + path);
        }
        return bytes.orElse(null);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private boolean isCurrentRelease12plus() throws IOException {
        Throwable throwable = null;
        Object var2_3 = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.root, p -> p.toString().length() == 2);){
            for (Path subdir : stream) {
                String rel = JRTUtil.sanitizedFileName(subdir);
                if (rel.length() != 1) continue;
                try {
                    char releaseCode = rel.charAt(0);
                    if (releaseCode <= 'B' || !Files.exists(this.fs.getPath(rel, "system-modules"), new LinkOption[0])) continue;
                    return true;
                }
                catch (NumberFormatException numberFormatException) {
                }
            }
        }
        catch (Throwable throwable2) {
            if (throwable == null) {
                throwable = throwable2;
            } else if (throwable != throwable2) {
                throwable.addSuppressed(throwable2);
            }
            throw throwable;
        }
        return false;
    }

    public int hashCode() {
        return this.jdkHome.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CtSym)) {
            return false;
        }
        CtSym other = (CtSym)obj;
        return this.jdkHome.equals(other.jdkHome);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CtSym [");
        sb.append("file=");
        sb.append(this.ctSymFile);
        sb.append("]");
        return sb.toString();
    }

    public static String getReleaseCode(String release) {
        int numericVersion = Integer.parseInt(release);
        if (numericVersion < 10) {
            return String.valueOf(numericVersion);
        }
        return String.valueOf((char)(65 + (numericVersion - 10)));
    }
}

