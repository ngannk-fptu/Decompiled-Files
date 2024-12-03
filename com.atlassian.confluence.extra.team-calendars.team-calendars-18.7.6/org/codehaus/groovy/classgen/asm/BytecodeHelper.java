/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import groovyjarjarasm.asm.Opcodes;
import groovyjarjarasm.asm.Type;
import java.lang.reflect.Modifier;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class BytecodeHelper
implements Opcodes {
    private static String DTT_CLASSNAME = BytecodeHelper.getClassInternalName(DefaultTypeTransformation.class.getName());

    public static String getClassInternalName(ClassNode t) {
        if (t.isPrimaryClassNode()) {
            if (t.isArray()) {
                return "[L" + BytecodeHelper.getClassInternalName(t.getComponentType()) + ";";
            }
            return BytecodeHelper.getClassInternalName(t.getName());
        }
        return BytecodeHelper.getClassInternalName(t.getTypeClass());
    }

    public static String getClassInternalName(Class t) {
        return Type.getInternalName(t);
    }

    public static String getClassInternalName(String name) {
        return name.replace('.', '/');
    }

    public static String getMethodDescriptor(ClassNode returnType, Parameter[] parameters) {
        StringBuilder buffer = new StringBuilder("(");
        for (int i = 0; i < parameters.length; ++i) {
            buffer.append(BytecodeHelper.getTypeDescription(parameters[i].getType()));
        }
        buffer.append(")");
        buffer.append(BytecodeHelper.getTypeDescription(returnType));
        return buffer.toString();
    }

    public static String getMethodDescriptor(MethodNode methodNode) {
        return BytecodeHelper.getMethodDescriptor(methodNode.getReturnType(), methodNode.getParameters());
    }

    public static String getMethodDescriptor(Class returnType, Class[] paramTypes) {
        StringBuilder buffer = new StringBuilder("(");
        for (int i = 0; i < paramTypes.length; ++i) {
            buffer.append(BytecodeHelper.getTypeDescription(paramTypes[i]));
        }
        buffer.append(")");
        buffer.append(BytecodeHelper.getTypeDescription(returnType));
        return buffer.toString();
    }

    public static String getTypeDescription(Class c) {
        return Type.getDescriptor(c);
    }

    public static String getClassLoadingTypeDescription(ClassNode c) {
        StringBuilder buf = new StringBuilder();
        boolean array = false;
        while (c.isArray()) {
            buf.append('[');
            c = c.getComponentType();
            array = true;
        }
        if (ClassHelper.isPrimitiveType(c)) {
            buf.append(BytecodeHelper.getTypeDescription(c));
        } else {
            if (array) {
                buf.append('L');
            }
            buf.append(c.getName());
            if (array) {
                buf.append(';');
            }
        }
        return buf.toString();
    }

    public static String getTypeDescription(ClassNode c) {
        return BytecodeHelper.getTypeDescription(c, true);
    }

    private static String getTypeDescription(ClassNode c, boolean end) {
        StringBuilder buf = new StringBuilder();
        ClassNode d = c;
        while (true) {
            if (ClassHelper.isPrimitiveType(d.redirect())) {
                int car = (d = d.redirect()) == ClassHelper.int_TYPE ? 73 : (d == ClassHelper.VOID_TYPE ? 86 : (d == ClassHelper.boolean_TYPE ? 90 : (d == ClassHelper.byte_TYPE ? 66 : (d == ClassHelper.char_TYPE ? 67 : (d == ClassHelper.short_TYPE ? 83 : (d == ClassHelper.double_TYPE ? 68 : (d == ClassHelper.float_TYPE ? 70 : 74)))))));
                buf.append((char)car);
                return buf.toString();
            }
            if (!d.isArray()) break;
            buf.append('[');
            d = d.getComponentType();
        }
        buf.append('L');
        String name = d.getName();
        int len = name.length();
        for (int i = 0; i < len; ++i) {
            char car = name.charAt(i);
            buf.append(car == '.' ? (char)'/' : (char)car);
        }
        if (end) {
            buf.append(';');
        }
        return buf.toString();
    }

    public static String[] getClassInternalNames(ClassNode[] names) {
        int size = names.length;
        String[] answer = new String[size];
        for (int i = 0; i < size; ++i) {
            answer[i] = BytecodeHelper.getClassInternalName(names[i]);
        }
        return answer;
    }

    public static void pushConstant(MethodVisitor mv, int value) {
        switch (value) {
            case 0: {
                mv.visitInsn(3);
                break;
            }
            case 1: {
                mv.visitInsn(4);
                break;
            }
            case 2: {
                mv.visitInsn(5);
                break;
            }
            case 3: {
                mv.visitInsn(6);
                break;
            }
            case 4: {
                mv.visitInsn(7);
                break;
            }
            case 5: {
                mv.visitInsn(8);
                break;
            }
            default: {
                if (value >= -128 && value <= 127) {
                    mv.visitIntInsn(16, value);
                    break;
                }
                if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                    mv.visitIntInsn(17, value);
                    break;
                }
                mv.visitLdcInsn(value);
            }
        }
    }

    public static void negateBoolean(MethodVisitor mv) {
        Label endLabel = new Label();
        Label falseLabel = new Label();
        mv.visitJumpInsn(154, falseLabel);
        mv.visitInsn(4);
        mv.visitJumpInsn(167, endLabel);
        mv.visitLabel(falseLabel);
        mv.visitInsn(3);
        mv.visitLabel(endLabel);
    }

    public static String formatNameForClassLoading(String name) {
        if (name == null) {
            return "java.lang.Object;";
        }
        if (name.equals("int") || name.equals("long") || name.equals("short") || name.equals("float") || name.equals("double") || name.equals("byte") || name.equals("char") || name.equals("boolean") || name.equals("void")) {
            return name;
        }
        if (name.startsWith("[")) {
            return name.replace('/', '.');
        }
        if (name.startsWith("L")) {
            if ((name = name.substring(1)).endsWith(";")) {
                name = name.substring(0, name.length() - 1);
            }
            return name.replace('/', '.');
        }
        String prefix = "";
        if (name.endsWith("[]")) {
            prefix = "[";
            if ((name = name.substring(0, name.length() - 2)).equals("int")) {
                return prefix + "I";
            }
            if (name.equals("long")) {
                return prefix + "J";
            }
            if (name.equals("short")) {
                return prefix + "S";
            }
            if (name.equals("float")) {
                return prefix + "F";
            }
            if (name.equals("double")) {
                return prefix + "D";
            }
            if (name.equals("byte")) {
                return prefix + "B";
            }
            if (name.equals("char")) {
                return prefix + "C";
            }
            if (name.equals("boolean")) {
                return prefix + "Z";
            }
            return prefix + "L" + name.replace('/', '.') + ";";
        }
        return name.replace('/', '.');
    }

    public static void doReturn(MethodVisitor mv, ClassNode returnType) {
        if (returnType == ClassHelper.double_TYPE) {
            mv.visitInsn(175);
        } else if (returnType == ClassHelper.float_TYPE) {
            mv.visitInsn(174);
        } else if (returnType == ClassHelper.long_TYPE) {
            mv.visitInsn(173);
        } else if (returnType == ClassHelper.boolean_TYPE || returnType == ClassHelper.char_TYPE || returnType == ClassHelper.byte_TYPE || returnType == ClassHelper.int_TYPE || returnType == ClassHelper.short_TYPE) {
            mv.visitInsn(172);
        } else if (returnType == ClassHelper.VOID_TYPE) {
            mv.visitInsn(177);
        } else {
            mv.visitInsn(176);
        }
    }

    private static boolean hasGenerics(Parameter[] param) {
        if (param.length == 0) {
            return false;
        }
        for (int i = 0; i < param.length; ++i) {
            ClassNode type = param[i].getType();
            if (!BytecodeHelper.hasGenerics(type)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasGenerics(ClassNode type) {
        return type.isArray() ? BytecodeHelper.hasGenerics(type.getComponentType()) : type.getGenericsTypes() != null;
    }

    public static String getGenericsMethodSignature(MethodNode node) {
        GenericsType[] generics = node.getGenericsTypes();
        Parameter[] param = node.getParameters();
        ClassNode returnType = node.getReturnType();
        if (generics == null && !BytecodeHelper.hasGenerics(param) && !BytecodeHelper.hasGenerics(returnType)) {
            return null;
        }
        StringBuilder ret = new StringBuilder(100);
        BytecodeHelper.getGenericsTypeSpec(ret, generics);
        GenericsType[] paramTypes = new GenericsType[param.length];
        for (int i = 0; i < param.length; ++i) {
            ClassNode pType = param[i].getType();
            paramTypes[i] = pType.getGenericsTypes() == null || !pType.isGenericsPlaceHolder() ? new GenericsType(pType) : pType.getGenericsTypes()[0];
        }
        BytecodeHelper.addSubTypes(ret, paramTypes, "(", ")");
        BytecodeHelper.addSubTypes(ret, new GenericsType[]{new GenericsType(returnType)}, "", "");
        return ret.toString();
    }

    private static boolean usesGenericsInClassSignature(ClassNode node) {
        if (!node.isUsingGenerics()) {
            return false;
        }
        if (BytecodeHelper.hasGenerics(node)) {
            return true;
        }
        ClassNode sclass = node.getUnresolvedSuperClass(false);
        if (sclass.isUsingGenerics()) {
            return true;
        }
        ClassNode[] interfaces = node.getInterfaces();
        if (interfaces != null) {
            for (int i = 0; i < interfaces.length; ++i) {
                if (!interfaces[i].isUsingGenerics()) continue;
                return true;
            }
        }
        return false;
    }

    public static String getGenericsSignature(ClassNode node) {
        if (!BytecodeHelper.usesGenericsInClassSignature(node)) {
            return null;
        }
        GenericsType[] genericsTypes = node.getGenericsTypes();
        StringBuilder ret = new StringBuilder(100);
        BytecodeHelper.getGenericsTypeSpec(ret, genericsTypes);
        GenericsType extendsPart = new GenericsType(node.getUnresolvedSuperClass(false));
        BytecodeHelper.writeGenericsBounds(ret, extendsPart, true);
        ClassNode[] interfaces = node.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            GenericsType interfacePart = new GenericsType(interfaces[i]);
            BytecodeHelper.writeGenericsBounds(ret, interfacePart, false);
        }
        return ret.toString();
    }

    private static void getGenericsTypeSpec(StringBuilder ret, GenericsType[] genericsTypes) {
        if (genericsTypes == null) {
            return;
        }
        ret.append('<');
        for (int i = 0; i < genericsTypes.length; ++i) {
            String name = genericsTypes[i].getName();
            ret.append(name);
            ret.append(':');
            BytecodeHelper.writeGenericsBounds(ret, genericsTypes[i], true);
        }
        ret.append('>');
    }

    public static String getGenericsBounds(ClassNode type) {
        GenericsType[] genericsTypes = type.getGenericsTypes();
        if (genericsTypes == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(100);
        if (type.isGenericsPlaceHolder()) {
            BytecodeHelper.addSubTypes(ret, type.getGenericsTypes(), "", "");
        } else {
            GenericsType gt = new GenericsType(type);
            BytecodeHelper.writeGenericsBounds(ret, gt, false);
        }
        return ret.toString();
    }

    private static void writeGenericsBoundType(StringBuilder ret, ClassNode printType, boolean writeInterfaceMarker) {
        if (writeInterfaceMarker && printType.isInterface()) {
            ret.append(":");
        }
        if (printType.isGenericsPlaceHolder() && printType.getGenericsTypes() != null) {
            ret.append("T");
            ret.append(printType.getGenericsTypes()[0].getName());
            ret.append(";");
        } else {
            ret.append(BytecodeHelper.getTypeDescription(printType, false));
            BytecodeHelper.addSubTypes(ret, printType.getGenericsTypes(), "<", ">");
            if (!ClassHelper.isPrimitiveType(printType)) {
                ret.append(";");
            }
        }
    }

    private static void writeGenericsBounds(StringBuilder ret, GenericsType type, boolean writeInterfaceMarker) {
        if (type.getUpperBounds() != null) {
            ClassNode[] bounds = type.getUpperBounds();
            for (int i = 0; i < bounds.length; ++i) {
                BytecodeHelper.writeGenericsBoundType(ret, bounds[i], writeInterfaceMarker);
            }
        } else if (type.getLowerBound() != null) {
            BytecodeHelper.writeGenericsBoundType(ret, type.getLowerBound(), writeInterfaceMarker);
        } else {
            BytecodeHelper.writeGenericsBoundType(ret, type.getType(), writeInterfaceMarker);
        }
    }

    private static void addSubTypes(StringBuilder ret, GenericsType[] types, String start, String end) {
        if (types == null) {
            return;
        }
        ret.append(start);
        for (int i = 0; i < types.length; ++i) {
            if (types[i].getType().isArray()) {
                ret.append("[");
                BytecodeHelper.addSubTypes(ret, new GenericsType[]{new GenericsType(types[i].getType().getComponentType())}, "", "");
                continue;
            }
            if (types[i].isPlaceholder()) {
                ret.append('T');
                String name = types[i].getName();
                ret.append(name);
                ret.append(';');
                continue;
            }
            if (types[i].isWildcard()) {
                if (types[i].getUpperBounds() != null) {
                    ret.append('+');
                    BytecodeHelper.writeGenericsBounds(ret, types[i], false);
                    continue;
                }
                if (types[i].getLowerBound() != null) {
                    ret.append('-');
                    BytecodeHelper.writeGenericsBounds(ret, types[i], false);
                    continue;
                }
                ret.append('*');
                continue;
            }
            BytecodeHelper.writeGenericsBounds(ret, types[i], false);
        }
        ret.append(end);
    }

    public static void load(MethodVisitor mv, ClassNode type, int idx) {
        if (type == ClassHelper.double_TYPE) {
            mv.visitVarInsn(24, idx);
        } else if (type == ClassHelper.float_TYPE) {
            mv.visitVarInsn(23, idx);
        } else if (type == ClassHelper.long_TYPE) {
            mv.visitVarInsn(22, idx);
        } else if (type == ClassHelper.boolean_TYPE || type == ClassHelper.char_TYPE || type == ClassHelper.byte_TYPE || type == ClassHelper.int_TYPE || type == ClassHelper.short_TYPE) {
            mv.visitVarInsn(21, idx);
        } else {
            mv.visitVarInsn(25, idx);
        }
    }

    public static void doCast(MethodVisitor mv, ClassNode type) {
        if (type == ClassHelper.OBJECT_TYPE) {
            return;
        }
        if (ClassHelper.isPrimitiveType(type) && type != ClassHelper.VOID_TYPE) {
            BytecodeHelper.unbox(mv, type);
        } else {
            mv.visitTypeInsn(192, type.isArray() ? BytecodeHelper.getTypeDescription(type) : BytecodeHelper.getClassInternalName(type.getName()));
        }
    }

    public static void doCastToPrimitive(MethodVisitor mv, ClassNode sourceType, ClassNode targetType) {
        mv.visitMethodInsn(182, BytecodeHelper.getClassInternalName(sourceType), targetType.getName() + "Value", "()" + BytecodeHelper.getTypeDescription(targetType), false);
    }

    public static void doCastToWrappedType(MethodVisitor mv, ClassNode sourceType, ClassNode targetType) {
        mv.visitMethodInsn(184, BytecodeHelper.getClassInternalName(targetType), "valueOf", "(" + BytecodeHelper.getTypeDescription(sourceType) + ")" + BytecodeHelper.getTypeDescription(targetType), false);
    }

    public static void doCast(MethodVisitor mv, Class type) {
        if (type == Object.class) {
            return;
        }
        if (type.isPrimitive() && type != Void.TYPE) {
            BytecodeHelper.unbox(mv, type);
        } else {
            mv.visitTypeInsn(192, type.isArray() ? BytecodeHelper.getTypeDescription(type) : BytecodeHelper.getClassInternalName(type.getName()));
        }
    }

    public static void unbox(MethodVisitor mv, Class type) {
        if (type.isPrimitive() && type != Void.TYPE) {
            String returnString = "(Ljava/lang/Object;)" + BytecodeHelper.getTypeDescription(type);
            mv.visitMethodInsn(184, DTT_CLASSNAME, type.getName() + "Unbox", returnString, false);
        }
    }

    public static void unbox(MethodVisitor mv, ClassNode type) {
        if (type.isPrimaryClassNode()) {
            return;
        }
        BytecodeHelper.unbox(mv, type.getTypeClass());
    }

    @Deprecated
    public static boolean box(MethodVisitor mv, ClassNode type) {
        if (type.isPrimaryClassNode()) {
            return false;
        }
        return BytecodeHelper.box(mv, type.getTypeClass());
    }

    @Deprecated
    public static boolean box(MethodVisitor mv, Class type) {
        if (ReflectionCache.getCachedClass((Class)type).isPrimitive && type != Void.TYPE) {
            String returnString = "(" + BytecodeHelper.getTypeDescription(type) + ")Ljava/lang/Object;";
            mv.visitMethodInsn(184, DTT_CLASSNAME, "box", returnString, false);
            return true;
        }
        return false;
    }

    public static void visitClassLiteral(MethodVisitor mv, ClassNode classNode) {
        if (ClassHelper.isPrimitiveType(classNode)) {
            mv.visitFieldInsn(178, BytecodeHelper.getClassInternalName(ClassHelper.getWrapper(classNode)), "TYPE", "Ljava/lang/Class;");
        } else {
            mv.visitLdcInsn(Type.getType(BytecodeHelper.getTypeDescription(classNode)));
        }
    }

    public static boolean isClassLiteralPossible(ClassNode classNode) {
        return Modifier.isPublic(classNode.getModifiers());
    }

    public static boolean isSameCompilationUnit(ClassNode a, ClassNode b) {
        CompileUnit cu1 = a.getCompileUnit();
        CompileUnit cu2 = b.getCompileUnit();
        return cu1 != null && cu2 != null && cu1 == cu2;
    }

    public static int hashCode(String str) {
        char[] chars = str.toCharArray();
        int h = 0;
        for (int i = 0; i < chars.length; ++i) {
            h = 31 * h + chars[i];
        }
        return h;
    }
}

