/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClasspathElement;
import io.github.classgraph.Resource;
import io.github.classgraph.Scanner;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fileslice.PathSlice;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

class ClasspathElementDir
extends ClasspathElement {
    private final Path classpathEltPath;
    private final Set<Path> scannedCanonicalPaths = new HashSet<Path>();
    private final NestedJarHandler nestedJarHandler;

    ClasspathElementDir(Scanner.ClasspathEntryWorkUnit workUnit, NestedJarHandler nestedJarHandler, ScanSpec scanSpec) {
        super(workUnit, scanSpec);
        this.classpathEltPath = (Path)workUnit.classpathEntryObj;
        this.nestedJarHandler = nestedJarHandler;
    }

    @Override
    void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, LogNode log) {
        if (!this.scanSpec.scanDirs) {
            if (log != null) {
                this.log(this.classpathElementIdx, "Skipping classpath element, since dir scanning is disabled: " + this.classpathEltPath, log);
            }
            this.skipClasspathElement = true;
            return;
        }
        try {
            int childClasspathEntryIdx = 0;
            for (String libDirPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_LIB_DIR_PREFIXES) {
                Path libDirPath = this.classpathEltPath.resolve(libDirPrefix);
                if (!FileUtils.canReadAndIsDir(libDirPath)) continue;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(libDirPath);){
                    for (Path filePath : stream) {
                        if (!Files.isRegularFile(filePath, new LinkOption[0]) || !filePath.getFileName().endsWith(".jar")) continue;
                        if (log != null) {
                            this.log(this.classpathElementIdx, "Found lib jar: " + filePath, log);
                        }
                        workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(filePath, this.getClassLoader(), this, childClasspathEntryIdx++, ""));
                    }
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
            if (this.packageRootPrefix.isEmpty()) {
                for (String packageRootPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES) {
                    Path packageRoot = this.classpathEltPath.resolve(packageRootPrefix);
                    if (!FileUtils.canReadAndIsDir(packageRoot)) continue;
                    if (log != null) {
                        this.log(this.classpathElementIdx, "Found package root: " + packageRootPrefix, log);
                    }
                    workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(packageRoot, this.getClassLoader(), this, childClasspathEntryIdx++, packageRootPrefix));
                }
            }
        }
        catch (SecurityException e) {
            if (log != null) {
                this.log(this.classpathElementIdx, "Skipping classpath element, since dir cannot be accessed: " + this.classpathEltPath, log);
            }
            this.skipClasspathElement = true;
        }
    }

    private Resource newResource(final Path resourcePath, final NestedJarHandler nestedJarHandler) {
        long length;
        try {
            length = Files.size(resourcePath);
        }
        catch (IOException | SecurityException e) {
            length = -1L;
        }
        return new Resource(this, length){
            private PathSlice pathSlice;
            private final AtomicBoolean isOpen;
            {
                super(classpathElement, length);
                this.isOpen = new AtomicBoolean();
            }

            @Override
            public String getPath() {
                String path = FastPathResolver.resolve(ClasspathElementDir.this.classpathEltPath.relativize(resourcePath).toString());
                while (path.startsWith("/")) {
                    path = path.substring(1);
                }
                return path;
            }

            @Override
            public String getPathRelativeToClasspathElement() {
                return ClasspathElementDir.this.packageRootPrefix.isEmpty() ? this.getPath() : ClasspathElementDir.this.packageRootPrefix + this.getPath();
            }

            @Override
            public long getLastModified() {
                try {
                    return resourcePath.toFile().lastModified();
                }
                catch (UnsupportedOperationException e) {
                    return 0L;
                }
            }

            @Override
            public Set<PosixFilePermission> getPosixFilePermissions() {
                Set<PosixFilePermission> posixFilePermissions = null;
                try {
                    posixFilePermissions = Files.readAttributes(resourcePath, PosixFileAttributes.class, new LinkOption[0]).permissions();
                }
                catch (IOException | SecurityException | UnsupportedOperationException exception) {
                    // empty catch block
                }
                return posixFilePermissions;
            }

            @Override
            public ByteBuffer read() throws IOException {
                if (ClasspathElementDir.this.skipClasspathElement) {
                    throw new IOException("Parent directory could not be opened");
                }
                if (this.isOpen.getAndSet(true)) {
                    throw new IOException("Resource is already open -- cannot open it again without first calling close()");
                }
                this.pathSlice = new PathSlice(resourcePath, nestedJarHandler);
                this.length = this.pathSlice.sliceLength;
                this.byteBuffer = this.pathSlice.read();
                return this.byteBuffer;
            }

            @Override
            ClassfileReader openClassfile() throws IOException {
                if (ClasspathElementDir.this.skipClasspathElement) {
                    throw new IOException("Parent directory could not be opened");
                }
                if (this.isOpen.getAndSet(true)) {
                    throw new IOException("Resource is already open -- cannot open it again without first calling close()");
                }
                this.pathSlice = new PathSlice(resourcePath, nestedJarHandler);
                this.length = this.pathSlice.sliceLength;
                return new ClassfileReader(this.pathSlice, (Resource)this);
            }

            @Override
            public InputStream open() throws IOException {
                if (ClasspathElementDir.this.skipClasspathElement) {
                    throw new IOException("Parent directory could not be opened");
                }
                if (this.isOpen.getAndSet(true)) {
                    throw new IOException("Resource is already open -- cannot open it again without first calling close()");
                }
                this.pathSlice = new PathSlice(resourcePath, nestedJarHandler);
                this.inputStream = this.pathSlice.open(this);
                this.length = this.pathSlice.sliceLength;
                return this.inputStream;
            }

            @Override
            public byte[] load() throws IOException {
                this.read();
                try (1 res = this;){
                    this.pathSlice = new PathSlice(resourcePath, nestedJarHandler);
                    byte[] bytes = this.pathSlice.load();
                    res.length = bytes.length;
                    byte[] byArray = bytes;
                    return byArray;
                }
            }

            @Override
            public void close() {
                if (this.isOpen.getAndSet(false)) {
                    if (this.byteBuffer != null) {
                        this.byteBuffer = null;
                    }
                    if (this.pathSlice != null) {
                        this.pathSlice.close();
                        nestedJarHandler.markSliceAsClosed(this.pathSlice);
                        this.pathSlice = null;
                    }
                    super.close();
                }
            }
        };
    }

    @Override
    Resource getResource(String relativePath) {
        Path resourcePath = this.classpathEltPath.resolve(relativePath);
        return FileUtils.canReadAndIsFile(resourcePath) ? this.newResource(resourcePath, this.nestedJarHandler) : null;
    }

    private void scanPathRecursively(Path path, LogNode log) {
        boolean isModularJar;
        Path canonicalPath;
        try {
            canonicalPath = path.toRealPath(new LinkOption[0]);
            if (!this.scannedCanonicalPaths.add(canonicalPath)) {
                if (log != null) {
                    log.log("Reached symlink cycle, stopping recursion: " + path);
                }
                return;
            }
        }
        catch (IOException | SecurityException e) {
            if (log != null) {
                log.log("Could not canonicalize path: " + path, e);
            }
            return;
        }
        String dirRelativePathStr = FastPathResolver.resolve(this.classpathEltPath.relativize(path).toString());
        while (dirRelativePathStr.startsWith("/")) {
            dirRelativePathStr = dirRelativePathStr.substring(1);
        }
        if (!dirRelativePathStr.endsWith("/")) {
            dirRelativePathStr = dirRelativePathStr + "/";
        }
        boolean isDefaultPackage = dirRelativePathStr.equals("/");
        if (this.nestedClasspathRootPrefixes != null && this.nestedClasspathRootPrefixes.contains(dirRelativePathStr)) {
            if (log != null) {
                log.log("Reached nested classpath root, stopping recursion to avoid duplicate scanning: " + dirRelativePathStr);
            }
            return;
        }
        if (!this.scanSpec.enableMultiReleaseVersions && dirRelativePathStr.startsWith("META-INF/versions/")) {
            if (log != null) {
                log.log("Found unexpected nested versioned entry in directory classpath element -- skipping: " + dirRelativePathStr);
            }
            return;
        }
        if (!this.checkResourcePathAcceptReject(dirRelativePathStr, log)) {
            return;
        }
        ScanSpec.ScanSpecPathMatch parentMatchStatus = this.scanSpec.dirAcceptMatchStatus(dirRelativePathStr);
        if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_REJECTED_PATH_PREFIX) {
            if (log != null) {
                log.log("Reached rejected directory, stopping recursive scan: " + dirRelativePathStr);
            }
            return;
        }
        if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.NOT_WITHIN_ACCEPTED_PATH) {
            return;
        }
        LogNode subLog = log == null ? null : log.log("1:" + canonicalPath, "Scanning Path: " + FastPathResolver.resolve(path.toString()) + (path.equals(canonicalPath) ? "" : " ; canonical path: " + FastPathResolver.resolve(canonicalPath.toString())));
        ArrayList<Path> pathsInDir = new ArrayList<Path>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);){
            for (Path subPath : stream) {
                pathsInDir.add(subPath);
            }
        }
        catch (IOException | SecurityException e) {
            if (log != null) {
                log.log("Could not read directory " + path + " : " + e.getMessage());
            }
            return;
        }
        Collections.sort(pathsInDir);
        boolean bl = isModularJar = VersionFinder.JAVA_MAJOR_VERSION >= 9 && this.getModuleName() != null;
        if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_ACCEPTED_PATH) {
            for (Path subPath : pathsInDir) {
                if (!Files.isRegularFile(subPath, new LinkOption[0])) continue;
                Path subPathRelative = this.classpathEltPath.relativize(subPath);
                String subPathRelativeStr = FastPathResolver.resolve(subPathRelative.toString());
                if (isModularJar && isDefaultPackage && subPathRelativeStr.endsWith(".class") && !subPathRelativeStr.equals("module-info.class")) continue;
                if (!this.checkResourcePathAcceptReject(subPathRelativeStr, subLog)) {
                    return;
                }
                if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX || parentMatchStatus == ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH || parentMatchStatus == ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_CLASS_PACKAGE && this.scanSpec.classfileIsSpecificallyAccepted(subPathRelativeStr)) {
                    Resource resource = this.newResource(subPath, this.nestedJarHandler);
                    this.addAcceptedResource(resource, parentMatchStatus, false, subLog);
                    try {
                        this.fileToLastModified.put(subPath.toFile(), subPath.toFile().lastModified());
                    }
                    catch (UnsupportedOperationException unsupportedOperationException) {}
                    continue;
                }
                if (subLog == null) continue;
                subLog.log("Skipping non-accepted file: " + subPathRelative);
            }
        } else if (this.scanSpec.enableClassInfo && dirRelativePathStr.equals("/")) {
            for (Path subPath : pathsInDir) {
                if (!subPath.getFileName().toString().equals("module-info.class") || !Files.isRegularFile(subPath, new LinkOption[0])) continue;
                Resource resource = this.newResource(subPath, this.nestedJarHandler);
                this.addAcceptedResource(resource, parentMatchStatus, true, subLog);
                try {
                    this.fileToLastModified.put(subPath.toFile(), subPath.toFile().lastModified());
                }
                catch (UnsupportedOperationException unsupportedOperationException) {}
                break;
            }
        }
        for (Path subPath : pathsInDir) {
            try {
                if (!Files.isDirectory(subPath, new LinkOption[0])) continue;
                this.scanPathRecursively(subPath, subLog);
            }
            catch (SecurityException e) {
                if (subLog == null) continue;
                subLog.log("Could not read sub-directory " + subPath + " : " + e.getMessage());
            }
        }
        if (subLog != null) {
            subLog.addElapsedTime();
        }
        try {
            File file = path.toFile();
            this.fileToLastModified.put(file, file.lastModified());
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
    }

    @Override
    void scanPaths(LogNode log) {
        if (!this.checkResourcePathAcceptReject(this.classpathEltPath.toString(), log)) {
            this.skipClasspathElement = true;
        }
        if (this.skipClasspathElement) {
            return;
        }
        if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this);
        }
        LogNode subLog = log == null ? null : this.log(this.classpathElementIdx, "Scanning Path classpath element " + this.getURI(), log);
        this.scanPathRecursively(this.classpathEltPath, subLog);
        this.finishScanPaths(subLog);
    }

    @Override
    public String getModuleName() {
        return this.moduleNameFromModuleDescriptor == null || this.moduleNameFromModuleDescriptor.isEmpty() ? null : this.moduleNameFromModuleDescriptor;
    }

    @Override
    public File getFile() {
        try {
            return this.classpathEltPath.toFile();
        }
        catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override
    URI getURI() {
        try {
            return this.classpathEltPath.toUri();
        }
        catch (IOError | SecurityException e) {
            throw new IllegalArgumentException("Could not convert to URI: " + this.classpathEltPath);
        }
    }

    @Override
    List<URI> getAllURIs() {
        return Collections.singletonList(this.getURI());
    }

    public String toString() {
        try {
            return this.classpathEltPath.toUri().toString();
        }
        catch (IOError | SecurityException e) {
            return this.classpathEltPath.toString();
        }
    }

    public int hashCode() {
        return Objects.hash(this.classpathEltPath);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClasspathElementDir)) {
            return false;
        }
        ClasspathElementDir other = (ClasspathElementDir)obj;
        return Objects.equals(this.classpathEltPath, other.classpathEltPath);
    }
}

