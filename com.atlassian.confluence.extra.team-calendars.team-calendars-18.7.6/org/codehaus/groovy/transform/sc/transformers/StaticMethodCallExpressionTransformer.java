/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticMethodCallExpressionTransformer {
    private final StaticCompilationTransformer transformer;

    public StaticMethodCallExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.transformer = staticCompilationTransformer;
    }

    Expression transformStaticMethodCallExpression(StaticMethodCallExpression orig) {
        MethodNode target = (MethodNode)orig.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (target != null) {
            MethodCallExpression call = new MethodCallExpression((Expression)new ClassExpression(orig.getOwnerType()), orig.getMethod(), orig.getArguments());
            call.setMethodTarget(target);
            call.setSourcePosition(orig);
            call.copyNodeMetaData(orig);
            return this.transformer.transform(call);
        }
        return this.transformer.superTransform(orig);
    }
}

