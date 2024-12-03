/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.internal.lang.reflect.PointcutExpressionImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareSoftImpl
implements DeclareSoft {
    private AjType<?> declaringType;
    private PointcutExpression pointcut;
    private AjType<?> exceptionType;
    private String missingTypeName;

    public DeclareSoftImpl(AjType<?> declaringType, String pcut, String exceptionTypeName) {
        this.declaringType = declaringType;
        this.pointcut = new PointcutExpressionImpl(pcut);
        try {
            ClassLoader cl = declaringType.getJavaClass().getClassLoader();
            this.exceptionType = AjTypeSystem.getAjType(Class.forName(exceptionTypeName, false, cl));
        }
        catch (ClassNotFoundException ex) {
            this.missingTypeName = exceptionTypeName;
        }
    }

    @Override
    public AjType getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public AjType getSoftenedExceptionType() throws ClassNotFoundException {
        if (this.missingTypeName != null) {
            throw new ClassNotFoundException(this.missingTypeName);
        }
        return this.exceptionType;
    }

    @Override
    public PointcutExpression getPointcutExpression() {
        return this.pointcut;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("declare soft : ");
        if (this.missingTypeName != null) {
            sb.append(this.exceptionType.getName());
        } else {
            sb.append(this.missingTypeName);
        }
        sb.append(" : ");
        sb.append(this.getPointcutExpression().asString());
        return sb.toString();
    }
}

