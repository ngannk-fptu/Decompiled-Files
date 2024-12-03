/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.util.Util;

public class CompilationUnit
implements ICompilationUnit {
    public char[] contents;
    public char[] fileName;
    public char[] mainTypeName;
    String encoding;
    public String destinationPath;
    public char[] module;
    private boolean ignoreOptionalProblems;
    private ModuleBinding moduleBinding;
    private Function<String, String> annotationPathProvider;

    public CompilationUnit(char[] contents, String fileName, String encoding) {
        this(contents, fileName, encoding, null);
    }

    public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath) {
        this(contents, fileName, encoding, destinationPath, false, null);
    }

    public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath, boolean ignoreOptionalProblems, String modName) {
        this(contents, fileName, encoding, destinationPath, ignoreOptionalProblems, modName, null);
    }

    public CompilationUnit(char[] contents, String fileName, String encoding, String destinationPath, boolean ignoreOptionalProblems, String modName, Function<String, String> annotationPathProvider) {
        this.annotationPathProvider = annotationPathProvider;
        this.contents = contents;
        if (modName != null) {
            this.module = modName.toCharArray();
        }
        char[] fileNameCharArray = fileName.toCharArray();
        switch (File.separatorChar) {
            case '/': {
                if (CharOperation.indexOf('\\', fileNameCharArray) == -1) break;
                CharOperation.replace(fileNameCharArray, '\\', '/');
                break;
            }
            case '\\': {
                if (CharOperation.indexOf('/', fileNameCharArray) == -1) break;
                CharOperation.replace(fileNameCharArray, '/', '\\');
            }
        }
        this.fileName = fileNameCharArray;
        int start = CharOperation.lastIndexOf(File.separatorChar, fileNameCharArray) + 1;
        int end = CharOperation.lastIndexOf('.', fileNameCharArray);
        if (end == -1) {
            end = fileNameCharArray.length;
        }
        this.mainTypeName = CharOperation.subarray(fileNameCharArray, start, end);
        this.encoding = encoding;
        this.destinationPath = destinationPath;
        this.ignoreOptionalProblems = ignoreOptionalProblems;
    }

    @Override
    public char[] getContents() {
        if (this.contents != null) {
            return this.contents;
        }
        try {
            return Util.getFileCharContent(new File(new String(this.fileName)), this.encoding);
        }
        catch (IOException e) {
            this.contents = CharOperation.NO_CHAR;
            throw new AbortCompilationUnit(null, e, this.encoding);
        }
    }

    @Override
    public char[] getFileName() {
        return this.fileName;
    }

    @Override
    public char[] getMainTypeName() {
        return this.mainTypeName;
    }

    @Override
    public char[][] getPackageName() {
        return null;
    }

    @Override
    public boolean ignoreOptionalProblems() {
        return this.ignoreOptionalProblems;
    }

    public String toString() {
        return "CompilationUnit[" + new String(this.fileName) + "]";
    }

    @Override
    public char[] getModuleName() {
        return this.module;
    }

    @Override
    public ModuleBinding module(LookupEnvironment rootEnvironment) {
        if (this.moduleBinding != null) {
            return this.moduleBinding;
        }
        this.moduleBinding = rootEnvironment.getModule(this.module);
        if (this.moduleBinding == null) {
            throw new IllegalStateException("Module should be known");
        }
        return this.moduleBinding;
    }

    @Override
    public String getDestinationPath() {
        return this.destinationPath;
    }

    @Override
    public String getExternalAnnotationPath(String qualifiedTypeName) {
        if (this.annotationPathProvider != null) {
            return this.annotationPathProvider.apply(qualifiedTypeName);
        }
        return null;
    }
}

