/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.internal.lang.reflect.InterTypeDeclarationImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;

public class InterTypeMethodDeclarationImpl
extends InterTypeDeclarationImpl
implements InterTypeMethodDeclaration {
    private String name;
    private Method baseMethod;
    private int parameterAdjustmentFactor = 1;
    private AjType<?>[] parameterTypes;
    private Type[] genericParameterTypes;
    private AjType<?> returnType;
    private Type genericReturnType;
    private AjType<?>[] exceptionTypes;

    public InterTypeMethodDeclarationImpl(AjType<?> decType, String target, int mods, String name, Method itdInterMethod) {
        super(decType, target, mods);
        this.name = name;
        this.baseMethod = itdInterMethod;
    }

    public InterTypeMethodDeclarationImpl(AjType<?> decType, AjType<?> targetType, Method base, int modifiers) {
        super(decType, targetType, modifiers);
        this.parameterAdjustmentFactor = 0;
        this.name = base.getName();
        this.baseMethod = base;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AjType<?> getReturnType() {
        return AjTypeSystem.getAjType(this.baseMethod.getReturnType());
    }

    @Override
    public Type getGenericReturnType() {
        Type gRet = this.baseMethod.getGenericReturnType();
        if (gRet instanceof Class) {
            return AjTypeSystem.getAjType((Class)gRet);
        }
        return gRet;
    }

    @Override
    public AjType<?>[] getParameterTypes() {
        Class<?>[] baseTypes = this.baseMethod.getParameterTypes();
        AjType[] ret = new AjType[baseTypes.length - this.parameterAdjustmentFactor];
        for (int i = this.parameterAdjustmentFactor; i < baseTypes.length; ++i) {
            ret[i - this.parameterAdjustmentFactor] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    @Override
    public Type[] getGenericParameterTypes() {
        Type[] baseTypes = this.baseMethod.getGenericParameterTypes();
        Type[] ret = new AjType[baseTypes.length - this.parameterAdjustmentFactor];
        for (int i = this.parameterAdjustmentFactor; i < baseTypes.length; ++i) {
            ret[i - this.parameterAdjustmentFactor] = baseTypes[i] instanceof Class ? AjTypeSystem.getAjType((Class)baseTypes[i]) : baseTypes[i];
        }
        return ret;
    }

    @Override
    public TypeVariable<Method>[] getTypeParameters() {
        return this.baseMethod.getTypeParameters();
    }

    @Override
    public AjType<?>[] getExceptionTypes() {
        Class<?>[] baseTypes = this.baseMethod.getExceptionTypes();
        AjType[] ret = new AjType[baseTypes.length];
        for (int i = 0; i < baseTypes.length; ++i) {
            ret[i] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Modifier.toString(this.getModifiers()));
        sb.append(" ");
        sb.append(this.getReturnType().toString());
        sb.append(" ");
        sb.append(this.targetTypeName);
        sb.append(".");
        sb.append(this.getName());
        sb.append("(");
        AjType<?>[] pTypes = this.getParameterTypes();
        for (int i = 0; i < pTypes.length - 1; ++i) {
            sb.append(pTypes[i].toString());
            sb.append(", ");
        }
        if (pTypes.length > 0) {
            sb.append(pTypes[pTypes.length - 1].toString());
        }
        sb.append(")");
        return sb.toString();
    }
}

