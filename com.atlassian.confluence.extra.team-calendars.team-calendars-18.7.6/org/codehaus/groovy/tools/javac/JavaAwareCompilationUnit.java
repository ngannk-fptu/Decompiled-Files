/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.javac;

import groovy.lang.GroovyClassLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.AnnotationConstantsVisitor;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.tools.javac.JavaAwareResolveVisitor;
import org.codehaus.groovy.tools.javac.JavaCompiler;
import org.codehaus.groovy.tools.javac.JavaCompilerFactory;
import org.codehaus.groovy.tools.javac.JavaStubGenerator;
import org.codehaus.groovy.tools.javac.JavacCompilerFactory;
import org.codehaus.groovy.transform.ASTTransformationCollectorCodeVisitor;

public class JavaAwareCompilationUnit
extends CompilationUnit {
    private List<String> javaSources;
    private JavaStubGenerator stubGenerator;
    private JavaCompilerFactory compilerFactory = new JavacCompilerFactory();
    private File generationGoal;
    private boolean keepStubs;

    public JavaAwareCompilationUnit(CompilerConfiguration configuration) {
        this(configuration, (GroovyClassLoader)null, (GroovyClassLoader)null);
    }

    public JavaAwareCompilationUnit(CompilerConfiguration configuration, GroovyClassLoader groovyClassLoader) {
        this(configuration, groovyClassLoader, (GroovyClassLoader)null);
    }

    public JavaAwareCompilationUnit(CompilerConfiguration configuration, GroovyClassLoader groovyClassLoader, GroovyClassLoader transformClassLoader) {
        super(configuration, null, groovyClassLoader, transformClassLoader);
        this.javaSources = new LinkedList<String>();
        Map<String, Object> options = configuration.getJointCompilationOptions();
        this.generationGoal = (File)options.get("stubDir");
        boolean useJava5 = CompilerConfiguration.isPostJDK5(configuration.getTargetBytecode());
        String encoding = configuration.getSourceEncoding();
        this.stubGenerator = new JavaStubGenerator(this.generationGoal, false, useJava5, encoding);
        this.keepStubs = Boolean.TRUE.equals(options.get("keepStubs"));
        this.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode node) throws CompilationFailedException {
                if (!JavaAwareCompilationUnit.this.javaSources.isEmpty()) {
                    VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
                    scopeVisitor.visitClass(node);
                    new JavaAwareResolveVisitor(JavaAwareCompilationUnit.this).startResolving(node, source);
                    AnnotationConstantsVisitor acv = new AnnotationConstantsVisitor();
                    acv.visitClass(node, source);
                }
            }
        }, 3);
        this.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                ASTTransformationCollectorCodeVisitor collector = new ASTTransformationCollectorCodeVisitor(source, JavaAwareCompilationUnit.this.getTransformLoader());
                collector.visitClass(classNode);
            }
        }, 3);
        this.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                try {
                    if (!JavaAwareCompilationUnit.this.javaSources.isEmpty()) {
                        JavaAwareCompilationUnit.this.stubGenerator.generateClass(classNode);
                    }
                }
                catch (FileNotFoundException fnfe) {
                    source.addException(fnfe);
                }
            }
        }, 3);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void gotoPhase(int phase) throws CompilationFailedException {
        super.gotoPhase(phase);
        if (phase == 4 && !this.javaSources.isEmpty()) {
            for (ModuleNode module : this.getAST().getModules()) {
                module.setImportsResolved(false);
            }
            try {
                JavaCompiler compiler = this.compilerFactory.createCompiler(this.getConfiguration());
                compiler.compile(this.javaSources, this);
            }
            finally {
                if (!this.keepStubs) {
                    this.stubGenerator.clean();
                }
                this.javaSources.clear();
            }
        }
    }

    @Override
    public void configure(CompilerConfiguration configuration) {
        super.configure(configuration);
        File targetDir = configuration.getTargetDirectory();
        if (targetDir != null) {
            String classOutput = targetDir.getAbsolutePath();
            this.getClassLoader().addClasspath(classOutput);
        }
    }

    private void addJavaSource(File file) {
        String path = file.getAbsolutePath();
        for (String source : this.javaSources) {
            if (!path.equals(source)) continue;
            return;
        }
        this.javaSources.add(path);
    }

    @Override
    public void addSources(String[] paths) {
        for (String path : paths) {
            this.addJavaOrGroovySource(new File(path));
        }
    }

    @Override
    public void addSources(File[] files) {
        for (File file : files) {
            this.addJavaOrGroovySource(file);
        }
    }

    private void addJavaOrGroovySource(File file) {
        if (file.getName().endsWith(".java")) {
            this.addJavaSource(file);
        } else {
            this.addSource(file);
        }
    }

    public JavaCompilerFactory getCompilerFactory() {
        return this.compilerFactory;
    }

    public void setCompilerFactory(JavaCompilerFactory compilerFactory) {
        this.compilerFactory = compilerFactory;
    }
}

