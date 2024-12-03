/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class ListExpressionTransformer {
    private final StaticCompilationTransformer transformer;

    public ListExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.transformer = staticCompilationTransformer;
    }

    Expression transformListExpression(ListExpression expr) {
        MethodNode target = (MethodNode)expr.getNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET);
        if (target != null && target instanceof ConstructorNode) {
            if (target.getDeclaringClass().isArray()) {
                return this.transformArrayConstructor(expr, target);
            }
            return this.transformRegularConstructor(expr, target);
        }
        return this.transformer.superTransform(expr);
    }

    private Expression transformArrayConstructor(ListExpression expr, MethodNode target) {
        ArrayExpression aex = new ArrayExpression(target.getDeclaringClass().getComponentType(), this.transformArguments(expr));
        aex.setSourcePosition(expr);
        return aex;
    }

    private Expression transformRegularConstructor(ListExpression expr, MethodNode target) {
        List<Expression> transformedArgs = this.transformArguments(expr);
        ConstructorCallExpression cce = new ConstructorCallExpression(target.getDeclaringClass(), new ArgumentListExpression(transformedArgs));
        cce.setSourcePosition(expr);
        cce.putNodeMetaData((Object)StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, target);
        return cce;
    }

    private List<Expression> transformArguments(ListExpression expr) {
        List<Expression> expressions = expr.getExpressions();
        LinkedList<Expression> transformedArgs = new LinkedList<Expression>();
        for (Expression expression : expressions) {
            transformedArgs.add(this.transformer.transform(expression));
        }
        return transformedArgs;
    }
}

