/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Reference;
import groovyjarjarasm.asm.Opcodes;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class CategoryASTTransformation
implements ASTTransformation,
Opcodes {
    private final VariableExpression thisExpression = CategoryASTTransformation.createThisExpression();

    private static VariableExpression createThisExpression() {
        VariableExpression expr = new VariableExpression("$this");
        expr.setClosureSharedVariable(true);
        return expr;
    }

    @Override
    public void visit(ASTNode[] nodes, final SourceUnit source) {
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof ClassNode)) {
            source.getErrorCollector().addError(new SyntaxErrorMessage(new SyntaxException("@Category can only be added to a ClassNode but got: " + (nodes.length == 2 ? nodes[1] : "nothing"), nodes[0].getLineNumber(), nodes[0].getColumnNumber()), source));
        }
        AnnotationNode annotation = (AnnotationNode)nodes[0];
        ClassNode parent = (ClassNode)nodes[1];
        ClassNode targetClass = this.getTargetClass(source, annotation);
        this.thisExpression.setType(targetClass);
        final LinkedList varStack = new LinkedList();
        if (!this.ensureNoInstanceFieldOrProperty(source, parent)) {
            return;
        }
        HashSet<String> names = new HashSet<String>();
        for (FieldNode fieldNode : parent.getFields()) {
            names.add(fieldNode.getName());
        }
        for (PropertyNode propertyNode : parent.getProperties()) {
            names.add(propertyNode.getName());
        }
        varStack.add(names);
        final Reference<Parameter> parameter = new Reference<Parameter>();
        ClassCodeExpressionTransformer classCodeExpressionTransformer = new ClassCodeExpressionTransformer(){

            @Override
            protected SourceUnit getSourceUnit() {
                return source;
            }

            private void addVariablesToStack(Parameter[] params) {
                HashSet<String> names = new HashSet<String>();
                names.addAll((Collection)varStack.getLast());
                for (Parameter param : params) {
                    names.add(param.getName());
                }
                varStack.add(names);
            }

            @Override
            public void visitCatchStatement(CatchStatement statement) {
                ((Set)varStack.getLast()).add(statement.getVariable().getName());
                super.visitCatchStatement(statement);
                ((Set)varStack.getLast()).remove(statement.getVariable().getName());
            }

            @Override
            public void visitMethod(MethodNode node) {
                this.addVariablesToStack(node.getParameters());
                super.visitMethod(node);
                varStack.removeLast();
            }

            @Override
            public void visitBlockStatement(BlockStatement block) {
                HashSet names = new HashSet();
                names.addAll((Collection)varStack.getLast());
                varStack.add(names);
                super.visitBlockStatement(block);
                varStack.remove(names);
            }

            @Override
            public void visitClosureExpression(ClosureExpression ce) {
                this.addVariablesToStack(ce.getParameters());
                super.visitClosureExpression(ce);
                varStack.removeLast();
            }

            @Override
            public void visitDeclarationExpression(DeclarationExpression expression) {
                if (expression.isMultipleAssignmentDeclaration()) {
                    TupleExpression te = expression.getTupleExpression();
                    List<Expression> list = te.getExpressions();
                    for (Expression arg : list) {
                        VariableExpression ve = (VariableExpression)arg;
                        ((Set)varStack.getLast()).add(ve.getName());
                    }
                } else {
                    VariableExpression ve = expression.getVariableExpression();
                    ((Set)varStack.getLast()).add(ve.getName());
                }
                super.visitDeclarationExpression(expression);
            }

            @Override
            public void visitForLoop(ForStatement forLoop) {
                Expression exp = forLoop.getCollectionExpression();
                exp.visit(this);
                Parameter loopParam = forLoop.getVariable();
                if (loopParam != null) {
                    ((Set)varStack.getLast()).add(loopParam.getName());
                }
                super.visitForLoop(forLoop);
            }

            @Override
            public void visitExpressionStatement(ExpressionStatement es) {
                Expression exp = es.getExpression();
                if (exp instanceof DeclarationExpression) {
                    exp.visit(this);
                }
                super.visitExpressionStatement(es);
            }

            @Override
            public Expression transform(Expression exp) {
                if (exp instanceof VariableExpression) {
                    VariableExpression ve = (VariableExpression)exp;
                    if (ve.getName().equals("this")) {
                        return CategoryASTTransformation.this.thisExpression;
                    }
                    if (!((Set)varStack.getLast()).contains(ve.getName())) {
                        return new PropertyExpression((Expression)CategoryASTTransformation.this.thisExpression, ve.getName());
                    }
                } else if (exp instanceof PropertyExpression) {
                    VariableExpression vex;
                    PropertyExpression pe = (PropertyExpression)exp;
                    if (pe.getObjectExpression() instanceof VariableExpression && (vex = (VariableExpression)pe.getObjectExpression()).isThisExpression()) {
                        pe.setObjectExpression(CategoryASTTransformation.this.thisExpression);
                        return pe;
                    }
                } else if (exp instanceof ClosureExpression) {
                    ClosureExpression ce = (ClosureExpression)exp;
                    ce.getVariableScope().putReferencedLocalVariable((Parameter)parameter.get());
                    Parameter[] params = ce.getParameters();
                    if (params == null) {
                        params = Parameter.EMPTY_ARRAY;
                    } else if (params.length == 0) {
                        params = new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE, "it")};
                    }
                    this.addVariablesToStack(params);
                    ce.getCode().visit(this);
                    varStack.removeLast();
                }
                return super.transform(exp);
            }
        };
        for (MethodNode method : parent.getMethods()) {
            if (method.isStatic()) continue;
            method.setModifiers(method.getModifiers() | 8);
            Parameter[] origParams = method.getParameters();
            Parameter[] newParams = new Parameter[origParams.length + 1];
            Parameter p = new Parameter(targetClass, "$this");
            p.setClosureSharedVariable(true);
            newParams[0] = p;
            parameter.set(p);
            System.arraycopy(origParams, 0, newParams, 1, origParams.length);
            method.setParameters(newParams);
            classCodeExpressionTransformer.visitMethod(method);
        }
        new VariableScopeVisitor(source, true).visitClass(parent);
    }

    private boolean ensureNoInstanceFieldOrProperty(SourceUnit source, ClassNode parent) {
        boolean valid = true;
        for (FieldNode fieldNode : parent.getFields()) {
            if (fieldNode.isStatic() || fieldNode.getLineNumber() <= 0) continue;
            CategoryASTTransformation.addUnsupportedError(fieldNode, source);
            valid = false;
        }
        for (PropertyNode propertyNode : parent.getProperties()) {
            if (propertyNode.isStatic() || propertyNode.getLineNumber() <= 0) continue;
            CategoryASTTransformation.addUnsupportedError(propertyNode, source);
            valid = false;
        }
        return valid;
    }

    private static void addUnsupportedError(ASTNode node, SourceUnit unit) {
        unit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("The @Category transformation does not support instance " + (node instanceof FieldNode ? "fields" : "properties") + " but found [" + CategoryASTTransformation.getName(node) + "]", node.getLineNumber(), node.getColumnNumber()), unit));
    }

    private static String getName(ASTNode node) {
        if (node instanceof FieldNode) {
            return ((FieldNode)node).getName();
        }
        if (node instanceof PropertyNode) {
            return ((PropertyNode)node).getName();
        }
        return node.getText();
    }

    private ClassNode getTargetClass(SourceUnit source, AnnotationNode annotation) {
        Expression value = annotation.getMember("value");
        if (value == null || !(value instanceof ClassExpression)) {
            source.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException("@groovy.lang.Category must define 'value' which is the class to apply this category to", annotation.getLineNumber(), annotation.getColumnNumber(), annotation.getLastLineNumber(), annotation.getLastColumnNumber()), source));
            return null;
        }
        ClassExpression ce = (ClassExpression)value;
        return ce.getType();
    }
}

