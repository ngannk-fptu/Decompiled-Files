/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.io.File;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

public class Compiler {
    public static final Compiler DEFAULT = new Compiler();
    private CompilerConfiguration configuration = null;

    public Compiler() {
        this.configuration = null;
    }

    public Compiler(CompilerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void compile(File file) throws CompilationFailedException {
        CompilationUnit unit = new CompilationUnit(this.configuration);
        unit.addSource(file);
        unit.compile();
    }

    public void compile(File[] files) throws CompilationFailedException {
        CompilationUnit unit = new CompilationUnit(this.configuration);
        unit.addSources(files);
        unit.compile();
    }

    public void compile(String[] files) throws CompilationFailedException {
        CompilationUnit unit = new CompilationUnit(this.configuration);
        unit.addSources(files);
        unit.compile();
    }

    public void compile(String name, String code) throws CompilationFailedException {
        CompilationUnit unit = new CompilationUnit(this.configuration);
        unit.addSource(new SourceUnit(name, code, this.configuration, unit.getClassLoader(), unit.getErrorCollector()));
        unit.compile();
    }
}

