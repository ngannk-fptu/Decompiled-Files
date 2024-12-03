/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationClassRef;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassGraphClassLoader;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ClasspathElement;
import io.github.classgraph.ClasspathElementModule;
import io.github.classgraph.ModuleInfo;
import io.github.classgraph.ModuleInfoList;
import io.github.classgraph.ModulePathInfo;
import io.github.classgraph.ModuleRef;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.PackageInfoList;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import java.io.Closeable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classpath.ClasspathFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.json.JSONDeserializer;
import nonapi.io.github.classgraph.json.JSONSerializer;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.AcceptReject;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public final class ScanResult
implements Closeable {
    private List<String> rawClasspathEltOrderStrs;
    private List<ClasspathElement> classpathOrder;
    private ResourceList allAcceptedResourcesCached;
    private final AtomicInteger getResourcesWithPathCallCount = new AtomicInteger();
    private Map<String, ResourceList> pathToAcceptedResourcesCached;
    Map<String, ClassInfo> classNameToClassInfo;
    private Map<String, PackageInfo> packageNameToPackageInfo;
    private Map<String, ModuleInfo> moduleNameToModuleInfo;
    private Map<File, Long> fileToLastModified;
    private boolean isObtainedFromDeserialization;
    private ClassGraphClassLoader classGraphClassLoader;
    ClasspathFinder classpathFinder;
    private NestedJarHandler nestedJarHandler;
    ScanSpec scanSpec;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    protected ReflectionUtils reflectionUtils;
    private final LogNode topLevelLog;
    private final WeakReference<ScanResult> weakReference;
    private static Set<WeakReference<ScanResult>> nonClosedWeakReferences = Collections.newSetFromMap(new ConcurrentHashMap());
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final String CURRENT_SERIALIZATION_FORMAT = "10";

    static void init(ReflectionUtils reflectionUtils) {
        if (!initialized.getAndSet(true)) {
            FileUtils.closeDirectByteBuffer(ByteBuffer.allocateDirect(32), reflectionUtils, null);
        }
    }

    ScanResult(ScanSpec scanSpec, List<ClasspathElement> classpathOrder, List<String> rawClasspathEltOrderStrs, ClasspathFinder classpathFinder, Map<String, ClassInfo> classNameToClassInfo, Map<String, PackageInfo> packageNameToPackageInfo, Map<String, ModuleInfo> moduleNameToModuleInfo, Map<File, Long> fileToLastModified, NestedJarHandler nestedJarHandler, LogNode topLevelLog) {
        this.scanSpec = scanSpec;
        this.rawClasspathEltOrderStrs = rawClasspathEltOrderStrs;
        this.classpathOrder = classpathOrder;
        this.classpathFinder = classpathFinder;
        this.fileToLastModified = fileToLastModified;
        this.classNameToClassInfo = classNameToClassInfo;
        this.packageNameToPackageInfo = packageNameToPackageInfo;
        this.moduleNameToModuleInfo = moduleNameToModuleInfo;
        this.nestedJarHandler = nestedJarHandler;
        this.reflectionUtils = nestedJarHandler.reflectionUtils;
        this.topLevelLog = topLevelLog;
        if (classNameToClassInfo != null) {
            this.indexResourcesAndClassInfo(topLevelLog);
        }
        if (classNameToClassInfo != null) {
            HashSet<String> allRepeatableAnnotationNames = new HashSet<String>();
            for (ClassInfo classInfo : classNameToClassInfo.values()) {
                AnnotationClassRef classRef;
                String repeatableAnnotationName;
                Object val;
                AnnotationParameterValueList vals;
                AnnotationInfo repeatableMetaAnnotation;
                if (!classInfo.isAnnotation() || classInfo.annotationInfo == null || (repeatableMetaAnnotation = (AnnotationInfo)classInfo.annotationInfo.get("java.lang.annotation.Repeatable")) == null || (vals = repeatableMetaAnnotation.getParameterValues()).isEmpty() || !((val = vals.getValue("value")) instanceof AnnotationClassRef) || (repeatableAnnotationName = (classRef = (AnnotationClassRef)val).getName()) == null) continue;
                allRepeatableAnnotationNames.add(repeatableAnnotationName);
            }
            if (!allRepeatableAnnotationNames.isEmpty()) {
                for (ClassInfo classInfo : classNameToClassInfo.values()) {
                    classInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames);
                }
            }
        }
        this.classGraphClassLoader = new ClassGraphClassLoader(this);
        this.weakReference = new WeakReference<ScanResult>(this);
        nonClosedWeakReferences.add(this.weakReference);
    }

    private void indexResourcesAndClassInfo(LogNode log) {
        Collection<ClassInfo> allClassInfo = this.classNameToClassInfo.values();
        for (ClassInfo classInfo : allClassInfo) {
            classInfo.setScanResult(this);
        }
        if (this.scanSpec.enableInterClassDependencies) {
            for (ClassInfo ci : new ArrayList<ClassInfo>(this.classNameToClassInfo.values())) {
                HashSet<ClassInfo> refdClassesFiltered = new HashSet<ClassInfo>();
                for (ClassInfo refdClassInfo : ci.findReferencedClassInfo(log)) {
                    if (refdClassInfo == null || ci.equals(refdClassInfo) || refdClassInfo.getName().equals("java.lang.Object") || refdClassInfo.isExternalClass() && !this.scanSpec.enableExternalClasses) continue;
                    refdClassInfo.setScanResult(this);
                    refdClassesFiltered.add(refdClassInfo);
                }
                ci.setReferencedClasses(new ClassInfoList(refdClassesFiltered, true));
            }
        }
    }

    public List<File> getClasspathFiles() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ArrayList<File> classpathElementOrderFiles = new ArrayList<File>();
        for (ClasspathElement classpathElement : this.classpathOrder) {
            File file = classpathElement.getFile();
            if (file == null) continue;
            classpathElementOrderFiles.add(file);
        }
        return classpathElementOrderFiles;
    }

    public String getClasspath() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        return JarUtils.pathElementsToPathStr(this.getClasspathFiles());
    }

    public List<URI> getClasspathURIs() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ArrayList<URI> classpathElementOrderURIs = new ArrayList<URI>();
        for (ClasspathElement classpathElement : this.classpathOrder) {
            try {
                for (URI uri : classpathElement.getAllURIs()) {
                    if (uri == null) continue;
                    classpathElementOrderURIs.add(uri);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
        }
        return classpathElementOrderURIs;
    }

    public List<URL> getClasspathURLs() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ArrayList<URL> classpathElementOrderURLs = new ArrayList<URL>();
        for (URI uri : this.getClasspathURIs()) {
            try {
                classpathElementOrderURLs.add(uri.toURL());
            }
            catch (IllegalArgumentException | MalformedURLException exception) {}
        }
        return classpathElementOrderURLs;
    }

    public List<ModuleRef> getModules() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ArrayList<ModuleRef> moduleRefs = new ArrayList<ModuleRef>();
        for (ClasspathElement classpathElement : this.classpathOrder) {
            if (!(classpathElement instanceof ClasspathElementModule)) continue;
            moduleRefs.add(((ClasspathElementModule)classpathElement).getModuleRef());
        }
        return moduleRefs;
    }

    public ModulePathInfo getModulePathInfo() {
        this.scanSpec.modulePathInfo.getRuntimeInfo(this.reflectionUtils);
        return this.scanSpec.modulePathInfo;
    }

    public ResourceList getAllResources() {
        if (this.allAcceptedResourcesCached == null) {
            ResourceList acceptedResourcesList = new ResourceList();
            for (ClasspathElement classpathElt : this.classpathOrder) {
                acceptedResourcesList.addAll(classpathElt.acceptedResources);
            }
            this.allAcceptedResourcesCached = acceptedResourcesList;
        }
        return this.allAcceptedResourcesCached;
    }

    public Map<String, ResourceList> getAllResourcesAsMap() {
        if (this.pathToAcceptedResourcesCached == null) {
            HashMap<String, ResourceList> pathToAcceptedResourceListMap = new HashMap<String, ResourceList>();
            for (Resource res : this.getAllResources()) {
                ResourceList resList = (ResourceList)pathToAcceptedResourceListMap.get(res.getPath());
                if (resList == null) {
                    resList = new ResourceList();
                    pathToAcceptedResourceListMap.put(res.getPath(), resList);
                }
                resList.add(res);
            }
            this.pathToAcceptedResourcesCached = pathToAcceptedResourceListMap;
        }
        return this.pathToAcceptedResourcesCached;
    }

    public ResourceList getResourcesWithPath(String resourcePath) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        String path = FileUtils.sanitizeEntryPath(resourcePath, true, true);
        ResourceList matchingResources = null;
        if (this.getResourcesWithPathCallCount.incrementAndGet() > 3) {
            matchingResources = this.getAllResourcesAsMap().get(path);
        } else {
            for (ClasspathElement classpathElt : this.classpathOrder) {
                for (Resource res : classpathElt.acceptedResources) {
                    if (!res.getPath().equals(path)) continue;
                    if (matchingResources == null) {
                        matchingResources = new ResourceList();
                    }
                    matchingResources.add(res);
                }
            }
        }
        return matchingResources == null ? ResourceList.EMPTY_LIST : matchingResources;
    }

    public ResourceList getResourcesWithPathIgnoringAccept(String resourcePath) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        String path = FileUtils.sanitizeEntryPath(resourcePath, true, true);
        ResourceList matchingResources = new ResourceList();
        for (ClasspathElement classpathElt : this.classpathOrder) {
            Resource matchingResource = classpathElt.getResource(path);
            if (matchingResource == null) continue;
            matchingResources.add(matchingResource);
        }
        return matchingResources;
    }

    @Deprecated
    public ResourceList getResourcesWithPathIgnoringWhitelist(String resourcePath) {
        return this.getResourcesWithPathIgnoringAccept(resourcePath);
    }

    public ResourceList getResourcesWithLeafName(String leafName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ResourceList allAcceptedResources = this.getAllResources();
        if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
        }
        ResourceList filteredResources = new ResourceList();
        for (Resource classpathResource : allAcceptedResources) {
            int lastSlashIdx;
            String relativePath = classpathResource.getPath();
            if (!relativePath.substring((lastSlashIdx = relativePath.lastIndexOf(47)) + 1).equals(leafName)) continue;
            filteredResources.add(classpathResource);
        }
        return filteredResources;
    }

    public ResourceList getResourcesWithExtension(String extension) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ResourceList allAcceptedResources = this.getAllResources();
        if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
        }
        String bareExtension = extension;
        while (bareExtension.startsWith(".")) {
            bareExtension = bareExtension.substring(1);
        }
        ResourceList filteredResources = new ResourceList();
        for (Resource classpathResource : allAcceptedResources) {
            String relativePath = classpathResource.getPath();
            int lastSlashIdx = relativePath.lastIndexOf(47);
            int lastDotIdx = relativePath.lastIndexOf(46);
            if (lastDotIdx <= lastSlashIdx || !relativePath.substring(lastDotIdx + 1).equalsIgnoreCase(bareExtension)) continue;
            filteredResources.add(classpathResource);
        }
        return filteredResources;
    }

    public ResourceList getResourcesMatchingPattern(Pattern pattern) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        ResourceList allAcceptedResources = this.getAllResources();
        if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
        }
        ResourceList filteredResources = new ResourceList();
        for (Resource classpathResource : allAcceptedResources) {
            String relativePath = classpathResource.getPath();
            if (!pattern.matcher(relativePath).matches()) continue;
            filteredResources.add(classpathResource);
        }
        return filteredResources;
    }

    public ResourceList getResourcesMatchingWildcard(String wildcardString) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        return this.getResourcesMatchingPattern(AcceptReject.globToPattern(wildcardString, false));
    }

    public ModuleInfo getModuleInfo(String moduleName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return this.moduleNameToModuleInfo.get(moduleName);
    }

    public ModuleInfoList getModuleInfo() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return new ModuleInfoList(this.moduleNameToModuleInfo.values());
    }

    public PackageInfo getPackageInfo(String packageName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return this.packageNameToPackageInfo.get(packageName);
    }

    public PackageInfoList getPackageInfo() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return new PackageInfoList(this.packageNameToPackageInfo.values());
    }

    public Map<ClassInfo, ClassInfoList> getClassDependencyMap() {
        HashMap<ClassInfo, ClassInfoList> map = new HashMap<ClassInfo, ClassInfoList>();
        for (ClassInfo ci : this.getAllClasses()) {
            map.put(ci, ci.getClassDependencies());
        }
        return map;
    }

    public Map<ClassInfo, ClassInfoList> getReverseClassDependencyMap() {
        HashMap<ClassInfo, HashSet<ClassInfo>> revMapSet = new HashMap<ClassInfo, HashSet<ClassInfo>>();
        for (ClassInfo ci : this.getAllClasses()) {
            for (ClassInfo dep : ci.getClassDependencies()) {
                HashSet<ClassInfo> set = (HashSet<ClassInfo>)revMapSet.get(dep);
                if (set == null) {
                    set = new HashSet<ClassInfo>();
                    revMapSet.put(dep, set);
                }
                set.add(ci);
            }
        }
        HashMap<ClassInfo, ClassInfoList> revMapList = new HashMap<ClassInfo, ClassInfoList>();
        for (Map.Entry ent : revMapSet.entrySet()) {
            revMapList.put((ClassInfo)ent.getKey(), new ClassInfoList((Set)ent.getValue(), true));
        }
        return revMapList;
    }

    public ClassInfo getClassInfo(String className) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return this.classNameToClassInfo.get(className);
    }

    public ClassInfoList getAllClasses() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return ClassInfo.getAllClasses(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getAllEnums() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return ClassInfo.getAllEnums(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getAllRecords() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return ClassInfo.getAllRecords(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public Map<String, ClassInfo> getAllClassesAsMap() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return this.classNameToClassInfo;
    }

    public ClassInfoList getAllStandardClasses() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return ClassInfo.getAllStandardClasses(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getSubclasses(Class<?> superclass) {
        return this.getSubclasses(superclass.getName());
    }

    public ClassInfoList getSubclasses(String superclassName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        if (superclassName.equals("java.lang.Object")) {
            return this.getAllStandardClasses();
        }
        ClassInfo superclass = this.classNameToClassInfo.get(superclassName);
        return superclass == null ? ClassInfoList.EMPTY_LIST : superclass.getSubclasses();
    }

    public ClassInfoList getSuperclasses(String subclassName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        ClassInfo subclass = this.classNameToClassInfo.get(subclassName);
        return subclass == null ? ClassInfoList.EMPTY_LIST : subclass.getSuperclasses();
    }

    public ClassInfoList getSuperclasses(Class<?> subclass) {
        return this.getSuperclasses(subclass.getName());
    }

    public ClassInfoList getClassesWithMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
        Assert.isAnnotation(methodAnnotation);
        return this.getClassesWithMethodAnnotation(methodAnnotation.getName());
    }

    public ClassInfoList getClassesWithMethodAnnotation(String methodAnnotationName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!(this.scanSpec.enableClassInfo && this.scanSpec.enableMethodInfo && this.scanSpec.enableAnnotationInfo)) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableMethodInfo(), and #enableAnnotationInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(methodAnnotationName);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithMethodAnnotation();
    }

    public ClassInfoList getClassesWithMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
        Assert.isAnnotation(methodParameterAnnotation);
        return this.getClassesWithMethodParameterAnnotation(methodParameterAnnotation.getName());
    }

    public ClassInfoList getClassesWithMethodParameterAnnotation(String methodParameterAnnotationName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!(this.scanSpec.enableClassInfo && this.scanSpec.enableMethodInfo && this.scanSpec.enableAnnotationInfo)) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableMethodInfo(), and #enableAnnotationInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(methodParameterAnnotationName);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithMethodParameterAnnotation();
    }

    public ClassInfoList getClassesWithFieldAnnotation(Class<? extends Annotation> fieldAnnotation) {
        Assert.isAnnotation(fieldAnnotation);
        return this.getClassesWithFieldAnnotation(fieldAnnotation.getName());
    }

    public ClassInfoList getClassesWithFieldAnnotation(String fieldAnnotationName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!(this.scanSpec.enableClassInfo && this.scanSpec.enableFieldInfo && this.scanSpec.enableAnnotationInfo)) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableFieldInfo(), and #enableAnnotationInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(fieldAnnotationName);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithFieldAnnotation();
    }

    public ClassInfoList getAllInterfaces() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        return ClassInfo.getAllImplementedInterfaceClasses(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getInterfaces(String className) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(className);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getInterfaces();
    }

    public ClassInfoList getInterfaces(Class<?> classRef) {
        return this.getInterfaces(classRef.getName());
    }

    public ClassInfoList getClassesImplementing(Class<?> interfaceClass) {
        Assert.isInterface(interfaceClass);
        return this.getClassesImplementing(interfaceClass.getName());
    }

    public ClassInfoList getClassesImplementing(String interfaceName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(interfaceName);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesImplementing();
    }

    public ClassInfoList getAllAnnotations() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo || !this.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
        }
        return ClassInfo.getAllAnnotationClasses(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getAllInterfacesAndAnnotations() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo || !this.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
        }
        return ClassInfo.getAllInterfacesOrAnnotationClasses(this.classNameToClassInfo.values(), this.scanSpec);
    }

    public ClassInfoList getClassesWithAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.getClassesWithAnnotation(annotation.getName());
    }

    public ClassInfoList getClassesWithAnnotation(String annotationName) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo || !this.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(annotationName);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithAnnotation();
    }

    public ClassInfoList getAnnotationsOnClass(String className) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo || !this.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
        }
        ClassInfo classInfo = this.classNameToClassInfo.get(className);
        return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getAnnotations();
    }

    public boolean classpathContentsModifiedSinceScan() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (this.fileToLastModified == null) {
            return true;
        }
        for (Map.Entry<File, Long> ent : this.fileToLastModified.entrySet()) {
            if (ent.getKey().lastModified() == ent.getValue().longValue()) continue;
            return true;
        }
        return false;
    }

    public long classpathContentsLastModifiedTime() {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        long maxLastModifiedTime = 0L;
        if (this.fileToLastModified != null) {
            long currTime = System.currentTimeMillis();
            for (long timestamp : this.fileToLastModified.values()) {
                if (timestamp <= maxLastModifiedTime || timestamp >= currTime) continue;
                maxLastModifiedTime = timestamp;
            }
        }
        return maxLastModifiedTime;
    }

    ClassLoader[] getClassLoaderOrderRespectingParentDelegation() {
        return this.classpathFinder.getClassLoaderOrderRespectingParentDelegation();
    }

    public Class<?> loadClass(String className, boolean returnNullIfClassNotFound) throws IllegalArgumentException {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (className == null || className.isEmpty()) {
            throw new NullPointerException("className cannot be null or empty");
        }
        try {
            return Class.forName(className, this.scanSpec.initializeLoadedClasses, this.classGraphClassLoader);
        }
        catch (ClassNotFoundException | LinkageError e) {
            if (returnNullIfClassNotFound) {
                return null;
            }
            throw new IllegalArgumentException("Could not load class " + className + " : " + e, e);
        }
    }

    public <T> Class<T> loadClass(String className, Class<T> superclassOrInterfaceType, boolean returnNullIfClassNotFound) throws IllegalArgumentException {
        Class<?> loadedClass;
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (className == null || className.isEmpty()) {
            throw new NullPointerException("className cannot be null or empty");
        }
        if (superclassOrInterfaceType == null) {
            throw new NullPointerException("superclassOrInterfaceType parameter cannot be null");
        }
        try {
            loadedClass = Class.forName(className, this.scanSpec.initializeLoadedClasses, this.classGraphClassLoader);
        }
        catch (ClassNotFoundException | LinkageError e) {
            if (returnNullIfClassNotFound) {
                return null;
            }
            throw new IllegalArgumentException("Could not load class " + className + " : " + e);
        }
        if (loadedClass != null && !superclassOrInterfaceType.isAssignableFrom(loadedClass)) {
            if (returnNullIfClassNotFound) {
                return null;
            }
            throw new IllegalArgumentException("Loaded class " + loadedClass.getName() + " cannot be cast to " + superclassOrInterfaceType.getName());
        }
        Class<?> castClass = loadedClass;
        return castClass;
    }

    public static ScanResult fromJSON(String json) {
        ScanResult scanResult;
        Matcher matcher = Pattern.compile("\\{[\\n\\r ]*\"format\"[ ]?:[ ]?\"([^\"]+)\"").matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("JSON is not in correct format");
        }
        if (!CURRENT_SERIALIZATION_FORMAT.equals(matcher.group(1))) {
            throw new IllegalArgumentException("JSON was serialized in a different format from the format used by the current version of ClassGraph -- please serialize and deserialize your ScanResult using the same version of ClassGraph");
        }
        SerializationFormat deserialized = JSONDeserializer.deserializeObject(SerializationFormat.class, json);
        if (deserialized == null || !deserialized.format.equals(CURRENT_SERIALIZATION_FORMAT)) {
            throw new IllegalArgumentException("JSON was serialized by newer version of ClassGraph");
        }
        ClassGraph classGraph = new ClassGraph();
        classGraph.scanSpec = deserialized.scanSpec;
        try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(ClassGraph.DEFAULT_NUM_WORKER_THREADS);){
            scanResult = classGraph.getClasspathScanResult(executorService);
        }
        scanResult.rawClasspathEltOrderStrs = deserialized.classpath;
        scanResult.scanSpec = deserialized.scanSpec;
        scanResult.classNameToClassInfo = new HashMap<String, ClassInfo>();
        if (deserialized.classInfo != null) {
            for (ClassInfo ci : deserialized.classInfo) {
                scanResult.classNameToClassInfo.put(ci.getName(), ci);
                ci.setScanResult(scanResult);
            }
        }
        scanResult.moduleNameToModuleInfo = new HashMap<String, ModuleInfo>();
        if (deserialized.moduleInfo != null) {
            for (ModuleInfo mi : deserialized.moduleInfo) {
                scanResult.moduleNameToModuleInfo.put(mi.getName(), mi);
            }
        }
        scanResult.packageNameToPackageInfo = new HashMap<String, PackageInfo>();
        if (deserialized.packageInfo != null) {
            for (PackageInfo pi : deserialized.packageInfo) {
                scanResult.packageNameToPackageInfo.put(pi.getName(), pi);
            }
        }
        scanResult.indexResourcesAndClassInfo(null);
        scanResult.isObtainedFromDeserialization = true;
        return scanResult;
    }

    public String toJSON(int indentWidth) {
        if (this.closed.get()) {
            throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
        }
        if (!this.scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
        }
        ArrayList<ClassInfo> allClassInfo = new ArrayList<ClassInfo>(this.classNameToClassInfo.values());
        CollectionUtils.sortIfNotEmpty(allClassInfo);
        ArrayList<PackageInfo> allPackageInfo = new ArrayList<PackageInfo>(this.packageNameToPackageInfo.values());
        CollectionUtils.sortIfNotEmpty(allPackageInfo);
        ArrayList<ModuleInfo> allModuleInfo = new ArrayList<ModuleInfo>(this.moduleNameToModuleInfo.values());
        CollectionUtils.sortIfNotEmpty(allModuleInfo);
        return JSONSerializer.serializeObject(new SerializationFormat(CURRENT_SERIALIZATION_FORMAT, this.scanSpec, allClassInfo, allPackageInfo, allModuleInfo, this.rawClasspathEltOrderStrs), indentWidth, false);
    }

    public String toJSON() {
        return this.toJSON(0);
    }

    public boolean isObtainedFromDeserialization() {
        return this.isObtainedFromDeserialization;
    }

    @Override
    public void close() {
        if (!this.closed.getAndSet(true)) {
            nonClosedWeakReferences.remove(this.weakReference);
            if (this.classpathOrder != null) {
                this.classpathOrder.clear();
                this.classpathOrder = null;
            }
            if (this.allAcceptedResourcesCached != null) {
                for (Resource classpathResource : this.allAcceptedResourcesCached) {
                    classpathResource.close();
                }
                this.allAcceptedResourcesCached.clear();
                this.allAcceptedResourcesCached = null;
            }
            if (this.pathToAcceptedResourcesCached != null) {
                this.pathToAcceptedResourcesCached.clear();
                this.pathToAcceptedResourcesCached = null;
            }
            this.classGraphClassLoader = null;
            if (this.classNameToClassInfo != null) {
                // empty if block
            }
            if (this.packageNameToPackageInfo != null) {
                this.packageNameToPackageInfo.clear();
                this.packageNameToPackageInfo = null;
            }
            if (this.moduleNameToModuleInfo != null) {
                this.moduleNameToModuleInfo.clear();
                this.moduleNameToModuleInfo = null;
            }
            if (this.fileToLastModified != null) {
                this.fileToLastModified.clear();
                this.fileToLastModified = null;
            }
            if (this.nestedJarHandler != null) {
                this.nestedJarHandler.close(this.topLevelLog);
                this.nestedJarHandler = null;
            }
            this.classGraphClassLoader = null;
            this.classpathFinder = null;
            this.reflectionUtils = null;
            if (this.topLevelLog != null) {
                this.topLevelLog.flush();
            }
        }
    }

    public static void closeAll() {
        for (WeakReference<ScanResult> nonClosedWeakReference : new ArrayList<WeakReference<ScanResult>>(nonClosedWeakReferences)) {
            ScanResult scanResult = (ScanResult)nonClosedWeakReference.get();
            if (scanResult == null) continue;
            scanResult.close();
        }
    }

    private static class SerializationFormat {
        public String format;
        public ScanSpec scanSpec;
        public List<String> classpath;
        public List<ClassInfo> classInfo;
        public List<PackageInfo> packageInfo;
        public List<ModuleInfo> moduleInfo;

        public SerializationFormat() {
        }

        public SerializationFormat(String serializationFormatStr, ScanSpec scanSpec, List<ClassInfo> classInfo, List<PackageInfo> packageInfo, List<ModuleInfo> moduleInfo, List<String> classpath) {
            this.format = serializationFormatStr;
            this.scanSpec = scanSpec;
            this.classpath = classpath;
            this.classInfo = classInfo;
            this.packageInfo = packageInfo;
            this.moduleInfo = moduleInfo;
        }
    }
}

