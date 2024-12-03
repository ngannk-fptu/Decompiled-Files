/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMember;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXAnnotatedElement;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

public abstract class JavaXMember
extends JavaXAnnotatedElement
implements XMember {
    private final Type type;
    private final TypeEnvironment env;
    private final JavaXType xType;

    protected static Type typeOf(Member member, TypeEnvironment env) {
        if (member instanceof Field) {
            return env.bind(((Field)member).getGenericType());
        }
        if (member instanceof Method) {
            return env.bind(((Method)member).getGenericReturnType());
        }
        throw new IllegalArgumentException("Member " + member + " is neither a field nor a method");
    }

    protected JavaXMember(Member member, Type type, TypeEnvironment env, JavaReflectionManager factory, JavaXType xType) {
        super((AnnotatedElement)((Object)member), factory);
        this.type = type;
        this.env = env;
        this.xType = xType;
    }

    @Override
    public XClass getType() {
        return this.xType.getType();
    }

    @Override
    public abstract String getName();

    public Type getJavaType() {
        return this.env.bind(this.type);
    }

    protected TypeEnvironment getTypeEnvironment() {
        return this.env;
    }

    public Member getMember() {
        return (Member)((Object)this.toAnnotatedElement());
    }

    @Override
    public XClass getDeclaringClass() {
        return this.getFactory().toXClass((Class)this.getMember().getDeclaringClass());
    }

    @Override
    public Class<? extends Collection> getCollectionClass() {
        return this.xType.getCollectionClass();
    }

    @Override
    public XClass getClassOrElementClass() {
        return this.xType.getClassOrElementClass();
    }

    @Override
    public XClass getElementClass() {
        return this.xType.getElementClass();
    }

    @Override
    public XClass getMapKey() {
        return this.xType.getMapKey();
    }

    @Override
    public boolean isArray() {
        return this.xType.isArray();
    }

    @Override
    public boolean isCollection() {
        return this.xType.isCollection();
    }

    @Override
    public int getModifiers() {
        return this.getMember().getModifiers();
    }

    @Override
    public final boolean isTypeResolved() {
        return this.xType.isResolved();
    }

    @Override
    public void setAccessible(boolean accessible) {
        ((AccessibleObject)((Object)this.getMember())).setAccessible(accessible);
    }
}

