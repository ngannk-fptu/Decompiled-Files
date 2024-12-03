/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen;

import com.mchange.v1.lang.ClassUtils;
import com.mchange.v2.codegen.IndentedWriter;
import java.io.File;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class CodegenUtils {
    public static String getModifierString(int n) {
        StringBuffer stringBuffer = new StringBuffer(32);
        if (Modifier.isPublic(n)) {
            stringBuffer.append("public ");
        }
        if (Modifier.isProtected(n)) {
            stringBuffer.append("protected ");
        }
        if (Modifier.isPrivate(n)) {
            stringBuffer.append("private ");
        }
        if (Modifier.isAbstract(n)) {
            stringBuffer.append("abstract ");
        }
        if (Modifier.isStatic(n)) {
            stringBuffer.append("static ");
        }
        if (Modifier.isFinal(n)) {
            stringBuffer.append("final ");
        }
        if (Modifier.isSynchronized(n)) {
            stringBuffer.append("synchronized ");
        }
        if (Modifier.isTransient(n)) {
            stringBuffer.append("transient ");
        }
        if (Modifier.isVolatile(n)) {
            stringBuffer.append("volatile ");
        }
        if (Modifier.isStrict(n)) {
            stringBuffer.append("strictfp ");
        }
        if (Modifier.isNative(n)) {
            stringBuffer.append("native ");
        }
        if (Modifier.isInterface(n)) {
            stringBuffer.append("interface ");
        }
        return stringBuffer.toString().trim();
    }

    public static Class unarrayClass(Class clazz) {
        Class<?> clazz2 = clazz;
        while (clazz2.isArray()) {
            clazz2 = clazz2.getComponentType();
        }
        return clazz2;
    }

    public static boolean inSamePackage(String string, String string2) {
        int n = string.lastIndexOf(46);
        int n2 = string2.lastIndexOf(46);
        if (n < 0 || n2 < 0) {
            return true;
        }
        if (string.substring(0, n).equals(string.substring(0, n))) {
            return string2.indexOf(46) < 0;
        }
        return false;
    }

    public static String fqcnLastElement(String string) {
        return ClassUtils.fqcnLastElement(string);
    }

    public static String methodSignature(Method method) {
        return CodegenUtils.methodSignature(method, null);
    }

    public static String methodSignature(Method method, String[] stringArray) {
        return CodegenUtils.methodSignature(1, method, stringArray);
    }

    public static String methodSignature(int n, Method method, String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append(CodegenUtils.getModifierString(n));
        stringBuffer.append(' ');
        stringBuffer.append(ClassUtils.simpleClassName(method.getReturnType()));
        stringBuffer.append(' ');
        stringBuffer.append(method.getName());
        stringBuffer.append('(');
        Class<?>[] classArray = method.getParameterTypes();
        int n2 = classArray.length;
        for (int i = 0; i < n2; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(ClassUtils.simpleClassName(classArray[i]));
            stringBuffer.append(' ');
            stringBuffer.append(stringArray == null ? String.valueOf((char)(97 + i)) : stringArray[i]);
        }
        stringBuffer.append(')');
        Class<?>[] classArray2 = method.getExceptionTypes();
        if (classArray2.length > 0) {
            stringBuffer.append(" throws ");
            int n3 = classArray2.length;
            for (n2 = 0; n2 < n3; ++n2) {
                if (n2 != 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(ClassUtils.simpleClassName(classArray2[n2]));
            }
        }
        return stringBuffer.toString();
    }

    public static String methodCall(Method method) {
        return CodegenUtils.methodCall(method, null);
    }

    public static String methodCall(Method method, String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append(method.getName());
        stringBuffer.append('(');
        Class<?>[] classArray = method.getParameterTypes();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(stringArray == null ? CodegenUtils.generatedArgumentName(i) : stringArray[i]);
        }
        stringBuffer.append(')');
        return stringBuffer.toString();
    }

    public static String reflectiveMethodObjectArray(Method method) {
        return CodegenUtils.reflectiveMethodObjectArray(method, null);
    }

    public static String reflectiveMethodObjectArray(Method method, String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append("new Object[] ");
        stringBuffer.append('{');
        Class<?>[] classArray = method.getParameterTypes();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(stringArray == null ? CodegenUtils.generatedArgumentName(i) : stringArray[i]);
        }
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    public static String reflectiveMethodParameterTypeArray(Method method) {
        StringBuffer stringBuffer = new StringBuffer(256);
        stringBuffer.append("new Class[] ");
        stringBuffer.append('{');
        Class<?>[] classArray = method.getParameterTypes();
        int n = classArray.length;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(ClassUtils.simpleClassName(classArray[i]));
            stringBuffer.append(".class");
        }
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    public static String generatedArgumentName(int n) {
        return String.valueOf((char)(97 + n));
    }

    public static String simpleClassName(Class clazz) {
        return ClassUtils.simpleClassName(clazz);
    }

    public static IndentedWriter toIndentedWriter(Writer writer) {
        return writer instanceof IndentedWriter ? (IndentedWriter)writer : new IndentedWriter(writer);
    }

    public static String packageNameToFileSystemDirPath(String string) {
        StringBuffer stringBuffer = new StringBuffer(string);
        int n = stringBuffer.length();
        for (int i = 0; i < n; ++i) {
            if (stringBuffer.charAt(i) != '.') continue;
            stringBuffer.setCharAt(i, File.separatorChar);
        }
        stringBuffer.append(File.separatorChar);
        return stringBuffer.toString();
    }

    private CodegenUtils() {
    }
}

