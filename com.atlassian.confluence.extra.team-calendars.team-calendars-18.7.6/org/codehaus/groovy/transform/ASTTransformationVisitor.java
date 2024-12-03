/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyClassLoader;
import groovy.transform.CompilationUnitAware;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.ASTTransformationsContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.ASTTransformationCollectorCodeVisitor;
import org.codehaus.groovy.transform.GroovyASTTransformation;

public final class ASTTransformationVisitor
extends ClassCodeVisitorSupport {
    private final ASTTransformationsContext context;
    private final CompilePhase phase;
    private SourceUnit source;
    private List<ASTNode[]> targetNodes;
    private Map<ASTNode, List<ASTTransformation>> transforms;

    private ASTTransformationVisitor(CompilePhase phase, ASTTransformationsContext context) {
        this.phase = phase;
        this.context = context;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    @Override
    public void visitClass(ClassNode classNode) {
        Map<Class<? extends ASTTransformation>, Set<ASTNode>> baseTransforms = classNode.getTransforms(this.phase);
        if (!baseTransforms.isEmpty()) {
            HashMap<Class<? extends ASTTransformation>, ASTTransformation> transformInstances = new HashMap<Class<? extends ASTTransformation>, ASTTransformation>();
            for (Class<? extends ASTTransformation> clazz : baseTransforms.keySet()) {
                try {
                    transformInstances.put(clazz, clazz.newInstance());
                }
                catch (InstantiationException e) {
                    this.source.getErrorCollector().addError(new SimpleMessage("Could not instantiate Transformation Processor " + clazz, this.source));
                }
                catch (IllegalAccessException e) {
                    this.source.getErrorCollector().addError(new SimpleMessage("Could not instantiate Transformation Processor " + clazz, this.source));
                }
            }
            this.transforms = new HashMap<ASTNode, List<ASTTransformation>>();
            for (Map.Entry entry : baseTransforms.entrySet()) {
                for (ASTNode node : (Set)entry.getValue()) {
                    List<ASTTransformation> list = this.transforms.get(node);
                    if (list == null) {
                        list = new ArrayList<ASTTransformation>();
                        this.transforms.put(node, list);
                    }
                    list.add((ASTTransformation)transformInstances.get(entry.getKey()));
                }
            }
            this.targetNodes = new LinkedList<ASTNode[]>();
            super.visitClass(classNode);
            for (ASTNode[] aSTNodeArray : this.targetNodes) {
                for (ASTTransformation snt : this.transforms.get(aSTNodeArray[0])) {
                    if (snt instanceof CompilationUnitAware) {
                        ((CompilationUnitAware)((Object)snt)).setCompilationUnit(this.context.getCompilationUnit());
                    }
                    snt.visit(aSTNodeArray, this.source);
                }
            }
        }
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (!this.transforms.containsKey(annotation)) continue;
            this.targetNodes.add(new ASTNode[]{annotation, node});
        }
    }

    public static void addPhaseOperations(final CompilationUnit compilationUnit) {
        ASTTransformationsContext context = compilationUnit.getASTTransformationsContext();
        ASTTransformationVisitor.addGlobalTransforms(context);
        compilationUnit.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

            @Override
            public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                ASTTransformationCollectorCodeVisitor collector = new ASTTransformationCollectorCodeVisitor(source, compilationUnit.getTransformLoader());
                collector.visitClass(classNode);
            }
        }, 4);
        block3: for (CompilePhase phase : CompilePhase.values()) {
            final ASTTransformationVisitor visitor = new ASTTransformationVisitor(phase, context);
            switch (phase) {
                case INITIALIZATION: 
                case PARSING: 
                case CONVERSION: {
                    continue block3;
                }
                default: {
                    compilationUnit.addPhaseOperation(new CompilationUnit.PrimaryClassNodeOperation(){

                        @Override
                        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
                            visitor.source = source;
                            visitor.visitClass(classNode);
                        }
                    }, phase.getPhaseNumber());
                }
            }
        }
    }

    public static void addGlobalTransformsAfterGrab(ASTTransformationsContext context) {
        ASTTransformationVisitor.doAddGlobalTransforms(context, false);
    }

    public static void addGlobalTransforms(ASTTransformationsContext context) {
        ASTTransformationVisitor.doAddGlobalTransforms(context, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void doAddGlobalTransforms(ASTTransformationsContext context, boolean isFirstScan) {
        CompilationUnit compilationUnit = context.getCompilationUnit();
        GroovyClassLoader transformLoader = compilationUnit.getTransformLoader();
        LinkedHashMap<String, URL> transformNames = new LinkedHashMap<String, URL>();
        try {
            Enumeration<URL> globalServices = transformLoader.getResources("META-INF/services/org.codehaus.groovy.transform.ASTTransformation");
            while (globalServices.hasMoreElements()) {
                String className;
                URL service = globalServices.nextElement();
                BufferedReader svcIn = null;
                svcIn = new BufferedReader(new InputStreamReader(service.openStream(), "UTF-8"));
                try {
                    className = svcIn.readLine();
                }
                catch (IOException ioe) {
                    compilationUnit.getErrorCollector().addError(new SimpleMessage("IOException reading the service definition at " + service.toExternalForm() + " because of exception " + ioe.toString(), null));
                    if (svcIn == null) continue;
                    svcIn.close();
                    continue;
                }
                try {
                    Set<String> disabledGlobalTransforms = compilationUnit.getConfiguration().getDisabledGlobalASTTransformations();
                    if (disabledGlobalTransforms == null) {
                        disabledGlobalTransforms = Collections.emptySet();
                    }
                    while (className != null) {
                        if (!className.startsWith("#") && className.length() > 0 && !disabledGlobalTransforms.contains(className)) {
                            if (transformNames.containsKey(className)) {
                                try {
                                    if (!service.toURI().equals(((URL)transformNames.get(className)).toURI())) {
                                        compilationUnit.getErrorCollector().addWarning(2, "The global transform for class " + className + " is defined in both " + ((URL)transformNames.get(className)).toExternalForm() + " and " + service.toExternalForm() + " - the former definition will be used and the latter ignored.", null, null);
                                    }
                                }
                                catch (URISyntaxException e) {
                                    compilationUnit.getErrorCollector().addWarning(2, "Failed to parse URL as URI because of exception " + e.toString(), null, null);
                                }
                            } else {
                                transformNames.put(className, service);
                            }
                        }
                        try {
                            className = svcIn.readLine();
                        }
                        catch (IOException ioe) {
                            compilationUnit.getErrorCollector().addError(new SimpleMessage("IOException reading the service definition at " + service.toExternalForm() + " because of exception " + ioe.toString(), null));
                        }
                    }
                }
                catch (Throwable throwable) {
                    throw throwable;
                }
                finally {
                    if (svcIn == null) continue;
                    svcIn.close();
                }
            }
        }
        catch (IOException e) {
            compilationUnit.getErrorCollector().addError(new SimpleMessage("IO Exception attempting to load global transforms:" + e.getMessage(), null));
        }
        try {
            Class.forName("java.lang.annotation.Annotation");
        }
        catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Global ASTTransformations are not enabled in retro builds of groovy.\n");
            sb.append("The following transformations will be ignored:");
            for (Map.Entry entry : transformNames.entrySet()) {
                sb.append('\t');
                sb.append((String)entry.getKey());
                sb.append('\n');
            }
            compilationUnit.getErrorCollector().addWarning(new WarningMessage(2, sb.toString(), null, null));
            return;
        }
        if (isFirstScan) {
            for (Map.Entry entry : transformNames.entrySet()) {
                context.getGlobalTransformNames().add((String)entry.getKey());
            }
            ASTTransformationVisitor.addPhaseOperationsForGlobalTransforms(context.getCompilationUnit(), transformNames, isFirstScan);
        } else {
            Iterator it = transformNames.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry;
                entry = it.next();
                if (context.getGlobalTransformNames().add((String)entry.getKey())) continue;
                it.remove();
            }
            ASTTransformationVisitor.addPhaseOperationsForGlobalTransforms(context.getCompilationUnit(), transformNames, isFirstScan);
        }
    }

    private static void addPhaseOperationsForGlobalTransforms(CompilationUnit compilationUnit, Map<String, URL> transformNames, boolean isFirstScan) {
        GroovyClassLoader transformLoader = compilationUnit.getTransformLoader();
        for (Map.Entry<String, URL> entry : transformNames.entrySet()) {
            try {
                Class gTransClass = transformLoader.loadClass(entry.getKey(), false, true, false);
                GroovyASTTransformation transformAnnotation = gTransClass.getAnnotation(GroovyASTTransformation.class);
                if (transformAnnotation == null) {
                    compilationUnit.getErrorCollector().addWarning(new WarningMessage(2, "Transform Class " + entry.getKey() + " is specified as a global transform in " + entry.getValue().toExternalForm() + " but it is not annotated by " + GroovyASTTransformation.class.getName() + " the global transform associated with it may fail and cause the compilation to fail.", null, null));
                    continue;
                }
                if (ASTTransformation.class.isAssignableFrom(gTransClass)) {
                    final ASTTransformation instance = (ASTTransformation)gTransClass.newInstance();
                    if (instance instanceof CompilationUnitAware) {
                        ((CompilationUnitAware)((Object)instance)).setCompilationUnit(compilationUnit);
                    }
                    CompilationUnit.SourceUnitOperation suOp = new CompilationUnit.SourceUnitOperation(){

                        @Override
                        public void call(SourceUnit source) throws CompilationFailedException {
                            instance.visit(new ASTNode[]{source.getAST()}, source);
                        }
                    };
                    if (isFirstScan) {
                        compilationUnit.addPhaseOperation(suOp, transformAnnotation.phase().getPhaseNumber());
                        continue;
                    }
                    compilationUnit.addNewPhaseOperation(suOp, transformAnnotation.phase().getPhaseNumber());
                    continue;
                }
                compilationUnit.getErrorCollector().addError(new SimpleMessage("Transform Class " + entry.getKey() + " specified at " + entry.getValue().toExternalForm() + " is not an ASTTransformation.", null));
            }
            catch (Exception e) {
                compilationUnit.getErrorCollector().addError(new SimpleMessage("Could not instantiate global transform class " + entry.getKey() + " specified at " + entry.getValue().toExternalForm() + "  because of exception " + e.toString(), null));
            }
        }
    }
}

