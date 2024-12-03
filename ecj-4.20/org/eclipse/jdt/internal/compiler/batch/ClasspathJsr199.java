/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJep247;
import org.eclipse.jdt.internal.compiler.batch.ClasspathLocation;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

public class ClasspathJsr199
extends ClasspathLocation {
    private static final Set<JavaFileObject.Kind> fileTypes = new HashSet<JavaFileObject.Kind>();
    private JavaFileManager fileManager;
    private JavaFileManager.Location location;
    private FileSystem.Classpath jrt;

    static {
        fileTypes.add(JavaFileObject.Kind.CLASS);
    }

    public ClasspathJsr199(JavaFileManager file, JavaFileManager.Location location) {
        super(null, null);
        this.fileManager = file;
        this.location = location;
    }

    public ClasspathJsr199(FileSystem.Classpath jrt, JavaFileManager file, JavaFileManager.Location location) {
        super(null, null);
        this.fileManager = file;
        this.jrt = jrt;
        this.location = location;
    }

    public ClasspathJsr199(ClasspathJep247 older, JavaFileManager file, JavaFileManager.Location location) {
        super(null, null);
        this.fileManager = file;
        this.jrt = older;
        this.location = location;
    }

    public List fetchLinkedJars(FileSystem.ClasspathSectionProblemReporter problemReporter) {
        return null;
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String aQualifiedBinaryFileName, boolean asBinaryOnly) {
        JavaFileObject jfo;
        String qualifiedBinaryFileName;
        block17: {
            if (this.jrt != null) {
                return this.jrt.findClass(typeName, qualifiedPackageName, moduleName, aQualifiedBinaryFileName, asBinaryOnly);
            }
            qualifiedBinaryFileName = File.separatorChar == '/' ? aQualifiedBinaryFileName : aQualifiedBinaryFileName.replace(File.separatorChar, '/');
            int lastDot = qualifiedBinaryFileName.lastIndexOf(46);
            String className = lastDot < 0 ? qualifiedBinaryFileName : qualifiedBinaryFileName.substring(0, lastDot);
            jfo = null;
            try {
                jfo = this.fileManager.getJavaFileForInput(this.location, className, JavaFileObject.Kind.CLASS);
            }
            catch (IOException iOException) {}
            if (jfo != null) break block17;
            return null;
        }
        try {
            Throwable throwable = null;
            Object var11_12 = null;
            try (InputStream inputStream = jfo.openInputStream();){
                ClassFileReader reader = ClassFileReader.read(inputStream, qualifiedBinaryFileName);
                if (reader != null) {
                    return new NameEnvironmentAnswer(reader, this.fetchAccessRestriction(qualifiedBinaryFileName));
                }
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (ClassFormatException classFormatException) {
        }
        catch (IOException iOException) {}
        return null;
    }

    @Override
    public char[][][] findTypeNames(String aQualifiedPackageName, String moduleName) {
        if (this.jrt != null) {
            return this.jrt.findTypeNames(aQualifiedPackageName, moduleName);
        }
        String qualifiedPackageName = File.separatorChar == '/' ? aQualifiedPackageName : aQualifiedPackageName.replace(File.separatorChar, '/');
        Iterable<JavaFileObject> files = null;
        try {
            files = this.fileManager.list(this.location, qualifiedPackageName, fileTypes, false);
        }
        catch (IOException iOException) {}
        if (files == null) {
            return null;
        }
        ArrayList<char[][]> answers = new ArrayList<char[][]>();
        char[][] packageName = CharOperation.splitOn(File.separatorChar, qualifiedPackageName.toCharArray());
        for (JavaFileObject file : files) {
            int indexOfDot;
            String fileName = file.toUri().getPath();
            int last = fileName.lastIndexOf(47);
            if (last <= 0 || (indexOfDot = fileName.lastIndexOf(46)) == -1) continue;
            String typeName = fileName.substring(last + 1, indexOfDot);
            answers.add(CharOperation.arrayConcat(packageName, typeName.toCharArray()));
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
        if (this.jrt != null) {
            this.jrt.initialize();
        }
    }

    @Override
    public void acceptModule(IModule mod) {
    }

    @Override
    public char[][] getModulesDeclaringPackage(String aQualifiedPackageName, String moduleName) {
        if (this.jrt != null) {
            return this.jrt.getModulesDeclaringPackage(aQualifiedPackageName, moduleName);
        }
        String qualifiedPackageName = File.separatorChar == '/' ? aQualifiedPackageName : aQualifiedPackageName.replace(File.separatorChar, '/');
        boolean result = false;
        try {
            Iterable<JavaFileObject> files = this.fileManager.list(this.location, qualifiedPackageName, fileTypes, false);
            Iterator<JavaFileObject> f = files.iterator();
            if (f.hasNext()) {
                result = true;
            } else {
                files = this.fileManager.list(this.location, qualifiedPackageName, fileTypes, true);
                f = files.iterator();
                if (f.hasNext()) {
                    result = true;
                }
            }
        }
        catch (IOException iOException) {}
        return this.singletonModuleNameIf(result);
    }

    @Override
    public boolean hasCompilationUnit(String qualifiedPackageName, String moduleName) {
        if (this.jrt != null) {
            return this.jrt.hasCompilationUnit(qualifiedPackageName, moduleName);
        }
        return false;
    }

    @Override
    public void reset() {
        try {
            super.reset();
            this.fileManager.flush();
        }
        catch (IOException iOException) {}
        if (this.jrt != null) {
            this.jrt.reset();
        }
    }

    public String toString() {
        return "Classpath for Jsr 199 JavaFileManager: " + this.location;
    }

    @Override
    public char[] normalizedPath() {
        if (this.normalizedPath == null) {
            this.normalizedPath = this.path.toCharArray();
        }
        return this.normalizedPath;
    }

    @Override
    public String getPath() {
        if (this.path == null) {
            this.path = this.location.getName();
        }
        return this.path;
    }

    @Override
    public int getMode() {
        return 2;
    }

    @Override
    public boolean hasAnnotationFileFor(String qualifiedTypeName) {
        return false;
    }

    @Override
    public Collection<String> getModuleNames(Collection<String> limitModules) {
        if (this.jrt != null) {
            return this.jrt.getModuleNames(limitModules);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasModule() {
        if (this.jrt != null) {
            return this.jrt.hasModule();
        }
        return super.hasModule();
    }

    @Override
    public IModule getModule(char[] name) {
        if (this.jrt != null) {
            return this.jrt.getModule(name);
        }
        return super.getModule(name);
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName) {
        return this.findClass(typeName, qualifiedPackageName, moduleName, qualifiedBinaryFileName, false);
    }
}

