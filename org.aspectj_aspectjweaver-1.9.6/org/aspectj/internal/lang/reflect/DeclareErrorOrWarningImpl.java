/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.internal.lang.reflect.PointcutExpressionImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareErrorOrWarningImpl
implements DeclareErrorOrWarning {
    private PointcutExpression pc;
    private String msg;
    private boolean isError;
    private AjType declaringType;

    public DeclareErrorOrWarningImpl(String pointcut, String message, boolean isError, AjType decType) {
        this.pc = new PointcutExpressionImpl(pointcut);
        this.msg = message;
        this.isError = isError;
        this.declaringType = decType;
    }

    @Override
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public PointcutExpression getPointcutExpression() {
        return this.pc;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

    @Override
    public boolean isError() {
        return this.isError;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare ");
        sb.append(this.isError() ? "error : " : "warning : ");
        sb.append(this.getPointcutExpression().asString());
        sb.append(" : ");
        sb.append("\"");
        sb.append(this.getMessage());
        sb.append("\"");
        return sb.toString();
    }
}

