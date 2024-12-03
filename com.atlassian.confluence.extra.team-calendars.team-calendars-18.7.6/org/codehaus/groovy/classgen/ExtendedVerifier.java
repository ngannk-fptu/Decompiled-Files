/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.ast.tools.ParameterUtils;
import org.codehaus.groovy.classgen.AnnotationVisitor;
import org.codehaus.groovy.control.AnnotationConstantsVisitor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;

public class ExtendedVerifier
extends ClassCodeVisitorSupport {
    public static final String JVM_ERROR_MESSAGE = "Please make sure you are running on a JVM >= 1.5";
    private SourceUnit source;
    private ClassNode currentClass;

    public ExtendedVerifier(SourceUnit sourceUnit) {
        this.source = sourceUnit;
    }

    @Override
    public void visitClass(ClassNode node) {
        AnnotationConstantsVisitor acv = new AnnotationConstantsVisitor();
        acv.visitClass(node, this.source);
        this.currentClass = node;
        if (node.isAnnotationDefinition()) {
            this.visitAnnotations(node, 64);
        } else {
            this.visitAnnotations(node, 1);
        }
        PackageNode packageNode = node.getPackage();
        if (packageNode != null) {
            this.visitAnnotations(packageNode, 128);
        }
        node.visitContents(this);
    }

    @Override
    public void visitField(FieldNode node) {
        this.visitAnnotations(node, 8);
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        this.visitAnnotations(expression, 32);
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        this.visitConstructorOrMethod((MethodNode)node, 2);
    }

    @Override
    public void visitMethod(MethodNode node) {
        this.visitConstructorOrMethod(node, 4);
    }

    private void visitConstructorOrMethod(MethodNode node, int methodTarget) {
        Statement code;
        this.visitAnnotations(node, methodTarget);
        for (int i = 0; i < node.getParameters().length; ++i) {
            Parameter parameter = node.getParameters()[i];
            this.visitAnnotations(parameter, 16);
        }
        if (this.currentClass.isAnnotationDefinition() && !node.isStaticConstructor()) {
            ReturnStatement code2;
            ErrorCollector errorCollector = new ErrorCollector(this.source.getConfiguration());
            AnnotationVisitor visitor = new AnnotationVisitor(this.source, errorCollector);
            visitor.setReportClass(this.currentClass);
            visitor.checkReturnType(node.getReturnType(), node);
            if (node.getParameters().length > 0) {
                this.addError("Annotation members may not have parameters.", node.getParameters()[0]);
            }
            if (node.getExceptions().length > 0) {
                this.addError("Annotation members may not have a throws clause.", node.getExceptions()[0]);
            }
            if ((code2 = (ReturnStatement)node.getCode()) != null) {
                visitor.visitExpression(node.getName(), code2.getExpression(), node.getReturnType());
                visitor.checkCircularReference(this.currentClass, node.getReturnType(), code2.getExpression());
            }
            this.source.getErrorCollector().addCollectorContents(errorCollector);
        }
        if ((code = node.getCode()) != null) {
            code.visit(this);
        }
    }

    @Override
    public void visitProperty(PropertyNode node) {
    }

    protected void visitAnnotations(AnnotatedNode node, int target) {
        if (node.getAnnotations().isEmpty()) {
            return;
        }
        this.currentClass.setAnnotated(true);
        if (!this.isAnnotationCompatible()) {
            this.addError("Annotations are not supported in the current runtime. Please make sure you are running on a JVM >= 1.5", node);
            return;
        }
        LinkedHashMap<String, List<AnnotationNode>> runtimeAnnotations = new LinkedHashMap<String, List<AnnotationNode>>();
        for (AnnotationNode unvisited : node.getAnnotations()) {
            boolean isTargetAnnotation;
            AnnotationNode visited = this.visitAnnotation(unvisited);
            String name = visited.getClassNode().getName();
            if (visited.hasRuntimeRetention()) {
                ArrayList<AnnotationNode> seen = (ArrayList<AnnotationNode>)runtimeAnnotations.get(name);
                if (seen == null) {
                    seen = new ArrayList<AnnotationNode>();
                }
                seen.add(visited);
                runtimeAnnotations.put(name, seen);
            }
            if (!(isTargetAnnotation = name.equals("java.lang.annotation.Target")) && !visited.isTargetAllowed(target)) {
                this.addError("Annotation @" + name + " is not allowed on element " + AnnotationNode.targetToName(target), visited);
            }
            ExtendedVerifier.visitDeprecation(node, visited);
            this.visitOverride(node, visited);
        }
        this.checkForDuplicateAnnotations(runtimeAnnotations);
    }

    private void checkForDuplicateAnnotations(Map<String, List<AnnotationNode>> runtimeAnnotations) {
        for (Map.Entry<String, List<AnnotationNode>> next : runtimeAnnotations.entrySet()) {
            if (next.getValue().size() <= 1) continue;
            String repeatableName = null;
            AnnotationNode repeatee = next.getValue().get(0);
            List<AnnotationNode> repeateeAnnotations = repeatee.getClassNode().getAnnotations();
            for (AnnotationNode anno : repeateeAnnotations) {
                ClassExpression ce;
                ClassNode annoClassNode = anno.getClassNode();
                if (!annoClassNode.getName().equals("java.lang.annotation.Repeatable")) continue;
                Expression value = anno.getMember("value");
                if (!(value instanceof ClassExpression) || (ce = (ClassExpression)value).getType() == null || !ce.getType().isAnnotationDefinition()) break;
                repeatableName = ce.getType().getName();
                break;
            }
            if (repeatableName != null) {
                this.addError("Annotation @" + next.getKey() + " has RUNTIME retention and " + next.getValue().size() + " occurrences. Automatic repeated annotations are not supported in this version of Groovy. Consider using the explicit @" + repeatableName + " collector annotation instead.", next.getValue().get(1));
                continue;
            }
            this.addError("Annotation @" + next.getKey() + " has RUNTIME retention and " + next.getValue().size() + " occurrences. Duplicate annotations not allowed.", next.getValue().get(1));
        }
    }

    private static void visitDeprecation(AnnotatedNode node, AnnotationNode visited) {
        if (visited.getClassNode().isResolved() && visited.getClassNode().getName().equals("java.lang.Deprecated")) {
            if (node instanceof MethodNode) {
                MethodNode mn = (MethodNode)node;
                mn.setModifiers(mn.getModifiers() | 0x20000);
            } else if (node instanceof FieldNode) {
                FieldNode fn = (FieldNode)node;
                fn.setModifiers(fn.getModifiers() | 0x20000);
            } else if (node instanceof ClassNode) {
                ClassNode cn = (ClassNode)node;
                cn.setModifiers(cn.getModifiers() | 0x20000);
            }
        }
    }

    private void visitOverride(AnnotatedNode node, AnnotationNode visited) {
        ClassNode annotationClassNode = visited.getClassNode();
        if (annotationClassNode.isResolved() && annotationClassNode.getName().equals("java.lang.Override") && node instanceof MethodNode && !Boolean.TRUE.equals(node.getNodeMetaData("DEFAULT_PARAMETER_GENERATED"))) {
            boolean override = false;
            MethodNode origMethod = (MethodNode)node;
            ClassNode cNode = origMethod.getDeclaringClass();
            if (origMethod.hasDefaultValue()) {
                List<MethodNode> variants = cNode.getDeclaredMethods(origMethod.getName());
                for (MethodNode m : variants) {
                    if (!m.getAnnotations().contains(visited) || !ExtendedVerifier.isOverrideMethod(m)) continue;
                    override = true;
                    break;
                }
            } else {
                override = ExtendedVerifier.isOverrideMethod(origMethod);
            }
            if (!override) {
                this.addError("Method '" + origMethod.getName() + "' from class '" + cNode.getName() + "' does not override method from its superclass or interfaces but is annotated with @Override.", visited);
            }
        }
    }

    private static boolean isOverrideMethod(MethodNode method) {
        ClassNode cNode;
        ClassNode next = cNode = method.getDeclaringClass();
        block0: while (next != null) {
            ClassNode correctedNext;
            MethodNode found;
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(next);
            MethodNode mn = GenericsUtils.correctToGenericsSpec(genericsSpec, method);
            if (next != cNode && (found = ExtendedVerifier.getDeclaredMethodCorrected(genericsSpec, mn, correctedNext = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, next))) != null) break;
            ArrayList<ClassNode> ifaces = new ArrayList<ClassNode>();
            ifaces.addAll(Arrays.asList(next.getInterfaces()));
            Map<String, ClassNode> updatedGenericsSpec = new HashMap<String, ClassNode>(genericsSpec);
            while (!ifaces.isEmpty()) {
                ClassNode iNode;
                ClassNode origInterface = (ClassNode)ifaces.remove(0);
                if (origInterface.equals(ClassHelper.OBJECT_TYPE)) continue;
                MethodNode found2 = ExtendedVerifier.getDeclaredMethodCorrected(updatedGenericsSpec = GenericsUtils.createGenericsSpec(origInterface, updatedGenericsSpec), mn, iNode = GenericsUtils.correctToGenericsSpecRecurse(updatedGenericsSpec, origInterface));
                if (found2 != null) break block0;
                ifaces.addAll(Arrays.asList(iNode.getInterfaces()));
            }
            ClassNode superClass = next.getUnresolvedSuperClass();
            if (superClass != null) {
                next = GenericsUtils.correctToGenericsSpecRecurse(updatedGenericsSpec, superClass);
                continue;
            }
            next = null;
        }
        return next != null;
    }

    private static MethodNode getDeclaredMethodCorrected(Map genericsSpec, MethodNode mn, ClassNode correctedNext) {
        for (MethodNode orig : correctedNext.getDeclaredMethods(mn.getName())) {
            MethodNode method = GenericsUtils.correctToGenericsSpec((Map<String, ClassNode>)genericsSpec, orig);
            if (!ParameterUtils.parametersEqual(method.getParameters(), mn.getParameters())) continue;
            return method;
        }
        return null;
    }

    private AnnotationNode visitAnnotation(AnnotationNode unvisited) {
        ErrorCollector errorCollector = new ErrorCollector(this.source.getConfiguration());
        AnnotationVisitor visitor = new AnnotationVisitor(this.source, errorCollector);
        AnnotationNode visited = visitor.visit(unvisited);
        this.source.getErrorCollector().addCollectorContents(errorCollector);
        return visited;
    }

    protected boolean isAnnotationCompatible() {
        return CompilerConfiguration.isPostJDK5(this.source.getConfiguration().getTargetBytecode());
    }

    @Override
    protected void addError(String msg, ASTNode expr) {
        this.source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), this.source));
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    public void visitGenericType(GenericsType genericsType) {
    }
}

