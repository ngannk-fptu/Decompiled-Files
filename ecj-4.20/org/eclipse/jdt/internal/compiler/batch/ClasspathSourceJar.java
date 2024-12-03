/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClasspathSourceJar
extends ClasspathJar {
    private String encoding;

    public ClasspathSourceJar(File file, boolean closeZipFileAtEnd, AccessRuleSet accessRuleSet, String encoding, String destinationPath) {
        super(file, closeZipFileAtEnd, accessRuleSet, destinationPath);
        this.encoding = encoding;
    }

    @Override
    public NameEnvironmentAnswer findClass(char[] typeName, String qualifiedPackageName, String moduleName, String qualifiedBinaryFileName, boolean asBinaryOnly) {
        if (!this.isPackage(qualifiedPackageName, moduleName)) {
            return null;
        }
        ZipEntry sourceEntry = this.zipFile.getEntry(String.valueOf(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)) + ".java");
        if (sourceEntry != null) {
            try {
                char[] contents = null;
                try (InputStream stream = null;){
                    stream = this.zipFile.getInputStream(sourceEntry);
                    contents = Util.getInputStreamAsCharArray(stream, -1, this.encoding);
                }
                CompilationUnit compilationUnit = new CompilationUnit(contents, String.valueOf(qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - 6)) + ".java", this.encoding, this.destinationPath);
                compilationUnit.module = this.module == null ? null : this.module.name();
                return new NameEnvironmentAnswer(compilationUnit, this.fetchAccessRestriction(qualifiedBinaryFileName));
            }
            catch (IOException iOException) {}
        }
        return null;
    }

    @Override
    public int getMode() {
        return 1;
    }
}

