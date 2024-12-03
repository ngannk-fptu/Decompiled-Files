/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc;

import java.util.Collections;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.classgen.asm.WriterControllerFactory;
import org.codehaus.groovy.classgen.asm.sc.StaticTypesWriterControllerFactoryImpl;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.StaticTypesTransformation;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.sc.StaticCompilationVisitor;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;

@GroovyASTTransformation(phase=CompilePhase.INSTRUCTION_SELECTION)
public class StaticCompileTransformation
extends StaticTypesTransformation {
    private final StaticTypesWriterControllerFactoryImpl factory = new StaticTypesWriterControllerFactoryImpl();

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotationInformation = (AnnotationNode)nodes[0];
        AnnotatedNode node = (AnnotatedNode)nodes[1];
        StaticTypeCheckingVisitor visitor = null;
        Map<String, Expression> members = annotationInformation.getMembers();
        Expression extensions = members.get("extensions");
        if (node instanceof ClassNode) {
            ClassNode classNode = (ClassNode)node;
            visitor = this.newVisitor(source, classNode);
            visitor.setCompilationUnit(this.compilationUnit);
            this.addTypeCheckingExtensions(visitor, extensions);
            classNode.putNodeMetaData(WriterControllerFactory.class, this.factory);
            node.putNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE, !visitor.isSkipMode(node));
            visitor.initialize();
            visitor.visitClass(classNode);
        } else if (node instanceof MethodNode) {
            MethodNode methodNode = (MethodNode)node;
            ClassNode declaringClass = methodNode.getDeclaringClass();
            visitor = this.newVisitor(source, declaringClass);
            visitor.setCompilationUnit(this.compilationUnit);
            this.addTypeCheckingExtensions(visitor, extensions);
            methodNode.putNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE, !visitor.isSkipMode(node));
            if (declaringClass.getNodeMetaData(WriterControllerFactory.class) == null) {
                declaringClass.putNodeMetaData(WriterControllerFactory.class, this.factory);
            }
            visitor.setMethodsToBeVisited(Collections.singleton(methodNode));
            visitor.initialize();
            visitor.visitMethod(methodNode);
        } else {
            source.addError(new SyntaxException("[Static type checking] - Unimplemented node type", node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber()));
        }
        if (visitor != null) {
            visitor.performSecondPass();
        }
        StaticCompilationTransformer transformer = new StaticCompilationTransformer(source, visitor);
        if (node instanceof ClassNode) {
            transformer.visitClass((ClassNode)node);
        } else if (node instanceof MethodNode) {
            transformer.visitMethod((MethodNode)node);
        }
    }

    @Override
    protected StaticTypeCheckingVisitor newVisitor(SourceUnit unit, ClassNode node) {
        return new StaticCompilationVisitor(unit, node);
    }
}

