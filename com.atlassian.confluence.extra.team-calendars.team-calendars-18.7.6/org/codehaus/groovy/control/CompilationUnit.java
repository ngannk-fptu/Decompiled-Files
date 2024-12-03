/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.transform.CompilationUnitAware;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.ClassCompletionVerifier;
import org.codehaus.groovy.classgen.EnumCompletionVisitor;
import org.codehaus.groovy.classgen.EnumVisitor;
import org.codehaus.groovy.classgen.ExtendedVerifier;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.InnerClassCompletionVisitor;
import org.codehaus.groovy.classgen.InnerClassVisitor;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.ASTTransformationsContext;
import org.codehaus.groovy.control.ClassNodeResolver;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.GenericsVisitor;
import org.codehaus.groovy.control.LabelVerifier;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.OptimizerVisitor;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.StaticImportVisitor;
import org.codehaus.groovy.control.StaticVerifier;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.io.InputStreamReaderSource;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.tools.GroovyClass;
import org.codehaus.groovy.transform.ASTTransformationVisitor;
import org.codehaus.groovy.transform.AnnotationCollectorTransform;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.trait.TraitComposer;

public class CompilationUnit
extends ProcessingUnit {
    protected ASTTransformationsContext astTransformationsContext;
    protected Map<String, SourceUnit> sources;
    protected Map summariesBySourceName;
    protected Map summariesByPublicClassName;
    protected Map classSourcesByPublicClassName;
    protected List<String> names;
    protected LinkedList<SourceUnit> queuedSources;
    protected CompileUnit ast;
    protected List<GroovyClass> generatedClasses;
    protected Verifier verifier;
    protected boolean debug;
    protected boolean configured;
    protected ClassgenCallback classgenCallback;
    protected ProgressCallback progressCallback;
    protected ResolveVisitor resolveVisitor;
    protected StaticImportVisitor staticImportVisitor;
    protected OptimizerVisitor optimizer;
    protected ClassNodeResolver classNodeResolver;
    LinkedList[] phaseOperations;
    LinkedList[] newPhaseOperations;
    private final SourceUnitOperation resolve = new SourceUnitOperation(){

        @Override
        public void call(SourceUnit source) throws CompilationFailedException {
            List<ClassNode> classes = source.ast.getClasses();
            for (ClassNode node : classes) {
                VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
                scopeVisitor.visitClass(node);
                CompilationUnit.this.resolveVisitor.setClassNodeResolver(CompilationUnit.this.classNodeResolver);
                CompilationUnit.this.resolveVisitor.startResolving(node, source);
            }
        }
    };
    private PrimaryClassNodeOperation staticImport = new PrimaryClassNodeOperation(){

        @Override
        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
            CompilationUnit.this.staticImportVisitor.visitClass(classNode, source);
        }
    };
    private SourceUnitOperation convert = new SourceUnitOperation(){

        @Override
        public void call(SourceUnit source) throws CompilationFailedException {
            source.convert();
            CompilationUnit.this.ast.addModule(source.getAST());
            if (CompilationUnit.this.progressCallback != null) {
                CompilationUnit.this.progressCallback.call(source, CompilationUnit.this.phase);
            }
        }
    };
    private GroovyClassOperation output = new GroovyClassOperation(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void call(GroovyClass gclass) throws CompilationFailedException {
            String name = gclass.getName().replace('.', File.separatorChar) + ".class";
            File path = new File(CompilationUnit.this.configuration.getTargetDirectory(), name);
            File directory = path.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            byte[] bytes = gclass.getBytes();
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(path);
                stream.write(bytes, 0, bytes.length);
            }
            catch (IOException e) {
                CompilationUnit.this.getErrorCollector().addError(Message.create(e.getMessage(), CompilationUnit.this));
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
    };
    private SourceUnitOperation compileCompleteCheck = new SourceUnitOperation(){

        @Override
        public void call(SourceUnit source) throws CompilationFailedException {
            List<ClassNode> classes = source.ast.getClasses();
            for (ClassNode node : classes) {
                CompileUnit cu = node.getCompileUnit();
                Iterator<String> iter = cu.iterateClassNodeToCompile();
                while (iter.hasNext()) {
                    String name = iter.next();
                    SourceUnit su = CompilationUnit.this.ast.getScriptSourceLocation(name);
                    List<ClassNode> classesInSourceUnit = su.ast.getClasses();
                    StringBuilder message = new StringBuilder();
                    message.append("Compilation incomplete: expected to find the class ").append(name).append(" in ").append(su.getName());
                    if (classesInSourceUnit.isEmpty()) {
                        message.append(", but the file seems not to contain any classes");
                    } else {
                        message.append(", but the file contains the classes: ");
                        boolean first = true;
                        for (ClassNode cn : classesInSourceUnit) {
                            if (!first) {
                                message.append(", ");
                            } else {
                                first = false;
                            }
                            message.append(cn.getName());
                        }
                    }
                    CompilationUnit.this.getErrorCollector().addErrorAndContinue(new SimpleMessage(message.toString(), CompilationUnit.this));
                    iter.remove();
                }
            }
        }
    };
    private PrimaryClassNodeOperation classgen = new PrimaryClassNodeOperation(){

        @Override
        public boolean needSortedInput() {
            return true;
        }

        @Override
        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
            String sourceName;
            CompilationUnit.this.optimizer.visitClass(classNode, source);
            if (!classNode.isSynthetic()) {
                GenericsVisitor genericsVisitor = new GenericsVisitor(source);
                genericsVisitor.visitClass(classNode);
            }
            try {
                CompilationUnit.this.verifier.visitClass(classNode);
            }
            catch (GroovyRuntimeException rpe) {
                ASTNode node = rpe.getNode();
                CompilationUnit.this.getErrorCollector().addError(new SyntaxException(rpe.getMessage(), node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()), source);
            }
            LabelVerifier lv = new LabelVerifier(source);
            lv.visitClass(classNode);
            ClassCompletionVerifier completionVerifier = new ClassCompletionVerifier(source);
            completionVerifier.visitClass(classNode);
            ExtendedVerifier xverifier = new ExtendedVerifier(source);
            xverifier.visitClass(classNode);
            CompilationUnit.this.getErrorCollector().failIfErrors();
            ClassVisitor visitor = CompilationUnit.this.createClassVisitor();
            String string = sourceName = source == null ? classNode.getModule().getDescription() : source.getName();
            if (sourceName != null) {
                sourceName = sourceName.substring(Math.max(sourceName.lastIndexOf(92), sourceName.lastIndexOf(47)) + 1);
            }
            AsmClassGenerator generator = new AsmClassGenerator(source, context, visitor, sourceName);
            generator.visitClass(classNode);
            byte[] bytes = ((ClassWriter)visitor).toByteArray();
            CompilationUnit.this.generatedClasses.add(new GroovyClass(classNode.getName(), bytes));
            if (CompilationUnit.this.classgenCallback != null) {
                CompilationUnit.this.classgenCallback.call(visitor, classNode);
            }
            LinkedList<ClassNode> innerClasses = generator.getInnerClasses();
            while (!innerClasses.isEmpty()) {
                CompilationUnit.this.classgen.call(source, context, innerClasses.removeFirst());
            }
        }
    };
    private SourceUnitOperation mark = new SourceUnitOperation(){

        @Override
        public void call(SourceUnit source) throws CompilationFailedException {
            if (source.phase < CompilationUnit.this.phase) {
                source.gotoPhase(CompilationUnit.this.phase);
            }
            if (source.phase == CompilationUnit.this.phase && CompilationUnit.this.phaseComplete && !source.phaseComplete) {
                source.completePhase();
            }
        }
    };

    public CompilationUnit() {
        this((CompilerConfiguration)null, (CodeSource)null, (GroovyClassLoader)null);
    }

    public CompilationUnit(GroovyClassLoader loader) {
        this(null, null, loader);
    }

    public CompilationUnit(CompilerConfiguration configuration) {
        this(configuration, (CodeSource)null, (GroovyClassLoader)null);
    }

    public CompilationUnit(CompilerConfiguration configuration, CodeSource security, GroovyClassLoader loader) {
        this(configuration, security, loader, null);
    }

    public CompilationUnit(CompilerConfiguration configuration, CodeSource security, GroovyClassLoader loader, GroovyClassLoader transformLoader) {
        super(configuration, loader, null);
        this.astTransformationsContext = new ASTTransformationsContext(this, transformLoader);
        this.names = new ArrayList<String>();
        this.queuedSources = new LinkedList();
        this.sources = new HashMap<String, SourceUnit>();
        this.summariesBySourceName = new HashMap();
        this.summariesByPublicClassName = new HashMap();
        this.classSourcesByPublicClassName = new HashMap();
        this.ast = new CompileUnit(this.classLoader, security, this.configuration);
        this.generatedClasses = new ArrayList<GroovyClass>();
        this.verifier = new Verifier();
        this.resolveVisitor = new ResolveVisitor(this);
        this.staticImportVisitor = new StaticImportVisitor();
        this.optimizer = new OptimizerVisitor(this);
        this.phaseOperations = new LinkedList[10];
        this.newPhaseOperations = new LinkedList[10];
        for (int i = 0; i < this.phaseOperations.length; ++i) {
            this.phaseOperations[i] = new LinkedList();
            this.newPhaseOperations[i] = new LinkedList();
        }
        this.addPhaseOperation(new SourceUnitOperation(){

            @Override
            public void call(SourceUnit source) throws CompilationFailedException {
                source.parse();
            }
        }, 2);
        this.addPhaseOperation(this.convert, 3);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                EnumVisitor ev = new EnumVisitor(CompilationUnit.this, source);
                ev.visitClass(classNode);
            }
        }, 3);
        this.addPhaseOperation(this.resolve, 4);
        this.addPhaseOperation(this.staticImport, 4);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                InnerClassVisitor iv = new InnerClassVisitor(CompilationUnit.this, source);
                iv.visitClass(classNode);
            }
        }, 4);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                TraitComposer.doExtendTraits(classNode, source, CompilationUnit.this);
            }
        }, 5);
        this.addPhaseOperation(this.compileCompleteCheck, 5);
        this.addPhaseOperation(this.classgen, 7);
        this.addPhaseOperation(this.output);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                AnnotationCollectorTransform.ClassChanger actt = new AnnotationCollectorTransform.ClassChanger();
                actt.transformClass(classNode);
            }
        }, 4);
        ASTTransformationVisitor.addPhaseOperations(this);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                StaticVerifier sv = new StaticVerifier();
                sv.visitClass(classNode, source);
            }
        }, 4);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                InnerClassCompletionVisitor iv = new InnerClassCompletionVisitor(CompilationUnit.this, source);
                iv.visitClass(classNode);
            }
        }, 5);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                EnumCompletionVisitor ecv = new EnumCompletionVisitor(CompilationUnit.this, source);
                ecv.visitClass(classNode);
            }
        }, 5);
        this.addPhaseOperation(new PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                Object callback = classNode.getNodeMetaData((Object)StaticCompilationMetadataKeys.DYNAMIC_OUTER_NODE_CALLBACK);
                if (callback instanceof PrimaryClassNodeOperation) {
                    ((PrimaryClassNodeOperation)callback).call(source, context, classNode);
                    classNode.removeNodeMetaData((Object)StaticCompilationMetadataKeys.DYNAMIC_OUTER_NODE_CALLBACK);
                }
            }
        }, 6);
        if (configuration != null) {
            List<CompilationCustomizer> customizers = configuration.getCompilationCustomizers();
            for (CompilationCustomizer customizer : customizers) {
                if (customizer instanceof CompilationUnitAware) {
                    ((CompilationUnitAware)((Object)customizer)).setCompilationUnit(this);
                }
                this.addPhaseOperation(customizer, customizer.getPhase().getPhaseNumber());
            }
        }
        this.classgenCallback = null;
        this.classNodeResolver = new ClassNodeResolver();
    }

    public GroovyClassLoader getTransformLoader() {
        return this.astTransformationsContext.getTransformLoader() == null ? this.getClassLoader() : this.astTransformationsContext.getTransformLoader();
    }

    public void addPhaseOperation(SourceUnitOperation op, int phase) {
        if (phase < 0 || phase > 9) {
            throw new IllegalArgumentException("phase " + phase + " is unknown");
        }
        this.phaseOperations[phase].add(op);
    }

    public void addPhaseOperation(PrimaryClassNodeOperation op, int phase) {
        if (phase < 0 || phase > 9) {
            throw new IllegalArgumentException("phase " + phase + " is unknown");
        }
        this.phaseOperations[phase].add(op);
    }

    public void addFirstPhaseOperation(PrimaryClassNodeOperation op, int phase) {
        if (phase < 0 || phase > 9) {
            throw new IllegalArgumentException("phase " + phase + " is unknown");
        }
        this.phaseOperations[phase].add(0, op);
    }

    public void addPhaseOperation(GroovyClassOperation op) {
        this.phaseOperations[8].addFirst(op);
    }

    public void addNewPhaseOperation(SourceUnitOperation op, int phase) {
        if (phase < 0 || phase > 9) {
            throw new IllegalArgumentException("phase " + phase + " is unknown");
        }
        this.newPhaseOperations[phase].add(op);
    }

    @Override
    public void configure(CompilerConfiguration configuration) {
        super.configure(configuration);
        this.debug = configuration.getDebug();
        if (!this.configured && this.classLoader instanceof GroovyClassLoader) {
            this.appendCompilerConfigurationClasspathToClassLoader(configuration, this.classLoader);
        }
        this.configured = true;
    }

    private void appendCompilerConfigurationClasspathToClassLoader(CompilerConfiguration configuration, GroovyClassLoader classLoader) {
    }

    public CompileUnit getAST() {
        return this.ast;
    }

    public Map getSummariesBySourceName() {
        return this.summariesBySourceName;
    }

    public Map getSummariesByPublicClassName() {
        return this.summariesByPublicClassName;
    }

    public Map getClassSourcesByPublicClassName() {
        return this.classSourcesByPublicClassName;
    }

    public boolean isPublicClass(String className) {
        return this.summariesByPublicClassName.containsKey(className);
    }

    public List getClasses() {
        return this.generatedClasses;
    }

    public ClassNode getFirstClassNode() {
        return this.ast.getModules().get(0).getClasses().get(0);
    }

    public ClassNode getClassNode(final String name) {
        ClassNode[] result;
        block2: {
            result = new ClassNode[]{null};
            PrimaryClassNodeOperation handler = new PrimaryClassNodeOperation(){

                @Override
                public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
                    if (classNode.getName().equals(name)) {
                        result[0] = classNode;
                    }
                }
            };
            try {
                this.applyToPrimaryClassNodes(handler);
            }
            catch (CompilationFailedException e) {
                if (!this.debug) break block2;
                e.printStackTrace();
            }
        }
        return result[0];
    }

    public ASTTransformationsContext getASTTransformationsContext() {
        return this.astTransformationsContext;
    }

    public void addSources(String[] paths) {
        for (String path : paths) {
            this.addSource(new File(path));
        }
    }

    public void addSources(File[] files) {
        for (File file : files) {
            this.addSource(file);
        }
    }

    public SourceUnit addSource(File file) {
        return this.addSource(new SourceUnit(file, this.configuration, this.classLoader, this.getErrorCollector()));
    }

    public SourceUnit addSource(URL url) {
        return this.addSource(new SourceUnit(url, this.configuration, this.classLoader, this.getErrorCollector()));
    }

    public SourceUnit addSource(String name, InputStream stream) {
        InputStreamReaderSource source = new InputStreamReaderSource(stream, this.configuration);
        return this.addSource(new SourceUnit(name, source, this.configuration, this.classLoader, this.getErrorCollector()));
    }

    public SourceUnit addSource(String name, String scriptText) {
        return this.addSource(new SourceUnit(name, scriptText, this.configuration, this.classLoader, this.getErrorCollector()));
    }

    public SourceUnit addSource(SourceUnit source) {
        String name = source.getName();
        source.setClassLoader(this.classLoader);
        for (SourceUnit su : this.queuedSources) {
            if (!name.equals(su.getName())) continue;
            return su;
        }
        this.queuedSources.add(source);
        return source;
    }

    public Iterator<SourceUnit> iterator() {
        return new Iterator<SourceUnit>(){
            Iterator<String> nameIterator;
            {
                this.nameIterator = CompilationUnit.this.names.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.nameIterator.hasNext();
            }

            @Override
            public SourceUnit next() {
                String name = this.nameIterator.next();
                return CompilationUnit.this.sources.get(name);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void addClassNode(ClassNode node) {
        ModuleNode module = new ModuleNode(this.ast);
        this.ast.addModule(module);
        module.addClass(node);
    }

    public void setClassgenCallback(ClassgenCallback visitor) {
        this.classgenCallback = visitor;
    }

    public void setProgressCallback(ProgressCallback callback) {
        this.progressCallback = callback;
    }

    public ClassgenCallback getClassgenCallback() {
        return this.classgenCallback;
    }

    public ProgressCallback getProgressCallback() {
        return this.progressCallback;
    }

    public void compile() throws CompilationFailedException {
        this.compile(9);
    }

    public void compile(int throughPhase) throws CompilationFailedException {
        this.gotoPhase(1);
        throughPhase = Math.min(throughPhase, 9);
        while (throughPhase >= this.phase && this.phase <= 9) {
            if (this.phase == 4) {
                this.doPhaseOperation(this.resolve);
                if (this.dequeued()) continue;
            }
            this.processPhaseOperations(this.phase);
            this.processNewPhaseOperations(this.phase);
            if (this.progressCallback != null) {
                this.progressCallback.call(this, this.phase);
            }
            this.completePhase();
            this.applyToSourceUnits(this.mark);
            if (this.dequeued()) continue;
            this.gotoPhase(this.phase + 1);
            if (this.phase != 7) continue;
            this.sortClasses();
        }
        this.errorCollector.failIfErrors();
    }

    private void processPhaseOperations(int ph) {
        LinkedList ops = this.phaseOperations[ph];
        for (Object next : ops) {
            this.doPhaseOperation(next);
        }
    }

    private void processNewPhaseOperations(int currPhase) {
        this.recordPhaseOpsInAllOtherPhases(currPhase);
        LinkedList currentPhaseNewOps = this.newPhaseOperations[currPhase];
        while (!currentPhaseNewOps.isEmpty()) {
            Object operation = currentPhaseNewOps.removeFirst();
            this.phaseOperations[currPhase].add(operation);
            this.doPhaseOperation(operation);
            this.recordPhaseOpsInAllOtherPhases(currPhase);
            currentPhaseNewOps = this.newPhaseOperations[currPhase];
        }
    }

    private void doPhaseOperation(Object operation) {
        if (operation instanceof PrimaryClassNodeOperation) {
            this.applyToPrimaryClassNodes((PrimaryClassNodeOperation)operation);
        } else if (operation instanceof SourceUnitOperation) {
            this.applyToSourceUnits((SourceUnitOperation)operation);
        } else {
            this.applyToGeneratedGroovyClasses((GroovyClassOperation)operation);
        }
    }

    private void recordPhaseOpsInAllOtherPhases(int currPhase) {
        for (int ph = 1; ph <= 9; ++ph) {
            if (ph == currPhase || this.newPhaseOperations[ph].isEmpty()) continue;
            this.phaseOperations[ph].addAll(this.newPhaseOperations[ph]);
            this.newPhaseOperations[ph].clear();
        }
    }

    private void sortClasses() throws CompilationFailedException {
        for (ModuleNode module : this.ast.getModules()) {
            module.sortClasses();
        }
    }

    protected boolean dequeued() throws CompilationFailedException {
        boolean dequeue;
        boolean bl = dequeue = !this.queuedSources.isEmpty();
        while (!this.queuedSources.isEmpty()) {
            SourceUnit su = this.queuedSources.removeFirst();
            String name = su.getName();
            this.names.add(name);
            this.sources.put(name, su);
        }
        if (dequeue) {
            this.gotoPhase(1);
        }
        return dequeue;
    }

    protected ClassVisitor createClassVisitor() {
        CompilerConfiguration config = this.getConfiguration();
        int computeMaxStackAndFrames = 1;
        if (CompilerConfiguration.isPostJDK7(config.getTargetBytecode()) || Boolean.TRUE.equals(config.getOptimizationOptions().get("indy"))) {
            computeMaxStackAndFrames += 2;
        }
        return new ClassWriter(computeMaxStackAndFrames){

            private ClassNode getClassNode(String name) {
                CompileUnit cu = CompilationUnit.this.getAST();
                ClassNode cn = cu.getClass(name);
                if (cn != null) {
                    return cn;
                }
                cn = cu.getGeneratedInnerClass(name);
                if (cn != null) {
                    return cn;
                }
                try {
                    cn = ClassHelper.make(cu.getClassLoader().loadClass(name, false, true), false);
                }
                catch (Exception e) {
                    throw new GroovyBugError(e);
                }
                return cn;
            }

            private ClassNode getCommonSuperClassNode(ClassNode c, ClassNode d) {
                if (c.isDerivedFrom(d)) {
                    return d;
                }
                if (d.isDerivedFrom(c)) {
                    return c;
                }
                if (c.isInterface() || d.isInterface()) {
                    return ClassHelper.OBJECT_TYPE;
                }
                while ((c = c.getSuperClass()) != null && !d.isDerivedFrom(c)) {
                }
                if (c == null) {
                    return ClassHelper.OBJECT_TYPE;
                }
                return c;
            }

            @Override
            protected String getCommonSuperClass(String arg1, String arg2) {
                ClassNode a = this.getClassNode(arg1.replace('/', '.'));
                ClassNode b = this.getClassNode(arg2.replace('/', '.'));
                return this.getCommonSuperClassNode(a, b).getName().replace('.', '/');
            }
        };
    }

    protected void mark() throws CompilationFailedException {
        this.applyToSourceUnits(this.mark);
    }

    public void applyToSourceUnits(SourceUnitOperation body) throws CompilationFailedException {
        for (String name : this.names) {
            SourceUnit source = this.sources.get(name);
            if (source.phase >= this.phase && (source.phase != this.phase || source.phaseComplete)) continue;
            try {
                body.call(source);
            }
            catch (CompilationFailedException e) {
                throw e;
            }
            catch (Exception e) {
                GroovyBugError gbe = new GroovyBugError(e);
                this.changeBugText(gbe, source);
                throw gbe;
            }
            catch (GroovyBugError e) {
                this.changeBugText(e, source);
                throw e;
            }
        }
        this.getErrorCollector().failIfErrors();
    }

    private static int getSuperClassCount(ClassNode element) {
        int count = 0;
        while (element != null) {
            ++count;
            element = element.getSuperClass();
        }
        return count;
    }

    private int getSuperInterfaceCount(ClassNode element) {
        ClassNode[] interfaces;
        int count = 1;
        for (ClassNode anInterface : interfaces = element.getInterfaces()) {
            count = Math.max(count, this.getSuperInterfaceCount(anInterface) + 1);
        }
        return count;
    }

    private List<ClassNode> getPrimaryClassNodes(boolean sort) {
        ArrayList<ClassNode> unsorted = new ArrayList<ClassNode>();
        for (ModuleNode module : this.ast.getModules()) {
            for (ClassNode classNode : module.getClasses()) {
                unsorted.add(classNode);
            }
        }
        if (!sort) {
            return unsorted;
        }
        int unsortedSize = unsorted.size();
        int[] indexClass = new int[unsortedSize];
        int[] indexInterface = new int[unsortedSize];
        int i = 0;
        for (ClassNode element : unsorted) {
            if (element.isInterface()) {
                indexInterface[i] = this.getSuperInterfaceCount(element);
                indexClass[i] = -1;
            } else {
                indexClass[i] = CompilationUnit.getSuperClassCount(element);
                indexInterface[i] = -1;
            }
            ++i;
        }
        List<ClassNode> sorted = CompilationUnit.getSorted(indexInterface, unsorted);
        sorted.addAll(CompilationUnit.getSorted(indexClass, unsorted));
        return sorted;
    }

    private static List<ClassNode> getSorted(int[] index, List<ClassNode> unsorted) {
        int unsortedSize = unsorted.size();
        ArrayList<ClassNode> sorted = new ArrayList<ClassNode>(unsortedSize);
        for (int i = 0; i < unsortedSize; ++i) {
            int min = -1;
            for (int j = 0; j < unsortedSize; ++j) {
                if (index[j] == -1 || min != -1 && index[j] >= index[min]) continue;
                min = j;
            }
            if (min == -1) break;
            sorted.add(unsorted.get(min));
            index[min] = -1;
        }
        return sorted;
    }

    public void applyToPrimaryClassNodes(PrimaryClassNodeOperation body) throws CompilationFailedException {
        for (ClassNode classNode : this.getPrimaryClassNodes(body.needSortedInput())) {
            SourceUnit context = null;
            try {
                context = classNode.getModule().getContext();
                if (context != null && context.phase >= this.phase && (context.phase != this.phase || context.phaseComplete)) continue;
                int offset = 1;
                Iterator<InnerClassNode> iterator = classNode.getInnerClasses();
                while (iterator.hasNext()) {
                    iterator.next();
                    ++offset;
                }
                body.call(context, new GeneratorContext(this.ast, offset), classNode);
            }
            catch (CompilationFailedException offset) {
            }
            catch (NullPointerException npe) {
                GroovyBugError gbe = new GroovyBugError("unexpected NullpointerException", npe);
                this.changeBugText(gbe, context);
                throw gbe;
            }
            catch (GroovyBugError e) {
                this.changeBugText(e, context);
                throw e;
            }
            catch (NoClassDefFoundError e) {
                this.convertUncaughtExceptionToCompilationError(e);
            }
            catch (Exception e) {
                this.convertUncaughtExceptionToCompilationError(e);
            }
        }
        this.getErrorCollector().failIfErrors();
    }

    private void convertUncaughtExceptionToCompilationError(Throwable e) {
        ErrorCollector nestedCollector = null;
        for (Throwable next = e.getCause(); next != e && next != null; next = next.getCause()) {
            if (!(next instanceof MultipleCompilationErrorsException)) continue;
            MultipleCompilationErrorsException mcee = (MultipleCompilationErrorsException)next;
            nestedCollector = mcee.collector;
            break;
        }
        if (nestedCollector != null) {
            this.getErrorCollector().addCollectorContents(nestedCollector);
        } else {
            Exception err = e instanceof Exception ? (Exception)e : new RuntimeException(e);
            this.getErrorCollector().addError(new ExceptionMessage(err, this.configuration.getDebug(), this));
        }
    }

    public void applyToGeneratedGroovyClasses(GroovyClassOperation body) throws CompilationFailedException {
        if (!(this.phase == 8 || this.phase == 7 && this.phaseComplete)) {
            throw new GroovyBugError("CompilationUnit not ready for output(). Current phase=" + this.getPhaseDescription());
        }
        for (GroovyClass gclass : this.generatedClasses) {
            try {
                body.call(gclass);
            }
            catch (CompilationFailedException compilationFailedException) {
            }
            catch (NullPointerException npe) {
                throw npe;
            }
            catch (GroovyBugError e) {
                this.changeBugText(e, null);
                throw e;
            }
            catch (Exception e) {
                throw new GroovyBugError(e);
            }
        }
        this.getErrorCollector().failIfErrors();
    }

    private void changeBugText(GroovyBugError e, SourceUnit context) {
        e.setBugText("exception in phase '" + this.getPhaseDescription() + "' in source unit '" + (context != null ? context.getName() : "?") + "' " + e.getBugText());
    }

    public ClassNodeResolver getClassNodeResolver() {
        return this.classNodeResolver;
    }

    public void setClassNodeResolver(ClassNodeResolver classNodeResolver) {
        this.classNodeResolver = classNodeResolver;
    }

    public static abstract class GroovyClassOperation {
        public abstract void call(GroovyClass var1) throws CompilationFailedException;
    }

    public static abstract class PrimaryClassNodeOperation {
        public abstract void call(SourceUnit var1, GeneratorContext var2, ClassNode var3) throws CompilationFailedException;

        public boolean needSortedInput() {
            return false;
        }
    }

    public static abstract class SourceUnitOperation {
        public abstract void call(SourceUnit var1) throws CompilationFailedException;
    }

    public static abstract class ProgressCallback {
        public abstract void call(ProcessingUnit var1, int var2) throws CompilationFailedException;
    }

    public static abstract class ClassgenCallback {
        public abstract void call(ClassVisitor var1, ClassNode var2) throws CompilationFailedException;
    }
}

