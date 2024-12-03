/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.internal.lang.reflect.PointcutExpressionImpl;
import org.aspectj.lang.annotation.AdviceName;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PointcutExpression;

public class AdviceImpl
implements Advice {
    private static final String AJC_INTERNAL = "org.aspectj.runtime.internal";
    private final AdviceKind kind;
    private final Method adviceMethod;
    private PointcutExpression pointcutExpression;
    private boolean hasExtraParam = false;
    private Type[] genericParameterTypes;
    private AjType[] parameterTypes;
    private AjType[] exceptionTypes;

    protected AdviceImpl(Method method, String pointcut, AdviceKind type) {
        this.kind = type;
        this.adviceMethod = method;
        this.pointcutExpression = new PointcutExpressionImpl(pointcut);
    }

    protected AdviceImpl(Method method, String pointcut, AdviceKind type, String extraParamName) {
        this(method, pointcut, type);
        this.hasExtraParam = true;
    }

    @Override
    public AjType getDeclaringType() {
        return AjTypeSystem.getAjType(this.adviceMethod.getDeclaringClass());
    }

    @Override
    public Type[] getGenericParameterTypes() {
        if (this.genericParameterTypes == null) {
            Type[] genTypes = this.adviceMethod.getGenericParameterTypes();
            int syntheticCount = 0;
            for (Type t : genTypes) {
                if (!(t instanceof Class) || !((Class)t).getPackage().getName().equals(AJC_INTERNAL)) continue;
                ++syntheticCount;
            }
            this.genericParameterTypes = new Type[genTypes.length - syntheticCount];
            for (int i = 0; i < this.genericParameterTypes.length; ++i) {
                this.genericParameterTypes[i] = genTypes[i] instanceof Class ? AjTypeSystem.getAjType((Class)genTypes[i]) : genTypes[i];
            }
        }
        return this.genericParameterTypes;
    }

    @Override
    public AjType<?>[] getParameterTypes() {
        if (this.parameterTypes == null) {
            Class<?>[] ptypes = this.adviceMethod.getParameterTypes();
            int syntheticCount = 0;
            for (Class<?> c : ptypes) {
                if (!c.getPackage().getName().equals(AJC_INTERNAL)) continue;
                ++syntheticCount;
            }
            this.parameterTypes = new AjType[ptypes.length - syntheticCount];
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                this.parameterTypes[i] = AjTypeSystem.getAjType(ptypes[i]);
            }
        }
        return this.parameterTypes;
    }

    @Override
    public AjType<?>[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            Class<?>[] exTypes = this.adviceMethod.getExceptionTypes();
            this.exceptionTypes = new AjType[exTypes.length];
            for (int i = 0; i < exTypes.length; ++i) {
                this.exceptionTypes[i] = AjTypeSystem.getAjType(exTypes[i]);
            }
        }
        return this.exceptionTypes;
    }

    @Override
    public AdviceKind getKind() {
        return this.kind;
    }

    @Override
    public String getName() {
        String adviceName = this.adviceMethod.getName();
        if (adviceName.startsWith("ajc$")) {
            adviceName = "";
            AdviceName name = this.adviceMethod.getAnnotation(AdviceName.class);
            if (name != null) {
                adviceName = name.value();
            }
        }
        return adviceName;
    }

    @Override
    public PointcutExpression getPointcutExpression() {
        return this.pointcutExpression;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.getName().length() > 0) {
            sb.append("@AdviceName(\"");
            sb.append(this.getName());
            sb.append("\") ");
        }
        if (this.getKind() == AdviceKind.AROUND) {
            sb.append(this.adviceMethod.getGenericReturnType().toString());
            sb.append(" ");
        }
        switch (this.getKind()) {
            case AFTER: {
                sb.append("after(");
                break;
            }
            case AFTER_RETURNING: {
                sb.append("after(");
                break;
            }
            case AFTER_THROWING: {
                sb.append("after(");
                break;
            }
            case AROUND: {
                sb.append("around(");
                break;
            }
            case BEFORE: {
                sb.append("before(");
            }
        }
        AjType<?>[] ptypes = this.getParameterTypes();
        int len = ptypes.length;
        if (this.hasExtraParam) {
            --len;
        }
        for (int i = 0; i < len; ++i) {
            sb.append(ptypes[i].getName());
            if (i + 1 >= len) continue;
            sb.append(",");
        }
        sb.append(") ");
        switch (this.getKind()) {
            case AFTER_RETURNING: {
                sb.append("returning");
                if (this.hasExtraParam) {
                    sb.append("(");
                    sb.append(ptypes[len - 1].getName());
                    sb.append(") ");
                }
            }
            case AFTER_THROWING: {
                sb.append("throwing");
                if (!this.hasExtraParam) break;
                sb.append("(");
                sb.append(ptypes[len - 1].getName());
                sb.append(") ");
            }
        }
        AjType<?>[] exTypes = this.getExceptionTypes();
        if (exTypes.length > 0) {
            sb.append("throws ");
            for (int i = 0; i < exTypes.length; ++i) {
                sb.append(exTypes[i].getName());
                if (i + 1 >= exTypes.length) continue;
                sb.append(",");
            }
            sb.append(" ");
        }
        sb.append(": ");
        sb.append(this.getPointcutExpression().asString());
        return sb.toString();
    }
}

