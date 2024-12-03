/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.internal.lang.reflect.PerClauseImpl;
import org.aspectj.internal.lang.reflect.PointcutExpressionImpl;
import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.PointcutBasedPerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutBasedPerClauseImpl
extends PerClauseImpl
implements PointcutBasedPerClause {
    private final PointcutExpression pointcutExpression;

    public PointcutBasedPerClauseImpl(PerClauseKind kind, String pointcutExpression) {
        super(kind);
        this.pointcutExpression = new PointcutExpressionImpl(pointcutExpression);
    }

    @Override
    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        switch (this.getKind()) {
            case PERCFLOW: {
                sb.append("percflow(");
                break;
            }
            case PERCFLOWBELOW: {
                sb.append("percflowbelow(");
                break;
            }
            case PERTARGET: {
                sb.append("pertarget(");
                break;
            }
            case PERTHIS: {
                sb.append("perthis(");
            }
        }
        sb.append(this.pointcutExpression.asString());
        sb.append(")");
        return sb.toString();
    }
}

