/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClasspathElement;
import io.github.classgraph.ModuleReaderProxy;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.Resource;
import io.github.classgraph.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.recycler.RecycleOnClose;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ProxyingInputStream;
import nonapi.io.github.classgraph.utils.VersionFinder;

class ClasspathElementModule
extends ClasspathElement {
    final ModuleRef moduleRef;
    SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap;
    private Recycler<ModuleReaderProxy, IOException> moduleReaderProxyRecycler;
    private final Set<String> allResourcePaths = new HashSet<String>();

    ClasspathElementModule(ModuleRef moduleRef, SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap, Scanner.ClasspathEntryWorkUnit workUnit, ScanSpec scanSpec) {
        super(workUnit, scanSpec);
        this.moduleRefToModuleReaderProxyRecyclerMap = moduleRefToModuleReaderProxyRecyclerMap;
        this.moduleRef = moduleRef;
    }

    @Override
    void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueueIgnored, LogNode log) throws InterruptedException {
        if (!this.scanSpec.scanModules) {
            if (log != null) {
                this.log(this.classpathElementIdx, "Skipping module, since module scanning is disabled: " + this.getModuleName(), log);
            }
            this.skipClasspathElement = true;
            return;
        }
        try {
            this.moduleReaderProxyRecycler = this.moduleRefToModuleReaderProxyRecyclerMap.get(this.moduleRef, log);
        }
        catch (IOException | SingletonMap.NewInstanceException | SingletonMap.NullSingletonException e) {
            if (log != null) {
                this.log(this.classpathElementIdx, "Skipping invalid module " + this.getModuleName() + " : " + (e.getCause() == null ? e : e.getCause()), log);
            }
            this.skipClasspathElement = true;
            return;
        }
    }

    private Resource newResource(final String resourcePath) {
        return new Resource(this, -1L){
            private ModuleReaderProxy moduleReaderProxy;
            private final AtomicBoolean isOpen;
            {
                super(classpathElement, length);
                this.isOpen = new AtomicBoolean();
            }

            @Override
            public String getPath() {
                return resourcePath;
            }

            @Override
            public long getLastModified() {
                return 0L;
            }

            @Override
            public Set<PosixFilePermission> getPosixFilePermissions() {
                return null;
            }

            @Override
            public ByteBuffer read() throws IOException {
                if (ClasspathElementModule.this.skipClasspathElement) {
                    throw new IOException("Module could not be opened");
                }
                if (this.isOpen.getAndSet(true)) {
                    throw new IOException("Resource is already open -- cannot open it again without first calling close()");
                }
                try {
                    this.moduleReaderProxy = (ModuleReaderProxy)ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                    this.byteBuffer = this.moduleReaderProxy.read(resourcePath);
                    this.length = this.byteBuffer.remaining();
                    return this.byteBuffer;
                }
                catch (OutOfMemoryError | SecurityException e) {
                    this.close();
                    throw new IOException("Could not open " + this, e);
                }
            }

            @Override
            ClassfileReader openClassfile() throws IOException {
                return new ClassfileReader(this.open(), (Resource)this);
            }

            @Override
            public URI getURI() {
                ModuleReaderProxy localModuleReaderProxy = (ModuleReaderProxy)ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                try {
                    URI uRI = localModuleReaderProxy.find(resourcePath);
                    ClasspathElementModule.this.moduleReaderProxyRecycler.recycle(localModuleReaderProxy);
                    return uRI;
                }
                catch (Throwable throwable) {
                    try {
                        ClasspathElementModule.this.moduleReaderProxyRecycler.recycle(localModuleReaderProxy);
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public InputStream open() throws IOException {
                if (ClasspathElementModule.this.skipClasspathElement) {
                    throw new IOException("Module could not be opened");
                }
                if (this.isOpen.getAndSet(true)) {
                    throw new IOException("Resource is already open -- cannot open it again without first calling close()");
                }
                try {
                    final 1 thisResource = this;
                    this.moduleReaderProxy = (ModuleReaderProxy)ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                    this.inputStream = new ProxyingInputStream(this.moduleReaderProxy.open(resourcePath)){

                        @Override
                        public void close() throws IOException {
                            super.close();
                            try {
                                thisResource.close();
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                    };
                    this.length = -1L;
                    return this.inputStream;
                }
                catch (SecurityException e) {
                    this.close();
                    throw new IOException("Could not open " + this, e);
                }
            }

            @Override
            public byte[] load() throws IOException {
                try (1 res = this;){
                    byte[] byteArray;
                    this.read();
                    if (res.byteBuffer.hasArray() && res.byteBuffer.position() == 0 && res.byteBuffer.limit() == res.byteBuffer.capacity()) {
                        byteArray = res.byteBuffer.array();
                    } else {
                        byteArray = new byte[res.byteBuffer.remaining()];
                        res.byteBuffer.get(byteArray);
                    }
                    res.length = byteArray.length;
                    byte[] byArray = byteArray;
                    return byArray;
                }
            }

            @Override
            public void close() {
                if (this.isOpen.getAndSet(false)) {
                    if (this.moduleReaderProxy != null) {
                        if (this.byteBuffer != null) {
                            this.moduleReaderProxy.release(this.byteBuffer);
                            this.byteBuffer = null;
                        }
                        ClasspathElementModule.this.moduleReaderProxyRecycler.recycle(this.moduleReaderProxy);
                        this.moduleReaderProxy = null;
                    }
                    super.close();
                }
            }
        };
    }

    @Override
    Resource getResource(String relativePath) {
        return this.allResourcePaths.contains(relativePath) ? this.newResource(relativePath) : null;
    }

    @Override
    void scanPaths(LogNode log) {
        if (this.skipClasspathElement) {
            return;
        }
        if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this);
        }
        LogNode subLog = log == null ? null : this.log(this.classpathElementIdx, "Scanning module " + this.moduleRef.getName(), log);
        boolean isModularJar = VersionFinder.JAVA_MAJOR_VERSION >= 9 && this.getModuleName() != null;
        try {
            RecycleOnClose<ModuleReaderProxy, IOException> moduleReaderProxyRecycleOnClose = this.moduleReaderProxyRecycler.acquireRecycleOnClose();
            try {
                List<String> resourceRelativePaths;
                try {
                    resourceRelativePaths = moduleReaderProxyRecycleOnClose.get().list();
                }
                catch (SecurityException e) {
                    if (subLog != null) {
                        subLog.log("Could not get resource list for module " + this.moduleRef.getName(), e);
                    }
                    if (moduleReaderProxyRecycleOnClose != null) {
                        moduleReaderProxyRecycleOnClose.close();
                    }
                    return;
                }
                CollectionUtils.sortIfNotEmpty(resourceRelativePaths);
                String prevParentRelativePath = null;
                ScanSpec.ScanSpecPathMatch prevParentMatchStatus = null;
                for (String relativePath : resourceRelativePaths) {
                    if (relativePath.endsWith("/")) continue;
                    if (!this.scanSpec.enableMultiReleaseVersions && relativePath.startsWith("META-INF/versions/")) {
                        if (subLog == null) continue;
                        subLog.log("Found unexpected nested versioned entry in module -- skipping: " + relativePath);
                        continue;
                    }
                    if (isModularJar && relativePath.indexOf(47) < 0 && relativePath.endsWith(".class") && !relativePath.equals("module-info.class") || !this.checkResourcePathAcceptReject(relativePath, log)) continue;
                    int lastSlashIdx = relativePath.lastIndexOf(47);
                    String parentRelativePath = lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
                    boolean parentRelativePathChanged = !parentRelativePath.equals(prevParentRelativePath);
                    ScanSpec.ScanSpecPathMatch parentMatchStatus = prevParentRelativePath == null || parentRelativePathChanged ? this.scanSpec.dirAcceptMatchStatus(parentRelativePath) : prevParentMatchStatus;
                    prevParentRelativePath = parentRelativePath;
                    prevParentMatchStatus = parentMatchStatus;
                    if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_REJECTED_PATH_PREFIX) {
                        if (subLog == null) continue;
                        subLog.log("Skipping rejected path: " + relativePath);
                        continue;
                    }
                    if (!this.allResourcePaths.add(relativePath)) continue;
                    if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX || parentMatchStatus == ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH || parentMatchStatus == ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_CLASS_PACKAGE && this.scanSpec.classfileIsSpecificallyAccepted(relativePath)) {
                        this.addAcceptedResource(this.newResource(relativePath), parentMatchStatus, false, subLog);
                        continue;
                    }
                    if (!this.scanSpec.enableClassInfo || !relativePath.equals("module-info.class")) continue;
                    this.addAcceptedResource(this.newResource(relativePath), parentMatchStatus, true, subLog);
                }
                File moduleFile = this.moduleRef.getLocationFile();
                if (moduleFile != null && moduleFile.exists()) {
                    this.fileToLastModified.put(moduleFile, moduleFile.lastModified());
                }
            }
            finally {
                if (moduleReaderProxyRecycleOnClose != null) {
                    try {
                        moduleReaderProxyRecycleOnClose.close();
                    }
                    catch (Throwable throwable) {
                        Throwable throwable2;
                        throwable2.addSuppressed(throwable);
                    }
                }
            }
        }
        catch (IOException e) {
            if (subLog != null) {
                subLog.log("Exception opening module " + this.moduleRef.getName(), e);
            }
            this.skipClasspathElement = true;
        }
        this.finishScanPaths(subLog);
    }

    ModuleRef getModuleRef() {
        return this.moduleRef;
    }

    @Override
    public String getModuleName() {
        String moduleName = this.moduleRef.getName();
        if (moduleName == null || moduleName.isEmpty()) {
            moduleName = this.moduleNameFromModuleDescriptor;
        }
        return moduleName == null || moduleName.isEmpty() ? null : moduleName;
    }

    private String getModuleNameOrEmpty() {
        String moduleName = this.getModuleName();
        return moduleName == null ? "" : moduleName;
    }

    @Override
    URI getURI() {
        URI uri = this.moduleRef.getLocation();
        if (uri == null) {
            throw new IllegalArgumentException("Module " + this.getModuleName() + " has a null location");
        }
        return uri;
    }

    @Override
    List<URI> getAllURIs() {
        return Collections.singletonList(this.getURI());
    }

    @Override
    File getFile() {
        try {
            File file;
            URI uri = this.moduleRef.getLocation();
            if (uri != null && !uri.getScheme().equals("jrt") && (file = new File(uri)).exists()) {
                return file;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public String toString() {
        return this.moduleRef.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClasspathElementModule)) {
            return false;
        }
        ClasspathElementModule other = (ClasspathElementModule)obj;
        return this.getModuleNameOrEmpty().equals(other.getModuleNameOrEmpty());
    }

    public int hashCode() {
        return this.getModuleNameOrEmpty().hashCode();
    }
}

