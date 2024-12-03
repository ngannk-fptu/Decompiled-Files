/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import java.util.ArrayList;
import java.util.Arrays;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class NotYetImplementedASTTransformation
extends AbstractASTTransformation {
    private static final ClassNode CATCHED_THROWABLE_TYPE = ClassHelper.make(Throwable.class);
    private static final ClassNode ASSERTION_FAILED_ERROR_TYPE = ClassHelper.make("junit.framework.AssertionFailedError");

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }
        AnnotationNode annotationNode = (AnnotationNode)nodes[0];
        ASTNode node = nodes[1];
        if (!(node instanceof MethodNode)) {
            this.addError("@NotYetImplemented must only be applied on test methods!", node);
            return;
        }
        MethodNode methodNode = (MethodNode)node;
        ArrayList<Statement> statements = new ArrayList<Statement>();
        Statement statement = methodNode.getCode();
        if (statement instanceof BlockStatement) {
            statements.addAll(((BlockStatement)statement).getStatements());
        }
        if (statements.isEmpty()) {
            return;
        }
        BlockStatement rewrittenMethodCode = new BlockStatement();
        rewrittenMethodCode.addStatement(this.tryCatchAssertionFailedError(annotationNode, methodNode, statements));
        rewrittenMethodCode.addStatement(this.throwAssertionFailedError(annotationNode));
        methodNode.setCode(rewrittenMethodCode);
    }

    private TryCatchStatement tryCatchAssertionFailedError(AnnotationNode annotationNode, MethodNode methodNode, ArrayList<Statement> statements) {
        TryCatchStatement tryCatchStatement = new TryCatchStatement(GeneralUtils.block(methodNode.getVariableScope(), statements), EmptyStatement.INSTANCE);
        tryCatchStatement.addCatch(GeneralUtils.catchS(GeneralUtils.param(CATCHED_THROWABLE_TYPE, "ex"), ReturnStatement.RETURN_NULL_OR_VOID));
        return tryCatchStatement;
    }

    private Statement throwAssertionFailedError(AnnotationNode annotationNode) {
        ThrowStatement throwStatement = GeneralUtils.throwS(GeneralUtils.ctorX(ASSERTION_FAILED_ERROR_TYPE, GeneralUtils.args(GeneralUtils.constX("Method is marked with @NotYetImplemented but passes unexpectedly"))));
        throwStatement.setSourcePosition(annotationNode);
        return throwStatement;
    }
}

