/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.asm.MethodVisitor
 *  org.springframework.asm.Type
 */
package org.springframework.data.mapping.model;

import java.lang.reflect.Modifier;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;

abstract class BytecodeUtil {
    private BytecodeUtil() {
    }

    static Class<?> autoboxType(Class<?> unboxed) {
        if (unboxed.equals(Boolean.TYPE)) {
            return Boolean.class;
        }
        if (unboxed.equals(Byte.TYPE)) {
            return Byte.class;
        }
        if (unboxed.equals(Character.TYPE)) {
            return Character.class;
        }
        if (unboxed.equals(Double.TYPE)) {
            return Double.class;
        }
        if (unboxed.equals(Float.TYPE)) {
            return Float.class;
        }
        if (unboxed.equals(Integer.TYPE)) {
            return Integer.class;
        }
        if (unboxed.equals(Long.TYPE)) {
            return Long.class;
        }
        if (unboxed.equals(Short.TYPE)) {
            return Short.class;
        }
        if (unboxed.equals(Void.TYPE)) {
            return Void.class;
        }
        return unboxed;
    }

    static void autoboxIfNeeded(Class<?> in, Class<?> out, MethodVisitor visitor) {
        if (in.equals(Boolean.class) && out.equals(Boolean.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Boolean", "booleanValue", "()Z", false);
        }
        if (in.equals(Boolean.TYPE) && out.equals(Boolean.class)) {
            visitor.visitMethodInsn(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
        if (in.equals(Byte.class) && out.equals(Byte.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Byte", "byteValue", "()B", false);
        }
        if (in.equals(Byte.TYPE) && out.equals(Byte.class)) {
            visitor.visitMethodInsn(184, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        }
        if (in.equals(Character.class) && out.equals(Character.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Character", "charValue", "()C", false);
        }
        if (in.equals(Character.TYPE) && out.equals(Character.class)) {
            visitor.visitMethodInsn(184, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        }
        if (in.equals(Double.class) && out.equals(Double.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Double", "doubleValue", "()D", false);
        }
        if (in.equals(Double.TYPE) && out.equals(Double.class)) {
            visitor.visitMethodInsn(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        }
        if (in.equals(Float.class) && out.equals(Float.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Float", "floatValue", "()F", false);
        }
        if (in.equals(Float.TYPE) && out.equals(Float.class)) {
            visitor.visitMethodInsn(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        }
        if (in.equals(Integer.class) && out.equals(Integer.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Integer", "intValue", "()I", false);
        }
        if (in.equals(Integer.TYPE) && out.equals(Integer.class)) {
            visitor.visitMethodInsn(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        }
        if (in.equals(Long.class) && out.equals(Long.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Long", "longValue", "()J", false);
        }
        if (in.equals(Long.TYPE) && out.equals(Long.class)) {
            visitor.visitMethodInsn(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        }
        if (in.equals(Short.class) && out.equals(Short.TYPE)) {
            visitor.visitMethodInsn(182, "java/lang/Short", "shortValue", "()S", false);
        }
        if (in.equals(Short.TYPE) && out.equals(Short.class)) {
            visitor.visitMethodInsn(184, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        }
    }

    static boolean isAccessible(Class<?> type) {
        return BytecodeUtil.isAccessible(type.getModifiers());
    }

    static boolean isAccessible(int modifiers) {
        return !Modifier.isPrivate(modifiers);
    }

    static boolean isDefault(int modifiers) {
        return !Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers);
    }

    static String referenceName(Class<?> type) {
        return type.isArray() ? Type.getInternalName(type) : BytecodeUtil.referenceName(Type.getInternalName(type));
    }

    static String referenceName(String internalTypeName) {
        return String.format("L%s;", internalTypeName);
    }

    static String signatureTypeName(Class<?> type) {
        if (type.equals(Boolean.TYPE)) {
            return "Z";
        }
        if (type.equals(Byte.TYPE)) {
            return "B";
        }
        if (type.equals(Character.TYPE)) {
            return "C";
        }
        if (type.equals(Double.TYPE)) {
            return "D";
        }
        if (type.equals(Float.TYPE)) {
            return "F";
        }
        if (type.equals(Integer.TYPE)) {
            return "I";
        }
        if (type.equals(Long.TYPE)) {
            return "J";
        }
        if (type.equals(Short.TYPE)) {
            return "S";
        }
        if (type.equals(Void.TYPE)) {
            return "V";
        }
        return BytecodeUtil.referenceName(type);
    }

    static void visitDefaultValue(Class<?> parameterType, MethodVisitor mv) {
        if (parameterType.isPrimitive()) {
            if (parameterType == Integer.TYPE || parameterType == Short.TYPE || parameterType == Boolean.TYPE) {
                mv.visitInsn(3);
            }
            if (parameterType == Long.TYPE) {
                mv.visitInsn(9);
            }
            if (parameterType == Double.TYPE) {
                mv.visitInsn(14);
            }
            if (parameterType == Float.TYPE) {
                mv.visitInsn(11);
            }
            if (parameterType == Character.TYPE || parameterType == Byte.TYPE) {
                mv.visitIntInsn(16, 0);
            }
        } else {
            mv.visitInsn(1);
        }
    }
}

