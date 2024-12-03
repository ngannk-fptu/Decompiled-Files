/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.CompileStatic;
import groovy.transform.TypeCheckingMode;
import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AnnotationCollectorTransform;

public class CompileDynamicProcessor
extends AnnotationCollectorTransform {
    private static final ClassNode COMPILESTATIC_NODE = ClassHelper.make(CompileStatic.class);
    private static final ClassNode TYPECHECKINGMODE_NODE = ClassHelper.make(TypeCheckingMode.class);

    @Override
    public List<AnnotationNode> visit(AnnotationNode collector, AnnotationNode aliasAnnotationUsage, AnnotatedNode aliasAnnotated, SourceUnit source) {
        AnnotationNode node = new AnnotationNode(COMPILESTATIC_NODE);
        node.addMember("value", new PropertyExpression((Expression)new ClassExpression(TYPECHECKINGMODE_NODE), "SKIP"));
        return Collections.singletonList(node);
    }
}

