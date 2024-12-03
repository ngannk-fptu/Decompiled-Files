/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathJmod
extends ClasspathJar {
    public static char[] CLASSES = "classes".toCharArray();
    public static char[] CLASSES_FOLDER = "classes/".toCharArray();

    public ClasspathJmod(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String destinationPath) {
        super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
    }

    @Override
    public List<FileSystem.Classpath> fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
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
                    qualifiedBinaryFileName = new String(CharOperation.append(CLASSES_FOLDER, qualifiedBinaryFileName.toCharArray()));
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
        return this.zipFile.getEntry(String.valueOf(qualifiedTypeName = new String(CharOperation.append(CLASSES_FOLDER, qualifiedTypeName.toCharArray()))) + ".eea") != null;
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
            int first = CharOperation.indexOf(CLASSES_FOLDER, fileName.toCharArray(), false);
            int last = fileName.lastIndexOf(47);
            if (last <= 0 || !qualifiedPackageName.equals(packageName = fileName.substring(first + 1, last)) || (indexOfDot = fileName.lastIndexOf(46)) == -1) continue;
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
    public synchronized char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        if (this.packageCache != null) {
            return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
        }
        this.packageCache = new HashSet(41);
        this.packageCache.add(Util.EMPTY_STRING);
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            char[] folder;
            char[] entryName = e.nextElement().getName().toCharArray();
            int index = CharOperation.indexOf('/', entryName);
            if (index == -1 || !CharOperation.equals(CLASSES, folder = CharOperation.subarray(entryName, 0, index))) continue;
            char[] fileName = CharOperation.subarray(entryName, index + 1, entryName.length);
            this.addToPackageCache(new String(fileName), false);
        }
        return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
    }

    @Override
    public boolean hasCompilationUnit(String qualifiedPackageName, String moduleName) {
        qualifiedPackageName = String.valueOf(qualifiedPackageName) + '/';
        Enumeration<? extends ZipEntry> e = this.zipFile.entries();
        while (e.hasMoreElements()) {
            String tail;
            String fileName;
            char[] folder;
            char[] entryName = e.nextElement().getName().toCharArray();
            int index = CharOperation.indexOf('/', entryName);
            if (index == -1 || !CharOperation.equals(CLASSES, folder = CharOperation.subarray(entryName, 0, index)) || !(fileName = new String(CharOperation.subarray(entryName, index + 1, entryName.length))).startsWith(qualifiedPackageName) || fileName.length() <= qualifiedPackageName.length() || (tail = fileName.substring(qualifiedPackageName.length())).indexOf(47) != -1 || !tail.toLowerCase().endsWith(".class")) continue;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Classpath for JMod file " + this.file.getPath();
    }

    @Override
    public IModule getModule() {
        return this.module;
    }
}

