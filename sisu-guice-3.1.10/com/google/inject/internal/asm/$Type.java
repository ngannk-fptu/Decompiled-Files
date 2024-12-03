/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class $Type {
    public static final int VOID = 0;
    public static final int BOOLEAN = 1;
    public static final int CHAR = 2;
    public static final int BYTE = 3;
    public static final int SHORT = 4;
    public static final int INT = 5;
    public static final int FLOAT = 6;
    public static final int LONG = 7;
    public static final int DOUBLE = 8;
    public static final int ARRAY = 9;
    public static final int OBJECT = 10;
    public static final int METHOD = 11;
    public static final $Type VOID_TYPE = new $Type(0, null, 0x56050000, 1);
    public static final $Type BOOLEAN_TYPE = new $Type(1, null, 1509950721, 1);
    public static final $Type CHAR_TYPE = new $Type(2, null, 1124075009, 1);
    public static final $Type BYTE_TYPE = new $Type(3, null, 1107297537, 1);
    public static final $Type SHORT_TYPE = new $Type(4, null, 1392510721, 1);
    public static final $Type INT_TYPE = new $Type(5, null, 1224736769, 1);
    public static final $Type FLOAT_TYPE = new $Type(6, null, 1174536705, 1);
    public static final $Type LONG_TYPE = new $Type(7, null, 1241579778, 1);
    public static final $Type DOUBLE_TYPE = new $Type(8, null, 1141048066, 1);
    private final int a;
    private final char[] b;
    private final int c;
    private final int d;

    private $Type(int n, char[] cArray, int n2, int n3) {
        this.a = n;
        this.b = cArray;
        this.c = n2;
        this.d = n3;
    }

    public static $Type getType(String string) {
        return $Type.a(string.toCharArray(), 0);
    }

    public static $Type getObjectType(String string) {
        char[] cArray = string.toCharArray();
        return new $Type(cArray[0] == '[' ? 9 : 10, cArray, 0, cArray.length);
    }

    public static $Type getMethodType(String string) {
        return $Type.a(string.toCharArray(), 0);
    }

    public static $Type getMethodType($Type $Type, $Type ... $TypeArray) {
        return $Type.getType($Type.getMethodDescriptor($Type, $TypeArray));
    }

    public static $Type getType(Class clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                return INT_TYPE;
            }
            if (clazz == Void.TYPE) {
                return VOID_TYPE;
            }
            if (clazz == Boolean.TYPE) {
                return BOOLEAN_TYPE;
            }
            if (clazz == Byte.TYPE) {
                return BYTE_TYPE;
            }
            if (clazz == Character.TYPE) {
                return CHAR_TYPE;
            }
            if (clazz == Short.TYPE) {
                return SHORT_TYPE;
            }
            if (clazz == Double.TYPE) {
                return DOUBLE_TYPE;
            }
            if (clazz == Float.TYPE) {
                return FLOAT_TYPE;
            }
            return LONG_TYPE;
        }
        return $Type.getType($Type.getDescriptor(clazz));
    }

    public static $Type getType(Constructor constructor) {
        return $Type.getType($Type.getConstructorDescriptor(constructor));
    }

    public static $Type getType(Method method) {
        return $Type.getType($Type.getMethodDescriptor(method));
    }

    public static $Type[] getArgumentTypes(String string) {
        char c;
        char[] cArray = string.toCharArray();
        int n = 1;
        int n2 = 0;
        while ((c = cArray[n++]) != ')') {
            if (c == 'L') {
                while (cArray[n++] != ';') {
                }
                ++n2;
                continue;
            }
            if (c == '[') continue;
            ++n2;
        }
        $Type[] $TypeArray = new $Type[n2];
        n = 1;
        n2 = 0;
        while (cArray[n] != ')') {
            $TypeArray[n2] = $Type.a(cArray, n);
            n += $TypeArray[n2].d + ($TypeArray[n2].a == 10 ? 2 : 0);
            ++n2;
        }
        return $TypeArray;
    }

    public static $Type[] getArgumentTypes(Method method) {
        Class<?>[] classArray = method.getParameterTypes();
        $Type[] $TypeArray = new $Type[classArray.length];
        for (int i = classArray.length - 1; i >= 0; --i) {
            $TypeArray[i] = $Type.getType(classArray[i]);
        }
        return $TypeArray;
    }

    public static $Type getReturnType(String string) {
        char[] cArray = string.toCharArray();
        return $Type.a(cArray, string.indexOf(41) + 1);
    }

    public static $Type getReturnType(Method method) {
        return $Type.getType(method.getReturnType());
    }

    public static int getArgumentsAndReturnSizes(String string) {
        int n = 1;
        int n2 = 1;
        while (true) {
            char c;
            if ((c = string.charAt(n2++)) == ')') {
                c = string.charAt(n2);
                return n << 2 | (c == 'V' ? 0 : (c == 'D' || c == 'J' ? 2 : 1));
            }
            if (c == 'L') {
                while (string.charAt(n2++) != ';') {
                }
                ++n;
                continue;
            }
            if (c == '[') {
                while ((c = string.charAt(n2)) == '[') {
                    ++n2;
                }
                if (c != 'D' && c != 'J') continue;
                --n;
                continue;
            }
            if (c == 'D' || c == 'J') {
                n += 2;
                continue;
            }
            ++n;
        }
    }

    private static $Type a(char[] cArray, int n) {
        switch (cArray[n]) {
            case 'V': {
                return VOID_TYPE;
            }
            case 'Z': {
                return BOOLEAN_TYPE;
            }
            case 'C': {
                return CHAR_TYPE;
            }
            case 'B': {
                return BYTE_TYPE;
            }
            case 'S': {
                return SHORT_TYPE;
            }
            case 'I': {
                return INT_TYPE;
            }
            case 'F': {
                return FLOAT_TYPE;
            }
            case 'J': {
                return LONG_TYPE;
            }
            case 'D': {
                return DOUBLE_TYPE;
            }
            case '[': {
                int n2 = 1;
                while (cArray[n + n2] == '[') {
                    ++n2;
                }
                if (cArray[n + n2] == 'L') {
                    ++n2;
                    while (cArray[n + n2] != ';') {
                        ++n2;
                    }
                }
                return new $Type(9, cArray, n, n2 + 1);
            }
            case 'L': {
                int n3 = 1;
                while (cArray[n + n3] != ';') {
                    ++n3;
                }
                return new $Type(10, cArray, n + 1, n3 - 1);
            }
        }
        return new $Type(11, cArray, n, cArray.length - n);
    }

    public int getSort() {
        return this.a;
    }

    public int getDimensions() {
        int n = 1;
        while (this.b[this.c + n] == '[') {
            ++n;
        }
        return n;
    }

    public $Type getElementType() {
        return $Type.a(this.b, this.c + this.getDimensions());
    }

    public String getClassName() {
        switch (this.a) {
            case 0: {
                return "void";
            }
            case 1: {
                return "boolean";
            }
            case 2: {
                return "char";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 5: {
                return "int";
            }
            case 6: {
                return "float";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "double";
            }
            case 9: {
                StringBuffer stringBuffer = new StringBuffer(this.getElementType().getClassName());
                for (int i = this.getDimensions(); i > 0; --i) {
                    stringBuffer.append("[]");
                }
                return stringBuffer.toString();
            }
            case 10: {
                return new String(this.b, this.c, this.d).replace('/', '.');
            }
        }
        return null;
    }

    public String getInternalName() {
        return new String(this.b, this.c, this.d);
    }

    public $Type[] getArgumentTypes() {
        return $Type.getArgumentTypes(this.getDescriptor());
    }

    public $Type getReturnType() {
        return $Type.getReturnType(this.getDescriptor());
    }

    public int getArgumentsAndReturnSizes() {
        return $Type.getArgumentsAndReturnSizes(this.getDescriptor());
    }

    public String getDescriptor() {
        StringBuffer stringBuffer = new StringBuffer();
        this.a(stringBuffer);
        return stringBuffer.toString();
    }

    public static String getMethodDescriptor($Type $Type, $Type ... $TypeArray) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < $TypeArray.length; ++i) {
            $TypeArray[i].a(stringBuffer);
        }
        stringBuffer.append(')');
        $Type.a(stringBuffer);
        return stringBuffer.toString();
    }

    private void a(StringBuffer stringBuffer) {
        if (this.b == null) {
            stringBuffer.append((char)((this.c & 0xFF000000) >>> 24));
        } else if (this.a == 10) {
            stringBuffer.append('L');
            stringBuffer.append(this.b, this.c, this.d);
            stringBuffer.append(';');
        } else {
            stringBuffer.append(this.b, this.c, this.d);
        }
    }

    public static String getInternalName(Class clazz) {
        return clazz.getName().replace('.', '/');
    }

    public static String getDescriptor(Class clazz) {
        StringBuffer stringBuffer = new StringBuffer();
        $Type.a(stringBuffer, clazz);
        return stringBuffer.toString();
    }

    public static String getConstructorDescriptor(Constructor constructor) {
        Class<?>[] classArray = constructor.getParameterTypes();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < classArray.length; ++i) {
            $Type.a(stringBuffer, classArray[i]);
        }
        return stringBuffer.append(")V").toString();
    }

    public static String getMethodDescriptor(Method method) {
        Class<?>[] classArray = method.getParameterTypes();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        for (int i = 0; i < classArray.length; ++i) {
            $Type.a(stringBuffer, classArray[i]);
        }
        stringBuffer.append(')');
        $Type.a(stringBuffer, method.getReturnType());
        return stringBuffer.toString();
    }

    private static void a(StringBuffer stringBuffer, Class clazz) {
        Class<?> clazz2 = clazz;
        while (true) {
            if (clazz2.isPrimitive()) {
                int n = clazz2 == Integer.TYPE ? 73 : (clazz2 == Void.TYPE ? 86 : (clazz2 == Boolean.TYPE ? 90 : (clazz2 == Byte.TYPE ? 66 : (clazz2 == Character.TYPE ? 67 : (clazz2 == Short.TYPE ? 83 : (clazz2 == Double.TYPE ? 68 : (clazz2 == Float.TYPE ? 70 : 74)))))));
                stringBuffer.append((char)n);
                return;
            }
            if (!clazz2.isArray()) break;
            stringBuffer.append('[');
            clazz2 = clazz2.getComponentType();
        }
        stringBuffer.append('L');
        String string = clazz2.getName();
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            stringBuffer.append(c == '.' ? (char)'/' : (char)c);
        }
        stringBuffer.append(';');
    }

    public int getSize() {
        return this.b == null ? this.c & 0xFF : 1;
    }

    public int getOpcode(int n) {
        if (n == 46 || n == 79) {
            return n + (this.b == null ? (this.c & 0xFF00) >> 8 : 4);
        }
        return n + (this.b == null ? (this.c & 0xFF0000) >> 16 : 4);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof $Type)) {
            return false;
        }
        $Type $Type = ($Type)object;
        if (this.a != $Type.a) {
            return false;
        }
        if (this.a >= 9) {
            if (this.d != $Type.d) {
                return false;
            }
            int n = this.c;
            int n2 = $Type.c;
            int n3 = n + this.d;
            while (n < n3) {
                if (this.b[n] != $Type.b[n2]) {
                    return false;
                }
                ++n;
                ++n2;
            }
        }
        return true;
    }

    public int hashCode() {
        int n = 13 * this.a;
        if (this.a >= 9) {
            int n2;
            int n3 = n2 + this.d;
            for (n2 = this.c; n2 < n3; ++n2) {
                n = 17 * (n + this.b[n2]);
            }
        }
        return n;
    }

    public String toString() {
        return this.getDescriptor();
    }
}

