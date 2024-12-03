/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.optional.depend.AntAnalyzer;
import org.apache.tools.ant.taskdefs.optional.depend.ClassFileUtils;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.FileUtils;

public class Depend
extends MatchingTask {
    private static final int ONE_SECOND = 1000;
    private Path srcPath;
    private Path destPath;
    private File cache;
    private Map<String, Map<String, ClassFileInfo>> affectedClassMap;
    private Map<String, ClassFileInfo> classFileInfoMap;
    private Map<String, Set<File>> classpathDependencies;
    private Map<String, String> outOfDateClasses;
    private boolean closure = false;
    private boolean warnOnRmiStubs = true;
    private boolean dump = false;
    private Path dependClasspath;
    private static final String CACHE_FILE_NAME = "dependencies.txt";
    private static final String CLASSNAME_PREPEND = "||:";

    public void setClasspath(Path classpath) {
        if (this.dependClasspath == null) {
            this.dependClasspath = classpath;
        } else {
            this.dependClasspath.append(classpath);
        }
    }

    public Path getClasspath() {
        return this.dependClasspath;
    }

    public Path createClasspath() {
        if (this.dependClasspath == null) {
            this.dependClasspath = new Path(this.getProject());
        }
        return this.dependClasspath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setWarnOnRmiStubs(boolean warnOnRmiStubs) {
        this.warnOnRmiStubs = warnOnRmiStubs;
    }

    private Map<String, List<String>> readCachedDependencies(File depFile) throws IOException {
        HashMap<String, List<String>> dependencyMap = new HashMap<String, List<String>>();
        int prependLength = CLASSNAME_PREPEND.length();
        try (BufferedReader in = new BufferedReader(new FileReader(depFile));){
            String line;
            List dependencyList = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith(CLASSNAME_PREPEND)) {
                    String className = line.substring(prependLength);
                    dependencyList = dependencyMap.computeIfAbsent(className, k -> new ArrayList());
                    continue;
                }
                if (dependencyList == null) continue;
                dependencyList.add(line);
            }
        }
        return dependencyMap;
    }

    private void writeCachedDependencies(Map<String, List<String>> dependencyMap) throws IOException {
        if (this.cache != null) {
            this.cache.mkdirs();
            File depFile = new File(this.cache, CACHE_FILE_NAME);
            try (BufferedWriter pw = new BufferedWriter(new FileWriter(depFile));){
                for (Map.Entry<String, List<String>> e : dependencyMap.entrySet()) {
                    pw.write(String.format("%s%s%n", CLASSNAME_PREPEND, e.getKey()));
                    for (String s : e.getValue()) {
                        pw.write(s);
                        pw.newLine();
                    }
                }
            }
        }
    }

    private Path getCheckClassPath() {
        Path p;
        if (this.dependClasspath == null) {
            return null;
        }
        LinkedHashSet dependNotInDest = new LinkedHashSet();
        this.dependClasspath.forEach(dependNotInDest::add);
        this.destPath.forEach(dependNotInDest::remove);
        if (dependNotInDest.isEmpty()) {
            p = null;
        } else {
            p = new Path(this.getProject());
            dependNotInDest.forEach(p::add);
        }
        this.log("Classpath without dest dir is " + p, 4);
        return p;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void determineDependencies() throws IOException {
        this.affectedClassMap = new HashMap<String, Map<String, ClassFileInfo>>();
        this.classFileInfoMap = new HashMap<String, ClassFileInfo>();
        boolean cacheDirty = false;
        HashMap<String, List<String>> dependencyMap = new HashMap();
        File cacheFile = null;
        boolean cacheFileExists = true;
        long cacheLastModified = Long.MAX_VALUE;
        if (this.cache != null) {
            cacheFile = new File(this.cache, CACHE_FILE_NAME);
            cacheFileExists = cacheFile.exists();
            cacheLastModified = cacheFile.lastModified();
            if (cacheFileExists) {
                dependencyMap = this.readCachedDependencies(cacheFile);
            }
        }
        for (ClassFileInfo info : this.getClassFiles()) {
            this.log("Adding class info for " + info.className, 4);
            this.classFileInfoMap.put(info.className, info);
            ArrayList<String> dependencyList = null;
            if (this.cache != null && cacheFileExists && cacheLastModified > info.absoluteFile.lastModified()) {
                dependencyList = (List)dependencyMap.get(info.className);
            }
            if (dependencyList == null) {
                AntAnalyzer analyzer = new AntAnalyzer();
                analyzer.addRootClass(info.className);
                analyzer.addClassPath(this.destPath);
                analyzer.setClosure(false);
                dependencyList = Collections.list(analyzer.getClassDependencies());
                dependencyList.forEach(o -> this.log("Class " + info.className + " depends on " + o, 4));
                cacheDirty = true;
                dependencyMap.put(info.className, dependencyList);
            }
            for (String dependentClass : dependencyList) {
                this.affectedClassMap.computeIfAbsent(dependentClass, k -> new HashMap()).put(info.className, info);
                this.log(dependentClass + " affects " + info.className, 4);
            }
        }
        this.classpathDependencies = null;
        Path checkPath = this.getCheckClassPath();
        if (checkPath != null) {
            this.classpathDependencies = new HashMap<String, Set<File>>();
            try (AntClassLoader loader = this.getProject().createClassLoader(checkPath);){
                HashMap<String, void> classpathFileCache = new HashMap<String, void>();
                Object nullFileMarker = new Object();
                for (Map.Entry e : dependencyMap.entrySet()) {
                    String className = (String)e.getKey();
                    this.log("Determining classpath dependencies for " + className, 4);
                    List dependencyList = (List)e.getValue();
                    HashSet<File> dependencies = new HashSet<File>();
                    this.classpathDependencies.put(className, dependencies);
                    for (String dependency : dependencyList) {
                        void var18_19;
                        this.log("Looking for " + dependency, 4);
                        Object v = classpathFileCache.get(dependency);
                        if (v == null) {
                            void var18_24;
                            Object object = nullFileMarker;
                            if (!dependency.startsWith("java.") && !dependency.startsWith("javax.")) {
                                URL classURL = loader.getResource(dependency.replace('.', '/') + ".class");
                                this.log("URL is " + classURL, 4);
                                if (classURL != null) {
                                    if ("jar".equals(classURL.getProtocol())) {
                                        String jarFilePath = classURL.getFile();
                                        int classMarker = jarFilePath.indexOf(33);
                                        if (!(jarFilePath = jarFilePath.substring(0, classMarker)).startsWith("file:")) throw new IOException("Bizarre nested path in jar: protocol: " + jarFilePath);
                                        File file = new File(FileUtils.getFileUtils().fromURI(jarFilePath));
                                    } else if ("file".equals(classURL.getProtocol())) {
                                        File file = new File(FileUtils.getFileUtils().fromURI(classURL.toExternalForm()));
                                    }
                                    this.log("Class " + className + " depends on " + var18_24 + " due to " + dependency, 4);
                                }
                            } else {
                                this.log("Ignoring base classlib dependency " + dependency, 4);
                            }
                            classpathFileCache.put(dependency, var18_24);
                        }
                        if (var18_19 == nullFileMarker) continue;
                        File jarFile = (File)var18_19;
                        this.log("Adding a classpath dependency on " + jarFile, 4);
                        dependencies.add(jarFile);
                    }
                }
            }
        } else {
            this.log("No classpath to check", 4);
        }
        if (this.cache == null || !cacheDirty) return;
        this.writeCachedDependencies(dependencyMap);
    }

    private int deleteAllAffectedFiles() {
        int count = 0;
        for (String className : this.outOfDateClasses.keySet()) {
            count += this.deleteAffectedFiles(className);
            ClassFileInfo classInfo = this.classFileInfoMap.get(className);
            if (classInfo == null || !classInfo.absoluteFile.exists()) continue;
            if (classInfo.sourceFile == null) {
                this.warnOutOfDateButNotDeleted(classInfo, className, className);
                continue;
            }
            classInfo.absoluteFile.delete();
            ++count;
        }
        return count;
    }

    private int deleteAffectedFiles(String className) {
        int count = 0;
        Map<String, ClassFileInfo> affectedClasses = this.affectedClassMap.get(className);
        if (affectedClasses == null) {
            return count;
        }
        for (Map.Entry<String, ClassFileInfo> e : affectedClasses.entrySet()) {
            String affectedClass = e.getKey();
            ClassFileInfo affectedClassInfo = e.getValue();
            if (!affectedClassInfo.absoluteFile.exists()) continue;
            if (affectedClassInfo.sourceFile == null) {
                this.warnOutOfDateButNotDeleted(affectedClassInfo, affectedClass, className);
                continue;
            }
            this.log("Deleting file " + affectedClassInfo.absoluteFile.getPath() + " since " + className + " out of date", 3);
            affectedClassInfo.absoluteFile.delete();
            ++count;
            if (this.closure) {
                count += this.deleteAffectedFiles(affectedClass);
                continue;
            }
            if (!affectedClass.contains("$")) continue;
            String topLevelClassName = affectedClass.substring(0, affectedClass.indexOf("$"));
            this.log("Top level class = " + topLevelClassName, 3);
            ClassFileInfo topLevelClassInfo = this.classFileInfoMap.get(topLevelClassName);
            if (topLevelClassInfo == null || !topLevelClassInfo.absoluteFile.exists()) continue;
            this.log("Deleting file " + topLevelClassInfo.absoluteFile.getPath() + " since one of its inner classes was removed", 3);
            topLevelClassInfo.absoluteFile.delete();
            ++count;
            if (!this.closure) continue;
            count += this.deleteAffectedFiles(topLevelClassName);
        }
        return count;
    }

    private void warnOutOfDateButNotDeleted(ClassFileInfo affectedClassInfo, String affectedClass, String className) {
        if (affectedClassInfo.isUserWarned) {
            return;
        }
        int level = 1;
        if (!this.warnOnRmiStubs && this.isRmiStub(affectedClass, className)) {
            level = 3;
        }
        this.log("The class " + affectedClass + " in file " + affectedClassInfo.absoluteFile.getPath() + " is out of date due to " + className + " but has not been deleted because its source file could not be determined", level);
        affectedClassInfo.isUserWarned = true;
    }

    private boolean isRmiStub(String affectedClass, String className) {
        return this.isStub(affectedClass, className, "_Stub") || this.isStub(affectedClass, className, "_Skel") || this.isStub(affectedClass, className, "_Stub") || this.isStub(affectedClass, className, "_Skel");
    }

    private boolean isStub(String affectedClass, String baseClass, String suffix) {
        return (baseClass + suffix).equals(affectedClass);
    }

    private void dumpDependencies() {
        this.log("Reverse Dependency Dump for " + this.affectedClassMap.size() + " classes:", 4);
        this.affectedClassMap.forEach((className, affectedClasses) -> {
            this.log(" Class " + className + " affects:", 4);
            affectedClasses.forEach((affectedClass, info) -> this.log("    " + affectedClass + " in " + ((ClassFileInfo)info).absoluteFile.getPath(), 4));
        });
        if (this.classpathDependencies != null) {
            this.log("Classpath file dependencies (Forward):", 4);
            this.classpathDependencies.forEach((className, dependencies) -> {
                this.log(" Class " + className + " depends on:", 4);
                dependencies.forEach(f -> this.log("    " + f.getPath(), 4));
            });
        }
    }

    private void determineOutOfDateClasses() {
        this.outOfDateClasses = new HashMap<String, String>();
        this.directories(this.srcPath).forEach(srcDir -> {
            DirectoryScanner ds = this.getDirectoryScanner((File)srcDir);
            this.scanDir((File)srcDir, ds.getIncludedFiles());
        });
        if (this.classpathDependencies == null) {
            return;
        }
        block0: for (Map.Entry<String, Set<File>> e : this.classpathDependencies.entrySet()) {
            ClassFileInfo info;
            String className = e.getKey();
            if (this.outOfDateClasses.containsKey(className) || (info = this.classFileInfoMap.get(className)) == null) continue;
            for (File classpathFile : e.getValue()) {
                if (classpathFile.lastModified() <= info.absoluteFile.lastModified()) continue;
                this.log("Class " + className + " is out of date with respect to " + classpathFile, 4);
                this.outOfDateClasses.put(className, className);
                continue block0;
            }
        }
    }

    @Override
    public void execute() throws BuildException {
        try {
            long start = System.currentTimeMillis();
            if (this.srcPath == null) {
                throw new BuildException("srcdir attribute must be set", this.getLocation());
            }
            if (!this.directories(this.srcPath).findAny().isPresent()) {
                throw new BuildException("srcdir attribute must be non-empty", this.getLocation());
            }
            if (this.destPath == null) {
                this.destPath = this.srcPath;
            }
            if (this.cache != null && this.cache.exists() && !this.cache.isDirectory()) {
                throw new BuildException("The cache, if specified, must point to a directory");
            }
            if (this.cache != null && !this.cache.exists()) {
                this.cache.mkdirs();
            }
            this.determineDependencies();
            if (this.dump) {
                this.dumpDependencies();
            }
            this.determineOutOfDateClasses();
            int count = this.deleteAllAffectedFiles();
            long duration = (System.currentTimeMillis() - start) / 1000L;
            int summaryLogLevel = count > 0 ? 2 : 4;
            this.log("Deleted " + count + " out of date files in " + duration + " seconds", summaryLogLevel);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    protected void scanDir(File srcDir, String[] files) {
        for (String f : files) {
            File srcFile = new File(srcDir, f);
            if (!f.endsWith(".java")) continue;
            String filePath = srcFile.getPath();
            String className = filePath.substring(srcDir.getPath().length() + 1, filePath.length() - ".java".length());
            ClassFileInfo info = this.classFileInfoMap.get(className = ClassFileUtils.convertSlashName(className));
            if (info == null) {
                this.outOfDateClasses.put(className, className);
                continue;
            }
            if (srcFile.lastModified() <= info.absoluteFile.lastModified()) continue;
            this.outOfDateClasses.put(className, className);
        }
    }

    private List<ClassFileInfo> getClassFiles() {
        ArrayList<ClassFileInfo> classFileList = new ArrayList<ClassFileInfo>();
        this.directories(this.destPath).forEach(dir -> this.addClassFiles((List<ClassFileInfo>)classFileList, (File)dir, (File)dir));
        return classFileList;
    }

    private File findSourceFile(String classname, File sourceFileKnownToExist) {
        int innerIndex = classname.indexOf(36);
        String sourceFilename = innerIndex != -1 ? classname.substring(0, innerIndex) + ".java" : classname + ".java";
        return this.directories(this.srcPath).map(d -> new File((File)d, sourceFilename)).filter(Predicate.isEqual(sourceFileKnownToExist).or(File::exists)).findFirst().orElse(null);
    }

    private void addClassFiles(List<ClassFileInfo> classFileList, File dir, File root) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        int rootLength = root.getPath().length();
        File sourceFileKnownToExist = null;
        for (File file : children) {
            if (file.getName().endsWith(".class")) {
                ClassFileInfo info = new ClassFileInfo();
                info.absoluteFile = file;
                String relativeName = file.getPath().substring(rootLength + 1, file.getPath().length() - ".class".length());
                info.className = ClassFileUtils.convertSlashName(relativeName);
                sourceFileKnownToExist = this.findSourceFile(relativeName, sourceFileKnownToExist);
                info.sourceFile = sourceFileKnownToExist;
                classFileList.add(info);
                continue;
            }
            this.addClassFiles(classFileList, file, root);
        }
    }

    public void setSrcdir(Path srcPath) {
        this.srcPath = srcPath;
    }

    public void setDestDir(Path destPath) {
        this.destPath = destPath;
    }

    public void setCache(File cache) {
        this.cache = cache;
    }

    public void setClosure(boolean closure) {
        this.closure = closure;
    }

    public void setDump(boolean dump) {
        this.dump = dump;
    }

    private Stream<File> directories(ResourceCollection rc) {
        return rc.stream().map(r -> r.as(FileProvider.class)).filter(Objects::nonNull).map(FileProvider::getFile).filter(File::isDirectory);
    }

    private static class ClassFileInfo {
        private File absoluteFile;
        private String className;
        private File sourceFile;
        private boolean isUserWarned = false;

        private ClassFileInfo() {
        }
    }
}

