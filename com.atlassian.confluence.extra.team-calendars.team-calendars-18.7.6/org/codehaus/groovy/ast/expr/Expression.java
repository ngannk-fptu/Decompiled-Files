/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public abstract class Expression
extends AnnotatedNode {
    private ClassNode type = ClassHelper.DYNAMIC_TYPE;

    public abstract Expression transformExpression(ExpressionTransformer var1);

    protected List<Expression> transformExpressions(List<? extends Expression> expressions, ExpressionTransformer transformer) {
        ArrayList<Expression> list = new ArrayList<Expression>(expressions.size());
        for (Expression expression : expressions) {
            list.add(transformer.transform(expression));
        }
        return list;
    }

    protected <T extends Expression> List<T> transformExpressions(List<? extends Expression> expressions, ExpressionTransformer transformer, Class<T> transformedType) {
        ArrayList<T> list = new ArrayList<T>(expressions.size());
        for (Expression expression : expressions) {
            Expression transformed = transformer.transform(expression);
            if (!transformedType.isInstance(transformed)) {
                throw new GroovyBugError(String.format("Transformed expression should have type %s but has type %s", transformedType, transformed.getClass()));
            }
            list.add(transformedType.cast(transformed));
        }
        return list;
    }

    public ClassNode getType() {
        return this.type;
    }

    public void setType(ClassNode t) {
        this.type = t;
    }
}

