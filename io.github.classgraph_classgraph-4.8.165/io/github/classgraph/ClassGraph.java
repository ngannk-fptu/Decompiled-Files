/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassGraphException;
import io.github.classgraph.ModulePathInfo;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.ScanResult;
import io.github.classgraph.Scanner;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classpath.SystemJarFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.AcceptReject;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class ClassGraph {
    ScanSpec scanSpec = new ScanSpec();
    static final int DEFAULT_NUM_WORKER_THREADS = Math.max(2, (int)Math.ceil(Math.min(4.0, (double)Runtime.getRuntime().availableProcessors() * 0.75) + (double)Runtime.getRuntime().availableProcessors() * 1.25));
    public static CircumventEncapsulationMethod CIRCUMVENT_ENCAPSULATION = CircumventEncapsulationMethod.NONE;
    private final ReflectionUtils reflectionUtils = new ReflectionUtils();
    private LogNode topLevelLog;

    public ClassGraph() {
        ScanResult.init(this.reflectionUtils);
    }

    public static String getVersion() {
        return VersionFinder.getVersion();
    }

    public ClassGraph verbose() {
        if (this.topLevelLog == null) {
            this.topLevelLog = new LogNode();
        }
        return this;
    }

    public ClassGraph verbose(boolean verbose) {
        if (verbose) {
            this.verbose();
        }
        return this;
    }

    public ClassGraph enableAllInfo() {
        this.enableClassInfo();
        this.enableFieldInfo();
        this.enableMethodInfo();
        this.enableAnnotationInfo();
        this.enableStaticFinalFieldConstantInitializerValues();
        this.ignoreClassVisibility();
        this.ignoreFieldVisibility();
        this.ignoreMethodVisibility();
        return this;
    }

    public ClassGraph enableClassInfo() {
        this.scanSpec.enableClassInfo = true;
        this.scanSpec.enableMultiReleaseVersions = false;
        return this;
    }

    public ClassGraph ignoreClassVisibility() {
        this.enableClassInfo();
        this.scanSpec.ignoreClassVisibility = true;
        return this;
    }

    public ClassGraph enableMethodInfo() {
        this.enableClassInfo();
        this.scanSpec.enableMethodInfo = true;
        return this;
    }

    public ClassGraph ignoreMethodVisibility() {
        this.enableClassInfo();
        this.enableMethodInfo();
        this.scanSpec.ignoreMethodVisibility = true;
        return this;
    }

    public ClassGraph enableFieldInfo() {
        this.enableClassInfo();
        this.scanSpec.enableFieldInfo = true;
        return this;
    }

    public ClassGraph ignoreFieldVisibility() {
        this.enableClassInfo();
        this.enableFieldInfo();
        this.scanSpec.ignoreFieldVisibility = true;
        return this;
    }

    public ClassGraph enableStaticFinalFieldConstantInitializerValues() {
        this.enableClassInfo();
        this.enableFieldInfo();
        this.scanSpec.enableStaticFinalFieldConstantInitializerValues = true;
        return this;
    }

    public ClassGraph enableAnnotationInfo() {
        this.enableClassInfo();
        this.scanSpec.enableAnnotationInfo = true;
        return this;
    }

    public ClassGraph enableInterClassDependencies() {
        this.enableClassInfo();
        this.enableFieldInfo();
        this.enableMethodInfo();
        this.enableAnnotationInfo();
        this.ignoreClassVisibility();
        this.ignoreFieldVisibility();
        this.ignoreMethodVisibility();
        this.scanSpec.enableInterClassDependencies = true;
        return this;
    }

    public ClassGraph disableRuntimeInvisibleAnnotations() {
        this.enableClassInfo();
        this.scanSpec.disableRuntimeInvisibleAnnotations = true;
        return this;
    }

    public ClassGraph disableJarScanning() {
        this.scanSpec.scanJars = false;
        return this;
    }

    public ClassGraph disableNestedJarScanning() {
        this.scanSpec.scanNestedJars = false;
        return this;
    }

    public ClassGraph disableDirScanning() {
        this.scanSpec.scanDirs = false;
        return this;
    }

    public ClassGraph disableModuleScanning() {
        this.scanSpec.scanModules = false;
        return this;
    }

    public ClassGraph enableExternalClasses() {
        this.enableClassInfo();
        this.scanSpec.enableExternalClasses = true;
        return this;
    }

    public ClassGraph initializeLoadedClasses() {
        this.scanSpec.initializeLoadedClasses = true;
        return this;
    }

    public ClassGraph removeTemporaryFilesAfterScan() {
        this.scanSpec.removeTemporaryFilesAfterScan = true;
        return this;
    }

    public ClassGraph overrideClasspath(String overrideClasspath) {
        if (overrideClasspath.isEmpty()) {
            throw new IllegalArgumentException("Can't override classpath with an empty path");
        }
        for (String classpathElement : JarUtils.smartPathSplit(overrideClasspath, this.scanSpec)) {
            this.scanSpec.addClasspathOverride(classpathElement);
        }
        return this;
    }

    public ClassGraph overrideClasspath(Iterable<?> overrideClasspathElements) {
        if (!overrideClasspathElements.iterator().hasNext()) {
            throw new IllegalArgumentException("Can't override classpath with an empty path");
        }
        for (Object classpathElement : overrideClasspathElements) {
            this.scanSpec.addClasspathOverride(classpathElement);
        }
        return this;
    }

    public ClassGraph overrideClasspath(Object ... overrideClasspathElements) {
        if (overrideClasspathElements.length == 0) {
            throw new IllegalArgumentException("Can't override classpath with an empty path");
        }
        for (Object classpathElement : overrideClasspathElements) {
            this.scanSpec.addClasspathOverride(classpathElement);
        }
        return this;
    }

    public ClassGraph filterClasspathElements(ClasspathElementFilter classpathElementFilter) {
        this.scanSpec.filterClasspathElements(classpathElementFilter);
        return this;
    }

    public ClassGraph filterClasspathElementsByURL(ClasspathElementURLFilter classpathElementURLFilter) {
        this.scanSpec.filterClasspathElements(classpathElementURLFilter);
        return this;
    }

    public ClassGraph addClassLoader(ClassLoader classLoader) {
        this.scanSpec.addClassLoader(classLoader);
        return this;
    }

    public ClassGraph overrideClassLoaders(ClassLoader ... overrideClassLoaders) {
        this.scanSpec.overrideClassLoaders(overrideClassLoaders);
        return this;
    }

    public ClassGraph ignoreParentClassLoaders() {
        this.scanSpec.ignoreParentClassLoaders = true;
        return this;
    }

    public ClassGraph addModuleLayer(Object moduleLayer) {
        this.scanSpec.addModuleLayer(moduleLayer);
        return this;
    }

    public ClassGraph overrideModuleLayers(Object ... overrideModuleLayers) {
        this.scanSpec.overrideModuleLayers(overrideModuleLayers);
        return this;
    }

    public ClassGraph ignoreParentModuleLayers() {
        this.scanSpec.ignoreParentModuleLayers = true;
        return this;
    }

    public ClassGraph acceptPackages(String ... packageNames) {
        this.enableClassInfo();
        for (String packageName : packageNames) {
            String packageNameNormalized = AcceptReject.normalizePackageOrClassName(packageName);
            this.scanSpec.packageAcceptReject.addToAccept(packageNameNormalized);
            String path = AcceptReject.packageNameToPath(packageNameNormalized);
            this.scanSpec.pathAcceptReject.addToAccept(path + "/");
            if (packageNameNormalized.isEmpty()) {
                this.scanSpec.pathAcceptReject.addToAccept("");
            }
            if (packageNameNormalized.contains("*")) continue;
            if (packageNameNormalized.isEmpty()) {
                this.scanSpec.packagePrefixAcceptReject.addToAccept("");
                this.scanSpec.pathPrefixAcceptReject.addToAccept("");
                continue;
            }
            this.scanSpec.packagePrefixAcceptReject.addToAccept(packageNameNormalized + ".");
            this.scanSpec.pathPrefixAcceptReject.addToAccept(path + "/");
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistPackages(String ... packageNames) {
        return this.acceptPackages(packageNames);
    }

    public ClassGraph acceptPaths(String ... paths) {
        for (String path : paths) {
            String pathNormalized = AcceptReject.normalizePath(path);
            String packageName = AcceptReject.pathToPackageName(pathNormalized);
            this.scanSpec.packageAcceptReject.addToAccept(packageName);
            this.scanSpec.pathAcceptReject.addToAccept(pathNormalized + "/");
            if (pathNormalized.isEmpty()) {
                this.scanSpec.pathAcceptReject.addToAccept("");
            }
            if (pathNormalized.contains("*")) continue;
            if (pathNormalized.isEmpty()) {
                this.scanSpec.packagePrefixAcceptReject.addToAccept("");
                this.scanSpec.pathPrefixAcceptReject.addToAccept("");
                continue;
            }
            this.scanSpec.packagePrefixAcceptReject.addToAccept(packageName + ".");
            this.scanSpec.pathPrefixAcceptReject.addToAccept(pathNormalized + "/");
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistPaths(String ... paths) {
        return this.acceptPaths(paths);
    }

    public ClassGraph acceptPackagesNonRecursive(String ... packageNames) {
        this.enableClassInfo();
        for (String packageName : packageNames) {
            String packageNameNormalized = AcceptReject.normalizePackageOrClassName(packageName);
            if (packageNameNormalized.contains("*")) {
                throw new IllegalArgumentException("Cannot use a glob wildcard here: " + packageNameNormalized);
            }
            this.scanSpec.packageAcceptReject.addToAccept(packageNameNormalized);
            this.scanSpec.pathAcceptReject.addToAccept(AcceptReject.packageNameToPath(packageNameNormalized) + "/");
            if (!packageNameNormalized.isEmpty()) continue;
            this.scanSpec.pathAcceptReject.addToAccept("");
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistPackagesNonRecursive(String ... packageNames) {
        return this.acceptPackagesNonRecursive(packageNames);
    }

    public ClassGraph acceptPathsNonRecursive(String ... paths) {
        for (String path : paths) {
            if (path.contains("*")) {
                throw new IllegalArgumentException("Cannot use a glob wildcard here: " + path);
            }
            String pathNormalized = AcceptReject.normalizePath(path);
            this.scanSpec.packageAcceptReject.addToAccept(AcceptReject.pathToPackageName(pathNormalized));
            this.scanSpec.pathAcceptReject.addToAccept(pathNormalized + "/");
            if (!pathNormalized.isEmpty()) continue;
            this.scanSpec.pathAcceptReject.addToAccept("");
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistPathsNonRecursive(String ... paths) {
        return this.acceptPathsNonRecursive(paths);
    }

    public ClassGraph rejectPackages(String ... packageNames) {
        this.enableClassInfo();
        for (String packageName : packageNames) {
            String packageNameNormalized = AcceptReject.normalizePackageOrClassName(packageName);
            if (packageNameNormalized.isEmpty()) {
                throw new IllegalArgumentException("Rejecting the root package (\"\") will cause nothing to be scanned");
            }
            this.scanSpec.packageAcceptReject.addToReject(packageNameNormalized);
            String path = AcceptReject.packageNameToPath(packageNameNormalized);
            this.scanSpec.pathAcceptReject.addToReject(path + "/");
            if (packageNameNormalized.contains("*")) continue;
            this.scanSpec.packagePrefixAcceptReject.addToReject(packageNameNormalized + ".");
            this.scanSpec.pathPrefixAcceptReject.addToReject(path + "/");
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistPackages(String ... packageNames) {
        return this.rejectPackages(packageNames);
    }

    public ClassGraph rejectPaths(String ... paths) {
        for (String path : paths) {
            String pathNormalized = AcceptReject.normalizePath(path);
            if (pathNormalized.isEmpty()) {
                throw new IllegalArgumentException("Rejecting the root package (\"\") will cause nothing to be scanned");
            }
            String packageName = AcceptReject.pathToPackageName(pathNormalized);
            this.scanSpec.packageAcceptReject.addToReject(packageName);
            this.scanSpec.pathAcceptReject.addToReject(pathNormalized + "/");
            if (pathNormalized.contains("*")) continue;
            this.scanSpec.packagePrefixAcceptReject.addToReject(packageName + ".");
            this.scanSpec.pathPrefixAcceptReject.addToReject(pathNormalized + "/");
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistPaths(String ... paths) {
        return this.rejectPaths(paths);
    }

    public ClassGraph acceptClasses(String ... classNames) {
        this.enableClassInfo();
        for (String className : classNames) {
            String classNameNormalized = AcceptReject.normalizePackageOrClassName(className);
            this.scanSpec.classAcceptReject.addToAccept(classNameNormalized);
            this.scanSpec.classfilePathAcceptReject.addToAccept(AcceptReject.classNameToClassfilePath(classNameNormalized));
            String packageName = PackageInfo.getParentPackageName(classNameNormalized);
            this.scanSpec.classPackageAcceptReject.addToAccept(packageName);
            this.scanSpec.classPackagePathAcceptReject.addToAccept(AcceptReject.packageNameToPath(packageName) + "/");
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistClasses(String ... classNames) {
        return this.acceptClasses(classNames);
    }

    public ClassGraph rejectClasses(String ... classNames) {
        this.enableClassInfo();
        for (String className : classNames) {
            String classNameNormalized = AcceptReject.normalizePackageOrClassName(className);
            this.scanSpec.classAcceptReject.addToReject(classNameNormalized);
            this.scanSpec.classfilePathAcceptReject.addToReject(AcceptReject.classNameToClassfilePath(classNameNormalized));
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistClasses(String ... classNames) {
        return this.rejectClasses(classNames);
    }

    public ClassGraph acceptJars(String ... jarLeafNames) {
        for (String jarLeafName : jarLeafNames) {
            String leafName = JarUtils.leafName(jarLeafName);
            if (!leafName.equals(jarLeafName)) {
                throw new IllegalArgumentException("Can only accept jars by leafname: " + jarLeafName);
            }
            this.scanSpec.jarAcceptReject.addToAccept(leafName);
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistJars(String ... jarLeafNames) {
        return this.acceptJars(jarLeafNames);
    }

    public ClassGraph rejectJars(String ... jarLeafNames) {
        for (String jarLeafName : jarLeafNames) {
            String leafName = JarUtils.leafName(jarLeafName);
            if (!leafName.equals(jarLeafName)) {
                throw new IllegalArgumentException("Can only reject jars by leafname: " + jarLeafName);
            }
            this.scanSpec.jarAcceptReject.addToReject(leafName);
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistJars(String ... jarLeafNames) {
        return this.rejectJars(jarLeafNames);
    }

    private void acceptOrRejectLibOrExtJars(boolean accept, String ... jarLeafNames) {
        if (jarLeafNames.length == 0) {
            for (String libOrExtJar : SystemJarFinder.getJreLibOrExtJars()) {
                this.acceptOrRejectLibOrExtJars(accept, JarUtils.leafName(libOrExtJar));
            }
        } else {
            for (String jarLeafName : jarLeafNames) {
                String leafName = JarUtils.leafName(jarLeafName);
                if (!leafName.equals(jarLeafName)) {
                    throw new IllegalArgumentException("Can only " + (accept ? "accept" : "reject") + " jars by leafname: " + jarLeafName);
                }
                if (jarLeafName.contains("*")) {
                    Pattern pattern = AcceptReject.globToPattern(jarLeafName, true);
                    boolean found = false;
                    for (String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
                        String libOrExtJarLeafName = JarUtils.leafName(libOrExtJarPath);
                        if (!pattern.matcher(libOrExtJarLeafName).matches()) continue;
                        if (!libOrExtJarLeafName.contains("*")) {
                            this.acceptOrRejectLibOrExtJars(accept, libOrExtJarLeafName);
                        }
                        found = true;
                    }
                    if (found || this.topLevelLog == null) continue;
                    this.topLevelLog.log("Could not find lib or ext jar matching wildcard: " + jarLeafName);
                    continue;
                }
                boolean found = false;
                for (String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
                    String libOrExtJarLeafName = JarUtils.leafName(libOrExtJarPath);
                    if (!jarLeafName.equals(libOrExtJarLeafName)) continue;
                    if (accept) {
                        this.scanSpec.libOrExtJarAcceptReject.addToAccept(jarLeafName);
                    } else {
                        this.scanSpec.libOrExtJarAcceptReject.addToReject(jarLeafName);
                    }
                    if (this.topLevelLog != null) {
                        this.topLevelLog.log((accept ? "Accepting" : "Rejecting") + " lib or ext jar: " + libOrExtJarPath);
                    }
                    found = true;
                    break;
                }
                if (found || this.topLevelLog == null) continue;
                this.topLevelLog.log("Could not find lib or ext jar: " + jarLeafName);
            }
        }
    }

    public ClassGraph acceptLibOrExtJars(String ... jarLeafNames) {
        this.acceptOrRejectLibOrExtJars(true, jarLeafNames);
        return this;
    }

    @Deprecated
    public ClassGraph whitelistLibOrExtJars(String ... jarLeafNames) {
        return this.acceptLibOrExtJars(jarLeafNames);
    }

    public ClassGraph rejectLibOrExtJars(String ... jarLeafNames) {
        this.acceptOrRejectLibOrExtJars(false, jarLeafNames);
        return this;
    }

    @Deprecated
    public ClassGraph blacklistLibOrExtJars(String ... jarLeafNames) {
        return this.rejectLibOrExtJars(jarLeafNames);
    }

    public ClassGraph acceptModules(String ... moduleNames) {
        for (String moduleName : moduleNames) {
            this.scanSpec.moduleAcceptReject.addToAccept(AcceptReject.normalizePackageOrClassName(moduleName));
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistModules(String ... moduleNames) {
        return this.acceptModules(moduleNames);
    }

    public ClassGraph rejectModules(String ... moduleNames) {
        for (String moduleName : moduleNames) {
            this.scanSpec.moduleAcceptReject.addToReject(AcceptReject.normalizePackageOrClassName(moduleName));
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistModules(String ... moduleNames) {
        return this.rejectModules(moduleNames);
    }

    public ClassGraph acceptClasspathElementsContainingResourcePath(String ... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            String resourcePathNormalized = AcceptReject.normalizePath(resourcePath);
            this.scanSpec.classpathElementResourcePathAcceptReject.addToAccept(resourcePathNormalized);
        }
        return this;
    }

    @Deprecated
    public ClassGraph whitelistClasspathElementsContainingResourcePath(String ... resourcePaths) {
        return this.acceptClasspathElementsContainingResourcePath(resourcePaths);
    }

    public ClassGraph rejectClasspathElementsContainingResourcePath(String ... resourcePaths) {
        for (String resourcePath : resourcePaths) {
            String resourcePathNormalized = AcceptReject.normalizePath(resourcePath);
            this.scanSpec.classpathElementResourcePathAcceptReject.addToReject(resourcePathNormalized);
        }
        return this;
    }

    @Deprecated
    public ClassGraph blacklistClasspathElementsContainingResourcePath(String ... resourcePaths) {
        return this.rejectClasspathElementsContainingResourcePath(resourcePaths);
    }

    public ClassGraph enableRemoteJarScanning() {
        this.scanSpec.enableURLScheme("http");
        this.scanSpec.enableURLScheme("https");
        return this;
    }

    public ClassGraph enableURLScheme(String scheme) {
        this.scanSpec.enableURLScheme(scheme);
        return this;
    }

    public ClassGraph enableSystemJarsAndModules() {
        this.enableClassInfo();
        this.scanSpec.enableSystemJarsAndModules = true;
        return this;
    }

    public ClassGraph setMaxBufferedJarRAMSize(int maxBufferedJarRAMSize) {
        this.scanSpec.maxBufferedJarRAMSize = maxBufferedJarRAMSize;
        return this;
    }

    public ClassGraph enableMemoryMapping() {
        this.scanSpec.enableMemoryMapping = true;
        return this;
    }

    public ClassGraph enableMultiReleaseVersions() {
        this.scanSpec.enableMultiReleaseVersions = true;
        this.scanSpec.enableClassInfo = false;
        this.scanSpec.ignoreClassVisibility = false;
        this.scanSpec.enableMethodInfo = false;
        this.scanSpec.ignoreMethodVisibility = false;
        this.scanSpec.enableFieldInfo = false;
        this.scanSpec.ignoreFieldVisibility = false;
        this.scanSpec.enableStaticFinalFieldConstantInitializerValues = false;
        this.scanSpec.enableAnnotationInfo = false;
        this.scanSpec.enableInterClassDependencies = false;
        this.scanSpec.disableRuntimeInvisibleAnnotations = false;
        this.scanSpec.enableExternalClasses = false;
        this.scanSpec.enableSystemJarsAndModules = false;
        return this;
    }

    public ClassGraph enableRealtimeLogging() {
        this.verbose();
        LogNode.logInRealtime(true);
        return this;
    }

    public void scanAsync(final ExecutorService executorService, final int numParallelTasks, final ScanResultProcessor scanResultProcessor, final FailureHandler failureHandler) {
        if (scanResultProcessor == null) {
            throw new IllegalArgumentException("scanResultProcessor cannot be null");
        }
        if (failureHandler == null) {
            throw new IllegalArgumentException("failureHandler cannot be null");
        }
        executorService.execute(new Runnable(){

            @Override
            public void run() {
                try {
                    new Scanner(true, ClassGraph.this.scanSpec, executorService, numParallelTasks, scanResultProcessor, failureHandler, ClassGraph.this.reflectionUtils, ClassGraph.this.topLevelLog).call();
                }
                catch (InterruptedException | CancellationException | ExecutionException e) {
                    failureHandler.onFailure(e);
                }
            }
        });
    }

    private Future<ScanResult> scanAsync(boolean performScan, ExecutorService executorService, int numParallelTasks) {
        try {
            return executorService.submit(new Scanner(performScan, this.scanSpec, executorService, numParallelTasks, null, null, this.reflectionUtils, this.topLevelLog));
        }
        catch (InterruptedException e) {
            return executorService.submit(new Callable<ScanResult>(){

                @Override
                public ScanResult call() throws Exception {
                    throw e;
                }
            });
        }
    }

    public Future<ScanResult> scanAsync(ExecutorService executorService, int numParallelTasks) {
        return this.scanAsync(true, executorService, numParallelTasks);
    }

    public ScanResult scan(ExecutorService executorService, int numParallelTasks) {
        try {
            ScanResult scanResult = this.scanAsync(executorService, numParallelTasks).get();
            if (scanResult == null) {
                throw new NullPointerException();
            }
            return scanResult;
        }
        catch (InterruptedException | CancellationException e) {
            throw new ClassGraphException("Scan interrupted", e);
        }
        catch (ExecutionException e) {
            throw new ClassGraphException("Uncaught exception during scan", InterruptionChecker.getCause(e));
        }
    }

    public ScanResult scan(int numThreads) {
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(numThreads);){
            ScanResult scanResult = this.scan(executorService, numThreads);
            return scanResult;
        }
    }

    public ScanResult scan() {
        return this.scan(DEFAULT_NUM_WORKER_THREADS);
    }

    ScanResult getClasspathScanResult(AutoCloseableExecutorService executorService) {
        try {
            ScanResult scanResult = this.scanAsync(false, executorService, DEFAULT_NUM_WORKER_THREADS).get();
            if (scanResult == null) {
                throw new NullPointerException();
            }
            return scanResult;
        }
        catch (InterruptedException | CancellationException e) {
            throw new ClassGraphException("Scan interrupted", e);
        }
        catch (ExecutionException e) {
            throw new ClassGraphException("Uncaught exception during scan", InterruptionChecker.getCause(e));
        }
    }

    public List<File> getClasspathFiles() {
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(DEFAULT_NUM_WORKER_THREADS);){
            ScanResult scanResult = this.getClasspathScanResult(executorService);
            try {
                List<File> list = scanResult.getClasspathFiles();
                if (scanResult != null) {
                    scanResult.close();
                }
                return list;
            }
            catch (Throwable throwable) {
                if (scanResult != null) {
                    try {
                        scanResult.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public String getClasspath() {
        return JarUtils.pathElementsToPathStr(this.getClasspathFiles());
    }

    public List<URI> getClasspathURIs() {
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(DEFAULT_NUM_WORKER_THREADS);){
            ScanResult scanResult = this.getClasspathScanResult(executorService);
            try {
                List<URI> list = scanResult.getClasspathURIs();
                if (scanResult != null) {
                    scanResult.close();
                }
                return list;
            }
            catch (Throwable throwable) {
                if (scanResult != null) {
                    try {
                        scanResult.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public List<URL> getClasspathURLs() {
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(DEFAULT_NUM_WORKER_THREADS);){
            ScanResult scanResult = this.getClasspathScanResult(executorService);
            try {
                List<URL> list = scanResult.getClasspathURLs();
                if (scanResult != null) {
                    scanResult.close();
                }
                return list;
            }
            catch (Throwable throwable) {
                if (scanResult != null) {
                    try {
                        scanResult.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public List<ModuleRef> getModules() {
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(DEFAULT_NUM_WORKER_THREADS);){
            ScanResult scanResult = this.getClasspathScanResult(executorService);
            try {
                List<ModuleRef> list = scanResult.getModules();
                if (scanResult != null) {
                    scanResult.close();
                }
                return list;
            }
            catch (Throwable throwable) {
                if (scanResult != null) {
                    try {
                        scanResult.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public ModulePathInfo getModulePathInfo() {
        this.scanSpec.modulePathInfo.getRuntimeInfo(this.reflectionUtils);
        return this.scanSpec.modulePathInfo;
    }

    @FunctionalInterface
    public static interface FailureHandler {
        public void onFailure(Throwable var1);
    }

    @FunctionalInterface
    public static interface ScanResultProcessor {
        public void processScanResult(ScanResult var1);
    }

    @FunctionalInterface
    public static interface ClasspathElementURLFilter {
        public boolean includeClasspathElement(URL var1);
    }

    @FunctionalInterface
    public static interface ClasspathElementFilter {
        public boolean includeClasspathElement(String var1);
    }

    public static enum CircumventEncapsulationMethod {
        NONE,
        NARCISSUS,
        JVM_DRIVER;

    }
}

