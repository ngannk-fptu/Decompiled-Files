/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovy.lang.Mixin;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@Deprecated
@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class MixinASTTransformation
extends AbstractASTTransformation {
    private static final ClassNode MY_TYPE = ClassHelper.make(Mixin.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotationNode node = (AnnotationNode)nodes[0];
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        if (!MY_TYPE.equals(node.getClassNode())) {
            return;
        }
        Expression expr = node.getMember("value");
        if (expr == null) {
            return;
        }
        Expression useClasses = null;
        if (expr instanceof ClassExpression) {
            useClasses = expr;
        } else if (expr instanceof ListExpression) {
            ListExpression listExpression = (ListExpression)expr;
            for (Expression ex : listExpression.getExpressions()) {
                if (ex instanceof ClassExpression) continue;
                return;
            }
            useClasses = expr;
        }
        if (useClasses == null) {
            return;
        }
        if (parent instanceof ClassNode) {
            ClassNode annotatedClass = (ClassNode)parent;
            Parameter[] noparams = Parameter.EMPTY_ARRAY;
            MethodNode clinit = annotatedClass.getDeclaredMethod("<clinit>", noparams);
            if (clinit == null) {
                clinit = annotatedClass.addMethod("<clinit>", 4105, ClassHelper.VOID_TYPE, noparams, null, new BlockStatement());
                clinit.setSynthetic(true);
            }
            BlockStatement code = (BlockStatement)clinit.getCode();
            code.addStatement(GeneralUtils.stmt(GeneralUtils.callX(GeneralUtils.propX((Expression)GeneralUtils.classX(annotatedClass), "metaClass"), "mixin", useClasses)));
        }
    }
}

