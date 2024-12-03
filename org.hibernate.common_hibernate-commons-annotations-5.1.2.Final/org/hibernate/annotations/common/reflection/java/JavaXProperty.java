/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.annotations.common.reflection.java.JavaXType;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

final class JavaXProperty
extends JavaXMember
implements XProperty {
    private static final Object[] EMPTY_ARRAY = new Object[0];

    static JavaXProperty create(Member member, TypeEnvironment context, JavaReflectionManager factory) {
        Type propType = JavaXProperty.typeOf(member, context);
        JavaXType xType = factory.toXType(context, propType);
        return new JavaXProperty(member, propType, context, factory, xType);
    }

    private JavaXProperty(Member member, Type type, TypeEnvironment env, JavaReflectionManager factory, JavaXType xType) {
        super(member, type, env, factory, xType);
        assert (member instanceof Field || member instanceof Method);
    }

    @Override
    public String getName() {
        String fullName = this.getMember().getName();
        if (this.getMember() instanceof Method) {
            if (fullName.startsWith("get")) {
                return JavaXProperty.decapitalize(fullName.substring("get".length()));
            }
            if (fullName.startsWith("is")) {
                return JavaXProperty.decapitalize(fullName.substring("is".length()));
            }
            throw new RuntimeException("Method " + fullName + " is not a property getter");
        }
        return fullName;
    }

    private static String decapitalize(String name) {
        if (name != null && name.length() != 0) {
            if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
                return name;
            }
            char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
        return name;
    }

    @Override
    public Object invoke(Object target) {
        try {
            if (this.getMember() instanceof Method) {
                return ((Method)this.getMember()).invoke(target, EMPTY_ARRAY);
            }
            Field field = (Field)this.getMember();
            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type == Boolean.TYPE) {
                    return field.getBoolean(target);
                }
                if (type == Byte.TYPE) {
                    return field.getByte(target);
                }
                if (type == Character.TYPE) {
                    return Character.valueOf(field.getChar(target));
                }
                if (type == Integer.TYPE) {
                    return field.getInt(target);
                }
                if (type == Long.TYPE) {
                    return field.getLong(target);
                }
                if (type == Short.TYPE) {
                    return field.getShort(target);
                }
            }
            return field.get(target);
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("Invoking " + this.getName() + " on a  null object", e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invoking " + this.getName() + " with wrong parameters", e);
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to invoke " + this.getName(), e);
        }
    }

    @Override
    public Object invoke(Object target, Object ... parameters) {
        if (parameters.length != 0) {
            throw new IllegalArgumentException("An XProperty cannot have invoke parameters");
        }
        return this.invoke(target);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

