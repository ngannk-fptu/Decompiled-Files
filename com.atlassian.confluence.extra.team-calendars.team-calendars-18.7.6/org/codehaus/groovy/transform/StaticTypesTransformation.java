/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.CompilationUnitAware;
import java.util.Collections;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.stc.GroovyTypeCheckingExtensionSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;

@GroovyASTTransformation(phase=CompilePhase.INSTRUCTION_SELECTION)
public class StaticTypesTransformation
implements ASTTransformation,
CompilationUnitAware {
    public static final String STATIC_ERROR_PREFIX = "[Static type checking] - ";
    protected CompilationUnit compilationUnit;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationInformation = (AnnotationNode)nodes[0];
        Map<String, Expression> members = annotationInformation.getMembers();
        Expression extensions = members.get("extensions");
        AnnotatedNode node = (AnnotatedNode)nodes[1];
        StaticTypeCheckingVisitor visitor = null;
        if (node instanceof ClassNode) {
            ClassNode classNode = (ClassNode)node;
            visitor = this.newVisitor(source, classNode);
            visitor.setCompilationUnit(this.compilationUnit);
            this.addTypeCheckingExtensions(visitor, extensions);
            visitor.initialize();
            visitor.visitClass(classNode);
        } else if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode)node;
            visitor = this.newVisitor(source, methodNode.getDeclaringClass());
            visitor.setCompilationUnit(this.compilationUnit);
            this.addTypeCheckingExtensions(visitor, extensions);
            visitor.setMethodsToBeVisited(Collections.singleton(methodNode));
            visitor.initialize();
            visitor.visitMethod(methodNode);
        } else {
            source.addError(new SyntaxException("[Static type checking] - Unimplemented node type", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()));
        }
        if (visitor != null) {
            visitor.performSecondPass();
        }
    }

    protected void addTypeCheckingExtensions(StaticTypeCheckingVisitor visitor, Expression extensions) {
        if (extensions instanceof ConstantExpression) {
            visitor.addTypeCheckingExtension(new GroovyTypeCheckingExtensionSupport(visitor, extensions.getText(), this.compilationUnit));
        } else if (extensions instanceof ListExpression) {
            ListExpression list = (ListExpression)extensions;
            for (Expression ext : list.getExpressions()) {
                this.addTypeCheckingExtensions(visitor, ext);
            }
        }
    }

    protected StaticTypeCheckingVisitor newVisitor(SourceUnit unit, ClassNode node) {
        return new StaticTypeCheckingVisitor(unit, node);
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        this.compilationUnit = unit;
    }
}

