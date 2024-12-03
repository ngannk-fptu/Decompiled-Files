/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.javac.JavaAwareResolveVisitor;
import org.codehaus.groovy.tools.javac.JavaStubGenerator;

public class JavaStubCompilationUnit
extends CompilationUnit {
    private static final String DOT_GROOVY = ".groovy";
    private final JavaStubGenerator stubGenerator;
    private int stubCount;

    public JavaStubCompilationUnit(CompilerConfiguration config, GroovyClassLoader gcl, File destDir) {
        super(config, null, gcl);
        assert (config != null);
        Map<String, Object> options = config.getJointCompilationOptions();
        if (destDir == null) {
            destDir = (File)options.get("stubDir");
        }
        boolean useJava5 = CompilerConfiguration.isPostJDK5(this.configuration.getTargetBytecode());
        String encoding = this.configuration.getSourceEncoding();
        this.stubGenerator = new JavaStubGenerator(destDir, false, useJava5, encoding);
        this.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode node) throws CompilationFailedException {
                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
                scopeVisitor.visitClass(node);
                new JavaAwareResolveVisitor(JavaStubCompilationUnit.this).startResolving(node, source);
            }
        }, 3);
        this.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode node) throws CompilationFailedException {
                try {
                    JavaStubCompilationUnit.this.stubGenerator.generateClass(node);
                    JavaStubCompilationUnit.this.stubCount++;
                }
                catch (FileNotFoundException e) {
                    source.addException(e);
                }
            }
        }, 3);
    }

    public JavaStubCompilationUnit(CompilerConfiguration config, GroovyClassLoader gcl) {
        this(config, gcl, (File)null);
    }

    public int getStubCount() {
        return this.stubCount;
    }

    @Override
    public void compile() throws CompilationFailedException {
        this.stubCount = 0;
        super.compile(3);
    }

    @Override
    public void configure(CompilerConfiguration config) {
        super.configure(config);
        File targetDir = config.getTargetDirectory();
        if (targetDir != null) {
            String classOutput = targetDir.getAbsolutePath();
            this.getClassLoader().addClasspath(classOutput);
        }
    }

    @Override
    public SourceUnit addSource(File file) {
        if (this.hasAcceptedFileExtension(file.getName())) {
            return super.addSource(file);
        }
        return null;
    }

    @Override
    public SourceUnit addSource(URL url) {
        if (this.hasAcceptedFileExtension(url.getPath())) {
            return super.addSource(url);
        }
        return null;
    }

    private boolean hasAcceptedFileExtension(String name) {
        String lowerCasedName = name.toLowerCase();
        for (String extension : this.configuration.getScriptExtensions()) {
            if (!lowerCasedName.endsWith(extension)) continue;
            return true;
        }
        return false;
    }
}

