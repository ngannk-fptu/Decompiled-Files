/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.internal.lang.reflect.StringToType;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public class InterTypeDeclarationImpl
implements InterTypeDeclaration {
    private AjType<?> declaringType;
    protected String targetTypeName;
    private AjType<?> targetType;
    private int modifiers;

    public InterTypeDeclarationImpl(AjType<?> decType, String target, int mods) {
        this.declaringType = decType;
        this.targetTypeName = target;
        this.modifiers = mods;
        try {
            this.targetType = (AjType)StringToType.stringToType(target, decType.getJavaClass());
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public InterTypeDeclarationImpl(AjType<?> decType, AjType<?> targetType, int mods) {
        this.declaringType = decType;
        this.targetType = targetType;
        this.targetTypeName = targetType.getName();
        this.modifiers = mods;
    }

    @Override
    public AjType<?> getDeclaringType() {
        return this.declaringType;
    }

    @Override
    public AjType<?> getTargetType() throws ClassNotFoundException {
        if (this.targetType == null) {
            throw new ClassNotFoundException(this.targetTypeName);
        }
        return this.targetType;
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }
}

