/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.List;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;

public class NamedArgumentListExpression
extends MapExpression {
    public NamedArgumentListExpression() {
    }

    public NamedArgumentListExpression(List<MapEntryExpression> mapEntryExpressions) {
        super(mapEntryExpressions);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        NamedArgumentListExpression ret = new NamedArgumentListExpression(this.transformExpressions(this.getMapEntryExpressions(), transformer, MapEntryExpression.class));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }
}

