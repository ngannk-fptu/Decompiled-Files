/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.zip.ZipFile;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathLocation;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryModule;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IMultiModuleEntry;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.util.JRTUtil;

public class ClasspathJrt
extends ClasspathLocation
implements IMultiModuleEntry {
    public File file;
    protected ZipFile annotationZipFile;
    protected boolean closeZipFileAtEnd;
    protected static HashMap<String, Map<String, IModule>> ModulesCache = new HashMap();
    public final Set<String> moduleNamesCache;
    protected List<String> annotationPaths;

    public ClasspathJrt(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String destinationPath) {
        super(accessRuleSet, destinationPath);
        this.file = file;
        this.closeZipFileAtEnd = closeZipFileAtEnd;
        this.moduleNamesCache = new HashSet<String>();
    }

    public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
    }

    @Override
    public char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        List<String> modules = JRTUtil.getModulesDeclaringPackage(this.file, qualifiedPackageName, moduleName);
        return CharOperation.toCharArrays(modules);
    }

    @Override
    public boolean hasCompilationUnit(String qualifiedPackageName, String moduleName) {
        return JRTUtil.hasCompilationUnit(this.file, qualifiedPackageName, moduleName);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, false);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        block9: {
            if (!this.isPackage(qualifiedPackageName, moduleName)) {
                return null;
            }
            try {
                char[] answerModuleName;
                IBinaryType reader;
                block10: {
                    reader = ClassFileReader.readFromModule(this.file, moduleName, qualifiedBinaryFileName, this.moduleNamesCache::contains);
                    if (reader == null) break block9;
                    if (this.annotationPaths != null) {
                        String qualifiedClassName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - "CLASS".length() - 1);
                        for (String annotationPath : this.annotationPaths) {
                            try {
                                if (this.annotationZipFile == null) {
                                    this.annotationZipFile = ExternalAnnotationDecorator.getAnnotationZipFile(annotationPath, null);
                                }
                                if ((reader = ExternalAnnotationDecorator.create(reader, annotationPath, qualifiedClassName, this.annotationZipFile)).getExternalAnnotationStatus() != BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED) continue;
                                break block10;
                            }
                            catch (IOException iOException) {}
                        }
                        reader = new ExternalAnnotationDecorator(reader, null);
                    }
                }
                if ((answerModuleName = reader.getModule()) == null && moduleName != null) {
                    answerModuleName = moduleName.toCharArray();
                }
                return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName), answerModuleName);
            }
            catch (IOException | ClassFormatException exception) {}
        }
        return null;
    }

    @Override
    public boolean hasAnnotationFileFor(String qualifiedTypeName) {
        return false;
    }

    @Override
    public char[][][] findTypeNames(final String qualifiedPackageName, final String moduleName) {
        if (!this.isPackage(qualifiedPackageName, moduleName)) {
            return null;
        }
        final char[] packageArray = qualifiedPackageName.toCharArray();
        final ArrayList answers = new ArrayList();
        try {
            JRTUtil.walkModuleImage(this.file, new JRTUtil.JrtFileVisitor<Path>(){

                @Override
                public FileVisitResult visitPackage(Path dir, Path modPath, BasicFileAttributes attrs) throws IOException {
                    if (qualifiedPackageName.startsWith(dir.toString())) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path dir, Path modPath, BasicFileAttributes attrs) throws IOException {
                    Path parent = dir.getParent();
                    if (parent == null) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (!parent.toString().equals(qualifiedPackageName)) {
                        return FileVisitResult.CONTINUE;
                    }
                    String fileName = dir.getName(dir.getNameCount() - 1).toString();
                    ClasspathJrt.this.addTypeName(answers, fileName, -1, packageArray);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitModule(Path p, String name) throws IOException {
                    if (moduleName == null) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (!moduleName.equals(name)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            }, 7);
        }
        catch (IOException iOException) {}
        int size = answers.size();
        if (size != 0) {
            char[][][] result = new char[size][][];
            answers.toArray((T[])result);
            return result;
        }
        return null;
    }

    protected void addTypeName(ArrayList answers, String fileName, int last, char[] packageName) {
        int indexOfDot = fileName.lastIndexOf(46);
        if (indexOfDot != -1) {
            String typeName = fileName.substring(last + 1, indexOfDot);
            answers.add(CharOperation.arrayConcat(CharOperation.splitOn('/', packageName), typeName.toCharArray()));
        }
    }

    @Override
    public void initialize() throws IOException {
        this.loadModules();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadModules() {
        Map<String, IModule> cache = ModulesCache.get(this.file.getPath());
        if (cache == null) {
            try {
                final HashMap newCache = new HashMap();
                JRTUtil.walkModuleImage(this.file, new JRTUtil.JrtFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitPackage(Path dir, Path mod, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path f, Path mod, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitModule(Path p, String name) throws IOException {
                        ClasspathJrt.this.acceptModule(JRTUtil.getClassfileContent(ClasspathJrt.this.file, "module-info.class", name), (Map<String, IModule>)newCache);
                        ClasspathJrt.this.moduleNamesCache.add(name);
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }, 4);
                HashMap<String, Map<String, IModule>> hashMap = ModulesCache;
                synchronized (hashMap) {
                    if (ModulesCache.get(this.file.getPath()) == null) {
                        ModulesCache.put(this.file.getPath(), Collections.unmodifiableMap(newCache));
                    }
                }
            }
            catch (IOException iOException) {}
        } else {
            this.moduleNamesCache.addAll(cache.keySet());
        }
    }

    void acceptModule(ClassFileReader reader, Map<String, IModule> cache) {
        IBinaryModule moduleDecl;
        if (reader != null && (moduleDecl = reader.getModuleDeclaration()) != null) {
            cache.put(String.valueOf(moduleDecl.name()), moduleDecl);
        }
    }

    void acceptModule(byte[] content, Map<String, IModule> cache) {
        if (content == null) {
            return;
        }
        ClassFileReader reader = null;
        try {
            reader = new ClassFileReader(content, "module-info.class".toCharArray());
        }
        catch (ClassFormatException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            this.acceptModule(reader, cache);
        }
    }

    @Override
    public Collection<String> getModuleNames(Collection<String> limitModule, Function<String, IModule> getModule) {
        Map<String, IModule> cache = ModulesCache.get(this.file.getPath());
        return this.selectModules(cache.keySet(), limitModule, getModule);
    }

    @Override
    protected <T> List<String> allModules(Iterable<T> allSystemModules, Function<T, String> getModuleName, Function<T, IModule> getModule) {
        String moduleName;
        ArrayList<String> result = new ArrayList<String>();
        boolean hasJavaDotSE = false;
        for (T mod : allSystemModules) {
            moduleName = getModuleName.apply(mod);
            if (!"java.se".equals(moduleName)) continue;
            result.add(moduleName);
            hasJavaDotSE = true;
            break;
        }
        block1: for (T mod : allSystemModules) {
            IModule m;
            boolean isPotentialRoot;
            moduleName = getModuleName.apply(mod);
            boolean isJavaDotStart = moduleName.startsWith("java.");
            boolean bl = isPotentialRoot = !isJavaDotStart;
            if (!hasJavaDotSE) {
                isPotentialRoot |= isJavaDotStart;
            }
            if (!isPotentialRoot || (m = getModule.apply(mod)) == null) continue;
            IModule.IPackageExport[] iPackageExportArray = m.exports();
            int n = iPackageExportArray.length;
            int n2 = 0;
            while (n2 < n) {
                IModule.IPackageExport packageExport = iPackageExportArray[n2];
                if (!packageExport.isQualified()) {
                    result.add(moduleName);
                    continue block1;
                }
                ++n2;
            }
        }
        return result;
    }

    @Override
    public void reset() {
        if (this.closeZipFileAtEnd && this.annotationZipFile != null) {
            try {
                this.annotationZipFile.close();
            }
            catch (IOException iOException) {}
            this.annotationZipFile = null;
        }
        if (this.annotationPaths != null) {
            this.annotationPaths = null;
        }
    }

    public String toString() {
        return "Classpath for JRT System " + this.file.getPath();
    }

    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            String path2 = this.getPath();
            char[] rawName = path2.toCharArray();
            if (File.separatorChar == '\\') {
                CharOperation.replace(rawName, '\\', '/');
            }
            this.normalizedPath = CharOperation.subarray(rawName, 0, CharOperation.lastIndexOf('.', rawName));
        }
        return this.normalizedPath;
    }

    @Override
    public String getPath() {
        if (this.path == null) {
            try {
                this.path = this.file.getCanonicalPath();
            }
            catch (IOException iOException) {
                this.path = this.file.getAbsolutePath();
            }
        }
        return this.path;
    }

    @Override
    public int getMode() {
        return 2;
    }

    @Override
    public boolean hasModule() {
        return true;
    }

    @Override
    public IModule getModule(char[] moduleName) {
        Map<String, IModule> modules = ModulesCache.get(this.file.getPath());
        if (modules != null) {
            return modules.get(String.valueOf(moduleName));
        }
        return null;
    }

    @Override
    public boolean servesModule(char[] moduleName) {
        return this.getModule(moduleName) != null;
    }
}

