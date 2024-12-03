/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.ClasspathElement;
import io.github.classgraph.ClasspathElementDir;
import io.github.classgraph.ClasspathElementModule;
import io.github.classgraph.ClasspathElementZip;
import io.github.classgraph.ModuleInfo;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import nonapi.io.github.classgraph.classpath.ClasspathFinder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.classpath.ModuleFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class Scanner
implements Callable<ScanResult> {
    private final ScanSpec scanSpec;
    public boolean performScan;
    private final NestedJarHandler nestedJarHandler;
    private final ExecutorService executorService;
    private final InterruptionChecker interruptionChecker;
    private final int numParallelTasks;
    private final ClassGraph.ScanResultProcessor scanResultProcessor;
    private final ClassGraph.FailureHandler failureHandler;
    private final LogNode topLevelLog;
    private final ClasspathFinder classpathFinder;
    private final List<ClasspathElementModule> moduleOrder;
    private final SingletonMap<Object, ClasspathElement, IOException> classpathEntryObjToClasspathEntrySingletonMap = new SingletonMap<Object, ClasspathElement, IOException>(){

        @Override
        public ClasspathElement newInstance(Object classpathEntryObj, LogNode log) throws IOException, InterruptedException {
            throw new IOException("Should not reach here");
        }
    };

    Scanner(boolean performScan, ScanSpec scanSpec, ExecutorService executorService, int numParallelTasks, ClassGraph.ScanResultProcessor scanResultProcessor, ClassGraph.FailureHandler failureHandler, ReflectionUtils reflectionUtils, LogNode topLevelLog) throws InterruptedException {
        this.scanSpec = scanSpec;
        this.performScan = performScan;
        scanSpec.sortPrefixes();
        scanSpec.log(topLevelLog);
        if (topLevelLog != null) {
            if (scanSpec.pathAcceptReject != null && scanSpec.packagePrefixAcceptReject.isSpecificallyAccepted("")) {
                topLevelLog.log("Note: There is no need to accept the root package (\"\") -- not accepting anything will have the same effect of causing all packages to be scanned");
            }
            topLevelLog.log("Number of worker threads: " + numParallelTasks);
        }
        this.executorService = executorService;
        this.interruptionChecker = executorService instanceof AutoCloseableExecutorService ? ((AutoCloseableExecutorService)executorService).interruptionChecker : new InterruptionChecker();
        this.nestedJarHandler = new NestedJarHandler(scanSpec, this.interruptionChecker, reflectionUtils);
        this.numParallelTasks = numParallelTasks;
        this.scanResultProcessor = scanResultProcessor;
        this.failureHandler = failureHandler;
        this.topLevelLog = topLevelLog;
        LogNode classpathFinderLog = topLevelLog == null ? null : topLevelLog.log("Finding classpath");
        this.classpathFinder = new ClasspathFinder(scanSpec, reflectionUtils, classpathFinderLog);
        try {
            this.moduleOrder = new ArrayList<ClasspathElementModule>();
            ModuleFinder moduleFinder = this.classpathFinder.getModuleFinder();
            if (moduleFinder != null) {
                List<ModuleRef> nonSystemModuleRefs;
                ClassLoader defaultClassLoader;
                List<ModuleRef> systemModuleRefs = moduleFinder.getSystemModuleRefs();
                ClassLoader[] classLoaderOrderRespectingParentDelegation = this.classpathFinder.getClassLoaderOrderRespectingParentDelegation();
                ClassLoader classLoader = defaultClassLoader = classLoaderOrderRespectingParentDelegation != null && classLoaderOrderRespectingParentDelegation.length != 0 ? classLoaderOrderRespectingParentDelegation[0] : null;
                if (systemModuleRefs != null) {
                    for (ModuleRef systemModuleRef : systemModuleRefs) {
                        String moduleName = systemModuleRef.getName();
                        if (scanSpec.enableSystemJarsAndModules && scanSpec.moduleAcceptReject.acceptAndRejectAreEmpty() || scanSpec.moduleAcceptReject.isSpecificallyAcceptedAndNotRejected(moduleName)) {
                            ClasspathElementModule classpathElementModule = new ClasspathElementModule(systemModuleRef, this.nestedJarHandler.moduleRefToModuleReaderProxyRecyclerMap, new ClasspathEntryWorkUnit(null, defaultClassLoader, null, this.moduleOrder.size(), ""), scanSpec);
                            this.moduleOrder.add(classpathElementModule);
                            classpathElementModule.open(null, classpathFinderLog);
                            continue;
                        }
                        if (classpathFinderLog == null) continue;
                        classpathFinderLog.log("Skipping non-accepted or rejected system module: " + moduleName);
                    }
                }
                if ((nonSystemModuleRefs = moduleFinder.getNonSystemModuleRefs()) != null) {
                    for (ModuleRef nonSystemModuleRef : nonSystemModuleRefs) {
                        String moduleName = nonSystemModuleRef.getName();
                        if (moduleName == null) {
                            moduleName = "";
                        }
                        if (scanSpec.moduleAcceptReject.isAcceptedAndNotRejected(moduleName)) {
                            ClasspathElementModule classpathElementModule = new ClasspathElementModule(nonSystemModuleRef, this.nestedJarHandler.moduleRefToModuleReaderProxyRecyclerMap, new ClasspathEntryWorkUnit(null, defaultClassLoader, null, this.moduleOrder.size(), ""), scanSpec);
                            this.moduleOrder.add(classpathElementModule);
                            classpathElementModule.open(null, classpathFinderLog);
                            continue;
                        }
                        if (classpathFinderLog == null) continue;
                        classpathFinderLog.log("Skipping non-accepted or rejected module: " + moduleName);
                    }
                }
            }
        }
        catch (InterruptedException e) {
            this.nestedJarHandler.close(null);
            throw e;
        }
    }

    private static void findClasspathOrderRec(ClasspathElement currClasspathElement, Set<ClasspathElement> visitedClasspathElts, List<ClasspathElement> order) {
        if (visitedClasspathElts.add(currClasspathElement)) {
            if (!currClasspathElement.skipClasspathElement) {
                order.add(currClasspathElement);
            }
            List<ClasspathElement> childClasspathElementsSorted = CollectionUtils.sortCopy(currClasspathElement.childClasspathElements);
            for (ClasspathElement childClasspathElt : childClasspathElementsSorted) {
                Scanner.findClasspathOrderRec(childClasspathElt, visitedClasspathElts, order);
            }
        }
    }

    private List<ClasspathElement> findClasspathOrder(Set<ClasspathElement> toplevelClasspathElts) {
        List<ClasspathElement> toplevelClasspathEltsSorted = CollectionUtils.sortCopy(toplevelClasspathElts);
        HashSet<ClasspathElement> visitedClasspathElts = new HashSet<ClasspathElement>();
        ArrayList<ClasspathElement> order = new ArrayList<ClasspathElement>();
        for (ClasspathElement elt : toplevelClasspathEltsSorted) {
            Scanner.findClasspathOrderRec(elt, visitedClasspathElts, order);
        }
        return order;
    }

    private <W> void processWorkUnits(Collection<W> workUnits, LogNode log, WorkQueue.WorkUnitProcessor<W> workUnitProcessor) throws InterruptedException, ExecutionException {
        WorkQueue.runWorkQueue(workUnits, this.executorService, this.interruptionChecker, this.numParallelTasks, log, workUnitProcessor);
        if (log != null) {
            log.addElapsedTime();
        }
        this.interruptionChecker.check();
    }

    private static Object normalizeClasspathEntry(Object classpathEntryObj) throws IOException {
        if (classpathEntryObj == null) {
            throw new IOException("Got null classpath entry object");
        }
        Object classpathEntryObjNormalized = classpathEntryObj;
        if (!(classpathEntryObjNormalized instanceof Path)) {
            classpathEntryObjNormalized = FastPathResolver.resolve(FileUtils.currDirPath(), classpathEntryObjNormalized.toString());
        }
        if (classpathEntryObjNormalized instanceof String) {
            block25: {
                String classpathEntStr = (String)classpathEntryObjNormalized;
                boolean isURL = JarUtils.URL_SCHEME_PATTERN.matcher(classpathEntStr).matches();
                boolean isMultiSection = classpathEntStr.contains("!");
                if (isURL || isMultiSection) {
                    classpathEntStr = classpathEntStr.replace(" ", "%20").replace("#", "%23");
                    if (!isURL) {
                        classpathEntStr = "file:" + classpathEntStr;
                    }
                    if (isMultiSection) {
                        classpathEntStr = "jar:" + classpathEntStr;
                        classpathEntStr = classpathEntStr.replaceAll("!([^/])", "!/$1");
                    }
                    try {
                        URL classpathEntryURL = new URL(classpathEntStr);
                        classpathEntryObjNormalized = classpathEntryURL;
                        if (isMultiSection) break block25;
                        try {
                            String scheme = classpathEntryURL.getProtocol();
                            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                                URI classpathEntryURI = classpathEntryURL.toURI();
                                classpathEntryObjNormalized = Paths.get(classpathEntryURI);
                            }
                        }
                        catch (IllegalArgumentException | SecurityException | URISyntaxException scheme) {
                        }
                        catch (FileSystemNotFoundException scheme) {}
                    }
                    catch (MalformedURLException e) {
                        try {
                            URI classpathEntryURI = new URI(classpathEntStr);
                            classpathEntryObjNormalized = classpathEntryURI;
                            String scheme = classpathEntryURI.getScheme();
                            if (!"http".equals(scheme) && !"https".equals(scheme)) {
                                classpathEntryObjNormalized = Paths.get(classpathEntryURI);
                            }
                        }
                        catch (URISyntaxException e1) {
                            throw new IOException("Malformed URI: " + classpathEntryObjNormalized + " : " + e1);
                        }
                        catch (IllegalArgumentException | SecurityException e1) {
                        }
                        catch (FileSystemNotFoundException e1) {
                            // empty catch block
                        }
                    }
                }
            }
            if (classpathEntryObjNormalized instanceof String) {
                try {
                    classpathEntryObjNormalized = new File((String)classpathEntryObjNormalized).toPath();
                }
                catch (Exception e) {
                    try {
                        classpathEntryObjNormalized = Paths.get((String)classpathEntryObjNormalized, new String[0]);
                    }
                    catch (InvalidPathException e2) {
                        throw new IOException("Malformed path: " + classpathEntryObj + " : " + e2);
                    }
                }
            }
        }
        if (classpathEntryObjNormalized instanceof Path) {
            try {
                classpathEntryObjNormalized = ((Path)classpathEntryObjNormalized).toRealPath(new LinkOption[0]);
            }
            catch (IOException | SecurityException exception) {
                // empty catch block
            }
        }
        return classpathEntryObjNormalized;
    }

    private WorkQueue.WorkUnitProcessor<ClasspathEntryWorkUnit> newClasspathEntryWorkUnitProcessor(final Set<ClasspathElement> allClasspathEltsOut, final Set<ClasspathElement> toplevelClasspathEltsOut) {
        return new WorkQueue.WorkUnitProcessor<ClasspathEntryWorkUnit>(){

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void processWorkUnit(final ClasspathEntryWorkUnit workUnit, final WorkQueue<ClasspathEntryWorkUnit> workQueue, final LogNode log) throws InterruptedException {
                try {
                    boolean isJar;
                    workUnit.classpathEntryObj = Scanner.normalizeClasspathEntry(workUnit.classpathEntryObj);
                    if (workUnit.classpathEntryObj instanceof URL || workUnit.classpathEntryObj instanceof URI) {
                        isJar = true;
                    } else {
                        if (!(workUnit.classpathEntryObj instanceof Path)) throw new IOException("Got unexpected classpath entry object type " + workUnit.classpathEntryObj.getClass().getName() + " : " + workUnit.classpathEntryObj);
                        Path path = (Path)workUnit.classpathEntryObj;
                        if ("JrtFileSystem".equals(path.getFileSystem().getClass().getSimpleName())) {
                            throw new IOException("Ignoring JrtFS filesystem path (modules are scanned using the JPMS API): " + path);
                        }
                        if (FileUtils.canReadAndIsFile(path)) {
                            isJar = true;
                        } else if (FileUtils.canReadAndIsDir(path)) {
                            isJar = false;
                        } else {
                            if (FileUtils.canRead(path)) throw new IOException("Not a file or directory: " + path);
                            throw new IOException("Cannot read path: " + path);
                        }
                    }
                    Scanner.this.classpathEntryObjToClasspathEntrySingletonMap.get(workUnit.classpathEntryObj, log, new SingletonMap.NewInstanceFactory<ClasspathElement, IOException>(){

                        @Override
                        public ClasspathElement newInstance() throws IOException, InterruptedException {
                            ClasspathElement classpathElement = isJar ? new ClasspathElementZip(workUnit, Scanner.this.nestedJarHandler, Scanner.this.scanSpec) : new ClasspathElementDir(workUnit, Scanner.this.nestedJarHandler, Scanner.this.scanSpec);
                            allClasspathEltsOut.add(classpathElement);
                            LogNode subLog = log == null ? null : log.log("Opening classpath element " + classpathElement);
                            classpathElement.open(workQueue, subLog);
                            if (workUnit.parentClasspathElement != null) {
                                workUnit.parentClasspathElement.childClasspathElements.add(classpathElement);
                            } else {
                                toplevelClasspathEltsOut.add(classpathElement);
                            }
                            return classpathElement;
                        }
                    });
                    return;
                }
                catch (Exception e) {
                    if (log == null) return;
                    log.log("Skipping invalid classpath entry " + workUnit.classpathEntryObj + " : " + (e.getCause() == null ? e : e.getCause()));
                }
            }
        };
    }

    private void findNestedClasspathElements(List<AbstractMap.SimpleEntry<String, ClasspathElement>> classpathElts, LogNode log) {
        CollectionUtils.sortIfNotEmpty(classpathElts, new Comparator<AbstractMap.SimpleEntry<String, ClasspathElement>>(){

            @Override
            public int compare(AbstractMap.SimpleEntry<String, ClasspathElement> o1, AbstractMap.SimpleEntry<String, ClasspathElement> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        block0: for (int i = 0; i < classpathElts.size(); ++i) {
            AbstractMap.SimpleEntry<String, ClasspathElement> ei = classpathElts.get(i);
            String basePath = ei.getKey();
            int basePathLen = basePath.length();
            for (int j = i + 1; j < classpathElts.size(); ++j) {
                String nestedClasspathRelativePath;
                char nextChar;
                AbstractMap.SimpleEntry<String, ClasspathElement> ej = classpathElts.get(j);
                String comparePath = ej.getKey();
                int comparePathLen = comparePath.length();
                boolean foundNestedClasspathRoot = false;
                if (comparePath.startsWith(basePath) && comparePathLen > basePathLen && ((nextChar = comparePath.charAt(basePathLen)) == '/' || nextChar == '!') && (nestedClasspathRelativePath = comparePath.substring(basePathLen + 1)).indexOf(33) < 0) {
                    foundNestedClasspathRoot = true;
                    ClasspathElement baseElement = ei.getValue();
                    if (baseElement.nestedClasspathRootPrefixes == null) {
                        baseElement.nestedClasspathRootPrefixes = new ArrayList<String>();
                    }
                    baseElement.nestedClasspathRootPrefixes.add(nestedClasspathRelativePath + "/");
                    if (log != null) {
                        log.log(basePath + " is a prefix of the nested element " + comparePath);
                    }
                }
                if (!foundNestedClasspathRoot) continue block0;
            }
        }
    }

    private void preprocessClasspathElementsByType(List<ClasspathElement> finalTraditionalClasspathEltOrder, LogNode classpathFinderLog) {
        ArrayList<AbstractMap.SimpleEntry<String, ClasspathElement>> classpathEltDirs = new ArrayList<AbstractMap.SimpleEntry<String, ClasspathElement>>();
        ArrayList<AbstractMap.SimpleEntry<String, ClasspathElement>> classpathEltZips = new ArrayList<AbstractMap.SimpleEntry<String, ClasspathElement>>();
        for (ClasspathElement classpathElt : finalTraditionalClasspathEltOrder) {
            if (classpathElt instanceof ClasspathElementDir) {
                File file = classpathElt.getFile();
                String path = file == null ? classpathElt.toString() : file.getPath();
                classpathEltDirs.add(new AbstractMap.SimpleEntry<String, ClasspathElement>(path, classpathElt));
                continue;
            }
            if (!(classpathElt instanceof ClasspathElementZip)) continue;
            ClasspathElementZip classpathEltZip = (ClasspathElementZip)classpathElt;
            classpathEltZips.add(new AbstractMap.SimpleEntry<String, ClasspathElement>(classpathEltZip.getZipFilePath(), classpathElt));
            if (classpathEltZip.logicalZipFile == null) continue;
            if (classpathEltZip.logicalZipFile.addExportsManifestEntryValue != null) {
                for (String addExports : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addExportsManifestEntryValue, ' ', this.scanSpec)) {
                    this.scanSpec.modulePathInfo.addExports.add(addExports + "=ALL-UNNAMED");
                }
            }
            if (classpathEltZip.logicalZipFile.addOpensManifestEntryValue != null) {
                for (String addOpens : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addOpensManifestEntryValue, ' ', this.scanSpec)) {
                    this.scanSpec.modulePathInfo.addOpens.add(addOpens + "=ALL-UNNAMED");
                }
            }
            if (classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue == null) continue;
            classpathEltZip.moduleNameFromManifestFile = classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue;
        }
        this.findNestedClasspathElements(classpathEltDirs, classpathFinderLog);
        this.findNestedClasspathElements(classpathEltZips, classpathFinderLog);
    }

    private void maskClassfiles(List<ClasspathElement> classpathElementOrder, LogNode maskLog) {
        HashSet<String> acceptedClasspathRelativePathsFound = new HashSet<String>();
        for (int classpathIdx = 0; classpathIdx < classpathElementOrder.size(); ++classpathIdx) {
            ClasspathElement classpathElement = classpathElementOrder.get(classpathIdx);
            classpathElement.maskClassfiles(classpathIdx, acceptedClasspathRelativePathsFound, maskLog);
        }
        if (maskLog != null) {
            maskLog.addElapsedTime();
        }
    }

    private ScanResult performScan(List<ClasspathElement> finalClasspathEltOrder, List<String> finalClasspathEltOrderStrs, ClasspathFinder classpathFinder) throws InterruptedException, ExecutionException {
        if (this.scanSpec.enableClassInfo) {
            this.maskClassfiles(finalClasspathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Masking classfiles"));
        }
        HashMap<File, Long> fileToLastModified = new HashMap<File, Long>();
        for (ClasspathElement classpathElement : finalClasspathEltOrder) {
            fileToLastModified.putAll(classpathElement.fileToLastModified);
        }
        ConcurrentHashMap<String, ClassInfo> classNameToClassInfo = new ConcurrentHashMap<String, ClassInfo>();
        HashMap<String, PackageInfo> packageNameToPackageInfo = new HashMap<String, PackageInfo>();
        HashMap<String, ModuleInfo> moduleNameToModuleInfo = new HashMap<String, ModuleInfo>();
        if (this.scanSpec.enableClassInfo) {
            LogNode linkLog;
            ArrayList<ClassfileScanWorkUnit> classfileScanWorkItems = new ArrayList<ClassfileScanWorkUnit>();
            HashSet<String> acceptedClassNamesFound = new HashSet<String>();
            for (ClasspathElement classpathElement : finalClasspathEltOrder) {
                for (Resource resource : classpathElement.acceptedClassfileResources) {
                    String className = JarUtils.classfilePathToClassName(resource.getPath());
                    if (!(acceptedClassNamesFound.add(className) || className.equals("module-info") || className.equals("package-info") || className.endsWith(".package-info"))) {
                        throw new IllegalArgumentException("Class " + className + " should not have been scheduled more than once for scanning due to classpath masking -- please report this bug at: https://github.com/classgraph/classgraph/issues");
                    }
                    classfileScanWorkItems.add(new ClassfileScanWorkUnit(classpathElement, resource, false));
                }
            }
            ConcurrentLinkedQueue<Classfile> scannedClassfiles = new ConcurrentLinkedQueue<Classfile>();
            ClassfileScannerWorkUnitProcessor classfileWorkUnitProcessor = new ClassfileScannerWorkUnitProcessor(this.scanSpec, finalClasspathEltOrder, Collections.unmodifiableSet(acceptedClassNamesFound), scannedClassfiles);
            this.processWorkUnits(classfileScanWorkItems, this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classfiles"), classfileWorkUnitProcessor);
            LogNode logNode = linkLog = this.topLevelLog == null ? null : this.topLevelLog.log("Linking related classfiles");
            while (!scannedClassfiles.isEmpty()) {
                Classfile c = (Classfile)scannedClassfiles.remove();
                c.link(classNameToClassInfo, packageNameToPackageInfo, moduleNameToModuleInfo);
            }
            if (linkLog != null) {
                linkLog.addElapsedTime();
            }
        } else if (this.topLevelLog != null) {
            this.topLevelLog.log("Classfile scanning is disabled");
        }
        return new ScanResult(this.scanSpec, finalClasspathEltOrder, finalClasspathEltOrderStrs, classpathFinder, classNameToClassInfo, packageNameToPackageInfo, moduleNameToModuleInfo, fileToLastModified, this.nestedJarHandler, this.topLevelLog);
    }

    private ScanResult openClasspathElementsThenScan() throws InterruptedException, ExecutionException {
        ArrayList<ClasspathEntryWorkUnit> rawClasspathEntryWorkUnits = new ArrayList<ClasspathEntryWorkUnit>();
        List<ClasspathOrder.ClasspathEntry> rawClasspathOrder = this.classpathFinder.getClasspathOrder().getOrder();
        for (ClasspathOrder.ClasspathEntry rawClasspathEntry : rawClasspathOrder) {
            rawClasspathEntryWorkUnits.add(new ClasspathEntryWorkUnit(rawClasspathEntry.classpathEntryObj, rawClasspathEntry.classLoader, null, rawClasspathEntryWorkUnits.size(), ""));
        }
        Set<ClasspathElement> allClasspathElts = Collections.newSetFromMap(new ConcurrentHashMap());
        Set<ClasspathElement> toplevelClasspathElts = Collections.newSetFromMap(new ConcurrentHashMap());
        this.processWorkUnits(rawClasspathEntryWorkUnits, this.topLevelLog == null ? null : this.topLevelLog.log("Opening classpath elements"), this.newClasspathEntryWorkUnitProcessor(allClasspathElts, toplevelClasspathElts));
        List<ClasspathElement> classpathEltOrder = this.findClasspathOrder(toplevelClasspathElts);
        this.preprocessClasspathElementsByType(classpathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Finding nested classpath elements"));
        LogNode classpathOrderLog = this.topLevelLog == null ? null : this.topLevelLog.log("Final classpath element order:");
        int numElts = this.moduleOrder.size() + classpathEltOrder.size();
        ArrayList<ClasspathElement> finalClasspathEltOrder = new ArrayList<ClasspathElement>(numElts);
        ArrayList<String> finalClasspathEltOrderStrs = new ArrayList<String>(numElts);
        int classpathOrderIdx = 0;
        for (ClasspathElementModule classpathElementModule : this.moduleOrder) {
            classpathElementModule.classpathElementIdx = classpathOrderIdx++;
            finalClasspathEltOrder.add(classpathElementModule);
            finalClasspathEltOrderStrs.add(classpathElementModule.toString());
            if (classpathOrderLog == null) continue;
            ModuleRef moduleRef = classpathElementModule.getModuleRef();
            classpathOrderLog.log(moduleRef.toString());
        }
        for (ClasspathElement classpathElement : classpathEltOrder) {
            classpathElement.classpathElementIdx = classpathOrderIdx++;
            finalClasspathEltOrder.add(classpathElement);
            finalClasspathEltOrderStrs.add(classpathElement.toString());
            if (classpathOrderLog == null) continue;
            classpathOrderLog.log(classpathElement.toString());
        }
        this.processWorkUnits(finalClasspathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classpath elements"), new WorkQueue.WorkUnitProcessor<ClasspathElement>(){

            @Override
            public void processWorkUnit(ClasspathElement classpathElement, WorkQueue<ClasspathElement> workQueueIgnored, LogNode pathScanLog) throws InterruptedException {
                classpathElement.scanPaths(pathScanLog);
            }
        });
        ArrayList<ClasspathElement> finalClasspathEltOrderFiltered = finalClasspathEltOrder;
        if (!this.scanSpec.classpathElementResourcePathAcceptReject.acceptIsEmpty()) {
            finalClasspathEltOrderFiltered = new ArrayList(finalClasspathEltOrder.size());
            for (ClasspathElement classpathElement : finalClasspathEltOrder) {
                if (!classpathElement.containsSpecificallyAcceptedClasspathElementResourcePath) continue;
                finalClasspathEltOrderFiltered.add(classpathElement);
            }
        }
        if (this.performScan) {
            return this.performScan(finalClasspathEltOrderFiltered, finalClasspathEltOrderStrs, this.classpathFinder);
        }
        if (this.topLevelLog != null) {
            this.topLevelLog.log("Only returning classpath elements (not performing a scan)");
        }
        return new ScanResult(this.scanSpec, finalClasspathEltOrderFiltered, finalClasspathEltOrderStrs, this.classpathFinder, null, null, null, null, this.nestedJarHandler, this.topLevelLog);
    }

    @Override
    public ScanResult call() throws InterruptedException, CancellationException, ExecutionException {
        boolean removeTemporaryFilesAfterScan;
        ScanResult scanResult;
        block13: {
            scanResult = null;
            long scanStart = System.currentTimeMillis();
            removeTemporaryFilesAfterScan = this.scanSpec.removeTemporaryFilesAfterScan;
            try {
                scanResult = this.openClasspathElementsThenScan();
                if (this.topLevelLog != null) {
                    this.topLevelLog.log("~", String.format("Total time: %.3f sec", (double)(System.currentTimeMillis() - scanStart) * 0.001));
                    this.topLevelLog.flush();
                }
                if (this.scanResultProcessor == null) break block13;
                try {
                    this.scanResultProcessor.processScanResult(scanResult);
                }
                catch (Exception e) {
                    scanResult.close();
                    throw new ExecutionException(e);
                }
                scanResult.close();
            }
            catch (Throwable e) {
                if (this.topLevelLog != null) {
                    this.topLevelLog.log("~", e instanceof InterruptedException || e instanceof CancellationException ? "Scan interrupted or canceled" : (e instanceof ExecutionException || e instanceof RuntimeException ? "Uncaught exception during scan" : e.getMessage()), InterruptionChecker.getCause(e));
                    this.topLevelLog.flush();
                }
                removeTemporaryFilesAfterScan = true;
                this.interruptionChecker.interrupt();
                if (this.failureHandler == null) {
                    if (removeTemporaryFilesAfterScan) {
                        this.nestedJarHandler.close(this.topLevelLog);
                    }
                    throw e;
                }
                try {
                    this.failureHandler.onFailure(e);
                }
                catch (Exception f) {
                    if (this.topLevelLog != null) {
                        this.topLevelLog.log("~", "The failure handler threw an exception:", (Throwable)f);
                        this.topLevelLog.flush();
                    }
                    ExecutionException failureHandlerException = new ExecutionException("Exception while calling failure handler", f);
                    failureHandlerException.addSuppressed(e);
                    if (removeTemporaryFilesAfterScan) {
                        this.nestedJarHandler.close(this.topLevelLog);
                    }
                    throw failureHandlerException;
                }
            }
        }
        if (removeTemporaryFilesAfterScan) {
            this.nestedJarHandler.close(this.topLevelLog);
        }
        return scanResult;
    }

    private static class ClassfileScannerWorkUnitProcessor
    implements WorkQueue.WorkUnitProcessor<ClassfileScanWorkUnit> {
        private final ScanSpec scanSpec;
        private final List<ClasspathElement> classpathOrder;
        private final Set<String> acceptedClassNamesFound;
        private final Set<String> classNamesScheduledForExtendedScanning = Collections.newSetFromMap(new ConcurrentHashMap());
        private final Queue<Classfile> scannedClassfiles;
        private final ConcurrentHashMap<String, String> stringInternMap = new ConcurrentHashMap();

        public ClassfileScannerWorkUnitProcessor(ScanSpec scanSpec, List<ClasspathElement> classpathOrder, Set<String> acceptedClassNamesFound, Queue<Classfile> scannedClassfiles) {
            this.scanSpec = scanSpec;
            this.classpathOrder = classpathOrder;
            this.acceptedClassNamesFound = acceptedClassNamesFound;
            this.scannedClassfiles = scannedClassfiles;
        }

        @Override
        public void processWorkUnit(ClassfileScanWorkUnit workUnit, WorkQueue<ClassfileScanWorkUnit> workQueue, LogNode log) throws InterruptedException {
            block9: {
                LogNode subLog = ((ClassfileScanWorkUnit)workUnit).classfileResource.scanLog == null ? null : ((ClassfileScanWorkUnit)workUnit).classfileResource.scanLog.log(workUnit.classfileResource.getPath(), "Parsing classfile");
                try {
                    Classfile classfile = new Classfile(workUnit.classpathElement, this.classpathOrder, this.acceptedClassNamesFound, this.classNamesScheduledForExtendedScanning, workUnit.classfileResource.getPath(), workUnit.classfileResource, workUnit.isExternalClass, this.stringInternMap, workQueue, this.scanSpec, subLog);
                    this.scannedClassfiles.add(classfile);
                    if (subLog != null) {
                        subLog.addElapsedTime();
                    }
                }
                catch (Classfile.SkipClassException e) {
                    if (subLog != null) {
                        subLog.log(workUnit.classfileResource.getPath(), "Skipping classfile: " + e.getMessage());
                        subLog.addElapsedTime();
                    }
                }
                catch (Classfile.ClassfileFormatException e) {
                    if (subLog != null) {
                        subLog.log(workUnit.classfileResource.getPath(), "Invalid classfile: " + e.getMessage());
                        subLog.addElapsedTime();
                    }
                }
                catch (IOException e) {
                    if (subLog != null) {
                        subLog.log(workUnit.classfileResource.getPath(), "Could not read classfile: " + e);
                        subLog.addElapsedTime();
                    }
                }
                catch (Exception e) {
                    if (subLog == null) break block9;
                    subLog.log(workUnit.classfileResource.getPath(), "Could not read classfile", (Throwable)e);
                    subLog.addElapsedTime();
                }
            }
        }
    }

    static class ClassfileScanWorkUnit {
        private final ClasspathElement classpathElement;
        private final Resource classfileResource;
        private final boolean isExternalClass;

        ClassfileScanWorkUnit(ClasspathElement classpathElement, Resource classfileResource, boolean isExternalClass) {
            this.classpathElement = classpathElement;
            this.classfileResource = classfileResource;
            this.isExternalClass = isExternalClass;
        }
    }

    static class ClasspathEntryWorkUnit {
        Object classpathEntryObj;
        final ClassLoader classLoader;
        final ClasspathElement parentClasspathElement;
        final int classpathElementIdxWithinParent;
        final String packageRootPrefix;

        public ClasspathEntryWorkUnit(Object classpathEntryObj, ClassLoader classLoader, ClasspathElement parentClasspathElement, int classpathElementIdxWithinParent, String packageRootPrefix) {
            this.classpathEntryObj = classpathEntryObj;
            this.classLoader = classLoader;
            this.parentClasspathElement = parentClasspathElement;
            this.classpathElementIdxWithinParent = classpathElementIdxWithinParent;
            this.packageRootPrefix = packageRootPrefix;
        }
    }
}

