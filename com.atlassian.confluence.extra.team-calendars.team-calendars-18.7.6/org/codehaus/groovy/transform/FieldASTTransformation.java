/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Lazy;
import groovy.transform.Field;
import groovyjarjarasm.asm.Opcodes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.codehaus.groovy.transform.LazyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class FieldASTTransformation
extends ClassCodeExpressionTransformer
implements ASTTransformation,
Opcodes {
    private static final Class MY_CLASS = Field.class;
    private static final ClassNode MY_TYPE = ClassHelper.make(MY_CLASS);
    private static final ClassNode LAZY_TYPE = ClassHelper.make(Lazy.class);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode ASTTRANSFORMCLASS_TYPE = ClassHelper.make(GroovyASTTransformationClass.class);
    private SourceUnit sourceUnit;
    private DeclarationExpression candidate;
    private boolean insideScriptBody;
    private String variableName;
    private FieldNode fieldNode;
    private ClosureExpression currentClosure;
    private ConstructorCallExpression currentAIC;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.sourceUnit = source;
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new GroovyBugError("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        if (parent instanceof DeclarationExpression) {
            DeclarationExpression de = (DeclarationExpression)parent;
            ClassNode cNode = de.getDeclaringClass();
            if (!cNode.isScript()) {
                this.addError("Annotation " + MY_TYPE_NAME + " can only be used within a Script.", parent);
                return;
            }
            this.candidate = de;
            if (de.isMultipleAssignmentDeclaration()) {
                this.addError("Annotation " + MY_TYPE_NAME + " not supported with multiple assignment notation.", parent);
                return;
            }
            VariableExpression ve = de.getVariableExpression();
            this.variableName = ve.getName();
            this.fieldNode = new FieldNode(this.variableName, ve.getModifiers(), ve.getType(), null, de.getRightExpression());
            this.fieldNode.setSourcePosition(de);
            cNode.addField(this.fieldNode);
            List<AnnotationNode> annotations = de.getAnnotations();
            for (AnnotationNode annotation : annotations) {
                ClassNode annotationClassNode;
                if (annotation.getClassNode().equals(LAZY_TYPE)) {
                    LazyASTTransformation.visitField(annotation, this.fieldNode);
                }
                if (!this.notTransform(annotationClassNode = annotation.getClassNode()) && !this.acceptableTransform(annotation)) continue;
                this.fieldNode.addAnnotation(annotation);
            }
            super.visitClass(cNode);
            VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(source);
            scopeVisitor.visitClass(cNode);
        }
    }

    private boolean acceptableTransform(AnnotationNode annotation) {
        return !annotation.getClassNode().equals(MY_TYPE);
    }

    private boolean notTransform(ClassNode annotationClassNode) {
        return annotationClassNode.getAnnotations(ASTTRANSFORMCLASS_TYPE).isEmpty();
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof DeclarationExpression) {
            DeclarationExpression de = (DeclarationExpression)expr;
            if (de.getLeftExpression() == this.candidate.getLeftExpression()) {
                if (this.insideScriptBody) {
                    return new ConstantExpression(null);
                }
                this.addError("Annotation " + MY_TYPE_NAME + " can only be used within a Script body.", expr);
                return expr;
            }
        } else if (this.insideScriptBody && expr instanceof VariableExpression && this.currentClosure != null) {
            VariableExpression ve = (VariableExpression)expr;
            if (ve.getName().equals(this.variableName)) {
                this.adjustToClassVar(ve);
                return ve;
            }
        } else if (this.currentAIC != null && expr instanceof ArgumentListExpression) {
            Expression skip = null;
            List<Expression> origArgList = ((ArgumentListExpression)expr).getExpressions();
            for (int i = 0; i < origArgList.size(); ++i) {
                Expression arg = origArgList.get(i);
                if (!this.matchesCandidate(arg)) continue;
                skip = arg;
                this.adjustConstructorAndFields(i, this.currentAIC.getType());
                break;
            }
            if (skip != null) {
                return this.adjustedArgList(skip, origArgList);
            }
        }
        return expr.transformExpression(this);
    }

    private boolean matchesCandidate(Expression arg) {
        return arg instanceof VariableExpression && ((VariableExpression)arg).getAccessedVariable() == this.candidate.getVariableExpression().getAccessedVariable();
    }

    private Expression adjustedArgList(Expression skip, List<Expression> origArgs) {
        ArrayList<Expression> newArgs = new ArrayList<Expression>(origArgs.size() - 1);
        for (Expression origArg : origArgs) {
            if (skip == origArg) continue;
            newArgs.add(origArg);
        }
        return new ArgumentListExpression(newArgs);
    }

    private void adjustConstructorAndFields(int skipIndex, ClassNode type) {
        List<ConstructorNode> constructors = type.getDeclaredConstructors();
        if (constructors.size() == 1) {
            ConstructorNode constructor = constructors.get(0);
            Parameter[] params = constructor.getParameters();
            Parameter[] newParams = new Parameter[params.length - 1];
            int to = 0;
            for (int from = 0; from < params.length; ++from) {
                if (from == skipIndex) continue;
                newParams[to++] = params[from];
            }
            type.removeConstructor(constructor);
            type.addConstructor(constructor.getModifiers(), newParams, constructor.getExceptions(), constructor.getCode());
            type.removeField(this.variableName);
        }
    }

    private void adjustToClassVar(VariableExpression expr) {
        expr.setAccessedVariable(this.fieldNode);
        VariableScope variableScope = this.currentClosure.getVariableScope();
        Iterator<Variable> iterator = variableScope.getReferencedLocalVariablesIterator();
        while (iterator.hasNext()) {
            Variable next = iterator.next();
            if (!next.getName().equals(this.variableName)) continue;
            iterator.remove();
        }
        variableScope.putReferencedClassVariable(this.fieldNode);
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        ClosureExpression old = this.currentClosure;
        this.currentClosure = expression;
        super.visitClosureExpression(expression);
        this.currentClosure = old;
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression cce) {
        if (!this.insideScriptBody || !cce.isUsingAnonymousInnerClass()) {
            return;
        }
        ConstructorCallExpression old = this.currentAIC;
        this.currentAIC = cce;
        Expression newArgs = this.transform(cce.getArguments());
        if (cce.getArguments() instanceof TupleExpression && newArgs instanceof TupleExpression) {
            List<Expression> argList = ((TupleExpression)cce.getArguments()).getExpressions();
            argList.clear();
            argList.addAll(((TupleExpression)newArgs).getExpressions());
        }
        this.currentAIC = old;
    }

    @Override
    public void visitMethod(MethodNode node) {
        Boolean oldInsideScriptBody = this.insideScriptBody;
        if (node.isScriptBody()) {
            this.insideScriptBody = true;
        }
        super.visitMethod(node);
        this.insideScriptBody = oldInsideScriptBody;
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement es) {
        Expression exp = es.getExpression();
        exp.visit(this);
        super.visitExpressionStatement(es);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }
}

