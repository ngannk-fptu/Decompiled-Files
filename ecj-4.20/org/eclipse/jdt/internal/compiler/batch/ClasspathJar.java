/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathLocation;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.util.ManifestAnalyzer;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathJar
extends ClasspathLocation {
    protected File file;
    protected ZipFile zipFile;
    protected ZipFile annotationZipFile;
    protected boolean closeZipFileAtEnd;
    protected Set<String> packageCache;
    protected List<String> annotationPaths;

    public ClasspathJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String destinationPath) {
        super(accessRuleSet, destinationPath);
        this.file = file;
        this.closeZipFileAtEnd = closeZipFileAtEnd;
    }

    /*
     * Loose catch block
     */
    @Override
    public List<FileSystem.Classpath> fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        ArrayList<FileSystem.Classpath> arrayList;
        InputStream inputStream;
        block18: {
            inputStream = null;
            this.initialize();
            ArrayList<FileSystem.Classpath> result = new ArrayList<FileSystem.Classpath>();
            ZipEntry manifest = this.zipFile.getEntry("META-INF/MANIFEST.MF");
            if (manifest != null) {
                inputStream = this.zipFile.getInputStream(manifest);
                ManifestAnalyzer analyzer = new ManifestAnalyzer();
                boolean success = analyzer.analyzeManifestContents(inputStream);
                List calledFileNames = analyzer.getCalledFileNames();
                if (problemReporter != null) {
                    if (!success || analyzer.getClasspathSectionsCount() == 1 && calledFileNames == null) {
                        problemReporter.invalidClasspathSection(this.getPath());
                    } else if (analyzer.getClasspathSectionsCount() > 1) {
                        problemReporter.multipleClasspathSections(this.getPath());
                    }
                }
                if (calledFileNames != null) {
                    Iterator calledFilesIterator = calledFileNames.iterator();
                    String directoryPath = this.getPath();
                    int lastSeparator = directoryPath.lastIndexOf(File.separatorChar);
                    directoryPath = directoryPath.substring(0, lastSeparator + 1);
                    while (calledFilesIterator.hasNext()) {
                        result.add(new ClasspathJar(new File(String.valueOf(directoryPath) + (String)calledFilesIterator.next()), this.closeZipFileAtEnd, this.accessRuleSet, this.destinationPath));
                    }
                }
            }
            arrayList = result;
            if (inputStream == null) break block18;
            try {
                inputStream.close();
            }
            catch (IOException iOException) {}
        }
        return arrayList;
        catch (IOException | IllegalArgumentException exception) {
            block19: {
                try {
                    if (inputStream == null) break block19;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException iOException) {}
                    }
                    throw throwable;
                }
                try {
                    inputStream.close();
                }
                catch (IOException iOException) {}
            }
            return null;
        }
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, false);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        block11: {
            if (!this.isPackage(qualifiedPackageName, moduleName)) {
                return null;
            }
            try {
                char[] modName;
                IBinaryType reader;
                block12: {
                    reader = ClassFileReader.read(this.zipFile, qualifiedBinaryFileName);
                    if (reader == null) break block11;
                    char[] cArray = modName = this.module == null ? null : this.module.name();
                    if (reader instanceof ClassFileReader) {
                        ClassFileReader classReader = reader;
                        if (classReader.moduleName == null) {
                            classReader.moduleName = modName;
                        } else {
                            modName = classReader.moduleName;
                        }
                    }
                    if (this.annotationPaths != null) {
                        String qualifiedClassName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - "CLASS".length() - 1);
                        for (String annotationPath : this.annotationPaths) {
                            try {
                                if (this.annotationZipFile == null) {
                                    this.annotationZipFile = ExternalAnnotationDecorator.getAnnotationZipFile(annotationPath, null);
                                }
                                if ((reader = ExternalAnnotationDecorator.create(reader, annotationPath, qualifiedClassName, this.annotationZipFile)).getExternalAnnotationStatus() != BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED) continue;
                                break block12;
                            }
                            catch (IOException iOException) {}
                        }
                        reader = new ExternalAnnotationDecorator(reader, null);
                    }
                }
                return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName), modName);
            }
            catch (IOException | ClassFormatException exception) {}
        }
        return null;
    }

    @Override
    public boolean hasAnnotationFileFor(String qualifiedTypeName) {
        if (this.zipFile == null) {
            return false;
        }
        return this.zipFile.getEntry(String.valueOf(qualifiedTypeName) + ".eea") != null;
    }

    @Override
    public char[][][] findTypeNames(String qualifiedPackageName, String moduleName) {
        if (!this.isPackage(qualifiedPackageName, moduleName)) {
            return null;
        }
        char[] packageArray = qualifiedPackageName.toCharArray();
        ArrayList<char[][]> answers = new ArrayList<char[][]>();
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            int indexOfDot;
            String packageName;
            String fileName = e.nextElement().getName();
            int last = fileName.lastIndexOf(47);
            if (last <= 0 || !qualifiedPackageName.equals(packageName = fileName.substring(0, last)) || (indexOfDot = fileName.lastIndexOf(46)) == -1) continue;
            String typeName = fileName.substring(last + 1, indexOfDot);
            answers.add(CharOperation.arrayConcat(CharOperation.splitOn('/', packageArray), typeName.toCharArray()));
        }
        int size = answers.size();
        if (size != 0) {
            char[][][] result = new char[size][][];
            answers.toArray((T[])result);
            return result;
        }
        return null;
    }

    @Override
    public void initialize() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
    }

    void acceptModule(ClassFileReader reader) {
        if (reader != null) {
            this.acceptModule(reader.getModuleDeclaration());
        }
    }

    void acceptModule(byte[] content) {
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
        if (reader != null && reader.getModuleDeclaration() != null) {
            this.acceptModule(reader);
        }
    }

    protected void addToPackageCache(String fileName, boolean endsWithSep) {
        int last = endsWithSep ? fileName.length() : fileName.lastIndexOf(47);
        while (last > 0) {
            String packageName = fileName.substring(0, last);
            if (this.packageCache.contains(packageName)) {
                return;
            }
            this.packageCache.add(packageName);
            last = packageName.lastIndexOf(47);
        }
    }

    @Override
    public synchronized char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        if (this.packageCache != null) {
            return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
        }
        this.packageCache = new HashSet<String>(41);
        this.packageCache.add(Util.EMPTY_STRING);
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            String fileName = e.nextElement().getName();
            this.addToPackageCache(fileName, false);
        }
        return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
    }

    @Override
    public boolean hasCompilationUnit(String qualifiedPackageName, String moduleName) {
        qualifiedPackageName = String.valueOf(qualifiedPackageName) + '/';
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            String tail;
            String fileName = e.nextElement().getName();
            if (!fileName.startsWith(qualifiedPackageName) || fileName.length() <= qualifiedPackageName.length() || (tail = fileName.substring(qualifiedPackageName.length())).indexOf(47) != -1 || !tail.toLowerCase().endsWith(".class")) continue;
            return true;
        }
        return false;
    }

    @Override
    public char[][] listPackages() {
        HashSet<String> packageNames = new HashSet<String>();
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            String fileName = e.nextElement().getName();
            int lastSlash = fileName.lastIndexOf(47);
            if (lastSlash == -1 || !fileName.toLowerCase().endsWith(".class")) continue;
            packageNames.add(fileName.substring(0, lastSlash).replace('/', '.'));
        }
        return (char[][])packageNames.stream().map(String::toCharArray).toArray(n -> new char[n][]);
    }

    @Override
    public void reset() {
        super.reset();
        if (this.closeZipFileAtEnd) {
            if (this.zipFile != null) {
                try {
                    this.zipFile.close();
                }
                catch (IOException iOException) {}
                this.zipFile = null;
            }
            if (this.annotationZipFile != null) {
                try {
                    this.annotationZipFile.close();
                }
                catch (IOException iOException) {}
                this.annotationZipFile = null;
            }
        }
        this.packageCache = null;
        this.annotationPaths = null;
    }

    public String toString() {
        return "Classpath for jar file " + this.file.getPath();
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
    public IModule getModule() {
        return this.module;
    }
}

