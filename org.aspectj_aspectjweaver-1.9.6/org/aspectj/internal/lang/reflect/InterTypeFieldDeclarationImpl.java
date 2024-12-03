/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.internal.lang.reflect.InterTypeDeclarationImpl;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;

public class InterTypeFieldDeclarationImpl
extends InterTypeDeclarationImpl
implements InterTypeFieldDeclaration {
    private String name;
    private AjType<?> type;
    private Type genericType;

    public InterTypeFieldDeclarationImpl(AjType<?> decType, String target, int mods, String name, AjType<?> type, Type genericType) {
        super(decType, target, mods);
        this.name = name;
        this.type = type;
        this.genericType = genericType;
    }

    public InterTypeFieldDeclarationImpl(AjType<?> decType, AjType<?> targetType, Field base) {
        super(decType, targetType, base.getModifiers());
        this.name = base.getName();
        this.type = AjTypeSystem.getAjType(base.getType());
        Type gt = base.getGenericType();
        this.genericType = gt instanceof Class ? AjTypeSystem.getAjType((Class)gt) : gt;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AjType<?> getType() {
        return this.type;
    }

    @Override
    public Type getGenericType() {
        return this.genericType;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Modifier.toString(this.getModifiers()));
        sb.append(" ");
        sb.append(this.getType().toString());
        sb.append(" ");
        sb.append(this.targetTypeName);
        sb.append(".");
        sb.append(this.getName());
        return sb.toString();
    }
}

