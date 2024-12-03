/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.internal.lang.reflect.InterTypeDeclarationImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;

public class InterTypeConstructorDeclarationImpl
extends InterTypeDeclarationImpl
implements InterTypeConstructorDeclaration {
    private Method baseMethod;

    public InterTypeConstructorDeclarationImpl(AjType<?> decType, String target, int mods, Method baseMethod) {
        super(decType, target, mods);
        this.baseMethod = baseMethod;
    }

    @Override
    public AjType<?>[] getParameterTypes() {
        Class<?>[] baseTypes = this.baseMethod.getParameterTypes();
        AjType[] ret = new AjType[baseTypes.length - 1];
        for (int i = 1; i < baseTypes.length; ++i) {
            ret[i - 1] = AjTypeSystem.getAjType(baseTypes[i]);
        }
        return ret;
    }

    @Override
    public Type[] getGenericParameterTypes() {
        Type[] baseTypes = this.baseMethod.getGenericParameterTypes();
        Type[] ret = new AjType[baseTypes.length - 1];
        for (int i = 1; i < baseTypes.length; ++i) {
            ret[i - 1] = baseTypes[i] instanceof Class ? AjTypeSystem.getAjType((Class)baseTypes[i]) : baseTypes[i];
        }
        return ret;
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
        sb.append(this.targetTypeName);
        sb.append(".new");
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

