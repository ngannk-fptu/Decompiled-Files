/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc.transformers;

import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.transform.sc.transformers.StaticCompilationTransformer;

public class ClosureExpressionTransformer {
    private final StaticCompilationTransformer transformer;

    public ClosureExpressionTransformer(StaticCompilationTransformer staticCompilationTransformer) {
        this.transformer = staticCompilationTransformer;
    }

    Expression transformClosureExpression(ClosureExpression expr) {
        Parameter[] parameters = expr.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (!parameter.hasInitialExpression()) continue;
                parameter.setInitialExpression(this.transformer.transform(parameter.getInitialExpression()));
            }
        }
        Statement code = expr.getCode();
        this.transformer.visitClassCodeContainer(code);
        return this.transformer.superTransform(expr);
    }
}

