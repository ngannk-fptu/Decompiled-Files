/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Classes {
    public static boolean isInnerClass(Class<?> clazz) {
        return !Modifier.isStatic(clazz.getModifiers()) && clazz.getEnclosingClass() != null;
    }

    public static boolean isConcrete(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        return !clazz.isInterface() && !Modifier.isAbstract(modifiers);
    }

    public static String toString(Member member) {
        Class<? extends Member> memberType = $Classes.memberType(member);
        if (memberType == Method.class) {
            return member.getDeclaringClass().getName() + "." + member.getName() + "()";
        }
        if (memberType == Field.class) {
            return member.getDeclaringClass().getName() + "." + member.getName();
        }
        if (memberType == Constructor.class) {
            return member.getDeclaringClass().getName() + ".<init>()";
        }
        throw new AssertionError();
    }

    public static Class<? extends Member> memberType(Member member) {
        $Preconditions.checkNotNull(member, "member");
        if (member instanceof Field) {
            return Field.class;
        }
        if (member instanceof Method) {
            return Method.class;
        }
        if (member instanceof Constructor) {
            return Constructor.class;
        }
        throw new IllegalArgumentException("Unsupported implementation class for Member, " + member.getClass());
    }
}

