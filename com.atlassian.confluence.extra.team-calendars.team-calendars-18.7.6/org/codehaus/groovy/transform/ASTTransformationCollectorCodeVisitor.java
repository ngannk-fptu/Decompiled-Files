/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyClassLoader;
import groovy.transform.AnnotationCollector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.AnnotationCollectorTransform;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.codehaus.groovy.transform.trait.TraitASTTransformation;
import org.codehaus.groovy.transform.trait.Traits;

public class ASTTransformationCollectorCodeVisitor
extends ClassCodeVisitorSupport {
    private SourceUnit source;
    private ClassNode classNode;
    private GroovyClassLoader transformLoader;

    public ASTTransformationCollectorCodeVisitor(SourceUnit source, GroovyClassLoader transformLoader) {
        this.source = source;
        this.transformLoader = transformLoader;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    @Override
    public void visitClass(ClassNode klassNode) {
        ClassNode oldClass = this.classNode;
        this.classNode = klassNode;
        super.visitClass(this.classNode);
        this.classNode = oldClass;
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        super.visitAnnotations(node);
        ArrayList<AnnotationNode> collected = new ArrayList<AnnotationNode>();
        Iterator<AnnotationNode> it = node.getAnnotations().iterator();
        while (it.hasNext()) {
            AnnotationNode annotation = it.next();
            if (!this.addCollectedAnnotations(collected, annotation, node)) continue;
            it.remove();
        }
        node.getAnnotations().addAll(collected);
        for (AnnotationNode annotation : node.getAnnotations()) {
            Annotation transformClassAnnotation = ASTTransformationCollectorCodeVisitor.getTransformClassAnnotation(annotation.getClassNode());
            if (transformClassAnnotation == null) continue;
            this.addTransformsToClassNode(annotation, transformClassAnnotation);
        }
    }

    private void assertStringConstant(Expression exp) {
        ConstantExpression ce;
        if (exp == null) {
            return;
        }
        if (!(exp instanceof ConstantExpression)) {
            this.source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("Expected a String constant.", exp.getLineNumber(), exp.getColumnNumber()), this.source));
        }
        if (!((ce = (ConstantExpression)exp).getValue() instanceof String)) {
            this.source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("Expected a String constant.", exp.getLineNumber(), exp.getColumnNumber()), this.source));
        }
    }

    private boolean addCollectedAnnotations(List<AnnotationNode> collected, AnnotationNode aliasNode, AnnotatedNode origin) {
        ClassNode classNode = aliasNode.getClassNode();
        boolean ret = false;
        for (AnnotationNode annotation : classNode.getAnnotations()) {
            if (!annotation.getClassNode().getName().equals(AnnotationCollector.class.getName())) continue;
            Expression processorExp = annotation.getMember("processor");
            AnnotationCollectorTransform act = null;
            this.assertStringConstant(processorExp);
            if (processorExp != null) {
                String className = (String)((ConstantExpression)processorExp).getValue();
                Class klass = this.loadTransformClass(className, aliasNode);
                if (klass != null) {
                    try {
                        act = (AnnotationCollectorTransform)klass.newInstance();
                    }
                    catch (InstantiationException e) {
                        this.source.getErrorCollector().addErrorAndContinue(new ExceptionMessage(e, true, this.source));
                    }
                    catch (IllegalAccessException e) {
                        this.source.getErrorCollector().addErrorAndContinue(new ExceptionMessage(e, true, this.source));
                    }
                }
            } else {
                act = new AnnotationCollectorTransform();
            }
            if (act != null) {
                collected.addAll(act.visit(annotation, aliasNode, origin, this.source));
            }
            ret = true;
        }
        return ret;
    }

    private void addTransformsToClassNode(AnnotationNode annotation, Annotation transformClassAnnotation) {
        List<String> transformClassNames = this.getTransformClassNames(annotation, transformClassAnnotation);
        if (transformClassNames.isEmpty()) {
            this.source.getErrorCollector().addError(new SimpleMessage("@GroovyASTTransformationClass in " + annotation.getClassNode().getName() + " does not specify any transform class names/classes", this.source));
        }
        for (String transformClass : transformClassNames) {
            Class klass = this.loadTransformClass(transformClass, annotation);
            if (klass == null) continue;
            this.verifyAndAddTransform(annotation, klass);
        }
    }

    private Class loadTransformClass(String transformClass, AnnotationNode annotation) {
        try {
            return this.transformLoader.loadClass(transformClass, false, true, false);
        }
        catch (ClassNotFoundException e) {
            this.source.getErrorCollector().addErrorAndContinue(new SimpleMessage("Could not find class for Transformation Processor " + transformClass + " declared by " + annotation.getClassNode().getName(), this.source));
            return null;
        }
    }

    private void verifyAndAddTransform(AnnotationNode annotation, Class klass) {
        this.verifyClass(annotation, klass);
        this.verifyCompilePhase(annotation, klass);
        this.addTransform(annotation, klass);
    }

    private void verifyCompilePhase(AnnotationNode annotation, Class<?> klass) {
        GroovyASTTransformation transformationClass = klass.getAnnotation(GroovyASTTransformation.class);
        if (transformationClass != null) {
            CompilePhase specifiedCompilePhase = transformationClass.phase();
            if (specifiedCompilePhase.getPhaseNumber() < CompilePhase.SEMANTIC_ANALYSIS.getPhaseNumber()) {
                this.source.getErrorCollector().addError(new SimpleMessage(annotation.getClassNode().getName() + " is defined to be run in compile phase " + (Object)((Object)specifiedCompilePhase) + ". Local AST transformations must run in " + (Object)((Object)CompilePhase.SEMANTIC_ANALYSIS) + " or later!", this.source));
            }
        } else {
            this.source.getErrorCollector().addError(new SimpleMessage("AST transformation implementation classes must be annotated with " + GroovyASTTransformation.class.getName() + ". " + klass.getName() + " lacks this annotation.", this.source));
        }
    }

    private void verifyClass(AnnotationNode annotation, Class klass) {
        if (!ASTTransformation.class.isAssignableFrom(klass)) {
            this.source.getErrorCollector().addError(new SimpleMessage("Not an ASTTransformation: " + klass.getName() + " declared by " + annotation.getClassNode().getName(), this.source));
        }
    }

    private void addTransform(AnnotationNode annotation, Class klass) {
        boolean apply;
        boolean bl = apply = !Traits.isTrait(this.classNode) || klass == TraitASTTransformation.class;
        if (apply) {
            this.classNode.addTransform(klass, annotation);
        }
    }

    private static Annotation getTransformClassAnnotation(ClassNode annotatedType) {
        if (!annotatedType.isResolved()) {
            return null;
        }
        for (Annotation ann : annotatedType.getTypeClass().getAnnotations()) {
            if (!ann.annotationType().getName().equals(GroovyASTTransformationClass.class.getName())) continue;
            return ann;
        }
        return null;
    }

    private List<String> getTransformClassNames(AnnotationNode annotation, Annotation transformClassAnnotation) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            Class[] classes;
            Method valueMethod = transformClassAnnotation.getClass().getMethod("value", new Class[0]);
            String[] names = (String[])valueMethod.invoke((Object)transformClassAnnotation, new Object[0]);
            result.addAll(Arrays.asList(names));
            Method classesMethod = transformClassAnnotation.getClass().getMethod("classes", new Class[0]);
            for (Class klass : classes = (Class[])classesMethod.invoke((Object)transformClassAnnotation, new Object[0])) {
                result.add(klass.getName());
            }
            if (names.length > 0 && classes.length > 0) {
                this.source.getErrorCollector().addError(new SimpleMessage("@GroovyASTTransformationClass in " + annotation.getClassNode().getName() + " should specify transforms only by class names or by classes and not by both", this.source));
            }
        }
        catch (Exception e) {
            this.source.addException(e);
        }
        return result;
    }
}

