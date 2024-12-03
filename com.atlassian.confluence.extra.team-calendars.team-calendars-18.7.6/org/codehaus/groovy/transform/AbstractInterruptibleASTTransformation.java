/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovyjarjarasm.asm.Opcodes;
import java.util.Arrays;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.LoopingStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.transform.ASTTransformation;

public abstract class AbstractInterruptibleASTTransformation
extends ClassCodeVisitorSupport
implements ASTTransformation,
Opcodes {
    protected static final String CHECK_METHOD_START_MEMBER = "checkOnMethodStart";
    private static final String APPLY_TO_ALL_CLASSES = "applyToAllClasses";
    private static final String APPLY_TO_ALL_MEMBERS = "applyToAllMembers";
    protected static final String THROWN_EXCEPTION_TYPE = "thrown";
    protected SourceUnit source;
    protected boolean checkOnMethodStart;
    protected boolean applyToAllClasses;
    protected boolean applyToAllMembers;
    protected ClassNode thrownExceptionType;

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    protected abstract ClassNode type();

    protected abstract Expression createCondition();

    protected abstract String getErrorMessage();

    protected void setupTransform(AnnotationNode node) {
        this.checkOnMethodStart = AbstractInterruptibleASTTransformation.getBooleanAnnotationParameter(node, CHECK_METHOD_START_MEMBER, true);
        this.applyToAllMembers = AbstractInterruptibleASTTransformation.getBooleanAnnotationParameter(node, APPLY_TO_ALL_MEMBERS, true);
        this.applyToAllClasses = this.applyToAllMembers ? AbstractInterruptibleASTTransformation.getBooleanAnnotationParameter(node, APPLY_TO_ALL_CLASSES, true) : false;
        this.thrownExceptionType = AbstractInterruptibleASTTransformation.getClassAnnotationParameter(node, THROWN_EXCEPTION_TYPE, ClassHelper.make(InterruptedException.class));
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        block14: {
            ModuleNode tree;
            AnnotatedNode annotatedNode;
            block13: {
                if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
                    AbstractInterruptibleASTTransformation.internalError("Expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
                }
                this.source = source;
                AnnotationNode node = (AnnotationNode)nodes[0];
                annotatedNode = (AnnotatedNode)nodes[1];
                if (!this.type().equals(node.getClassNode())) {
                    AbstractInterruptibleASTTransformation.internalError("Transformation called from wrong annotation: " + node.getClassNode().getName());
                }
                this.setupTransform(node);
                tree = source.getAST();
                if (!this.applyToAllClasses) break block13;
                if (tree == null) break block14;
                List<ClassNode> classes = tree.getClasses();
                for (ClassNode classNode : classes) {
                    this.visitClass(classNode);
                }
                break block14;
            }
            if (annotatedNode instanceof ClassNode) {
                this.visitClass((ClassNode)annotatedNode);
            } else if (!this.applyToAllMembers && annotatedNode instanceof MethodNode) {
                this.visitMethod((MethodNode)annotatedNode);
                this.visitClass(annotatedNode.getDeclaringClass());
            } else if (!this.applyToAllMembers && annotatedNode instanceof FieldNode) {
                this.visitField((FieldNode)annotatedNode);
                this.visitClass(annotatedNode.getDeclaringClass());
            } else if (!this.applyToAllMembers && annotatedNode instanceof DeclarationExpression) {
                this.visitDeclarationExpression((DeclarationExpression)annotatedNode);
                this.visitClass(annotatedNode.getDeclaringClass());
            } else if (tree != null) {
                List<ClassNode> classes = tree.getClasses();
                for (ClassNode classNode : classes) {
                    if (!classNode.isScript()) continue;
                    this.visitClass(classNode);
                }
            }
        }
    }

    protected static boolean getBooleanAnnotationParameter(AnnotationNode node, String parameterName, boolean defaultValue) {
        Expression member = node.getMember(parameterName);
        if (member != null) {
            if (member instanceof ConstantExpression) {
                try {
                    return DefaultGroovyMethods.asType(((ConstantExpression)member).getValue(), Boolean.class);
                }
                catch (Exception e) {
                    AbstractInterruptibleASTTransformation.internalError("Expecting boolean value for " + parameterName + " annotation parameter. Found " + member + "member");
                }
            } else {
                AbstractInterruptibleASTTransformation.internalError("Expecting boolean value for " + parameterName + " annotation parameter. Found " + member + "member");
            }
        }
        return defaultValue;
    }

    protected static ClassNode getClassAnnotationParameter(AnnotationNode node, String parameterName, ClassNode defaultValue) {
        Expression member = node.getMember(parameterName);
        if (member != null) {
            if (member instanceof ClassExpression) {
                try {
                    return member.getType();
                }
                catch (Exception e) {
                    AbstractInterruptibleASTTransformation.internalError("Expecting class value for " + parameterName + " annotation parameter. Found " + member + "member");
                }
            } else {
                AbstractInterruptibleASTTransformation.internalError("Expecting class value for " + parameterName + " annotation parameter. Found " + member + "member");
            }
        }
        return defaultValue;
    }

    protected static void internalError(String message) {
        throw new GroovyBugError("Internal error: " + message);
    }

    protected Statement createInterruptStatement() {
        return GeneralUtils.ifS(this.createCondition(), GeneralUtils.throwS(GeneralUtils.ctorX(this.thrownExceptionType, GeneralUtils.args(GeneralUtils.constX(this.getErrorMessage())))));
    }

    protected final Statement wrapBlock(Statement statement) {
        BlockStatement stmt = new BlockStatement();
        stmt.addStatement(this.createInterruptStatement());
        stmt.addStatement(statement);
        return stmt;
    }

    @Override
    public final void visitForLoop(ForStatement forStatement) {
        this.visitLoop(forStatement);
        super.visitForLoop(forStatement);
    }

    private void visitLoop(LoopingStatement loopStatement) {
        Statement statement = loopStatement.getLoopBlock();
        loopStatement.setLoopBlock(this.wrapBlock(statement));
    }

    @Override
    public final void visitDoWhileLoop(DoWhileStatement doWhileStatement) {
        this.visitLoop(doWhileStatement);
        super.visitDoWhileLoop(doWhileStatement);
    }

    @Override
    public final void visitWhileLoop(WhileStatement whileStatement) {
        this.visitLoop(whileStatement);
        super.visitWhileLoop(whileStatement);
    }
}

