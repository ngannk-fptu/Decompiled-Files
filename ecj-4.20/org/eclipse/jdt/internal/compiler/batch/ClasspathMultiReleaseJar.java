/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.classfmt.ExternalAnnotationDecorator;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathMultiReleaseJar
extends ClasspathJar {
    private FileSystem fs = null;
    Path releasePath = null;
    String compliance = null;

    public ClasspathMultiReleaseJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String destinationPath, String compliance) {
        super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
        this.compliance = compliance;
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();
        URI t = this.file.toURI();
        if (this.file.exists()) {
            URI uri = URI.create("jar:file:" + t.getRawPath());
            try {
                HashMap env = new HashMap();
                this.fs = FileSystems.newFileSystem(uri, env);
            }
            catch (FileSystemAlreadyExistsException fileSystemAlreadyExistsException) {
                this.fs = FileSystems.getFileSystem(uri);
            }
            this.releasePath = this.fs.getPath("/", "META-INF", "versions", this.compliance);
            if (!Files.exists(this.releasePath, new LinkOption[0])) {
                this.releasePath = null;
            }
        }
    }

    @Override
    public synchronized char[][] getModulesDeclaringPackage(String qualifiedPackageName, String moduleName) {
        block15: {
            if (this.releasePath == null) {
                return super.getModulesDeclaringPackage(qualifiedPackageName, moduleName);
            }
            if (this.packageCache != null) {
                return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
            }
            this.packageCache = new HashSet(41);
            this.packageCache.add(Util.EMPTY_STRING);
            Object e = this.zipFile.entries();
            while (e.hasMoreElements()) {
                String fileName = e.nextElement().getName();
                this.addToPackageCache(fileName, false);
            }
            try {
                if (this.releasePath == null || !Files.exists(this.releasePath, new LinkOption[0])) break block15;
                e = null;
                Object var4_6 = null;
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.releasePath);){
                    for (Path subdir : stream) {
                        Files.walkFileTree(subdir, (FileVisitor<? super Path>)new FileVisitor<Path>(){

                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path f, BasicFileAttributes attrs) throws IOException {
                                Path p = ClasspathMultiReleaseJar.this.releasePath.relativize(f);
                                ClasspathMultiReleaseJar.this.addToPackageCache(p.toString(), false);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path f, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                }
                catch (Throwable throwable) {
                    if (e == null) {
                        e = throwable;
                    } else if (e != throwable) {
                        ((Throwable)e).addSuppressed(throwable);
                    }
                    throw e;
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return this.singletonModuleNameIf(this.packageCache.contains(qualifiedPackageName));
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] binaryFileName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        block14: {
            if (!this.isPackage(qualifiedPackageName, moduleName)) {
                return null;
            }
            if (this.releasePath != null) {
                try {
                    String fileNameWithoutExtension;
                    char[] modName;
                    IBinaryType reader;
                    block15: {
                        Path p = this.releasePath.resolve(qualifiedBinaryFileName);
                        byte[] content = Files.readAllBytes(p);
                        reader = null;
                        if (content != null) {
                            reader = new ClassFileReader(content, qualifiedBinaryFileName.toCharArray());
                        }
                        if (reader == null) break block14;
                        char[] cArray = modName = this.module == null ? null : this.module.name();
                        if (reader instanceof ClassFileReader) {
                            ClassFileReader classReader = reader;
                            if (classReader.moduleName == null) {
                                classReader.moduleName = modName;
                            } else {
                                modName = classReader.moduleName;
                            }
                        }
                        fileNameWithoutExtension = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - SuffixConstants.SUFFIX_CLASS.length);
                        if (this.annotationPaths != null) {
                            String qualifiedClassName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - "CLASS".length() - 1);
                            for (String annotationPath : this.annotationPaths) {
                                try {
                                    if (this.annotationZipFile == null) {
                                        this.annotationZipFile = ExternalAnnotationDecorator.getAnnotationZipFile(annotationPath, null);
                                    }
                                    if ((reader = ExternalAnnotationDecorator.create(reader, annotationPath, qualifiedClassName, this.annotationZipFile)).getExternalAnnotationStatus() != BinaryTypeBinding.ExternalAnnotationStatus.TYPE_IS_ANNOTATED) continue;
                                    break block15;
                                }
                                catch (IOException iOException) {}
                            }
                            reader = new ExternalAnnotationDecorator(reader, null);
                        }
                    }
                    if (this.accessRuleSet == null) {
                        return new NameEnvironmentAnswer(reader, null, modName);
                    }
                    return new NameEnvironmentAnswer(reader, this.accessRuleSet.getViolatedRestriction(fileNameWithoutExtension.toCharArray()), modName);
                }
                catch (IOException | ClassFormatException exception) {}
            }
        }
        return super.findClass(binaryFileName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, asBinaryOnly);
    }
}

