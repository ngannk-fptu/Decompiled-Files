/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.customizers;

import groovy.lang.Closure;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.DelegatingCustomizer;
import org.codehaus.groovy.control.io.FileReaderSource;
import org.codehaus.groovy.control.io.ReaderSource;

public class SourceAwareCustomizer
extends DelegatingCustomizer {
    private Closure<Boolean> extensionValidator;
    private Closure<Boolean> baseNameValidator;
    private Closure<Boolean> sourceUnitValidator;
    private Closure<Boolean> classValidator;

    public SourceAwareCustomizer(CompilationCustomizer delegate) {
        super(delegate);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        String fileName = source.getName();
        ReaderSource reader = source.getSource();
        if (reader instanceof FileReaderSource) {
            FileReaderSource file = (FileReaderSource)reader;
            fileName = file.getFile().getName();
        }
        if (this.acceptSource(source) && this.acceptClass(classNode) && this.accept(fileName)) {
            this.delegate.call(source, context, classNode);
        }
    }

    public void setBaseNameValidator(Closure<Boolean> baseNameValidator) {
        this.baseNameValidator = baseNameValidator;
    }

    public void setExtensionValidator(Closure<Boolean> extensionValidator) {
        this.extensionValidator = extensionValidator;
    }

    public void setSourceUnitValidator(Closure<Boolean> sourceUnitValidator) {
        this.sourceUnitValidator = sourceUnitValidator;
    }

    public void setClassValidator(Closure<Boolean> classValidator) {
        this.classValidator = classValidator;
    }

    public boolean accept(String fileName) {
        int ext = fileName.lastIndexOf(".");
        String baseName = ext < 0 ? fileName : fileName.substring(0, ext);
        String extension = ext < 0 ? "" : fileName.substring(ext + 1);
        return this.acceptExtension(extension) && this.acceptBaseName(baseName);
    }

    public boolean acceptClass(ClassNode cnode) {
        return this.classValidator == null || this.classValidator.call((Object)cnode) != false;
    }

    public boolean acceptSource(SourceUnit unit) {
        return this.sourceUnitValidator == null || this.sourceUnitValidator.call((Object)unit) != false;
    }

    public boolean acceptExtension(String extension) {
        return this.extensionValidator == null || this.extensionValidator.call((Object)extension) != false;
    }

    public boolean acceptBaseName(String baseName) {
        return this.baseNameValidator == null || this.baseNameValidator.call((Object)baseName) != false;
    }
}

